package com.bandsync.service;

import com.bandsync.dto.request.BandaRequestDTO;
import com.bandsync.dto.response.BandaResponseDTO;
import com.bandsync.exception.BandaYaExisteException;
import com.bandsync.exception.CodigoInvitacionInvalidoException;
import com.bandsync.exception.OperacionNoPermitidaException;
import com.bandsync.exception.ResourceNotFoundException;
import com.bandsync.model.dominio.Banda;
import com.bandsync.model.enums.RolEnum;
import com.bandsync.model.persona.Artista;
import com.bandsync.repository.ArtistaRepository;
import com.bandsync.repository.BandaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class BandaService {

    private final BandaRepository bandaRepository;
    private final ArtistaRepository artistaRepository;
    private final NotificacionService notificacionService;

    public Banda crearBanda(BandaRequestDTO request, String artistaFundadorId) {
        bandaRepository.findByNombreAndCiudadBase(request.getNombre(), request.getCiudadBase())
                .ifPresent(b -> {
                    throw new BandaYaExisteException(request.getNombre(), request.getCiudadBase());
                });

        Artista fundador = artistaRepository.findById(artistaFundadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Artista", artistaFundadorId));

        Banda banda = Banda.builder()
                .nombre(request.getNombre())
                .generosMusicales(request.getGenerosMusicales())
                .ciudadBase(request.getCiudadBase())
                .activa(true)
                .urlPerfil(request.getNombre().toLowerCase().replace(" ", "-"))
                .integrantesIds(new ArrayList<>())
                .build();
        banda.getIntegrantesIds().add(artistaFundadorId);
        banda.generarCodigoInvitacion();

        Banda bandaGuardada = bandaRepository.save(banda);

        fundador.setRol(RolEnum.ADMIN_BANDA);
        fundador.setBandaId(bandaGuardada.getId());
        artistaRepository.save(fundador);

        log.info("Banda '{}' creada por artista {}", bandaGuardada.getNombre(), artistaFundadorId);
        return bandaGuardada;
    }

    public Banda obtenerPorId(String id) {
        return bandaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banda", id));
    }

    public Banda actualizar(String bandaId, BandaRequestDTO request) {
        Banda banda = obtenerPorId(bandaId);
        banda.setNombre(request.getNombre());
        banda.setGenerosMusicales(request.getGenerosMusicales());
        banda.setCiudadBase(request.getCiudadBase());
        return bandaRepository.save(banda);
    }

    public String generarNuevoCodigoInvitacion(String bandaId) {
        Banda banda = obtenerPorId(bandaId);
        banda.generarCodigoInvitacion();
        Banda guardada = bandaRepository.save(banda);
        log.info("Nuevo código de invitación generado para banda {}", bandaId);
        return guardada.getCodigoInvitacion();
    }

    public Banda agregarIntegrante(String codigoInvitacion, String artistaId) {
        Banda banda = bandaRepository.findByCodigoInvitacion(codigoInvitacion)
                .orElseThrow(() -> new CodigoInvitacionInvalidoException(codigoInvitacion));

        Artista artista = artistaRepository.findById(artistaId)
                .orElseThrow(() -> new ResourceNotFoundException("Artista", artistaId));

        if (artista.getBandaId() != null) {
            throw new OperacionNoPermitidaException("El artista ya pertenece a una banda");
        }

        if (banda.getIntegrantesIds() == null) {
            banda.setIntegrantesIds(new ArrayList<>());
        }
        banda.getIntegrantesIds().add(artistaId);

        artista.setBandaId(banda.getId());
        artista.setRol(RolEnum.INTEGRANTE);

        artistaRepository.save(artista);
        Banda bandaGuardada = bandaRepository.save(banda);
        log.info("Artista {} agregado a banda {}", artistaId, banda.getId());
        return bandaGuardada;
    }

    public void removerIntegrante(String bandaId, String artistaId, String solicitanteId) {
        Artista solicitante = artistaRepository.findById(solicitanteId)
                .orElseThrow(() -> new ResourceNotFoundException("Artista", solicitanteId));

        if (solicitante.getRol() != RolEnum.ADMIN_BANDA
                || !bandaId.equals(solicitante.getBandaId())) {
            throw new OperacionNoPermitidaException(
                    "Solo el administrador de la banda puede remover integrantes");
        }

        if (solicitanteId.equals(artistaId)) {
            long totalAdmins = artistaRepository.findByBandaIdAndActivoTrue(bandaId).stream()
                    .filter(a -> a.getRol() == RolEnum.ADMIN_BANDA)
                    .count();
            if (totalAdmins <= 1) {
                throw new OperacionNoPermitidaException(
                        "No puedes removerte si eres el único administrador de la banda");
            }
        }

        Banda banda = obtenerPorId(bandaId);
        banda.getIntegrantesIds().remove(artistaId);

        Artista artista = artistaRepository.findById(artistaId)
                .orElseThrow(() -> new ResourceNotFoundException("Artista", artistaId));
        artista.setBandaId(null);
        artista.setRol(RolEnum.INTEGRANTE);

        artistaRepository.save(artista);
        bandaRepository.save(banda);
        log.info("Artista {} removido de banda {} por solicitante {}", artistaId, bandaId, solicitanteId);
    }

    public BandaResponseDTO toResponseDTO(Banda banda) {
        return BandaResponseDTO.builder()
                .id(banda.getId())
                .nombre(banda.getNombre())
                .generosMusicales(banda.getGenerosMusicales())
                .ciudadBase(banda.getCiudadBase())
                .codigoInvitacion(banda.getCodigoInvitacion())
                .urlPerfil(banda.getUrlPerfil())
                .activa(banda.isActiva())
                .cantidadIntegrantes(banda.getIntegrantesIds() != null
                        ? banda.getIntegrantesIds().size() : 0)
                .build();
    }
}
