package com.bandsync.model.persona;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TypeAlias("manager")
public class Manager extends Artista {

    private String agencia;
    private double comisionPct;

    @Override
    public String obtenerFichaArtistica() {
        return "Manager: " + getNombre()
                + " | Rol: " + getRol()
                + " | Agencia: " + agencia
                + " | Comisión: " + comisionPct + "%"
                + " | Año ingreso: " + getAnioIngreso();
    }
}
