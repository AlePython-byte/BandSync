package com.bandsync.repository;

import com.bandsync.model.contrato.Contrato;
import com.bandsync.model.enums.EstadoContratoEnum;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContratoRepository extends MongoRepository<Contrato, String> {

    List<Contrato> findByBandaId(String bandaId);

    List<Contrato> findByBandaIdAndEstado(String bandaId, EstadoContratoEnum estado);

    @Query("{ 'bandaId': ?0, 'fechaVencimiento': { $gte: ?1, $lte: ?2 } }")
    List<Contrato> findContratosProximosAVencer(String bandaId, LocalDate hoy, LocalDate en30Dias);
}
