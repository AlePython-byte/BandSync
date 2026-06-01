package com.bandsync.controller;

import com.bandsync.dto.request.ConfirmacionEnsayoRequestDTO;
import com.bandsync.dto.request.EnsayoRequestDTO;
import com.bandsync.dto.response.ApiResponse;
import com.bandsync.dto.response.EnsayoResponseDTO;
import com.bandsync.model.dominio.Ensayo;
import com.bandsync.security.SecurityUtils;
import com.bandsync.service.ArtistaService;
import com.bandsync.service.EnsayoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Ensayos", description = "Programación y confirmación de ensayos")
@RequiredArgsConstructor
@Slf4j
public class EnsayoController {

    private final EnsayoService ensayoService;
    private final ArtistaService artistaService;

    @PostMapping("/bandas/{bandaId}/ensayos")
    @PreAuthorize("hasRole('ADMIN_BANDA')")
    @Operation(summary = "Programar ensayo", description = "Notifica a todos los integrantes activos por correo")
    public ResponseEntity<ApiResponse<EnsayoResponseDTO>> programar(
            @PathVariable String bandaId,
            @Valid @RequestBody EnsayoRequestDTO request) {
        Ensayo ensayo = ensayoService.programar(bandaId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(ensayoService.toResponseDTO(ensayo)));
    }

    @GetMapping("/bandas/{bandaId}/ensayos")
    @Operation(summary = "Listar todos los ensayos de una banda")
    public ResponseEntity<ApiResponse<List<EnsayoResponseDTO>>> listarPorBanda(
            @PathVariable String bandaId) {
        List<EnsayoResponseDTO> dtos = ensayoService.listarPorBanda(bandaId).stream()
                .map(ensayoService::toResponseDTO)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    @GetMapping("/bandas/{bandaId}/ensayos/proximos")
    @Operation(summary = "Listar ensayos futuros de una banda")
    public ResponseEntity<ApiResponse<List<EnsayoResponseDTO>>> listarProximos(
            @PathVariable String bandaId) {
        List<EnsayoResponseDTO> dtos = ensayoService.listarProximos(bandaId).stream()
                .map(ensayoService::toResponseDTO)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    @PostMapping("/ensayos/{id}/confirmar")
    @Operation(summary = "Confirmar o rechazar asistencia al ensayo")
    public ResponseEntity<ApiResponse<EnsayoResponseDTO>> confirmar(
            @PathVariable String id,
            @RequestBody ConfirmacionEnsayoRequestDTO request) {
        String artistaId = obtenerArtistaActualId();
        Ensayo ensayo = ensayoService.confirmarAsistencia(id, artistaId, request.isConfirma());
        return ResponseEntity.ok(ApiResponse.success(ensayoService.toResponseDTO(ensayo)));
    }

    private String obtenerArtistaActualId() {
        String correo = SecurityUtils.getCorreoActual();
        return artistaService.obtenerPorCorreo(correo).getId();
    }
}
