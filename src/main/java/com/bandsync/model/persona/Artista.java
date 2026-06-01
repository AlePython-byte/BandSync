package com.bandsync.model.persona;

import com.bandsync.model.enums.RolEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class Artista extends Persona {

    private String bandaId;
    private RolEnum rol;
    private int anioIngreso;
    private List<String> redesSociales;

    public abstract String obtenerFichaArtistica();
}
