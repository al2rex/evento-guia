package co.com.proyection.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record EventoGuiaRequest(
        @NotBlank(message = "eventoId es obligatorio")
        String eventoId,

        @NotBlank(message = "numeroGuia es obligatorio")
        String numeroGuia,

        @NotBlank(message = "estado es obligatorio")
        String estado,

        @NotNull(message = "fechaEvento es obligatoria")
        Instant fechaEvento,

        @NotBlank(message = "origen es obligatorio")
        String origen,

        String detalle,

        String ciudad
) {
}
