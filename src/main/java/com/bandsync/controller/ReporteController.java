package com.bandsync.controller;

import com.bandsync.dto.response.ApiResponse;
import com.bandsync.dto.response.HistorialActividadDTO;
import com.bandsync.dto.response.ReporteFinancieroDTO;
import com.bandsync.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/bandas")
@Tag(name = "Reportes", description = "Reportes financieros e historial de actividad")
@RequiredArgsConstructor
@Slf4j
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/{bandaId}/reportes/financiero")
    @PreAuthorize("hasAnyRole('ADMIN_BANDA', 'MANAGER')")
    @Operation(summary = "Reporte financiero de un período", description = "Incluye ingresos por shows y patrocinios, gastos, utilidad y distribución por artista")
    public ResponseEntity<ApiResponse<ReporteFinancieroDTO>> financiero(
            @PathVariable String bandaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        ReporteFinancieroDTO dto = reporteService.generarReporte(bandaId, inicio, fin);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/{bandaId}/historial")
    @Operation(summary = "Historial de actividad de la banda", description = "Retorna shows, ensayos, contratos y métricas de actividad")
    public ResponseEntity<ApiResponse<HistorialActividadDTO>> historial(
            @PathVariable String bandaId) {
        HistorialActividadDTO dto = reporteService.obtenerHistorial(bandaId);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }
}
