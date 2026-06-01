package com.bandsync.service;

import com.bandsync.dto.request.ShowRequestDTO;
import com.bandsync.dto.response.ShowResponseDTO;
import com.bandsync.exception.EstadoInvalidoException;
import com.bandsync.exception.ResourceNotFoundException;
import com.bandsync.model.contrato.ContratoShow;
import com.bandsync.model.dominio.Show;
import com.bandsync.model.enums.EstadoShowEnum;
import com.bandsync.model.persona.Artista;
import com.bandsync.repository.ArtistaRepository;
import com.bandsync.repository.BandaRepository;
import com.bandsync.repository.ContratoRepository;
import com.bandsync.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShowService {

    private final ShowRepository showRepository;
    private final BandaRepository bandaRepository;
    private final ContratoRepository contratoRepository;
    private final NotificacionService notificacionService;
    private final ArtistaRepository artistaRepository;

    public Show crear(String bandaId, ShowRequestDTO request) {
        bandaRepository.findById(bandaId)
                .orElseThrow(() -> new ResourceNotFoundException("Banda", bandaId));

        Show show = Show.builder()
                .bandaId(bandaId)
                .nombreEvento(request.getNombreEvento())
                .venue(request.getVenue())
                .ciudad(request.getCiudad())
                .fecha(request.getFecha())
                .tipoShow(request.getTipoShow())
                .tarifaAcordada(request.getTarifaAcordada())
                .estado(EstadoShowEnum.PENDIENTE)
                .build();

        Show guardado = showRepository.save(show);
        log.info("Show '{}' creado para banda {}", guardado.getNombreEvento(), bandaId);
        return guardado;
    }

    public Show obtenerPorId(String id) {
        return showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show", id));
    }

    public List<Show> listarPorBanda(String bandaId) {
        return showRepository.findByBandaIdOrderByFechaDesc(bandaId);
    }

    public Show cambiarEstado(String showId, EstadoShowEnum nuevoEstado) {
        Show show = obtenerPorId(showId);
        EstadoShowEnum actual = show.getEstado();

        if (actual == EstadoShowEnum.REALIZADO || actual == EstadoShowEnum.CANCELADO) {
            throw new EstadoInvalidoException("Show", actual.name(), nuevoEstado.name());
        }

        boolean valida = switch (actual) {
            case PENDIENTE -> nuevoEstado == EstadoShowEnum.CONFIRMADO
                    || nuevoEstado == EstadoShowEnum.CANCELADO;
            case CONFIRMADO -> nuevoEstado == EstadoShowEnum.REALIZADO
                    || nuevoEstado == EstadoShowEnum.CANCELADO;
            default -> false;
        };

        if (!valida) {
            throw new EstadoInvalidoException("Show", actual.name(), nuevoEstado.name());
        }

        show.setEstado(nuevoEstado);
        return showRepository.save(show);
    }

    public void notificarIntegrantesShow(String showId) {
        Show show = obtenerPorId(showId);
        List<Artista> integrantes = artistaRepository.findByBandaIdAndActivoTrue(show.getBandaId());
        integrantes.forEach(a ->
                notificacionService.enviarRecordatorioShow(
                        a.getCorreo(), show.getNombreEvento(), show.getVenue(), show.getFecha()));
        log.info("Notificación de show '{}' enviada a {} integrantes",
                show.getNombreEvento(), integrantes.size());
    }

    public ShowResponseDTO toResponseDTO(Show show) {
        boolean tieneContrato = contratoRepository.findByBandaId(show.getBandaId()).stream()
                .anyMatch(c -> c instanceof ContratoShow cs && show.getId().equals(cs.getShowId()));

        return ShowResponseDTO.builder()
                .id(show.getId())
                .bandaId(show.getBandaId())
                .nombreEvento(show.getNombreEvento())
                .venue(show.getVenue())
                .ciudad(show.getCiudad())
                .fecha(show.getFecha())
                .tipoShow(show.getTipoShow())
                .tarifaAcordada(show.getTarifaAcordada())
                .estado(show.getEstado())
                .tieneContrato(tieneContrato)
                .build();
    }
}
