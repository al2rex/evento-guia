package co.com.proyection.api;

import co.com.proyection.api.dto.EventoGuiaRequest;
import co.com.proyection.api.mapper.EventoGuiaMapper;
import co.com.proyection.model.events.EventoGuia;
import co.com.proyection.usecase.registrarevento.RegistrarEventoUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Handler {
    private final Validator validator;
    private final RegistrarEventoUseCase registrarEventoUseCase;


    public Handler(RegistrarEventoUseCase registrarEventoGuiaUseCase, Validator validator) {
        this.registrarEventoUseCase = registrarEventoGuiaUseCase;
        this.validator = validator;
    }

    public Mono<ServerResponse> registrarEvento(ServerRequest request) {
        return request.bodyToMono(EventoGuiaRequest.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("cuerpo de la peticion vacio")))
                .map(this::validar)
                .map(EventoGuiaMapper::aDominio)
                .flatMap(registrarEventoUseCase::ejecutar)
                .flatMap(this::responderCreado)
                .onErrorResume(IllegalArgumentException.class,
                        e -> ServerResponse.badRequest().bodyValue(
                                Map.of("message", e.getMessage())));
    }

    private EventoGuiaRequest validar(EventoGuiaRequest body) {
        Set<ConstraintViolation<EventoGuiaRequest>> violaciones = validator.validate(body);
        if (!violaciones.isEmpty()) {
            String mensaje = violaciones.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException(mensaje);
        }
        return body;
    }

    private Mono<ServerResponse> responderCreado(EventoGuia evento) {
        return ServerResponse.status(202)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(EventoGuiaMapper.aResponse(evento));
    }

}
