package br.com.fiap.pet_saude.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.fiap.pet_saude.model.Usuario;
import br.com.fiap.pet_saude.repository.UsuarioRepository;

@Service
public class UsuarioDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository repU;

    public UsuarioDetailsServiceImpl(UsuarioRepository repU) {
        this.repU = repU;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = repU.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário ou senha inválidos."));

        return new User(
                usuario.getUsername(),
                usuario.getSenha(),
                usuario.getPapeis().stream()
                        .map(papel -> new SimpleGrantedAuthority(papel.getDescricao().name()))
                        .toList()
        );
    }
}
