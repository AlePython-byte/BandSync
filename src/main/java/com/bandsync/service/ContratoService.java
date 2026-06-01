package com.bandsync.service;

import com.bandsync.dto.request.ContratoGrabacionRequestDTO;
import com.bandsync.dto.request.ContratoPatrocinioRequestDTO;
import com.bandsync.dto.request.ContratoShowRequestDTO;
import com.bandsync.dto.response.ContratoResponseDTO;
import com.bandsync.exception.ConflictoExclusividadException;
import com.bandsync.exception.EstadoInvalidoException;
import com.bandsync.exception.OperacionNoPermitidaException;
import com.bandsync.exception.ResourceNotFoundException;
import com.bandsync.model.contrato.Contrato;
import com.bandsync.model.contrato.ContratoGrabacion;
import com.bandsync.model.contrato.ContratoPatrocinio;
import com.bandsync.model.contrato.ContratoShow;
import com.bandsync.model.enums.EstadoContratoEnum;
import com.bandsync.model.persona.Artista;
import com.bandsync.model.persona.Productor;
import com.bandsync.repository.ArtistaRepository;
import com.bandsync.repository.BandaRepository;
import com.bandsync.repository.ContratoRepository;
import com.bandsync.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContratoService {

    private final ContratoRepository contratoRepository;
    private final BandaRepository bandaRepository;
    private final ShowRepository showRepository;
    private final NotificacionService notificacionService;
    private final ArtistaRepository artistaRepository;

    public ContratoShow crearContratoShow(String bandaId, ContratoShowRequestDTO request) {
        bandaRepository.findById(bandaId)
                .orElseThrow(() -> new ResourceNotFoundException("Banda", bandaId));
        showRepository.findById(request.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("Show", request.getShowId()));

        boolean conflicto = contratoRepository.findByBandaId(bandaId).stream()
                .anyMatch(c -> c instanceof ContratoShow cs
                        && request.getShowId().equals(cs.getShowId())
                        && (cs.getEstado() == EstadoContratoEnum.FIRMADO
                        || cs.getEstado() == EstadoContratoEnum.ENVIADO));
        if (conflicto) {
            throw new OperacionNoPermitidaException(
                    "Ya existe un contrato activo (FIRMADO o ENVIADO) para el show " + request.getShowId());
        }

        ContratoShow contrato = ContratoShow.builder()
                .bandaId(bandaId)
                .estado(EstadoContratoEnum.BORRADOR)
                .vigenciaDias(request.getVigenciaDias())
                .showId(request.getShowId())
                .tarifaBase(request.getTarifaBase())
                .anticipoPct(request.getAnticipoPct())
                .saldoPendiente(request.getTarifaBase())
                .clausulaCancelacion(request.getClausulaCancelacion())
                .build();

        ContratoShow guardado = (ContratoShow) contratoRepository.save(contrato);
        log.info("ContratoShow creado para banda {} / show {}", bandaId, request.getShowId());
        return guardado;
    }

    public ContratoGrabacion crearContratoGrabacion(String bandaId, ContratoGrabacionRequestDTO request) {
        bandaRepository.findById(bandaId)
                .orElseThrow(() -> new ResourceNotFoundException("Banda", bandaId));

        List<String> productoresIds = request.getProductoresIds() != null
                ? request.getProductoresIds() : List.of();

        for (String pId : productoresIds) {
            Artista artista = artistaRepository.findById(pId)
                    .orElseThrow(() -> new ResourceNotFoundException("Artista", pId));
            if (!(artista instanceof Productor)) {
                throw new OperacionNoPermitidaException(
                        "El artista " + pId + " no es un Productor");
            }
        }

        ContratoGrabacion contrato = ContratoGrabacion.builder()
                .bandaId(bandaId)
                .estado(EstadoContratoEnum.BORRADOR)
                .vigenciaDias(request.getVigenciaDias())
                .numTracks(request.getNumTracks())
                .pctRegalias(request.getPctRegalias())
                .derechosMaster(request.isDerechosMaster())
                .fechaLimiteEntrega(request.getFechaLimiteEntrega())
                .productoresIds(productoresIds)
                .build();

        ContratoGrabacion guardado = (ContratoGrabacion) contratoRepository.save(contrato);
        log.info("ContratoGrabacion creado para banda {}", bandaId);
        return guardado;
    }

    public ContratoPatrocinio crearContratoPatrocinio(String bandaId, ContratoPatrocinioRequestDTO request) {
        bandaRepository.findById(bandaId)
                .orElseThrow(() -> new ResourceNotFoundException("Banda", bandaId));

        ContratoPatrocinio nuevo = ContratoPatrocinio.builder()
                .bandaId(bandaId)
                .estado(EstadoContratoEnum.BORRADOR)
                .vigenciaDias(request.getVigenciaDias())
                .marca(request.getMarca())
                .montoPatrocinio(request.getMontoPatrocinio())
                .obligacionesImagen(request.getObligacionesImagen())
                .exclusividades(request.getExclusividades())
                .fechaVencimiento(request.getFechaVencimiento())
                .build();

        contratoRepository.findByBandaIdAndEstado(bandaId, EstadoContratoEnum.FIRMADO).stream()
                .filter(c -> c instanceof ContratoPatrocinio)
                .map(c -> (ContratoPatrocinio) c)
                .forEach(existente -> {
                    if (existente.tieneConflictoExclusividad(nuevo)) {
                        List<String> enComun = new ArrayList<>(
                                existente.getExclusividades() != null
                                        ? existente.getExclusividades() : List.of());
                        enComun.retainAll(nuevo.getExclusividades() != null
                                ? nuevo.getExclusividades() : List.of());
                        throw new ConflictoExclusividadException(existente.getMarca(), enComun);
                    }
                });

        ContratoPatrocinio guardado = (ContratoPatrocinio) contratoRepository.save(nuevo);
        log.info("ContratoPatrocinio creado para banda {} con marca {}", bandaId, request.getMarca());
        return guardado;
    }

    public Contrato cambiarEstado(String contratoId, EstadoContratoEnum nuevoEstado) {
        Contrato contrato = contratoRepository.findById(contratoId)
                .orElseThrow(() -> new ResourceNotFoundException("Contrato", contratoId));
        EstadoContratoEnum actual = contrato.getEstado();

        if (actual == EstadoContratoEnum.VENCIDO || actual == EstadoContratoEnum.CANCELADO) {
            throw new EstadoInvalidoException("Contrato", actual.name(), nuevoEstado.name());
        }

        boolean valida = switch (actual) {
            case BORRADOR -> nuevoEstado == EstadoContratoEnum.ENVIADO
                    || nuevoEstado == EstadoContratoEnum.CANCELADO;
            case ENVIADO -> nuevoEstado == EstadoContratoEnum.FIRMADO
                    || nuevoEstado == EstadoContratoEnum.CANCELADO;
            case FIRMADO -> nuevoEstado == EstadoContratoEnum.VENCIDO
                    || nuevoEstado == EstadoContratoEnum.CANCELADO;
            default -> false;
        };

        if (!valida) {
            throw new EstadoInvalidoException("Contrato", actual.name(), nuevoEstado.name());
        }

        contrato.cambiarEstado(nuevoEstado);
        if (nuevoEstado == EstadoContratoEnum.FIRMADO) {
            contrato.setFechaFirma(LocalDate.now());
        }
        return contratoRepository.save(contrato);
    }

    public List<Contrato> listarPorBanda(String bandaId) {
        return contratoRepository.findByBandaId(bandaId);
    }

    public List<Contrato> contratosProximosAVencer(String bandaId) {
        LocalDate hoy = LocalDate.now();
        List<Contrato> proximos = contratoRepository.findContratosProximosAVencer(
                bandaId, hoy, hoy.plusDays(30));

        List<Artista> integrantes = artistaRepository.findByBandaIdAndActivoTrue(bandaId);
        proximos.forEach(contrato -> {
            String tipo = contrato.getClass().getSimpleName();
            LocalDate vencimiento = (contrato instanceof ContratoPatrocinio cp)
                    ? cp.getFechaVencimiento()
                    : (contrato.getFechaFirma() != null
                    ? contrato.getFechaFirma().plusDays(contrato.getVigenciaDias())
                    : hoy.plusDays(contrato.getVigenciaDias()));
            integrantes.forEach(a ->
                    notificacionService.enviarAlertaVencimientoContrato(
                            a.getCorreo(), tipo, vencimiento));
        });

        return proximos;
    }

    public ContratoResponseDTO toResponseDTO(Contrato contrato) {
        Map<String, Object> detalles = new HashMap<>();
        if (contrato instanceof ContratoShow cs) {
            detalles.put("showId", cs.getShowId());
            detalles.put("tarifaBase", cs.getTarifaBase());
            detalles.put("anticipoPct", cs.getAnticipoPct());
            detalles.put("saldoPendiente", cs.getSaldoPendiente());
            detalles.put("clausulaCancelacion", cs.getClausulaCancelacion());
        } else if (contrato instanceof ContratoGrabacion cg) {
            detalles.put("numTracks", cg.getNumTracks());
            detalles.put("pctRegalias", cg.getPctRegalias());
            detalles.put("derechosMaster", cg.isDerechosMaster());
            detalles.put("fechaLimiteEntrega", cg.getFechaLimiteEntrega());
            detalles.put("productoresIds", cg.getProductoresIds());
        } else if (contrato instanceof ContratoPatrocinio cp) {
            detalles.put("marca", cp.getMarca());
            detalles.put("montoPatrocinio", cp.getMontoPatrocinio());
            detalles.put("obligacionesImagen", cp.getObligacionesImagen());
            detalles.put("exclusividades", cp.getExclusividades());
            detalles.put("fechaVencimiento", cp.getFechaVencimiento());
        }

        return ContratoResponseDTO.builder()
                .id(contrato.getId())
                .bandaId(contrato.getBandaId())
                .tipo(contrato.getClass().getSimpleName().toLowerCase())
                .fechaFirma(contrato.getFechaFirma())
                .vigenciaDias(contrato.getVigenciaDias())
                .estado(contrato.getEstado())
                .observaciones(contrato.getObservaciones())
                .montoFinal(contrato.calcularMontoFinal())
                .estaVigente(contrato.estaVigente())
                .detallesPorTipo(detalles)
                .build();
    }
}
