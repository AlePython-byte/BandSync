package com.bandsync.controller;

import com.bandsync.dto.request.ContratoGrabacionRequestDTO;
import com.bandsync.dto.request.ContratoPatrocinioRequestDTO;
import com.bandsync.dto.request.ContratoShowRequestDTO;
import com.bandsync.dto.response.ApiResponse;
import com.bandsync.dto.response.ContratoResponseDTO;
import com.bandsync.model.contrato.Contrato;
import com.bandsync.model.enums.EstadoContratoEnum;
import com.bandsync.service.ContratoService;
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
@Tag(name = "Contratos", description = "Gestión de contratos de show, grabación y patrocinio")
@RequiredArgsConstructor
@Slf4j
public class ContratoController {

    private final ContratoService contratoService;

    @PostMapping("/bandas/{bandaId}/contratos/show")
    @PreAuthorize("hasAnyRole('ADMIN_BANDA', 'MANAGER')")
    @Operation(summary = "Crear contrato de show")
    public ResponseEntity<ApiResponse<ContratoResponseDTO>> crearShow(
            @PathVariable String bandaId,
            @Valid @RequestBody ContratoShowRequestDTO request) {
        ContratoResponseDTO dto = contratoService.toResponseDTO(
                contratoService.crearContratoShow(bandaId, request));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(dto));
    }

    @PostMapping("/bandas/{bandaId}/contratos/grabacion")
    @PreAuthorize("hasAnyRole('ADMIN_BANDA', 'MANAGER')")
    @Operation(summary = "Crear contrato de grabación", description = "Todos los productoresIds deben ser artistas de tipo Productor")
    public ResponseEntity<ApiResponse<ContratoResponseDTO>> crearGrabacion(
            @PathVariable String bandaId,
            @Valid @RequestBody ContratoGrabacionRequestDTO request) {
        ContratoResponseDTO dto = contratoService.toResponseDTO(
                contratoService.crearContratoGrabacion(bandaId, request));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(dto));
    }

    @PostMapping("/bandas/{bandaId}/contratos/patrocinio")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Crear contrato de patrocinio", description = "Detecta conflictos de exclusividad con contratos FIRMADOS existentes")
    public ResponseEntity<ApiResponse<ContratoResponseDTO>> crearPatrocinio(
            @PathVariable String bandaId,
            @Valid @RequestBody ContratoPatrocinioRequestDTO request) {
        ContratoResponseDTO dto = contratoService.toResponseDTO(
                contratoService.crearContratoPatrocinio(bandaId, request));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(dto));
    }

    @GetMapping("/bandas/{bandaId}/contratos")
    @Operation(summary = "Listar contratos de una banda")
    public ResponseEntity<ApiResponse<List<ContratoResponseDTO>>> listarPorBanda(
            @PathVariable String bandaId) {
        List<ContratoResponseDTO> dtos = contratoService.listarPorBanda(bandaId).stream()
                .map(contratoService::toResponseDTO)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    @PatchMapping("/contratos/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN_BANDA', 'MANAGER')")
    @Operation(summary = "Cambiar estado del contrato", description = "Transiciones: BORRADOR→ENVIADO|CANCELADO, ENVIADO→FIRMADO|CANCELADO, FIRMADO→VENCIDO|CANCELADO")
    public ResponseEntity<ApiResponse<ContratoResponseDTO>> cambiarEstado(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        EstadoContratoEnum nuevoEstado = EstadoContratoEnum.valueOf(body.get("estado"));
        Contrato contrato = contratoService.cambiarEstado(id, nuevoEstado);
        return ResponseEntity.ok(ApiResponse.success(contratoService.toResponseDTO(contrato)));
    }

    @GetMapping("/bandas/{bandaId}/contratos/proximos-vencer")
    @Operation(summary = "Contratos que vencen en los próximos 30 días", description = "También envía alertas por correo a los integrantes")
    public ResponseEntity<ApiResponse<List<ContratoResponseDTO>>> proximosVencer(
            @PathVariable String bandaId) {
        List<ContratoResponseDTO> dtos = contratoService.contratosProximosAVencer(bandaId).stream()
                .map(contratoService::toResponseDTO)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }
}
