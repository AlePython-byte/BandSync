package com.bandsync.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContratoShowRequestDTO {

    @NotBlank
    private String showId;

    @NotNull
    @DecimalMin("0.0")
    private Double tarifaBase;

    @Min(0)
    @Max(100)
    private int anticipoPct;

    private String clausulaCancelacion;

    @Min(1)
    private int vigenciaDias;
}
