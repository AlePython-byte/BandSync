package com.bandsync.service;

import com.bandsync.dto.request.RiderTecnicoRequestDTO;
import com.bandsync.exception.OperacionNoPermitidaException;
import com.bandsync.exception.ResourceNotFoundException;
import com.bandsync.model.dominio.RiderTecnico;
import com.bandsync.model.dominio.Show;
import com.bandsync.repository.RiderTecnicoRepository;
import com.bandsync.repository.ShowRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiderService {

    private final RiderTecnicoRepository riderRepository;
    private final ShowRepository showRepository;

    public RiderTecnico crear(String showId, RiderTecnicoRequestDTO request) {
        showRepository.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show", showId));

        riderRepository.findByShowId(showId).ifPresent(r -> {
            throw new OperacionNoPermitidaException(
                    "Ya existe un rider técnico para el show " + showId);
        });

        RiderTecnico rider = RiderTecnico.builder()
                .showId(showId)
                .configuracionSonido(request.getConfiguracionSonido())
                .configuracionIluminacion(request.getConfiguracionIluminacion())
                .hospitalidad(request.getHospitalidad())
                .build();

        RiderTecnico guardado = riderRepository.save(rider);
        log.info("RiderTécnico creado para show {}", showId);
        return guardado;
    }

    public RiderTecnico obtenerPorShow(String showId) {
        return riderRepository.findByShowId(showId)
                .orElseThrow(() -> new ResourceNotFoundException("RiderTecnico", showId));
    }

    public RiderTecnico clonar(String riderOrigenId, String showDestinoId) {
        RiderTecnico origen = riderRepository.findById(riderOrigenId)
                .orElseThrow(() -> new ResourceNotFoundException("RiderTecnico", riderOrigenId));
        showRepository.findById(showDestinoId)
                .orElseThrow(() -> new ResourceNotFoundException("Show", showDestinoId));

        riderRepository.findByShowId(showDestinoId).ifPresent(r -> {
            throw new OperacionNoPermitidaException(
                    "Ya existe un rider técnico para el show destino " + showDestinoId);
        });

        RiderTecnico clon = RiderTecnico.builder()
                .showId(showDestinoId)
                .configuracionSonido(origen.getConfiguracionSonido())
                .configuracionIluminacion(origen.getConfiguracionIluminacion())
                .hospitalidad(origen.getHospitalidad())
                .build();

        RiderTecnico guardado = riderRepository.save(clon);
        log.info("RiderTécnico clonado de show {} a show {}", origen.getShowId(), showDestinoId);
        return guardado;
    }

    public byte[] exportarPDF(String riderId) {
        RiderTecnico rider = riderRepository.findById(riderId)
                .orElseThrow(() -> new ResourceNotFoundException("RiderTecnico", riderId));
        Show show = showRepository.findById(rider.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("Show", rider.getShowId()));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            document.add(new Paragraph("Rider Técnico — " + show.getNombreEvento())
                    .setBold().setFontSize(18f));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Configuración de Sonido").setBold().setFontSize(14f));
            document.add(new Paragraph(nvl(rider.getConfiguracionSonido())).setFontSize(12f));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Configuración de Iluminación").setBold().setFontSize(14f));
            document.add(new Paragraph(nvl(rider.getConfiguracionIluminacion())).setFontSize(12f));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Hospitalidad").setBold().setFontSize(14f));
            document.add(new Paragraph(nvl(rider.getHospitalidad())).setFontSize(12f));

        } catch (Exception e) {
            log.error("Error al generar PDF para rider {}: {}", riderId, e.getMessage(), e);
            throw new OperacionNoPermitidaException("Error al generar el PDF del rider técnico");
        }

        return baos.toByteArray();
    }

    private String nvl(String value) {
        return value != null ? value : "No especificado";
    }
}
