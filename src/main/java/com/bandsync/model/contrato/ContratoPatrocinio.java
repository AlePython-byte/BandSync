package com.bandsync.model.contrato;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TypeAlias("contrato_patrocinio")
public class ContratoPatrocinio extends Contrato {

    private String marca;
    private Double montoPatrocinio;
    private String obligacionesImagen;
    private List<String> exclusividades;
    private LocalDate fechaVencimiento;

    @Override
    public Double calcularMontoFinal() {
        return montoPatrocinio;
    }

    public boolean tieneConflictoExclusividad(ContratoPatrocinio otra) {
        if (this.exclusividades == null || otra.getExclusividades() == null) return false;
        return !Collections.disjoint(this.exclusividades, otra.getExclusividades());
    }
}
