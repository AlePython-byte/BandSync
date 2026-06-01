package com.bandsync.dto.response;

import com.bandsync.model.enums.EstadoShowEnum;
import com.bandsync.model.enums.TipoShowEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowResponseDTO {

    private String id;
    private String bandaId;
    private String nombreEvento;
    private String venue;
    private String ciudad;
    private LocalDateTime fecha;
    private TipoShowEnum tipoShow;
    private Double tarifaAcordada;
    private EstadoShowEnum estado;
    private boolean tieneContrato;
}
