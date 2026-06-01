package com.bandsync.model.dominio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "ensayos")
public class Ensayo {

    @Id
    private String id;
    private String bandaId;
    private LocalDateTime fecha;
    private int duracionMin;
    private String direccionSala;
    private List<String> temasAPracticar;
    private Map<String, Boolean> confirmaciones;

    public void confirmarAsistencia(String artistaId) {
        if (this.confirmaciones == null) this.confirmaciones = new HashMap<>();
        this.confirmaciones.put(artistaId, true);
    }

    public long contarConfirmaciones() {
        if (confirmaciones == null) return 0;
        return confirmaciones.values().stream().filter(Boolean::booleanValue).count();
    }
}
