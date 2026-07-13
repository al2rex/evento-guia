package co.com.proyection.api.dto;

import java.time.Instant;

public record EventoGuiaResponse(
        String eventoId,
        String numeroGuia,
        String estado,
        Instant fechaEvento,
        Instant fechaRecepcion

) {
}
