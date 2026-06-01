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
@TypeAlias("productor")
public class Productor extends Artista {

    private List<String> tipoProduccion;
    private String dawPrincipal;

    @Override
    public String obtenerFichaArtistica() {
        return "Productor: " + getNombre()
                + " | Rol: " + getRol()
                + " | Tipos de producción: " + tipoProduccion
                + " | DAW: " + dawPrincipal
                + " | Año ingreso: " + getAnioIngreso();
    }
}
