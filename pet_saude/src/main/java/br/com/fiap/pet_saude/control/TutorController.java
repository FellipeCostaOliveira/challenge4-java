package br.com.fiap.pet_saude.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import br.com.fiap.pet_saude.service.TutorService;

@Controller
public class TutorController {

    private final TutorService tutorService;

    public TutorController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    @GetMapping("/tutores")
    public ModelAndView listar(@RequestParam(required = false) String busca) {
        ModelAndView mv = new ModelAndView("tutor/lista");
        mv.addObject("tutores", tutorService.listar(busca));
        mv.addObject("busca", busca);
        return mv;
    }

    @GetMapping("/tutores/{id}")
    public ModelAndView detalhes(@PathVariable Long id) {
        ModelAndView mv = new ModelAndView("tutor/detalhes");
        mv.addObject("tutor", tutorService.buscar(id));
        mv.addObject("pets", tutorService.listarPets(id));
        return mv;
    }
}
