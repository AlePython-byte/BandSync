package com.bandsync.dto.request;

import com.bandsync.model.enums.TipoShowEnum;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowRequestDTO {

    @NotBlank
    private String nombreEvento;

    @NotBlank
    private String venue;

    @NotBlank
    private String ciudad;

    @Future
    private LocalDateTime fecha;

    @NotNull
    private TipoShowEnum tipoShow;

    @DecimalMin("0.0")
    private Double tarifaAcordada;
}
