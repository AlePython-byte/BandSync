package com.bandsync.model.contrato;

import com.bandsync.model.enums.EstadoContratoEnum;
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
@Document(collection = "contratos")
public abstract class Contrato {

    @Id
    private String id;
    private String bandaId;
    private LocalDate fechaFirma;
    private int vigenciaDias;
    private EstadoContratoEnum estado;
    private String observaciones;

    public abstract Double calcularMontoFinal();

    public boolean estaVigente() {
        if (fechaFirma == null) return false;
        LocalDate hoy = LocalDate.now();
        LocalDate vencimiento = fechaFirma.plusDays(vigenciaDias);
        return !hoy.isBefore(fechaFirma) && !hoy.isAfter(vencimiento);
    }

    public void cambiarEstado(EstadoContratoEnum nuevoEstado) {
        this.estado = nuevoEstado;
    }
}
