package br.com.fiap.pet_saude.control;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.fiap.pet_saude.exception.RegraDeNegocioException;
import br.com.fiap.pet_saude.model.Consulta;
import br.com.fiap.pet_saude.model.EnumStatusConsulta;
import br.com.fiap.pet_saude.model.EnumTipoConsulta;
import br.com.fiap.pet_saude.model.Pet;
import br.com.fiap.pet_saude.service.ConsultaService;
import br.com.fiap.pet_saude.service.PetService;
import jakarta.validation.Valid;

@Controller
public class ConsultaController {

    private final ConsultaService consultaService;
    private final PetService petService;

    public ConsultaController(ConsultaService consultaService, PetService petService) {
        this.consultaService = consultaService;
        this.petService = petService;
    }

    @GetMapping("/consultas")
    public ModelAndView listar(@RequestParam(required = false) EnumStatusConsulta status) {
        ModelAndView mv = new ModelAndView("consulta/lista");
        mv.addObject("consultas", consultaService.listarVisiveis(status));
        mv.addObject("statusFiltro", status);
        mv.addObject("statusOpcoes", EnumStatusConsulta.values());
        return mv;
    }

    @GetMapping("/consultas/agendar/{petId}")
    public ModelAndView formularioAgendar(@PathVariable Long petId) {
        Pet pet = petService.buscarComPermissao(petId);
        Consulta sugestao = new Consulta();
        sugestao.setTipo(EnumTipoConsulta.PREVENTIVA);
        return montarFormularioAgendamento(pet, sugestao);
    }

    @PostMapping("/consultas/agendar/{petId}")
    public ModelAndView agendar(@PathVariable Long petId,
                                @Valid @ModelAttribute("consulta") Consulta consulta,
                                BindingResult bindingResult,
                                RedirectAttributes redirect) {
        Pet pet = petService.buscarComPermissao(petId);
        if (bindingResult.hasErrors()) {
            return montarFormularioAgendamento(pet, consulta);
        }
        try {
            consultaService.agendar(petId, consulta);
            redirect.addFlashAttribute("sucesso", "Consulta de " + pet.getNome() + " agendada com sucesso.");
            return new ModelAndView("redirect:/pets/" + petId);
        } catch (RegraDeNegocioException e) {
            ModelAndView mv = montarFormularioAgendamento(pet, consulta);
            mv.addObject("erroNegocio", e.getMessage());
            return mv;
        }
    }

    @PostMapping("/consultas/{id}/cancelar")
    public String cancelar(@PathVariable Long id, RedirectAttributes redirect) {
        consultaService.cancelar(id);
        redirect.addFlashAttribute("sucesso", "Consulta cancelada.");
        return "redirect:/consultas";
    }

    @PostMapping("/admin/consultas/{id}/realizar")
    public String realizar(@PathVariable Long id, @RequestParam(required = false) String observacoes,
                           RedirectAttributes redirect) {
        consultaService.realizar(id, observacoes);
        redirect.addFlashAttribute("sucesso", "Consulta registrada como realizada. O plano preventivo do pet foi atualizado.");
        return "redirect:/consultas";
    }

    private ModelAndView montarFormularioAgendamento(Pet pet, Consulta consulta) {
        ModelAndView mv = new ModelAndView("consulta/agendar");
        mv.addObject("pet", pet);
        mv.addObject("consulta", consulta);
        mv.addObject("tipos", EnumTipoConsulta.values());
        mv.addObject("horarioAbertura", ConsultaService.ABERTURA);
        mv.addObject("ultimoHorario", ConsultaService.ULTIMO_HORARIO);
        mv.addObject("dataMinima", consultaService.hoje());
        return mv;
    }
}
