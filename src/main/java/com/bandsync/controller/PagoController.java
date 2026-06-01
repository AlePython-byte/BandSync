package com.bandsync.controller;

import com.bandsync.dto.request.PagoRequestDTO;
import com.bandsync.dto.response.ApiResponse;
import com.bandsync.dto.response.PagoResponseDTO;
import com.bandsync.model.dominio.Pago;
import com.bandsync.service.PagoService;
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
@RequestMapping("/api/bandas")
@Tag(name = "Pagos", description = "Registro y consulta de pagos por show")
@RequiredArgsConstructor
@Slf4j
public class PagoController {

    private final PagoService pagoService;

    @PostMapping("/{bandaId}/pagos")
    @PreAuthorize("hasAnyRole('ADMIN_BANDA', 'MANAGER')")
    @Operation(summary = "Registrar pago de show", description = "La suma de distribuciones debe ser 100%. Notifica a cada artista por correo")
    public ResponseEntity<ApiResponse<PagoResponseDTO>> registrar(
            @PathVariable String bandaId,
            @Valid @RequestBody PagoRequestDTO request) {
        Pago pago = pagoService.registrarPago(bandaId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(toDto(pago)));
    }

    @GetMapping("/{bandaId}/pagos")
    @Operation(summary = "Listar pagos de una banda ordenados por fecha")
    public ResponseEntity<ApiResponse<List<PagoResponseDTO>>> listarPorBanda(
            @PathVariable String bandaId) {
        List<PagoResponseDTO> dtos = pagoService.listarPorBanda(bandaId).stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    private PagoResponseDTO toDto(Pago p) {
        return PagoResponseDTO.builder()
                .id(p.getId())
                .bandaId(p.getBandaId())
                .showId(p.getShowId())
                .montoBruto(p.getMontoBruto())
                .gastosProduccion(p.getGastosProduccion())
                .montoNeto(p.getMontoNeto())
                .fecha(p.getFecha())
                .distribuciones(p.getDistribuciones())
                .build();
    }
}
