package com.bandsync.service;

import com.bandsync.dto.request.EnsayoRequestDTO;
import com.bandsync.dto.response.EnsayoResponseDTO;
import com.bandsync.exception.ResourceNotFoundException;
import com.bandsync.model.dominio.Banda;
import com.bandsync.model.dominio.Ensayo;
import com.bandsync.model.persona.Artista;
import com.bandsync.repository.ArtistaRepository;
import com.bandsync.repository.BandaRepository;
import com.bandsync.repository.EnsayoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnsayoService {

    private final EnsayoRepository ensayoRepository;
    private final BandaRepository bandaRepository;
    private final ArtistaRepository artistaRepository;
    private final NotificacionService notificacionService;

    public Ensayo programar(String bandaId, EnsayoRequestDTO request) {
        Banda banda = bandaRepository.findById(bandaId)
                .orElseThrow(() -> new ResourceNotFoundException("Banda", bandaId));

        List<Artista> artistas = artistaRepository.findByBandaIdAndActivoTrue(bandaId);
        Map<String, Boolean> confirmaciones = new HashMap<>();
        artistas.forEach(a -> confirmaciones.put(a.getId(), false));

        Ensayo ensayo = Ensayo.builder()
                .bandaId(bandaId)
                .fecha(request.getFecha())
                .duracionMin(request.getDuracionMin())
                .direccionSala(request.getDireccionSala())
                .temasAPracticar(request.getTemasAPracticar())
                .confirmaciones(confirmaciones)
                .build();

        Ensayo guardado = ensayoRepository.save(ensayo);

        artistas.forEach(a ->
                notificacionService.enviarRecordatorioEnsayo(
                        a.getCorreo(), banda.getNombre(), request.getFecha(), request.getDireccionSala()));

        log.info("Ensayo programado para banda {} el {}", bandaId, request.getFecha());
        return guardado;
    }

    public Ensayo confirmarAsistencia(String ensayoId, String artistaId, boolean confirma) {
        Ensayo ensayo = ensayoRepository.findById(ensayoId)
                .orElseThrow(() -> new ResourceNotFoundException("Ensayo", ensayoId));

        if (ensayo.getConfirmaciones() == null) {
            ensayo.setConfirmaciones(new HashMap<>());
        }
        ensayo.getConfirmaciones().put(artistaId, confirma);

        return ensayoRepository.save(ensayo);
    }

    public List<Ensayo> listarProximos(String bandaId) {
        return ensayoRepository.findByBandaIdAndFechaAfter(bandaId, LocalDateTime.now());
    }

    public List<Ensayo> listarPorBanda(String bandaId) {
        return ensayoRepository.findByBandaIdOrderByFechaDesc(bandaId);
    }

    public EnsayoResponseDTO toResponseDTO(Ensayo ensayo) {
        return EnsayoResponseDTO.builder()
                .id(ensayo.getId())
                .bandaId(ensayo.getBandaId())
                .fecha(ensayo.getFecha())
                .duracionMin(ensayo.getDuracionMin())
                .direccionSala(ensayo.getDireccionSala())
                .temasAPracticar(ensayo.getTemasAPracticar())
                .confirmaciones(ensayo.getConfirmaciones())
                .totalConfirmados((int) ensayo.contarConfirmaciones())
                .build();
    }
}
