package com.bandsync.service;

import com.bandsync.dto.response.ContratoResponseDTO;
import com.bandsync.dto.response.EnsayoResponseDTO;
import com.bandsync.dto.response.HistorialActividadDTO;
import com.bandsync.dto.response.ReporteFinancieroDTO;
import com.bandsync.dto.response.ShowResponseDTO;
import com.bandsync.exception.ResourceNotFoundException;
import com.bandsync.model.contrato.Contrato;
import com.bandsync.model.contrato.ContratoGrabacion;
import com.bandsync.model.contrato.ContratoPatrocinio;
import com.bandsync.model.contrato.ContratoShow;
import com.bandsync.model.dominio.Ensayo;
import com.bandsync.model.dominio.Pago;
import com.bandsync.model.dominio.Show;
import com.bandsync.model.enums.EstadoContratoEnum;
import com.bandsync.model.enums.EstadoShowEnum;
import com.bandsync.repository.BandaRepository;
import com.bandsync.repository.ContratoRepository;
import com.bandsync.repository.EnsayoRepository;
import com.bandsync.repository.PagoRepository;
import com.bandsync.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReporteService {

    private final PagoRepository pagoRepository;
    private final ContratoRepository contratoRepository;
    private final ShowRepository showRepository;
    private final EnsayoRepository ensayoRepository;
    private final BandaRepository bandaRepository;

    public ReporteFinancieroDTO generarReporte(String bandaId, LocalDate inicio, LocalDate fin) {
        bandaRepository.findById(bandaId)
                .orElseThrow(() -> new ResourceNotFoundException("Banda", bandaId));

        List<Pago> pagos = pagoRepository.findByBandaIdAndFechaBetween(bandaId, inicio, fin);

        double ingresoShows = pagos.stream()
                .mapToDouble(p -> p.getMontoBruto() != null ? p.getMontoBruto() : 0.0)
                .sum();

        double gastoTotal = pagos.stream()
                .mapToDouble(p -> p.getGastosProduccion() != null ? p.getGastosProduccion() : 0.0)
                .sum();

        double ingresoPatrocinios = contratoRepository
                .findByBandaIdAndEstado(bandaId, EstadoContratoEnum.FIRMADO).stream()
                .filter(c -> c instanceof ContratoPatrocinio)
                .mapToDouble(c -> {
                    Double monto = c.calcularMontoFinal();
                    return monto != null ? monto : 0.0;
                })
                .sum();

        List<Show> showsEnRango = showRepository.findByBandaIdAndFechaBetween(
                bandaId, inicio.atStartOfDay(), fin.atTime(23, 59, 59));
        int cantidadShows = (int) showsEnRango.stream()
                .filter(s -> s.getEstado() == EstadoShowEnum.REALIZADO)
                .count();

        Map<String, Double> distribucionPorArtista = new HashMap<>();
        pagos.stream()
                .filter(p -> p.getDistribuciones() != null)
                .forEach(p -> p.getDistribuciones().forEach((artistaId, monto) ->
                        distribucionPorArtista.merge(artistaId, monto, Double::sum)));

        return ReporteFinancieroDTO.builder()
                .bandaId(bandaId)
                .periodoInicio(inicio)
                .periodoFin(fin)
                .ingresoTotalShows(ingresoShows)
                .ingresoTotalPatrocinios(ingresoPatrocinios)
                .gastoTotal(gastoTotal)
                .utilidadNeta((ingresoShows + ingresoPatrocinios) - gastoTotal)
                .distribucionPorArtista(distribucionPorArtista)
                .cantidadShows(cantidadShows)
                .build();
    }

    public HistorialActividadDTO obtenerHistorial(String bandaId) {
        bandaRepository.findById(bandaId)
                .orElseThrow(() -> new ResourceNotFoundException("Banda", bandaId));

        List<Show> shows = showRepository.findByBandaIdOrderByFechaDesc(bandaId);
        List<Ensayo> ensayos = ensayoRepository.findByBandaIdOrderByFechaDesc(bandaId);
        List<Contrato> contratos = contratoRepository.findByBandaId(bandaId);

        Set<String> showIdsConContrato = contratos.stream()
                .filter(c -> c instanceof ContratoShow)
                .map(c -> ((ContratoShow) c).getShowId())
                .collect(Collectors.toSet());

        List<ShowResponseDTO> showsDTO = shows.stream()
                .map(s -> mapShow(s, showIdsConContrato))
                .toList();

        List<EnsayoResponseDTO> ensayosDTO = ensayos.stream()
                .map(this::mapEnsayo)
                .toList();

        List<ContratoResponseDTO> contratosDTO = contratos.stream()
                .map(this::mapContrato)
                .toList();

        long ensayosUltimoAnio = ensayoRepository
                .findByBandaIdAndFechaAfter(bandaId, LocalDateTime.now().minusYears(1))
                .size();

        long ciudadesVisitadas = shows.stream()
                .map(Show::getCiudad)
                .distinct()
                .count();

        Map<String, Object> metricas = Map.of(
                "totalShows", shows.size(),
                "ciudadesVisitadas", ciudadesVisitadas,
                "ensayosUltimoAnio", ensayosUltimoAnio
        );

        return HistorialActividadDTO.builder()
                .bandaId(bandaId)
                .shows(showsDTO)
                .ensayos(ensayosDTO)
                .contratos(contratosDTO)
                .metricas(metricas)
                .build();
    }

    private ShowResponseDTO mapShow(Show show, Set<String> showIdsConContrato) {
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
                .tieneContrato(showIdsConContrato.contains(show.getId()))
                .build();
    }

    private EnsayoResponseDTO mapEnsayo(Ensayo ensayo) {
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

    private ContratoResponseDTO mapContrato(Contrato contrato) {
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
