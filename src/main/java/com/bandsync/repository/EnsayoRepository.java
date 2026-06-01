package com.bandsync.repository;

import com.bandsync.model.dominio.Ensayo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EnsayoRepository extends MongoRepository<Ensayo, String> {

    List<Ensayo> findByBandaId(String bandaId);

    List<Ensayo> findByBandaIdAndFechaAfter(String bandaId, LocalDateTime fecha);

    List<Ensayo> findByBandaIdOrderByFechaDesc(String bandaId);
}
