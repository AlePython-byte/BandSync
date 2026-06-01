package com.bandsync.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroRequestDTO {

    @NotBlank
    private String nombre;

    @NotBlank
    @Email
    private String correo;

    @NotBlank
    @Size(min = 8)
    private String contrasena;

    private String telefono;

    @Past
    private LocalDate fechaNacimiento;

    // "vocalista" | "instrumentista" | "productor" | "manager"
    private String tipoArtista;

    private Map<String, Object> atributosEspecificos;
}
