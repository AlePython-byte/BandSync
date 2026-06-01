package com.bandsync.model.dominio;

import com.bandsync.model.enums.EstadoShowEnum;
import com.bandsync.model.enums.TipoShowEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "shows")
public class Show {

    @Id
    private String id;
    private String bandaId;
    private String nombreEvento;
    private String venue;
    private String ciudad;
    private LocalDateTime fecha;
    private TipoShowEnum tipoShow;
    private Double tarifaAcordada;
    private EstadoShowEnum estado;

    public void confirmar() {
        this.estado = EstadoShowEnum.CONFIRMADO;
    }

    public void cancelar() {
        this.estado = EstadoShowEnum.CANCELADO;
    }
}
