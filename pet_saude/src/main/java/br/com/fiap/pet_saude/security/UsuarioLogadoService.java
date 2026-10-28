package br.com.fiap.pet_saude.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.fiap.pet_saude.exception.AcessoNegadoException;
import br.com.fiap.pet_saude.model.EnumPapel;
import br.com.fiap.pet_saude.model.Tutor;
import br.com.fiap.pet_saude.model.Usuario;
import br.com.fiap.pet_saude.repository.UsuarioRepository;

/** Ponto único de acesso aos dados de quem está logado. */
@Service
public class UsuarioLogadoService {

    private final UsuarioRepository repU;

    public UsuarioLogadoService(UsuarioRepository repU) {
        this.repU = repU;
    }

    public boolean estaAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
    }

    public Usuario obterUsuarioLogado() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return repU.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Usuário autenticado não existe mais no banco."));
    }

    /** Verifica o papel direto nas authorities da sessão, sem ir ao banco. */
    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(autoridade -> EnumPapel.ADMIN.name().equals(autoridade.getAuthority()));
    }

    /** Retorna o tutor vinculado ao usuário logado; falha se for um usuário da clínica. */
    public Tutor obterTutorLogado() {
        Tutor tutor = obterUsuarioLogado().getTutor();
        if (tutor == null) {
            throw new AcessoNegadoException("Esta ação é exclusiva para tutores.");
        }
        return tutor;
    }
}
