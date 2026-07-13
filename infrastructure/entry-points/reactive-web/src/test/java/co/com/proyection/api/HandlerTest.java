package co.com.proyection.api;

import co.com.proyection.model.events.gateways.EventoGuiaPublisherGateway;
import co.com.proyection.model.events.gateways.IdempotenciaGateway;
import co.com.proyection.usecase.registrarevento.RegistrarEventoUseCase;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


public class HandlerTest {
    private WebTestClient webTestClient;

    private EventoGuiaPublisherGateway publisherGateway;
    private IdempotenciaGateway idempotenciaGateway;

    @BeforeEach
    void setUp() {

        publisherGateway = Mockito.mock(EventoGuiaPublisherGateway.class);
        idempotenciaGateway = Mockito.mock(IdempotenciaGateway.class);

        Validator validator =
                Validation.buildDefaultValidatorFactory().getValidator();

        RegistrarEventoUseCase useCase =
                new RegistrarEventoUseCase(publisherGateway, idempotenciaGateway);

        Handler handler = new Handler(useCase, validator);

        RouterFunction<?> router = new RouterRest().routerFunction(handler);

        webTestClient = WebTestClient
                .bindToRouterFunction(router)
                .build();
    }

    @Test
    void debeAceptarUnEventoValidoYResponder202() {

        when(idempotenciaGateway.existe(any()))
                .thenReturn(Mono.just(false));

        when(publisherGateway.publicar(any()))
                .thenReturn(Mono.empty());

        when(idempotenciaGateway.registrar(any()))
                .thenReturn(Mono.empty());

        String payload = """
                {
                  "eventoId": "evt-100",
                  "numeroGuia": "TCC000000001",
                  "estado": "EN_TRANSITO",
                  "fechaEvento": "2026-07-12T10:00:00Z",
                  "origen": "SCANNER_BODEGA",
                  "ciudad": "Bogota"
                }
                """;

        webTestClient.post()
                .uri("/api/v1/guias/eventos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isAccepted()
                .expectBody()
                .jsonPath("$.eventoId").isEqualTo("evt-100")
                .jsonPath("$.estado").isEqualTo("EN_TRANSITO");
    }

    @Test
    void debeResponder400CuandoFaltaUnCampoObligatorio() {

        String payload = """
                {
                  "numeroGuia": "TCC000000001",
                  "estado": "EN_TRANSITO",
                  "fechaEvento": "2026-07-12T10:00:00Z",
                  "origen": "SCANNER_BODEGA"
                }
                """;

        webTestClient.post()
                .uri("/api/v1/guias/eventos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void debeResponder400CuandoElEstadoNoExiste() {

        String payload = """
                {
                  "eventoId": "evt-101",
                  "numeroGuia": "TCC000000001",
                  "estado": "NO_EXISTE",
                  "fechaEvento": "2026-07-12T10:00:00Z",
                  "origen": "SCANNER_BODEGA"
                }
                """;

        webTestClient.post()
                .uri("/api/v1/guias/eventos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isBadRequest();
    }
}
