package com.bandsync.repository;

import com.bandsync.model.persona.Artista;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtistaRepository extends MongoRepository<Artista, String> {

    Optional<Artista> findByCorreo(String correo);

    List<Artista> findByBandaId(String bandaId);

    boolean existsByCorreo(String correo);

    List<Artista> findByBandaIdAndActivoTrue(String bandaId);
}
