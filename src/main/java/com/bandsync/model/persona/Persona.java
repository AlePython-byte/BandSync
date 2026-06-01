package com.bandsync.model.persona;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Document(collection = "artistas")
public abstract class Persona {

    @Id
    private String id;
    private String nombre;
    private String correo;
    private String telefono;
    private LocalDate fechaNacimiento;

    @JsonIgnore
    private String contrasena;

    private boolean activo;
}
