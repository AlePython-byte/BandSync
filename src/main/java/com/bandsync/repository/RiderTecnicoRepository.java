package com.bandsync.repository;

import com.bandsync.model.dominio.RiderTecnico;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RiderTecnicoRepository extends MongoRepository<RiderTecnico, String> {

    Optional<RiderTecnico> findByShowId(String showId);
}
