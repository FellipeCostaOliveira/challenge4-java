package br.com.fiap.pet_saude.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.fiap.pet_saude.exception.AcessoNegadoException;
import br.com.fiap.pet_saude.exception.RegraDeNegocioException;
import br.com.fiap.pet_saude.model.Consulta;
import br.com.fiap.pet_saude.model.EnumStatusConsulta;
import br.com.fiap.pet_saude.model.EnumTipoConsulta;
import br.com.fiap.pet_saude.model.Pet;
import br.com.fiap.pet_saude.repository.ConsultaRepository;
import br.com.fiap.pet_saude.security.UsuarioLogadoService;

@ExtendWith(MockitoExtension.class)
class ConsultaServiceTest {

    /** Sexta-feira, 23/10/2026, 10:00 no horário de Brasília. */
    private static final LocalDate HOJE = LocalDate.of(2026, 10, 23);

    @Mock
    private ConsultaRepository repC;

    @Mock
    private PetService petService;

    @Mock
    private UsuarioLogadoService usuarioLogadoService;

    private ConsultaService consultaService;
    private Pet pet;
    private Consulta novaConsulta;

    @BeforeEach
    void setUp() {
        ZoneId fuso = ZoneId.of("America/Sao_Paulo");
        Clock relogioFixo = Clock.fixed(HOJE.atTime(10, 0).atZone(fuso).toInstant(), fuso);
        consultaService = new ConsultaService(repC, petService, usuarioLogadoService, relogioFixo);

        pet = new Pet();
        pet.setId(1L);
        pet.setNome("Rex");
        pet.setAtivo(true);

        novaConsulta = new Consulta();
        novaConsulta.setDataConsulta(HOJE.plusDays(4)); // terça-feira
        novaConsulta.setHorario(LocalTime.of(10, 0));
        novaConsulta.setTipo(EnumTipoConsulta.PREVENTIVA);
        novaConsulta.setDescricao("Vacinação de rotina");
    }

    @Test
    void devePermitirAgendamentoQuandoNaoHaConflito() {
        when(petService.buscarComPermissao(1L)).thenReturn(pet);
        when(repC.existsByPetIdAndDataConsultaAndHorarioAndStatus(
                1L, novaConsulta.getDataConsulta(), novaConsulta.getHorario(), EnumStatusConsulta.AGENDADA))
                .thenReturn(false);
        when(repC.save(any(Consulta.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Consulta resultado = consultaService.agendar(1L, novaConsulta);

        assertEquals(EnumStatusConsulta.AGENDADA, resultado.getStatus());
        assertEquals(pet, resultado.getPet());
    }

    @Test
    void deveRecusarAgendamentoQuandoJaExisteConsultaNoMesmoHorarioParaOMesmoPet() {
        when(petService.buscarComPermissao(1L)).thenReturn(pet);
        when(repC.existsByPetIdAndDataConsultaAndHorarioAndStatus(
                1L, novaConsulta.getDataConsulta(), novaConsulta.getHorario(), EnumStatusConsulta.AGENDADA))
                .thenReturn(true);

        assertThrows(RegraDeNegocioException.class, () -> consultaService.agendar(1L, novaConsulta));
        verify(repC, never()).save(any());
    }

    @Test
    void deveRecusarAgendamentoParaPetInativo() {
        pet.setAtivo(false);
        when(petService.buscarComPermissao(1L)).thenReturn(pet);

        assertThrows(RegraDeNegocioException.class, () -> consultaService.agendar(1L, novaConsulta));
    }

    @Test
    void deveRecusarDataNoPassado() {
        assertThrows(RegraDeNegocioException.class,
                () -> consultaService.validarDataEHorario(HOJE.minusDays(1), LocalTime.of(10, 0)));
    }

    @Test
    void deveRecusarHorarioQueJaPassouNoDiaDeHoje() {
        assertThrows(RegraDeNegocioException.class,
                () -> consultaService.validarDataEHorario(HOJE, LocalTime.of(9, 0)));
    }

    @Test
    void deveRecusarDomingo() {
        assertThrows(RegraDeNegocioException.class,
                () -> consultaService.validarDataEHorario(LocalDate.of(2026, 10, 25), LocalTime.of(10, 0)));
    }

    @Test
    void deveRecusarHorarioForaDoFuncionamento() {
        assertThrows(RegraDeNegocioException.class,
                () -> consultaService.validarDataEHorario(HOJE.plusDays(1), LocalTime.of(7, 30)));
        assertThrows(RegraDeNegocioException.class,
                () -> consultaService.validarDataEHorario(HOJE.plusDays(1), LocalTime.of(18, 0)));
    }

    @Test
    void naoDevePermitirCancelarConsultaJaRealizada() {
        Consulta realizada = new Consulta();
        realizada.setPet(pet);
        realizada.setStatus(EnumStatusConsulta.REALIZADA);
        when(repC.findById(5L)).thenReturn(Optional.of(realizada));
        when(usuarioLogadoService.isAdmin()).thenReturn(true);

        assertThrows(RegraDeNegocioException.class, () -> consultaService.cancelar(5L));
    }

    @Test
    void tutorNaoPodeMarcarConsultaComoRealizada() {
        when(usuarioLogadoService.isAdmin()).thenReturn(false);

        assertThrows(AcessoNegadoException.class, () -> consultaService.realizar(5L, "ok"));
        verify(repC, never()).findById(any());
    }
}
