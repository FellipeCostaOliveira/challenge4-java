package br.com.fiap.pet_saude.service;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.pet_saude.exception.AcessoNegadoException;
import br.com.fiap.pet_saude.exception.RegistroNaoEncontradoException;
import br.com.fiap.pet_saude.exception.RegraDeNegocioException;
import br.com.fiap.pet_saude.model.Consulta;
import br.com.fiap.pet_saude.model.EnumStatusConsulta;
import br.com.fiap.pet_saude.model.Pet;
import br.com.fiap.pet_saude.repository.ConsultaRepository;
import br.com.fiap.pet_saude.security.UsuarioLogadoService;

@Service
public class ConsultaService {

    public static final LocalTime ABERTURA = LocalTime.of(8, 0);
    public static final LocalTime ULTIMO_HORARIO = LocalTime.of(17, 30);
    static final int TAMANHO_MAXIMO_OBSERVACOES = 300;
    private static final DateTimeFormatter DATA_HORA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    private final ConsultaRepository repC;
    private final PetService petService;
    private final UsuarioLogadoService usuarioLogadoService;
    private final Clock relogio;

    public ConsultaService(ConsultaRepository repC, PetService petService,
                           UsuarioLogadoService usuarioLogadoService, Clock relogio) {
        this.repC = repC;
        this.petService = petService;
        this.usuarioLogadoService = usuarioLogadoService;
        this.relogio = relogio;
    }

    /** Admin vê todas; tutor vê só as dos próprios pets. Filtro de status é opcional. */
    public List<Consulta> listarVisiveis(EnumStatusConsulta filtroStatus) {
        List<Consulta> consultas = usuarioLogadoService.isAdmin()
                ? repC.findAllByOrderByDataConsultaDescHorarioDesc()
                : repC.buscarPorTutor(usuarioLogadoService.obterTutorLogado().getId());
        if (filtroStatus == null) {
            return consultas;
        }
        return consultas.stream().filter(c -> c.getStatus() == filtroStatus).toList();
    }

    /** Data de hoje no fuso da clínica (usada como data mínima no formulário). */
    public LocalDate hoje() {
        return LocalDate.now(relogio);
    }

    public List<Consulta> listarHistoricoDoPet(Long petId) {
        petService.buscarComPermissao(petId);
        return repC.findByPetIdOrderByDataConsultaDescHorarioDesc(petId);
    }

    public List<Consulta> listarAgendaDoDia() {
        return repC.findByDataConsultaAndStatusOrderByHorarioAsc(LocalDate.now(relogio), EnumStatusConsulta.AGENDADA);
    }

    @Transactional
    public Consulta agendar(Long petId, Consulta dadosConsulta) {
        Pet pet = petService.buscarComPermissao(petId);
        if (!pet.estaAtivo()) {
            throw new RegraDeNegocioException("Este pet está inativo. Reative-o na edição antes de agendar.");
        }
        validarDataEHorario(dadosConsulta.getDataConsulta(), dadosConsulta.getHorario());
        validarConflitoDeAgenda(petId, dadosConsulta);

        Consulta consulta = new Consulta();
        consulta.setPet(pet);
        consulta.setDataConsulta(dadosConsulta.getDataConsulta());
        consulta.setHorario(dadosConsulta.getHorario());
        consulta.setTipo(dadosConsulta.getTipo());
        consulta.setDescricao(dadosConsulta.getDescricao().trim());
        consulta.setStatus(EnumStatusConsulta.AGENDADA);
        return repC.save(consulta);
    }

    @Transactional
    public Consulta realizar(Long id, String observacoes) {
        if (!usuarioLogadoService.isAdmin()) {
            throw new AcessoNegadoException("Apenas a equipe da clínica pode marcar uma consulta como realizada.");
        }
        if (observacoes != null && observacoes.length() > TAMANHO_MAXIMO_OBSERVACOES) {
            throw new RegraDeNegocioException("As observações devem ter, no máximo, 300 caracteres.");
        }
        Consulta consulta = buscarOuFalhar(id);
        garantirQueEstaAgendada(consulta, "marcada como realizada");
        consulta.setStatus(EnumStatusConsulta.REALIZADA);
        consulta.setObservacoes(observacoes == null || observacoes.isBlank() ? null : observacoes.trim());
        return repC.save(consulta);
    }

    @Transactional
    public Consulta cancelar(Long id) {
        Consulta consulta = buscarOuFalhar(id);
        if (!usuarioLogadoService.isAdmin()) {
            petService.buscarComPermissao(consulta.getPet().getId());
        }
        garantirQueEstaAgendada(consulta, "cancelada");
        consulta.setStatus(EnumStatusConsulta.CANCELADA);
        return repC.save(consulta);
    }

    void validarDataEHorario(LocalDate data, LocalTime horario) {
        LocalDateTime agora = LocalDateTime.now(relogio);
        if (data.isBefore(agora.toLocalDate())) {
            throw new RegraDeNegocioException("A data da consulta não pode estar no passado.");
        }
        if (data.isEqual(agora.toLocalDate()) && !horario.isAfter(agora.toLocalTime())) {
            throw new RegraDeNegocioException("Esse horário já passou. Escolha um horário mais tarde ou outro dia.");
        }
        if (data.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new RegraDeNegocioException("A clínica não atende aos domingos. Escolha de segunda a sábado.");
        }
        if (horario.isBefore(ABERTURA) || horario.isAfter(ULTIMO_HORARIO)) {
            throw new RegraDeNegocioException("Os atendimentos acontecem entre 08:00 e 17:30.");
        }
    }

    private void validarConflitoDeAgenda(Long petId, Consulta dadosConsulta) {
        boolean existeConflito = repC.existsByPetIdAndDataConsultaAndHorarioAndStatus(
                petId, dadosConsulta.getDataConsulta(), dadosConsulta.getHorario(), EnumStatusConsulta.AGENDADA);
        if (existeConflito) {
            throw new RegraDeNegocioException("Este pet já possui uma consulta agendada para "
                    + LocalDateTime.of(dadosConsulta.getDataConsulta(), dadosConsulta.getHorario()).format(DATA_HORA_BR)
                    + ".");
        }
    }

    private Consulta buscarOuFalhar(Long id) {
        return repC.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Consulta não encontrada."));
    }

    private void garantirQueEstaAgendada(Consulta consulta, String acao) {
        if (!consulta.isAgendada()) {
            throw new RegraDeNegocioException("Esta consulta já está " + consulta.getStatus().getDescricao().toLowerCase()
                    + " e não pode mais ser " + acao + ".");
        }
    }
}
