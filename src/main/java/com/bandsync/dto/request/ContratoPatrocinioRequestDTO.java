package com.bandsync.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ContratoPatrocinioRequestDTO {

    @NotBlank
    private String marca;

    @NotNull
    @DecimalMin("0.0")
    private Double montoPatrocinio;

    private String obligacionesImagen;

    private List<String> exclusividades;

    @Future
    private LocalDate fechaVencimiento;

    @Min(1)
    private int vigenciaDias;
}
