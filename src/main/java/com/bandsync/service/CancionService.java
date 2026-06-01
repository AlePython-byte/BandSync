package com.bandsync.service;

import com.bandsync.dto.request.CancionRequestDTO;
import com.bandsync.exception.ResourceNotFoundException;
import com.bandsync.model.dominio.Cancion;
import com.bandsync.model.enums.EstadoCancionEnum;
import com.bandsync.repository.BandaRepository;
import com.bandsync.repository.CancionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CancionService {

    private final CancionRepository cancionRepository;
    private final BandaRepository bandaRepository;

    public Cancion crear(String bandaId, CancionRequestDTO request) {
        bandaRepository.findById(bandaId)
                .orElseThrow(() -> new ResourceNotFoundException("Banda", bandaId));

        Cancion cancion = Cancion.builder()
                .bandaId(bandaId)
                .titulo(request.getTitulo())
                .duracionSeg(request.getDuracionSeg())
                .estado(request.getEstado())
                .autores(request.getAutores())
                .archivoDemo(request.getArchivoDemo())
                .build();

        Cancion guardada = cancionRepository.save(cancion);
        log.info("Canción '{}' creada para banda {}", guardada.getTitulo(), bandaId);
        return guardada;
    }

    public Cancion actualizarEstado(String cancionId, EstadoCancionEnum nuevoEstado) {
        Cancion cancion = cancionRepository.findById(cancionId)
                .orElseThrow(() -> new ResourceNotFoundException("Cancion", cancionId));
        cancion.actualizarEstado(nuevoEstado);
        return cancionRepository.save(cancion);
    }

    public List<Cancion> listarPorBanda(String bandaId, EstadoCancionEnum filtroEstado) {
        if (filtroEstado == null) {
            return cancionRepository.findByBandaId(bandaId);
        }
        return cancionRepository.findByBandaIdAndEstado(bandaId, filtroEstado);
    }

    public List<Cancion> obtenerSetlist(List<String> cancionIds) {
        Map<String, Cancion> porId = new HashMap<>();
        cancionRepository.findAllById(cancionIds)
                .forEach(c -> porId.put(c.getId(), c));

        return cancionIds.stream()
                .map(porId::get)
                .filter(c -> c != null && c.getEstado() == EstadoCancionEnum.LISTA)
                .toList();
    }
}
