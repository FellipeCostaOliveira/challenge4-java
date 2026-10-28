package br.com.fiap.pet_saude.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.fiap.pet_saude.dto.PainelAdmin;
import br.com.fiap.pet_saude.dto.PainelTutor;
import br.com.fiap.pet_saude.dto.PetComResumo;
import br.com.fiap.pet_saude.model.Consulta;
import br.com.fiap.pet_saude.model.EnumStatusConsulta;
import br.com.fiap.pet_saude.model.Pet;
import br.com.fiap.pet_saude.repository.ConsultaRepository;
import br.com.fiap.pet_saude.repository.PetRepository;
import br.com.fiap.pet_saude.repository.TutorRepository;

/** Monta os dados das duas homes: painel da clínica (com Radar de Prevenção) e painel do tutor. */
@Service
public class PainelService {

    private static final Comparator<PetComResumo> MAIS_URGENTE_PRIMEIRO = Comparator
            .comparingInt((PetComResumo item) -> item.resumo().situacao().getPrioridade())
            .thenComparing(item -> item.resumo().dataProximaConsultaSugerida());

    private final TutorRepository repT;
    private final PetRepository repP;
    private final ConsultaRepository repC;
    private final PetService petService;
    private final ConsultaService consultaService;
    private final PlanoPreventivoService planoPreventivoService;
    private final Clock relogio;

    public PainelService(TutorRepository repT, PetRepository repP, ConsultaRepository repC,
                         PetService petService, ConsultaService consultaService,
                         PlanoPreventivoService planoPreventivoService, Clock relogio) {
        this.repT = repT;
        this.repP = repP;
        this.repC = repC;
        this.petService = petService;
        this.consultaService = consultaService;
        this.planoPreventivoService = planoPreventivoService;
        this.relogio = relogio;
    }

    public PainelAdmin montarPainelAdmin() {
        List<PetComResumo> radar = comResumo(repP.findByAtivoTrueOrderByNomeAsc()).stream()
                .filter(item -> item.resumo().precisaDeContato())
                .sorted(MAIS_URGENTE_PRIMEIRO)
                .toList();

        return new PainelAdmin(
                repT.count(),
                repP.countByAtivoTrue(),
                repC.countByStatus(EnumStatusConsulta.AGENDADA),
                consultaService.listarAgendaDoDia(),
                radar);
    }

    public PainelTutor montarPainelTutor() {
        LocalDate hoje = LocalDate.now(relogio);
        List<Consulta> proximas = consultaService.listarVisiveis(EnumStatusConsulta.AGENDADA).stream()
                .filter(c -> !c.getDataConsulta().isBefore(hoje))
                .sorted(Comparator.comparing(Consulta::getDataConsulta).thenComparing(Consulta::getHorario))
                .toList();

        return new PainelTutor(comResumo(petService.listarVisiveis(null)), proximas);
    }

    private List<PetComResumo> comResumo(List<Pet> pets) {
        return pets.stream()
                .map(pet -> new PetComResumo(pet, planoPreventivoService.calcular(pet)))
                .toList();
    }
}
