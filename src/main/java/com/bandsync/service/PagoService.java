package com.bandsync.service;

import com.bandsync.dto.request.PagoRequestDTO;
import com.bandsync.exception.OperacionNoPermitidaException;
import com.bandsync.exception.ResourceNotFoundException;
import com.bandsync.model.dominio.Banda;
import com.bandsync.model.dominio.Pago;
import com.bandsync.model.dominio.Show;
import com.bandsync.model.persona.Artista;
import com.bandsync.repository.ArtistaRepository;
import com.bandsync.repository.BandaRepository;
import com.bandsync.repository.PagoRepository;
import com.bandsync.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PagoService {

    private final PagoRepository pagoRepository;
    private final ShowRepository showRepository;
    private final BandaRepository bandaRepository;
    private final ArtistaRepository artistaRepository;
    private final NotificacionService notificacionService;

    public Pago registrarPago(String bandaId, PagoRequestDTO request) {
        Show show = showRepository.findById(request.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("Show", request.getShowId()));

        if (!bandaId.equals(show.getBandaId())) {
            throw new OperacionNoPermitidaException(
                    "El show " + request.getShowId() + " no pertenece a la banda " + bandaId);
        }

        Banda banda = bandaRepository.findById(bandaId)
                .orElseThrow(() -> new ResourceNotFoundException("Banda", bandaId));

        Map<String, Double> distribuciones = request.getDistribuciones() != null
                ? request.getDistribuciones() : Map.of();

        double suma = distribuciones.values().stream().mapToDouble(Double::doubleValue).sum();
        if (Math.abs(suma - 100.0) > 0.01) {
            throw new OperacionNoPermitidaException(
                    "La suma de distribuciones debe ser 100% (actual: " + suma + "%)");
        }

        List<String> integrantesIds = banda.getIntegrantesIds() != null
                ? banda.getIntegrantesIds() : List.of();
        distribuciones.keySet().forEach(artistaId -> {
            if (!integrantesIds.contains(artistaId)) {
                throw new OperacionNoPermitidaException(
                        "El artista " + artistaId + " no es integrante de la banda");
            }
        });

        double gastos = request.getGastosProduccion() != null ? request.getGastosProduccion() : 0.0;

        Pago pago = Pago.builder()
                .bandaId(bandaId)
                .showId(request.getShowId())
                .montoBruto(request.getMontoBruto())
                .gastosProduccion(gastos)
                .fecha(LocalDate.now())
                .build();

        pago.calcularNeto();
        pago.distribuir(distribuciones);

        Pago guardado = pagoRepository.save(pago);
        log.info("Pago registrado para banda {} / show {}, montoNeto={}",
                bandaId, request.getShowId(), guardado.getMontoNeto());

        if (guardado.getDistribuciones() != null) {
            guardado.getDistribuciones().forEach((artistaId, monto) -> {
                try {
                    Artista artista = artistaRepository.findById(artistaId).orElse(null);
                    if (artista != null) {
                        notificacionService.enviarDetallePago(
                                artista.getCorreo(), artista.getNombre(), monto, show.getNombreEvento());
                    }
                } catch (Exception e) {
                    log.error("Error al notificar pago al artista {}: {}", artistaId, e.getMessage());
                }
            });
        }

        return guardado;
    }

    public List<Pago> listarPorBanda(String bandaId) {
        return pagoRepository.findByBandaId(bandaId).stream()
                .sorted(Comparator.comparing(Pago::getFecha,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }
}
