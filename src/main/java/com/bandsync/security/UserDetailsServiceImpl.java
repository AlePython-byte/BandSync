package com.bandsync.security;

import com.bandsync.repository.ArtistaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ArtistaRepository artistaRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        var artista = artistaRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Artista no encontrado: " + correo));

        if (!artista.isActivo()) {
            throw new UsernameNotFoundException("Cuenta desactivada: " + correo);
        }

        return new User(
                artista.getCorreo(),
                artista.getContrasena(),
                List.of(new SimpleGrantedAuthority("ROLE_" + artista.getRol().name()))
        );
    }
}
