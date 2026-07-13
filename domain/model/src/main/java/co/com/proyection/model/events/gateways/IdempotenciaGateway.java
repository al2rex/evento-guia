package co.com.proyection.model.events.gateways;

import reactor.core.publisher.Mono;

public interface IdempotenciaGateway {
    /**
     * true si el eventoId ya fue procesado previamente.
     */
    Mono<Boolean> existe(String eventoId);

    /**
     * Marca el eventoId como procesado. Debe hacerse solo cuando
     * la publicación fue exitosa.
     */
    Mono<Void> registrar(String eventoId);

}
