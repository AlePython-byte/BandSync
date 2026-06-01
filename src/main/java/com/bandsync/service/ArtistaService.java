package com.bandsync.service;

import com.bandsync.dto.response.ArtistaResponseDTO;
import com.bandsync.exception.OperacionNoPermitidaException;
import com.bandsync.exception.ResourceNotFoundException;
import com.bandsync.model.enums.RolEnum;
import com.bandsync.model.persona.Artista;
import com.bandsync.repository.ArtistaRepository;
import com.bandsync.repository.BandaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArtistaService {

    private final ArtistaRepository artistaRepository;
    private final BandaRepository bandaRepository;

    public Artista obtenerPorId(String id) {
        return artistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artista", id));
    }

    public Artista obtenerPorCorreo(String correo) {
        return artistaRepository.findByCorreo(correo)
                .orElseThrow(() -> new ResourceNotFoundException("Artista", correo));
    }

    public List<Artista> obtenerPorBanda(String bandaId) {
        return artistaRepository.findByBandaIdAndActivoTrue(bandaId);
    }

    @SuppressWarnings("unchecked")
    public Artista actualizarPerfil(String artistaId, Map<String, Object> cambios) {
        Artista artista = obtenerPorId(artistaId);

        if (cambios.containsKey("nombre")) {
            artista.setNombre((String) cambios.get("nombre"));
        }
        if (cambios.containsKey("telefono")) {
            artista.setTelefono((String) cambios.get("telefono"));
        }
        if (cambios.containsKey("redesSociales")) {
            artista.setRedesSociales((List<String>) cambios.get("redesSociales"));
        }

        return artistaRepository.save(artista);
    }

    public void desactivar(String artistaId, String solicitanteId) {
        Artista artista = obtenerPorId(artistaId);

        boolean esMismo = artistaId.equals(solicitanteId);
        if (!esMismo) {
            Artista solicitante = obtenerPorId(solicitanteId);
            boolean esAdminDeLaBanda = solicitante.getRol() == RolEnum.ADMIN_BANDA
                    && artista.getBandaId() != null
                    && artista.getBandaId().equals(solicitante.getBandaId());
            if (!esAdminDeLaBanda) {
                throw new OperacionNoPermitidaException(
                        "Solo el artista o el administrador de su banda pueden desactivar esta cuenta");
            }
        }

        if (artista.getRol() == RolEnum.ADMIN_BANDA && artista.getBandaId() != null) {
            long totalAdmins = artistaRepository.findByBandaIdAndActivoTrue(artista.getBandaId())
                    .stream()
                    .filter(a -> a.getRol() == RolEnum.ADMIN_BANDA)
                    .count();
            if (totalAdmins <= 1) {
                throw new OperacionNoPermitidaException(
                        "No puedes desactivar la cuenta si eres el único administrador de la banda");
            }
        }

        artista.setActivo(false);
        artistaRepository.save(artista);
        log.info("Artista {} desactivado por solicitante {}", artistaId, solicitanteId);
    }

    public ArtistaResponseDTO toResponseDTO(Artista artista) {
        return ArtistaResponseDTO.builder()
                .id(artista.getId())
                .nombre(artista.getNombre())
                .correo(artista.getCorreo())
                .telefono(artista.getTelefono())
                .rol(artista.getRol())
                .tipo(artista.getClass().getSimpleName().toLowerCase())
                .fichaArtistica(artista.obtenerFichaArtistica())
                .bandaId(artista.getBandaId())
                .activo(artista.isActivo())
                .build();
    }
}
