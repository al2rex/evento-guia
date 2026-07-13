package co.com.proyection.usecase.registrarevento;

import co.com.proyection.model.events.EventoGuia;
import co.com.proyection.model.events.gateways.EventoGuiaPublisherGateway;
import co.com.proyection.model.events.gateways.IdempotenciaGateway;
import reactor.core.publisher.Mono;

public class RegistrarEventoUseCase {
    private final EventoGuiaPublisherGateway publisherGateway;
    private final IdempotenciaGateway idempotenciaGateway;

    public RegistrarEventoUseCase(EventoGuiaPublisherGateway publisherGateway,
                                      IdempotenciaGateway idempotenciaGateway) {
        this.publisherGateway = publisherGateway;
        this.idempotenciaGateway = idempotenciaGateway;
    }

    public Mono<EventoGuia> ejecutar(EventoGuia evento) {
        return idempotenciaGateway.existe(evento.getEventoId())
                .flatMap(yaExiste -> yaExiste
                        ? Mono.just(evento)
                        : publicarYRegistrar(evento));
    }

    private Mono<EventoGuia> publicarYRegistrar(EventoGuia evento) {
        return publisherGateway.publicar(evento)
                .then(idempotenciaGateway.registrar(evento.getEventoId()))
                .thenReturn(evento);
    }

}
