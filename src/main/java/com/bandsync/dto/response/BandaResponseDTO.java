package com.bandsync.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BandaResponseDTO {

    private String id;
    private String nombre;
    private List<String> generosMusicales;
    private String ciudadBase;
    private String codigoInvitacion;
    private String urlPerfil;
    private boolean activa;
    private int cantidadIntegrantes;
}
