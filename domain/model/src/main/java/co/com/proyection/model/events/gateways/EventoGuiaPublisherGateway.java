package co.com.proyection.model.events.gateways;

import co.com.proyection.model.events.EventoGuia;
import reactor.core.publisher.Mono;

public interface EventoGuiaPublisherGateway {

    Mono<Void> publicar(EventoGuia evento);

}
