package com.bandsync.dto.request;

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
public class BandaRequestDTO {

    @NotBlank
    private String nombre;

    @NotNull
    private List<String> generosMusicales;

    @NotBlank
    private String ciudadBase;
}
