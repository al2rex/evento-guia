package co.com.proyection.usecase.registrarevento;

import co.com.proyection.model.events.EstadoGuia;
import co.com.proyection.model.events.EventoGuia;
import co.com.proyection.model.events.gateways.EventoGuiaPublisherGateway;
import co.com.proyection.model.events.gateways.IdempotenciaGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RegistrarEventoUseCaseTest {
    private EventoGuiaPublisherGateway publisherGateway;
    private IdempotenciaGateway idempotenciaGateway;
    private RegistrarEventoUseCase useCase;

    @BeforeEach
    void setUp() {
        publisherGateway = Mockito.mock(EventoGuiaPublisherGateway.class);
        idempotenciaGateway = Mockito.mock(IdempotenciaGateway.class);
        useCase = new RegistrarEventoUseCase(publisherGateway, idempotenciaGateway);
    }

    private EventoGuia eventoDePrueba(String eventoId) {
        return EventoGuia.crear(
                eventoId,
                "TCC123456789",
                EstadoGuia.EN_TRANSITO,
                Instant.parse("2026-07-12T10:00:00Z"),
                "SCANNER_BODEGA",
                null,
                "Medellin");
    }

    @Test
    void debePublicarYRegistrarCuandoEsEventoNuevo() {
        EventoGuia evento = eventoDePrueba("evt-001");
        when(idempotenciaGateway.existe("evt-001")).thenReturn(Mono.just(false));
        when(publisherGateway.publicar(evento)).thenReturn(Mono.empty());
        when(idempotenciaGateway.registrar("evt-001")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.ejecutar(evento))
                .expectNext(evento)
                .verifyComplete();

        verify(publisherGateway, times(1)).publicar(evento);
        verify(idempotenciaGateway, times(1)).registrar("evt-001");
    }

    @Test
    void noDebePublicarNuevamenteCuandoElEventoYaFueProcesado() {
        EventoGuia evento = eventoDePrueba("evt-duplicado");
        when(idempotenciaGateway.existe("evt-duplicado")).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.ejecutar(evento))
                .expectNext(evento)
                .verifyComplete();

        verify(publisherGateway, never()).publicar(any());
        verify(idempotenciaGateway, never()).registrar(eq("evt-duplicado"));
    }

    @Test
    void debePropagarErrorSiFallaLaPublicacion() {
        EventoGuia evento = eventoDePrueba("evt-error");

        when(idempotenciaGateway.existe("evt-error"))
                .thenReturn(Mono.just(false));

        when(idempotenciaGateway.registrar(any()))
                .thenReturn(Mono.empty());

        when(publisherGateway.publicar(evento))
                .thenReturn(Mono.error(new RuntimeException("broker no disponible")));

        StepVerifier.create(useCase.ejecutar(evento))
                .expectErrorMessage("broker no disponible")
                .verify();

        verify(idempotenciaGateway, never()).registrar(any());
    }
}
