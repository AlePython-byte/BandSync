package com.bandsync.model.contrato;

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
@TypeAlias("contrato_show")
public class ContratoShow extends Contrato {

    private Double tarifaBase;
    private int anticipoPct;
    private Double saldoPendiente;
    private String clausulaCancelacion;
    private String showId;

    @Override
    public Double calcularMontoFinal() {
        return tarifaBase;
    }

    public Double calcularAnticipo() {
        if (tarifaBase == null) return 0.0;
        return tarifaBase * anticipoPct / 100.0;
    }
}
