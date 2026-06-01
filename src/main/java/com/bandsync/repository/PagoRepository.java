package com.bandsync.repository;

import com.bandsync.model.dominio.Pago;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PagoRepository extends MongoRepository<Pago, String> {

    List<Pago> findByBandaId(String bandaId);

    List<Pago> findByBandaIdAndFechaBetween(String bandaId, LocalDate inicio, LocalDate fin);

    List<Pago> findByShowId(String showId);

    @Aggregation(pipeline = {
        "{ $match: { bandaId: ?0, fecha: { $gte: ?1, $lte: ?2 } } }",
        "{ $group: { _id: null, total: { $sum: '$montoNeto' } } }"
    })
    Double sumMontoNetoByBandaIdAndFechaBetween(String bandaId, LocalDate inicio, LocalDate fin);
}
