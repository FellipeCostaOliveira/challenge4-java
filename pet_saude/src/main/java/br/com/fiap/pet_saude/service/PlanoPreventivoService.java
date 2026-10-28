package br.com.fiap.pet_saude.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.fiap.pet_saude.dto.ResumoSaudePet;
import br.com.fiap.pet_saude.model.Consulta;
import br.com.fiap.pet_saude.model.EnumFaixaEtaria;
import br.com.fiap.pet_saude.model.EnumSituacaoSaude;
import br.com.fiap.pet_saude.model.EnumStatusConsulta;
import br.com.fiap.pet_saude.model.EnumTipoConsulta;
import br.com.fiap.pet_saude.model.Pet;
import br.com.fiap.pet_saude.repository.ConsultaRepository;

/**
 * Plano Preventivo PetPath: o diferencial da solução.
 * <p>
 * Em vez de só registrar consultas, o sistema interpreta o histórico do pet:
 * descobre a faixa etária pela espécie e idade, aplica o intervalo de check-up
 * recomendado para essa fase da vida e indica se o pet está em dia, se o
 * check-up está chegando ou se está atrasado. A mesma regra alimenta o
 * Radar de Prevenção usado pela equipe da clínica.
 */
@Service
public class PlanoPreventivoService {

    static final int DIAS_DE_AVISO_ANTECIPADO = 30;
    private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA_BR = DateTimeFormatter.ofPattern("HH:mm");

    private final ConsultaRepository repC;
    private final Clock relogio;

    public PlanoPreventivoService(ConsultaRepository repC, Clock relogio) {
        this.repC = repC;
        this.relogio = relogio;
    }

    public ResumoSaudePet calcular(Pet pet) {
        return calcular(pet, repC.findByPetIdOrderByDataConsultaDescHorarioDesc(pet.getId()));
    }

    public ResumoSaudePet calcular(Pet pet, List<Consulta> historico) {
        LocalDate hoje = LocalDate.now(relogio);
        EnumFaixaEtaria faixaEtaria = classificarFaixaEtaria(pet, hoje);
        int intervaloMeses = faixaEtaria.getIntervaloRecomendadoMeses();

        LocalDate ultimaRealizada = obterDataUltimaRealizada(historico);
        LocalDate proximaSugerida = ultimaRealizada == null ? hoje : ultimaRealizada.plusMonths(intervaloMeses);
        Consulta proximaAgendada = obterProximaAgendada(historico, hoje);
        EnumSituacaoSaude situacao = avaliarSituacao(ultimaRealizada, proximaSugerida, hoje);

        return new ResumoSaudePet(
                faixaEtaria,
                descreverIdade(pet.getDataNascimento(), hoje),
                intervaloMeses,
                historico.size(),
                contarRealizadas(historico, null),
                contarRealizadas(historico, EnumTipoConsulta.PREVENTIVA),
                ultimaRealizada,
                proximaSugerida,
                proximaAgendada,
                situacao,
                montarRecomendacao(situacao, faixaEtaria, proximaSugerida, proximaAgendada),
                calcularPercentualDoCiclo(ultimaRealizada, proximaSugerida, hoje));
    }

    EnumFaixaEtaria classificarFaixaEtaria(Pet pet, LocalDate hoje) {
        Period idade = Period.between(pet.getDataNascimento(), hoje);
        if (idade.toTotalMonths() < pet.getEspecie().getMesesAteAdulto()) {
            return EnumFaixaEtaria.FILHOTE;
        }
        if (idade.getYears() >= pet.getEspecie().getAnosAteIdoso()) {
            return EnumFaixaEtaria.IDOSO;
        }
        return EnumFaixaEtaria.ADULTO;
    }

    EnumSituacaoSaude avaliarSituacao(LocalDate ultimaRealizada, LocalDate proximaSugerida, LocalDate hoje) {
        if (ultimaRealizada == null) {
            return EnumSituacaoSaude.SEM_HISTORICO;
        }
        if (hoje.isAfter(proximaSugerida)) {
            return EnumSituacaoSaude.ATRASADO;
        }
        if (!hoje.isBefore(proximaSugerida.minusDays(DIAS_DE_AVISO_ANTECIPADO))) {
            return EnumSituacaoSaude.ATENCAO;
        }
        return EnumSituacaoSaude.EM_DIA;
    }

    private long contarRealizadas(List<Consulta> historico, EnumTipoConsulta tipo) {
        return historico.stream()
                .filter(c -> c.getStatus() == EnumStatusConsulta.REALIZADA)
                .filter(c -> tipo == null || c.getTipo() == tipo)
                .count();
    }

    private LocalDate obterDataUltimaRealizada(List<Consulta> historico) {
        return historico.stream()
                .filter(c -> c.getStatus() == EnumStatusConsulta.REALIZADA)
                .map(Consulta::getDataConsulta)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    private Consulta obterProximaAgendada(List<Consulta> historico, LocalDate hoje) {
        return historico.stream()
                .filter(Consulta::isAgendada)
                .filter(c -> !c.getDataConsulta().isBefore(hoje))
                .min(Comparator.comparing(Consulta::getDataConsulta).thenComparing(Consulta::getHorario))
                .orElse(null);
    }

    private int calcularPercentualDoCiclo(LocalDate ultimaRealizada, LocalDate proximaSugerida, LocalDate hoje) {
        if (ultimaRealizada == null) {
            return 100;
        }
        long diasDoCiclo = Math.max(1, ChronoUnit.DAYS.between(ultimaRealizada, proximaSugerida));
        long diasPassados = ChronoUnit.DAYS.between(ultimaRealizada, hoje);
        return (int) Math.max(0, Math.min(100, diasPassados * 100 / diasDoCiclo));
    }

    private String montarRecomendacao(EnumSituacaoSaude situacao, EnumFaixaEtaria faixaEtaria,
                                      LocalDate proximaSugerida, Consulta proximaAgendada) {
        if (proximaAgendada != null && situacao.exigeAcao()) {
            return "Consulta já agendada para " + proximaAgendada.getDataConsulta().format(DATA_BR)
                    + " às " + proximaAgendada.getHorario().format(HORA_BR)
                    + ". O check-up pendente será feito nessa data.";
        }
        return switch (situacao) {
            case SEM_HISTORICO -> "Ainda não há consultas realizadas. Agende uma primeira avaliação "
                    + "para iniciar o histórico de saúde deste pet.";
            case ATRASADO -> "Para um pet " + faixaEtaria.getDescricao().toLowerCase()
                    + ", o ideal é um check-up a cada " + faixaEtaria.getIntervaloRecomendadoMeses()
                    + " meses. O último venceu em " + proximaSugerida.format(DATA_BR)
                    + ". Agende uma consulta preventiva o quanto antes.";
            case ATENCAO -> "O próximo check-up recomendado é em " + proximaSugerida.format(DATA_BR)
                    + ". Já vale a pena agendar.";
            case EM_DIA -> "Acompanhamento em dia. Próximo check-up sugerido para "
                    + proximaSugerida.format(DATA_BR) + ".";
        };
    }

    static String descreverIdade(LocalDate nascimento, LocalDate hoje) {
        Period idade = Period.between(nascimento, hoje);
        int anos = idade.getYears();
        int meses = idade.getMonths();
        if (anos == 0 && meses == 0) {
            return "menos de 1 mês";
        }
        String textoAnos = anos == 1 ? "1 ano" : anos + " anos";
        String textoMeses = meses == 1 ? "1 mês" : meses + " meses";
        if (anos == 0) {
            return textoMeses;
        }
        return meses == 0 ? textoAnos : textoAnos + " e " + textoMeses;
    }
}
