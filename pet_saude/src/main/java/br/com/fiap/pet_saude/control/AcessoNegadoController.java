package br.com.fiap.pet_saude.control;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/** Destino do accessDeniedHandler do Spring Security (rotas exclusivas da clínica). */
@Controller
public class AcessoNegadoController {

    @GetMapping("/acesso_negado")
    public ModelAndView exibirPaginaAcessoNegado() {
        ModelAndView mv = new ModelAndView("erro/pagina");
        mv.setStatus(HttpStatus.FORBIDDEN);
        mv.addObject("titulo", "Acesso negado");
        mv.addObject("mensagem", "Você não possui permissão para acessar este conteúdo.");
        mv.addObject("icone", "bi-shield-lock");
        return mv;
    }
}
