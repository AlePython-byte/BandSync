package com.bandsync.model.contrato;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TypeAlias("contrato_grabacion")
public class ContratoGrabacion extends Contrato {

    private int numTracks;
    private double pctRegalias;
    private boolean derechosMaster;
    private LocalDate fechaLimiteEntrega;
    private List<String> productoresIds;

    @Override
    public Double calcularMontoFinal() {
        return numTracks * pctRegalias * 1000;
    }
}
