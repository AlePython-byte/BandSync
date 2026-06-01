package com.bandsync.repository;

import com.bandsync.model.dominio.Cancion;
import com.bandsync.model.enums.EstadoCancionEnum;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CancionRepository extends MongoRepository<Cancion, String> {

    List<Cancion> findByBandaId(String bandaId);

    List<Cancion> findByBandaIdAndEstado(String bandaId, EstadoCancionEnum estado);
}
