package com.bandsync.dto.response;

import com.bandsync.model.enums.EstadoCancionEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancionResponseDTO {

    private String id;
    private String bandaId;
    private String titulo;
    private int duracionSeg;
    private EstadoCancionEnum estado;
    private List<String> autores;
    private String archivoDemo;
}
