package com.bandsync.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiderTecnicoResponseDTO {

    private String id;
    private String showId;
    private String configuracionSonido;
    private String configuracionIluminacion;
    private String hospitalidad;
}
