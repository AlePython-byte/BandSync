package com.bandsync.controller;

import com.bandsync.dto.request.LoginRequestDTO;
import com.bandsync.dto.request.RegistroRequestDTO;
import com.bandsync.dto.response.ApiResponse;
import com.bandsync.dto.response.ArtistaResponseDTO;
import com.bandsync.dto.response.LoginResponseDTO;
import com.bandsync.model.persona.Artista;
import com.bandsync.service.ArtistaService;
import com.bandsync.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Registro e inicio de sesión")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final ArtistaService artistaService;

    @PostMapping("/registro")
    @Operation(summary = "Registrar nuevo artista")
    public ResponseEntity<ApiResponse<ArtistaResponseDTO>> registro(
            @Valid @RequestBody RegistroRequestDTO request) {
        Artista artista = authService.registrar(request);
        ArtistaResponseDTO dto = artistaService.toResponseDTO(artista);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Usuario registrado exitosamente", dto));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión y obtener JWT")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO request) {
        LoginResponseDTO dto = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }
}
