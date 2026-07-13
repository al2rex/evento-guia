package co.com.proyection.cache;

import co.com.proyection.model.events.gateways.IdempotenciaGateway;
import org.springframework.stereotype.Component;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class EventCache  implements IdempotenciaGateway {

    private final Cache<String, Boolean> eventosProcesados;

    public EventCache() {
        this.eventosProcesados = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(24))
                .maximumSize(1_000_000)
                .build();
    }

    @Override
    public Mono<Boolean> existe(String eventoId) {
        return Mono.fromSupplier(() -> eventosProcesados.getIfPresent(eventoId) != null);
    }

    @Override
    public Mono<Void> registrar(String eventoId) {
        return Mono.fromRunnable(() -> eventosProcesados.put(eventoId, Boolean.TRUE));
    }
}
