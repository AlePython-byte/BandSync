package com.bandsync.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnsayoRequestDTO {

    @Future
    private LocalDateTime fecha;

    @Min(15)
    @Max(480)
    private int duracionMin;

    @NotBlank
    private String direccionSala;

    private List<String> temasAPracticar;
}
