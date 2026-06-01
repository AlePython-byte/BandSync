package com.bandsync.repository;

import com.bandsync.model.dominio.Banda;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BandaRepository extends MongoRepository<Banda, String> {

    Optional<Banda> findByNombreAndCiudadBase(String nombre, String ciudadBase);

    Optional<Banda> findByCodigoInvitacion(String codigo);

    List<Banda> findByActivaTrue();
}
