package com.bandsync.controller;

import com.bandsync.dto.request.ShowRequestDTO;
import com.bandsync.dto.response.ApiResponse;
import com.bandsync.dto.response.ShowResponseDTO;
import com.bandsync.model.dominio.Show;
import com.bandsync.model.enums.EstadoShowEnum;
import com.bandsync.service.ShowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Shows", description = "Gestión de shows y eventos")
@RequiredArgsConstructor
@Slf4j
public class ShowController {

    private final ShowService showService;

    @PostMapping("/bandas/{bandaId}/shows")
    @PreAuthorize("hasAnyRole('ADMIN_BANDA', 'MANAGER')")
    @Operation(summary = "Crear show para una banda")
    public ResponseEntity<ApiResponse<ShowResponseDTO>> crear(
            @PathVariable String bandaId,
            @Valid @RequestBody ShowRequestDTO request) {
        Show show = showService.crear(bandaId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(showService.toResponseDTO(show)));
    }

    @GetMapping("/bandas/{bandaId}/shows")
    @Operation(summary = "Listar shows de una banda ordenados por fecha")
    public ResponseEntity<ApiResponse<List<ShowResponseDTO>>> listarPorBanda(
            @PathVariable String bandaId) {
        List<ShowResponseDTO> dtos = showService.listarPorBanda(bandaId).stream()
                .map(showService::toResponseDTO)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    @GetMapping("/shows/{id}")
    @Operation(summary = "Obtener show por ID")
    public ResponseEntity<ApiResponse<ShowResponseDTO>> obtener(@PathVariable String id) {
        Show show = showService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.success(showService.toResponseDTO(show)));
    }

    @PatchMapping("/shows/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN_BANDA', 'MANAGER')")
    @Operation(summary = "Cambiar estado del show", description = "Transiciones: PENDIENTE→CONFIRMADO|CANCELADO, CONFIRMADO→REALIZADO|CANCELADO")
    public ResponseEntity<ApiResponse<ShowResponseDTO>> cambiarEstado(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        EstadoShowEnum nuevoEstado = EstadoShowEnum.valueOf(body.get("estado"));
        Show show = showService.cambiarEstado(id, nuevoEstado);
        return ResponseEntity.ok(ApiResponse.success(showService.toResponseDTO(show)));
    }

    @PostMapping("/shows/{id}/notificar")
    @PreAuthorize("hasRole('ADMIN_BANDA')")
    @Operation(summary = "Notificar a integrantes sobre el show por correo")
    public ResponseEntity<Void> notificar(@PathVariable String id) {
        showService.notificarIntegrantesShow(id);
        return ResponseEntity.noContent().build();
    }
}
