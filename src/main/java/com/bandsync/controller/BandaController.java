package com.bandsync.controller;

import com.bandsync.dto.request.BandaRequestDTO;
import com.bandsync.dto.request.InvitacionRequestDTO;
import com.bandsync.dto.response.ApiResponse;
import com.bandsync.dto.response.BandaResponseDTO;
import com.bandsync.model.dominio.Banda;
import com.bandsync.security.SecurityUtils;
import com.bandsync.service.ArtistaService;
import com.bandsync.service.BandaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/bandas")
@Tag(name = "Bandas", description = "Gestión de bandas e integrantes")
@RequiredArgsConstructor
@Slf4j
public class BandaController {

    private final BandaService bandaService;
    private final ArtistaService artistaService;

    @PostMapping
    @Operation(summary = "Crear banda", description = "El artista autenticado se convierte en fundador y ADMIN_BANDA")
    public ResponseEntity<ApiResponse<BandaResponseDTO>> crear(
            @Valid @RequestBody BandaRequestDTO request) {
        String artistaFundadorId = obtenerArtistaActualId();
        Banda banda = bandaService.crearBanda(request, artistaFundadorId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(bandaService.toResponseDTO(banda)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener banda por ID")
    public ResponseEntity<ApiResponse<BandaResponseDTO>> obtener(@PathVariable String id) {
        Banda banda = bandaService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.success(bandaService.toResponseDTO(banda)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_BANDA')")
    @Operation(summary = "Actualizar datos de la banda")
    public ResponseEntity<ApiResponse<BandaResponseDTO>> actualizar(
            @PathVariable String id, @Valid @RequestBody BandaRequestDTO request) {
        Banda banda = bandaService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.success(bandaService.toResponseDTO(banda)));
    }

    @PostMapping("/{id}/codigo-invitacion")
    @PreAuthorize("hasRole('ADMIN_BANDA')")
    @Operation(summary = "Regenerar código de invitación")
    public ResponseEntity<ApiResponse<Map<String, String>>> regenerarCodigo(@PathVariable String id) {
        String codigo = bandaService.generarNuevoCodigoInvitacion(id);
        return ResponseEntity.ok(ApiResponse.success(Map.of("codigo", codigo)));
    }

    @PostMapping("/unirse")
    @Operation(summary = "Unirse a banda con código de invitación")
    public ResponseEntity<ApiResponse<BandaResponseDTO>> unirse(
            @Valid @RequestBody InvitacionRequestDTO request) {
        String artistaId = obtenerArtistaActualId();
        Banda banda = bandaService.agregarIntegrante(request.getCodigoInvitacion(), artistaId);
        return ResponseEntity.ok(ApiResponse.success(bandaService.toResponseDTO(banda)));
    }

    @DeleteMapping("/{bandaId}/integrantes/{artistaId}")
    @PreAuthorize("hasRole('ADMIN_BANDA')")
    @Operation(summary = "Remover integrante de la banda")
    public ResponseEntity<Void> removerIntegrante(
            @PathVariable String bandaId, @PathVariable String artistaId) {
        String solicitanteId = obtenerArtistaActualId();
        bandaService.removerIntegrante(bandaId, artistaId, solicitanteId);
        return ResponseEntity.noContent().build();
    }

    private String obtenerArtistaActualId() {
        String correo = SecurityUtils.getCorreoActual();
        return artistaService.obtenerPorCorreo(correo).getId();
    }
}
