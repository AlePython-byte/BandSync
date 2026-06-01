package com.bandsync.dto.request;

import com.bandsync.model.enums.EstadoCancionEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancionRequestDTO {

    @NotBlank
    private String titulo;

    @Min(1)
    private int duracionSeg;

    @NotNull
    private EstadoCancionEnum estado;

    private List<String> autores;

    private String archivoDemo;
}
