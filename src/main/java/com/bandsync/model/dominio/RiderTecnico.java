package com.bandsync.model.dominio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "riders")
public class RiderTecnico {

    @Id
    private String id;
    private String showId;
    private String configuracionSonido;
    private String configuracionIluminacion;
    private String hospitalidad;
}
