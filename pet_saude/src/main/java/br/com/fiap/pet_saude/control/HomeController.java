package br.com.fiap.pet_saude.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import br.com.fiap.pet_saude.security.UsuarioLogadoService;
import br.com.fiap.pet_saude.service.PainelService;

@Controller
public class HomeController {

    private final PainelService painelService;
    private final UsuarioLogadoService usuarioLogadoService;

    public HomeController(PainelService painelService, UsuarioLogadoService usuarioLogadoService) {
        this.painelService = painelService;
        this.usuarioLogadoService = usuarioLogadoService;
    }

    @GetMapping("/")
    public String raiz() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public ModelAndView home() {
        ModelAndView mv = new ModelAndView("home/index");
        if (usuarioLogadoService.isAdmin()) {
            mv.addObject("painelAdmin", painelService.montarPainelAdmin());
        } else {
            mv.addObject("painelTutor", painelService.montarPainelTutor());
        }
        return mv;
    }
}
