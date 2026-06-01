package com.bandsync.model.dominio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "pagos")
public class Pago {

    @Id
    private String id;
    private String bandaId;
    private String showId;
    private Double montoBruto;
    private Double gastosProduccion;
    private Double montoNeto;
    private LocalDate fecha;
    private Map<String, Double> distribuciones;

    public void calcularNeto() {
        if (montoBruto == null || gastosProduccion == null) return;
        this.montoNeto = montoBruto - gastosProduccion;
    }

    public void distribuir(Map<String, Double> porcentajes) {
        if (montoNeto == null || porcentajes == null) return;
        this.distribuciones = new HashMap<>();
        porcentajes.forEach((artistaId, pct) ->
                this.distribuciones.put(artistaId, montoNeto * pct / 100.0));
    }
}
