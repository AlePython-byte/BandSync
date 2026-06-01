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
@TypeAlias("instrumentista")
public class Instrumentista extends Artista {

    private List<String> instrumentos;
    private boolean esTitular;

    @Override
    public String obtenerFichaArtistica() {
        return "Instrumentista: " + getNombre()
                + " | Rol: " + getRol()
                + " | Instrumentos: " + instrumentos
                + " | Titular: " + esTitular
                + " | Año ingreso: " + getAnioIngreso();
    }
}
