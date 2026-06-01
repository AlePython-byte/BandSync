package com.bandsync.dto.response;

import com.bandsync.model.enums.EstadoContratoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContratoResponseDTO {

    private String id;
    private String bandaId;
    private String tipo;
    private LocalDate fechaFirma;
    private int vigenciaDias;
    private EstadoContratoEnum estado;
    private String observaciones;
    private Double montoFinal;
    private boolean estaVigente;
    private Map<String, Object> detallesPorTipo;
}
