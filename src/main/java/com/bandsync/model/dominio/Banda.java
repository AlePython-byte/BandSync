package com.bandsync.model.dominio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "bandas")
public class Banda {

    @Id
    private String id;
    private String nombre;
    private List<String> generosMusicales;
    private String ciudadBase;
    private String codigoInvitacion;
    private String urlPerfil;
    private boolean activa;
    private List<String> integrantesIds;

    public void generarCodigoInvitacion() {
        this.codigoInvitacion = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();
    }
}
