package com.bandsync.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiderTecnicoRequestDTO {

    @NotBlank
    private String showId;

    private String configuracionSonido;

    private String configuracionIluminacion;

    private String hospitalidad;
}
