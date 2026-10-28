package br.com.fiap.pet_saude.control;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import br.com.fiap.pet_saude.model.Usuario;
import br.com.fiap.pet_saude.security.UsuarioLogadoService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Disponibiliza para todas as telas os dados do usuário logado usados no menu
 * (nome, iniciais do avatar, perfil) e a URI atual para destacar o item ativo.
 */
@ControllerAdvice
public class AtributosGlobaisController {

    private final UsuarioLogadoService usuarioLogadoService;

    public AtributosGlobaisController(UsuarioLogadoService usuarioLogadoService) {
        this.usuarioLogadoService = usuarioLogadoService;
    }

    @ModelAttribute
    public void adicionarAtributosGlobais(Model model, HttpServletRequest request) {
        preencher(model.asMap(), request);
    }

    /** Também usado pelo {@link GerenciadorDeErros}, para as páginas de erro manterem o menu. */
    void preencher(Map<String, Object> modelo, HttpServletRequest request) {
        modelo.put("uriAtual", request.getRequestURI());
        if (!usuarioLogadoService.estaAutenticado()) {
            return;
        }
        Usuario usuario = usuarioLogadoService.obterUsuarioLogado();
        String nomeExibicao = usuario.getNomePerfil() != null ? usuario.getNomePerfil() : usuario.getUsername();
        modelo.put("usuarioLogado", usuario.getUsername());
        modelo.put("nomePerfilLogado", nomeExibicao);
        modelo.put("iniciaisPerfilLogado", extrairIniciais(nomeExibicao));
        modelo.put("isAdminLogado", usuarioLogadoService.isAdmin());
    }

    static String extrairIniciais(String nome) {
        return Arrays.stream(nome.trim().split("\\s+"))
                .filter(parte -> !parte.isEmpty())
                .limit(2)
                .map(parte -> parte.substring(0, 1).toUpperCase())
                .collect(Collectors.joining());
    }
}
