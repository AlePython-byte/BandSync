package com.bandsync.model.persona;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TypeAlias("vocalista")
public class Vocalista extends Artista {

    private String tessitura;
    private List<String> generosInterpreta;

    @Override
    public String obtenerFichaArtistica() {
        return "Vocalista: " + getNombre()
                + " | Rol: " + getRol()
                + " | Tessitura: " + tessitura
                + " | Géneros: " + generosInterpreta
                + " | Año ingreso: " + getAnioIngreso();
    }
}
