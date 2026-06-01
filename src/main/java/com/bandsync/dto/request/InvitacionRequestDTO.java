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
public class InvitacionRequestDTO {

    @NotBlank
    private String codigoInvitacion;

    @NotBlank
    private String tipoArtista;
}
