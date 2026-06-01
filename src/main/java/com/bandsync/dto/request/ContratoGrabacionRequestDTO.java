package com.bandsync.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContratoGrabacionRequestDTO {

    @Min(1)
    private int numTracks;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private double pctRegalias;

    private boolean derechosMaster;

    @Future
    private LocalDate fechaLimiteEntrega;

    private List<String> productoresIds;

    @Min(1)
    private int vigenciaDias;
}
