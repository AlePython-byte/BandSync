package com.bandsync.model.dominio;

import com.bandsync.model.enums.EstadoCancionEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "canciones")
public class Cancion {

    @Id
    private String id;
    private String bandaId;
    private String titulo;
    private int duracionSeg;
    private EstadoCancionEnum estado;
    private List<String> autores;
    private String archivoDemo;

    public void actualizarEstado(EstadoCancionEnum nuevoEstado) {
        this.estado = nuevoEstado;
    }
}
