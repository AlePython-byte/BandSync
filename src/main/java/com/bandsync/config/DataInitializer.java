package com.bandsync.config;

import com.bandsync.model.dominio.Banda;
import com.bandsync.model.dominio.Cancion;
import com.bandsync.model.dominio.Ensayo;
import com.bandsync.model.dominio.Show;
import com.bandsync.model.enums.EstadoCancionEnum;
import com.bandsync.model.enums.EstadoShowEnum;
import com.bandsync.model.enums.RolEnum;
import com.bandsync.model.enums.TipoShowEnum;
import com.bandsync.model.persona.Artista;
import com.bandsync.model.persona.Instrumentista;
import com.bandsync.model.persona.Manager;
import com.bandsync.model.persona.Productor;
import com.bandsync.model.persona.Vocalista;
import com.bandsync.repository.ArtistaRepository;
import com.bandsync.repository.BandaRepository;
import com.bandsync.repository.CancionRepository;
import com.bandsync.repository.EnsayoRepository;
import com.bandsync.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ArtistaRepository artistaRepository;
    private final BandaRepository bandaRepository;
    private final ShowRepository showRepository;
    private final EnsayoRepository ensayoRepository;
    private final CancionRepository cancionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (artistaRepository.count() > 0 || bandaRepository.count() > 0) {
            log.info("La base de datos ya contiene datos — DataInitializer omitido");
            return;
        }

        log.info("Iniciando carga de datos de ejemplo...");

        String pass = passwordEncoder.encode("Password1");
        int anio = LocalDate.now().getYear();

        // ── Artistas ──────────────────────────────────────────────────────────
        Artista maria = artistaRepository.save(Vocalista.builder()
                .nombre("María García")
                .correo("maria@test.com")
                .contrasena(pass)
                .telefono("3001234567")
                .fechaNacimiento(LocalDate.of(1995, 3, 15))
                .activo(true)
                .anioIngreso(anio)
                .rol(RolEnum.ADMIN_BANDA)
                .redesSociales(List.of("@mariagarcia"))
                .tessitura("Soprano")
                .generosInterpreta(List.of("Rock Alternativo", "Post-punk"))
                .build());

        Artista carlos = artistaRepository.save(Instrumentista.builder()
                .nombre("Carlos Pérez")
                .correo("carlos@test.com")
                .contrasena(pass)
                .telefono("3109876543")
                .fechaNacimiento(LocalDate.of(1992, 7, 22))
                .activo(true)
                .anioIngreso(anio)
                .rol(RolEnum.INTEGRANTE)
                .redesSociales(List.of("@carlosperez_guitar"))
                .instrumentos(List.of("Guitarra eléctrica", "Bajo"))
                .esTitular(true)
                .build());

        Artista juan = artistaRepository.save(Productor.builder()
                .nombre("Juan Martínez")
                .correo("juan@test.com")
                .contrasena(pass)
                .telefono("3201112233")
                .fechaNacimiento(LocalDate.of(1990, 11, 8))
                .activo(true)
                .anioIngreso(anio)
                .rol(RolEnum.INTEGRANTE)
                .tipoProduccion(List.of("Rock", "Electrónica"))
                .dawPrincipal("Ableton Live")
                .build());

        Artista ana = artistaRepository.save(Manager.builder()
                .nombre("Ana López")
                .correo("ana@test.com")
                .contrasena(pass)
                .telefono("3154445566")
                .fechaNacimiento(LocalDate.of(1988, 5, 30))
                .activo(true)
                .anioIngreso(anio)
                .rol(RolEnum.MANAGER)
                .agencia("Coyote Management")
                .comisionPct(15.0)
                .build());

        // ── Banda ─────────────────────────────────────────────────────────────
        Banda banda = Banda.builder()
                .nombre("Los Coyotes Eléctricos")
                .generosMusicales(List.of("Rock Alternativo", "Post-punk", "Electrónica"))
                .ciudadBase("Pasto, Colombia")
                .activa(true)
                .urlPerfil("los-coyotes-electricos")
                .integrantesIds(List.of(maria.getId(), carlos.getId(), juan.getId(), ana.getId()))
                .build();
        banda.generarCodigoInvitacion();
        banda = bandaRepository.save(banda);

        String bandaId = banda.getId();

        // Actualizar artistas con bandaId
        maria.setBandaId(bandaId);
        carlos.setBandaId(bandaId);
        juan.setBandaId(bandaId);
        ana.setBandaId(bandaId);
        artistaRepository.save(maria);
        artistaRepository.save(carlos);
        artistaRepository.save(juan);
        artistaRepository.save(ana);

        // ── Show ──────────────────────────────────────────────────────────────
        showRepository.save(Show.builder()
                .bandaId(bandaId)
                .nombreEvento("Noche Eléctrica Vol. 1")
                .venue("Teatro Sindamanoy")
                .ciudad("Pasto")
                .fecha(LocalDateTime.now().plusDays(30))
                .tipoShow(TipoShowEnum.HEADLINER)
                .tarifaAcordada(2_500_000.0)
                .estado(EstadoShowEnum.CONFIRMADO)
                .build());

        // ── Ensayo ────────────────────────────────────────────────────────────
        Map<String, Boolean> confirmaciones = new HashMap<>();
        confirmaciones.put(maria.getId(), true);
        confirmaciones.put(carlos.getId(), true);
        confirmaciones.put(juan.getId(), false);
        confirmaciones.put(ana.getId(), false);

        ensayoRepository.save(Ensayo.builder()
                .bandaId(bandaId)
                .fecha(LocalDateTime.now().plusDays(7))
                .duracionMin(120)
                .direccionSala("Sala La Cueva — Calle 18 #22-45, Pasto")
                .temasAPracticar(List.of("Corriente Alterna", "Lluvia de Estática", "El Último Voltio"))
                .confirmaciones(confirmaciones)
                .build());

        // ── Canciones ─────────────────────────────────────────────────────────
        cancionRepository.save(Cancion.builder()
                .bandaId(bandaId)
                .titulo("Corriente Alterna")
                .duracionSeg(210)
                .estado(EstadoCancionEnum.LISTA)
                .autores(List.of(maria.getId(), carlos.getId()))
                .build());

        cancionRepository.save(Cancion.builder()
                .bandaId(bandaId)
                .titulo("Lluvia de Estática")
                .duracionSeg(185)
                .estado(EstadoCancionEnum.EN_DESARROLLO)
                .autores(List.of(juan.getId()))
                .build());

        cancionRepository.save(Cancion.builder()
                .bandaId(bandaId)
                .titulo("El Último Voltio")
                .duracionSeg(0)
                .estado(EstadoCancionEnum.IDEA)
                .autores(List.of(maria.getId()))
                .build());

        log.info("Datos de ejemplo cargados exitosamente");
        log.info("  Banda   : {} — id: {}", banda.getNombre(), bandaId);
        log.info("  Código de invitación : {}", banda.getCodigoInvitacion());
        log.info("  Credenciales de prueba (password: Password1):");
        log.info("    maria@test.com  → ADMIN_BANDA  (Vocalista)");
        log.info("    carlos@test.com → INTEGRANTE   (Instrumentista)");
        log.info("    juan@test.com   → INTEGRANTE   (Productor)");
        log.info("    ana@test.com    → MANAGER      (Manager)");
    }
}
