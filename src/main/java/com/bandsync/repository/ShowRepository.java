package com.bandsync.repository;

import com.bandsync.model.dominio.Show;
import com.bandsync.model.enums.EstadoShowEnum;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowRepository extends MongoRepository<Show, String> {

    List<Show> findByBandaId(String bandaId);

    List<Show> findByBandaIdAndEstado(String bandaId, EstadoShowEnum estado);

    List<Show> findByBandaIdAndFechaBetween(String bandaId, LocalDateTime inicio, LocalDateTime fin);

    List<Show> findByBandaIdOrderByFechaDesc(String bandaId);
}
