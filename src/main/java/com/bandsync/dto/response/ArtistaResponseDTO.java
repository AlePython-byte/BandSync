package com.bandsync.dto.response;

import com.bandsync.model.enums.RolEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistaResponseDTO {

    private String id;
    private String nombre;
    private String correo;
    private String telefono;
    private RolEnum rol;
    private String tipo;
    private String fichaArtistica;
    private String bandaId;
    private boolean activo;
}
