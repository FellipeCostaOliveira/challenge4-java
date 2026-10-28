package br.com.fiap.pet_saude.control;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.fiap.pet_saude.model.EnumEspecie;
import br.com.fiap.pet_saude.model.Pet;
import br.com.fiap.pet_saude.service.ConsultaService;
import br.com.fiap.pet_saude.service.PetService;
import br.com.fiap.pet_saude.service.PlanoPreventivoService;
import jakarta.validation.Valid;

@Controller
public class PetController {

    private final PetService petService;
    private final ConsultaService consultaService;
    private final PlanoPreventivoService planoPreventivoService;

    public PetController(PetService petService, ConsultaService consultaService,
                         PlanoPreventivoService planoPreventivoService) {
        this.petService = petService;
        this.consultaService = consultaService;
        this.planoPreventivoService = planoPreventivoService;
    }

    /** Impede que id e tutor sejam alterados via parâmetros forjados no formulário. */
    @InitBinder("pet")
    public void protegerCamposSensiveis(WebDataBinder binder) {
        binder.setDisallowedFields("id", "tutor", "tutor.*");
    }

    @GetMapping("/pets")
    public ModelAndView listar(@RequestParam(required = false) String busca) {
        ModelAndView mv = new ModelAndView("pet/lista");
        mv.addObject("pets", petService.listarVisiveis(busca));
        mv.addObject("busca", busca);
        return mv;
    }

    @GetMapping("/pets/novo")
    public ModelAndView formularioNovo() {
        petService.garantirQuePodeCadastrar();
        return montarFormulario(new Pet(), false);
    }

    @PostMapping("/pets")
    public ModelAndView cadastrar(@Valid @ModelAttribute("pet") Pet pet, BindingResult bindingResult,
                                  RedirectAttributes redirect) {
        if (bindingResult.hasErrors()) {
            return montarFormulario(pet, false);
        }
        Pet salvo = petService.cadastrar(pet);
        redirect.addFlashAttribute("sucesso", salvo.getNome() + " foi cadastrado! Que tal agendar a primeira consulta?");
        return new ModelAndView("redirect:/pets/" + salvo.getId());
    }

    @GetMapping("/pets/{id}")
    public ModelAndView detalhes(@PathVariable Long id) {
        Pet pet = petService.buscarComPermissao(id);
        var historico = consultaService.listarHistoricoDoPet(id);
        ModelAndView mv = new ModelAndView("pet/detalhes");
        mv.addObject("pet", pet);
        mv.addObject("historico", historico);
        mv.addObject("resumoSaude", planoPreventivoService.calcular(pet, historico));
        return mv;
    }

    @GetMapping("/pets/{id}/editar")
    public ModelAndView formularioEditar(@PathVariable Long id) {
        return montarFormulario(petService.buscarComPermissao(id), true);
    }

    @PostMapping("/pets/{id}/atualizar")
    public ModelAndView atualizar(@PathVariable Long id, @Valid @ModelAttribute("pet") Pet pet,
                                  BindingResult bindingResult, RedirectAttributes redirect) {
        if (bindingResult.hasErrors()) {
            pet.setId(id);
            return montarFormulario(pet, true);
        }
        petService.atualizar(id, pet);
        redirect.addFlashAttribute("sucesso", "Dados do pet atualizados.");
        return new ModelAndView("redirect:/pets/" + id);
    }

    @PostMapping("/pets/{id}/remover")
    public String remover(@PathVariable Long id, RedirectAttributes redirect) {
        PetService.ResultadoRemocao resultado = petService.remover(id);
        if (resultado == PetService.ResultadoRemocao.INATIVADO) {
            redirect.addFlashAttribute("sucesso",
                    "O pet possui histórico de consultas, então foi inativado (o prontuário foi preservado).");
            return "redirect:/pets/" + id;
        }
        redirect.addFlashAttribute("sucesso", "Pet removido.");
        return "redirect:/pets";
    }

    private ModelAndView montarFormulario(Pet pet, boolean edicao) {
        ModelAndView mv = new ModelAndView("pet/formulario");
        mv.addObject("pet", pet);
        mv.addObject("especies", EnumEspecie.values());
        mv.addObject("edicao", edicao);
        return mv;
    }
}
