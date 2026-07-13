package co.com.proyection.api.mapper;

import co.com.proyection.api.dto.EventoGuiaRequest;
import co.com.proyection.api.dto.EventoGuiaResponse;
import co.com.proyection.model.events.EstadoGuia;
import co.com.proyection.model.events.EventoGuia;

public class EventoGuiaMapper {
    private EventoGuiaMapper() {
    }

    public static EventoGuia aDominio(EventoGuiaRequest request) {
        EstadoGuia estado = parseEstado(request.estado());
        return EventoGuia.crear(
                request.eventoId(),
                request.numeroGuia(),
                estado,
                request.fechaEvento(),
                request.origen(),
                request.detalle(),
                request.ciudad());
    }

    public static EventoGuiaResponse aResponse(EventoGuia evento) {
        return new EventoGuiaResponse(
                evento.getEventoId(),
                evento.getNumeroGuia(),
                evento.getEstado().name(),
                evento.getFechaEvento(),
                evento.getFechaRecepcion());
    }

    private static EstadoGuia parseEstado(String estado) {
        try {
            return EstadoGuia.valueOf(estado.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new IllegalArgumentException("estado invalido: " + estado);
        }
    }

}
