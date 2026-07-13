package co.com.proyection.model.events;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "eventoId")
public class EventoGuia {
    private final String eventoId;
    private final String numeroGuia;
    private final EstadoGuia estado;
    private final Instant fechaEvento;
    private final Instant fechaRecepcion;
    private final String origen;
    private final String detalle;
    private final String ciudad;

    /**
     * Punto único de creación: valida invariantes de negocio antes de
     * permitir que el evento exista. Evita que un estado inválido llegue
     * al caso de uso o al adaptador de publicación.
     */
    public static EventoGuia crear(String eventoId,
                                   String numeroGuia,
                                   EstadoGuia estado,
                                   Instant fechaEvento,
                                   String origen,
                                   String detalle,
                                   String ciudad) {
        if (isBlank(eventoId)) {
            throw new IllegalArgumentException("eventoId es obligatorio");
        }
        if (isBlank(numeroGuia)) {
            throw new IllegalArgumentException("numeroGuia es obligatorio");
        }
        if (Objects.isNull(estado)) {
            throw new IllegalArgumentException("estado es obligatorio");
        }
        if (Objects.isNull(fechaEvento)) {
            throw new IllegalArgumentException("fechaEvento es obligatoria");
        }
        if (isBlank(origen)) {
            throw new IllegalArgumentException("origen es obligatorio");
        }
        return EventoGuia.builder()
                .eventoId(eventoId)
                .numeroGuia(numeroGuia)
                .estado(estado)
                .fechaEvento(fechaEvento)
                .fechaRecepcion(Instant.now())
                .origen(origen)
                .detalle(detalle)
                .ciudad(ciudad)
                .build();
    }

    private static boolean isBlank(String value) {
        return Objects.isNull(value) || value.isBlank();
    }

}
