package com.bandsync.controller;

import com.bandsync.dto.request.CancionRequestDTO;
import com.bandsync.dto.response.ApiResponse;
import com.bandsync.dto.response.CancionResponseDTO;
import com.bandsync.model.dominio.Cancion;
import com.bandsync.model.enums.EstadoCancionEnum;
import com.bandsync.service.CancionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Canciones", description = "Gestión del repertorio y setlist")
@RequiredArgsConstructor
@Slf4j
public class CancionController {

    private final CancionService cancionService;

    @PostMapping("/bandas/{bandaId}/canciones")
    @Operation(summary = "Crear canción para una banda")
    public ResponseEntity<ApiResponse<CancionResponseDTO>> crear(
            @PathVariable String bandaId,
            @Valid @RequestBody CancionRequestDTO request) {
        Cancion cancion = cancionService.crear(bandaId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(toDto(cancion)));
    }

    @GetMapping("/bandas/{bandaId}/canciones")
    @Operation(summary = "Listar canciones de una banda", description = "Filtra opcionalmente por estado: IDEA, EN_DESARROLLO, LISTA, DESCARTADA")
    public ResponseEntity<ApiResponse<List<CancionResponseDTO>>> listarPorBanda(
            @PathVariable String bandaId,
            @RequestParam(required = false) EstadoCancionEnum estado) {
        List<CancionResponseDTO> dtos = cancionService.listarPorBanda(bandaId, estado).stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    @PatchMapping("/canciones/{id}/estado")
    @Operation(summary = "Cambiar estado de una canción")
    public ResponseEntity<ApiResponse<CancionResponseDTO>> cambiarEstado(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        EstadoCancionEnum nuevoEstado = EstadoCancionEnum.valueOf(body.get("estado"));
        Cancion cancion = cancionService.actualizarEstado(id, nuevoEstado);
        return ResponseEntity.ok(ApiResponse.success(toDto(cancion)));
    }

    @PostMapping("/canciones/setlist")
    @Operation(summary = "Obtener setlist filtrado por IDs", description = "Solo retorna canciones en estado LISTA, en el orden recibido")
    public ResponseEntity<ApiResponse<List<CancionResponseDTO>>> setlist(
            @RequestBody List<String> cancionIds) {
        List<CancionResponseDTO> dtos = cancionService.obtenerSetlist(cancionIds).stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    private CancionResponseDTO toDto(Cancion c) {
        return CancionResponseDTO.builder()
                .id(c.getId())
                .bandaId(c.getBandaId())
                .titulo(c.getTitulo())
                .duracionSeg(c.getDuracionSeg())
                .estado(c.getEstado())
                .autores(c.getAutores())
                .archivoDemo(c.getArchivoDemo())
                .build();
    }
}
