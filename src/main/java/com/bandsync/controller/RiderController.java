package com.bandsync.controller;

import com.bandsync.dto.request.RiderTecnicoRequestDTO;
import com.bandsync.dto.response.ApiResponse;
import com.bandsync.dto.response.RiderTecnicoResponseDTO;
import com.bandsync.model.dominio.RiderTecnico;
import com.bandsync.service.RiderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Riders Técnicos", description = "Gestión de riders técnicos y exportación PDF")
@RequiredArgsConstructor
@Slf4j
public class RiderController {

    private final RiderService riderService;

    @PostMapping("/riders")
    @PreAuthorize("hasRole('ADMIN_BANDA')")
    @Operation(summary = "Crear rider técnico para un show")
    public ResponseEntity<ApiResponse<RiderTecnicoResponseDTO>> crear(
            @Valid @RequestBody RiderTecnicoRequestDTO request) {
        RiderTecnico rider = riderService.crear(request.getShowId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(toDto(rider)));
    }

    @GetMapping("/shows/{showId}/rider")
    @Operation(summary = "Obtener rider técnico de un show")
    public ResponseEntity<ApiResponse<RiderTecnicoResponseDTO>> obtenerPorShow(
            @PathVariable String showId) {
        RiderTecnico rider = riderService.obtenerPorShow(showId);
        return ResponseEntity.ok(ApiResponse.success(toDto(rider)));
    }

    @PostMapping("/riders/{id}/clonar")
    @Operation(summary = "Clonar rider técnico a otro show")
    public ResponseEntity<ApiResponse<RiderTecnicoResponseDTO>> clonar(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String showDestinoId = body.get("showDestinoId");
        RiderTecnico rider = riderService.clonar(id, showDestinoId);
        return ResponseEntity.ok(ApiResponse.success(toDto(rider)));
    }

    @GetMapping(value = "/riders/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Exportar rider técnico como PDF")
    public ResponseEntity<byte[]> exportarPDF(@PathVariable String id) {
        byte[] pdf = riderService.exportarPDF(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.attachment().filename("rider-" + id + ".pdf").build());
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    private RiderTecnicoResponseDTO toDto(RiderTecnico r) {
        return RiderTecnicoResponseDTO.builder()
                .id(r.getId())
                .showId(r.getShowId())
                .configuracionSonido(r.getConfiguracionSonido())
                .configuracionIluminacion(r.getConfiguracionIluminacion())
                .hospitalidad(r.getHospitalidad())
                .build();
    }
}
