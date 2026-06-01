package com.bandsync.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnsayoResponseDTO {

    private String id;
    private String bandaId;
    private LocalDateTime fecha;
    private int duracionMin;
    private String direccionSala;
    private List<String> temasAPracticar;
    private Map<String, Boolean> confirmaciones;
    private int totalConfirmados;
}
