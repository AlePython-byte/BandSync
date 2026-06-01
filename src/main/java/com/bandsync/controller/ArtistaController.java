package com.bandsync.controller;

import com.bandsync.dto.response.ApiResponse;
import com.bandsync.dto.response.ArtistaResponseDTO;
import com.bandsync.model.persona.Artista;
import com.bandsync.security.SecurityUtils;
import com.bandsync.service.ArtistaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/artistas")
@Tag(name = "Artistas", description = "Gestión de perfiles de artista")
@RequiredArgsConstructor
@Slf4j
public class ArtistaController {

    private final ArtistaService artistaService;

    @GetMapping("/me")
    @Operation(summary = "Obtener mi perfil")
    public ResponseEntity<ApiResponse<ArtistaResponseDTO>> miPerfil() {
        String correo = SecurityUtils.getCorreoActual();
        Artista artista = artistaService.obtenerPorCorreo(correo);
        return ResponseEntity.ok(ApiResponse.success(artistaService.toResponseDTO(artista)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener artista por ID")
    public ResponseEntity<ApiResponse<ArtistaResponseDTO>> obtener(@PathVariable String id) {
        Artista artista = artistaService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.success(artistaService.toResponseDTO(artista)));
    }

    @GetMapping("/banda/{bandaId}")
    @Operation(summary = "Listar integrantes activos de una banda")
    public ResponseEntity<ApiResponse<List<ArtistaResponseDTO>>> porBanda(
            @PathVariable String bandaId) {
        List<ArtistaResponseDTO> dtos = artistaService.obtenerPorBanda(bandaId).stream()
                .map(artistaService::toResponseDTO)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    @PutMapping("/me")
    @Operation(summary = "Actualizar mi perfil", description = "Campos permitidos: nombre, telefono, redesSociales")
    public ResponseEntity<ApiResponse<ArtistaResponseDTO>> actualizarMiPerfil(
            @RequestBody Map<String, Object> cambios) {
        String artistaId = obtenerArtistaActualId();
        Artista artista = artistaService.actualizarPerfil(artistaId, cambios);
        return ResponseEntity.ok(ApiResponse.success(artistaService.toResponseDTO(artista)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar artista", description = "Solo el propio artista o un ADMIN_BANDA puede desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable String id) {
        String solicitanteId = obtenerArtistaActualId();
        artistaService.desactivar(id, solicitanteId);
        return ResponseEntity.noContent().build();
    }

    private String obtenerArtistaActualId() {
        String correo = SecurityUtils.getCorreoActual();
        return artistaService.obtenerPorCorreo(correo).getId();
    }
}
