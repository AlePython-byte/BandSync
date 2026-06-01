package com.bandsync.service;

import com.bandsync.dto.request.LoginRequestDTO;
import com.bandsync.dto.request.RegistroRequestDTO;
import com.bandsync.dto.response.LoginResponseDTO;
import com.bandsync.exception.CorreoYaRegistradoException;
import com.bandsync.exception.CredencialesInvalidasException;
import com.bandsync.exception.OperacionNoPermitidaException;
import com.bandsync.model.enums.RolEnum;
import com.bandsync.model.persona.Artista;
import com.bandsync.model.persona.Instrumentista;
import com.bandsync.model.persona.Manager;
import com.bandsync.model.persona.Productor;
import com.bandsync.model.persona.Vocalista;
import com.bandsync.repository.ArtistaRepository;
import com.bandsync.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final ArtistaRepository artistaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final NotificacionService notificacionService;

    public LoginResponseDTO login(LoginRequestDTO request) {
        Artista artista = artistaRepository.findByCorreo(request.getCorreo())
                .orElseThrow(CredencialesInvalidasException::new);

        if (!passwordEncoder.matches(request.getContrasena(), artista.getContrasena())) {
            throw new CredencialesInvalidasException();
        }

        if (!artista.isActivo()) {
            throw new OperacionNoPermitidaException("Cuenta desactivada");
        }

        String token = jwtUtil.generarToken(
                artista.getCorreo(),
                artista.getRol().name(),
                artista.getId(),
                artista.getBandaId()
        );

        return LoginResponseDTO.builder()
                .token(token)
                .rol(artista.getRol().name())
                .artistaId(artista.getId())
                .bandaId(artista.getBandaId())
                .nombre(artista.getNombre())
                .build();
    }

    public Artista registrar(RegistroRequestDTO request) {
        if (artistaRepository.existsByCorreo(request.getCorreo())) {
            throw new CorreoYaRegistradoException(request.getCorreo());
        }

        String contrasenaHasheada = passwordEncoder.encode(request.getContrasena());
        Map<String, Object> atributos = request.getAtributosEspecificos() != null
                ? request.getAtributosEspecificos() : Map.of();
        int anioActual = LocalDate.now().getYear();

        Artista artista = crearArtistaPorTipo(request, contrasenaHasheada, atributos, anioActual);
        Artista guardado = artistaRepository.save(artista);

        notificacionService.enviarConfirmacionRegistro(guardado.getCorreo(), guardado.getNombre());
        log.info("Artista registrado: {} ({})", guardado.getCorreo(), guardado.getClass().getSimpleName());

        return guardado;
    }

    @SuppressWarnings("unchecked")
    private Artista crearArtistaPorTipo(RegistroRequestDTO req, String contrasena,
                                         Map<String, Object> attr, int anio) {
        String tipo = req.getTipoArtista() != null ? req.getTipoArtista().toLowerCase() : "";
        return switch (tipo) {
            case "vocalista" -> Vocalista.builder()
                    .nombre(req.getNombre())
                    .correo(req.getCorreo())
                    .contrasena(contrasena)
                    .telefono(req.getTelefono())
                    .fechaNacimiento(req.getFechaNacimiento())
                    .activo(true)
                    .anioIngreso(anio)
                    .rol(RolEnum.INTEGRANTE)
                    .tessitura((String) attr.get("tessitura"))
                    .generosInterpreta((List<String>) attr.get("generosInterpreta"))
                    .build();
            case "instrumentista" -> Instrumentista.builder()
                    .nombre(req.getNombre())
                    .correo(req.getCorreo())
                    .contrasena(contrasena)
                    .telefono(req.getTelefono())
                    .fechaNacimiento(req.getFechaNacimiento())
                    .activo(true)
                    .anioIngreso(anio)
                    .rol(RolEnum.INTEGRANTE)
                    .instrumentos((List<String>) attr.get("instrumentos"))
                    .esTitular(Boolean.TRUE.equals(attr.get("esTitular")))
                    .build();
            case "productor" -> Productor.builder()
                    .nombre(req.getNombre())
                    .correo(req.getCorreo())
                    .contrasena(contrasena)
                    .telefono(req.getTelefono())
                    .fechaNacimiento(req.getFechaNacimiento())
                    .activo(true)
                    .anioIngreso(anio)
                    .rol(RolEnum.INTEGRANTE)
                    .tipoProduccion((List<String>) attr.get("tipoProduccion"))
                    .dawPrincipal((String) attr.get("dawPrincipal"))
                    .build();
            case "manager" -> Manager.builder()
                    .nombre(req.getNombre())
                    .correo(req.getCorreo())
                    .contrasena(contrasena)
                    .telefono(req.getTelefono())
                    .fechaNacimiento(req.getFechaNacimiento())
                    .activo(true)
                    .anioIngreso(anio)
                    .rol(RolEnum.INTEGRANTE)
                    .agencia((String) attr.get("agencia"))
                    .comisionPct(attr.get("comisionPct") != null
                            ? ((Number) attr.get("comisionPct")).doubleValue() : 0.0)
                    .build();
            default -> throw new OperacionNoPermitidaException(
                    "Tipo de artista no válido: " + req.getTipoArtista());
        };
    }
}
