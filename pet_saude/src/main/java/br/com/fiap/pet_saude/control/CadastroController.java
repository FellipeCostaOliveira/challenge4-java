package br.com.fiap.pet_saude.control;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import br.com.fiap.pet_saude.dto.CadastroTutorForm;
import br.com.fiap.pet_saude.exception.RegraDeNegocioException;
import br.com.fiap.pet_saude.service.CadastroService;
import jakarta.validation.Valid;

@Controller
public class CadastroController {

    private static final String VIEW_CADASTRO = "tutor/cadastro";

    private final CadastroService cadastroService;

    public CadastroController(CadastroService cadastroService) {
        this.cadastroService = cadastroService;
    }

    @GetMapping("/cadastro")
    public ModelAndView formularioCadastro() {
        return new ModelAndView(VIEW_CADASTRO, "form", new CadastroTutorForm());
    }

    @PostMapping("/cadastro")
    public ModelAndView processarCadastro(@Valid @ModelAttribute("form") CadastroTutorForm form,
                                          BindingResult bindingResult) {
        if (!form.senhasConferem()) {
            bindingResult.rejectValue("confirmarSenha", "senhas.divergentes", "As senhas não conferem.");
        }
        if (bindingResult.hasErrors()) {
            return new ModelAndView(VIEW_CADASTRO, "form", form);
        }
        try {
            cadastroService.cadastrarTutor(form);
            return new ModelAndView("redirect:/login?cadastroRealizado=true");
        } catch (RegraDeNegocioException e) {
            ModelAndView mv = new ModelAndView(VIEW_CADASTRO, "form", form);
            mv.addObject("erroNegocio", e.getMessage());
            return mv;
        }
    }
}
