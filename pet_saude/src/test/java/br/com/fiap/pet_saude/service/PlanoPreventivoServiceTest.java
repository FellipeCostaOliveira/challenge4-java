package br.com.fiap.pet_saude.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import br.com.fiap.pet_saude.dto.ResumoSaudePet;
import br.com.fiap.pet_saude.model.Consulta;
import br.com.fiap.pet_saude.model.EnumEspecie;
import br.com.fiap.pet_saude.model.EnumFaixaEtaria;
import br.com.fiap.pet_saude.model.EnumSituacaoSaude;
import br.com.fiap.pet_saude.model.EnumStatusConsulta;
import br.com.fiap.pet_saude.model.EnumTipoConsulta;
import br.com.fiap.pet_saude.model.Pet;
import br.com.fiap.pet_saude.repository.ConsultaRepository;

class PlanoPreventivoServiceTest {

    private static final LocalDate HOJE = LocalDate.of(2026, 10, 23);

    private PlanoPreventivoService service;

    @BeforeEach
    void setUp() {
        ZoneId fuso = ZoneId.of("America/Sao_Paulo");
        Clock relogioFixo = Clock.fixed(HOJE.atTime(10, 0).atZone(fuso).toInstant(), fuso);
        service = new PlanoPreventivoService(Mockito.mock(ConsultaRepository.class), relogioFixo);
    }

    @Test
    void deveClassificarFaixaEtariaConformeEspecieEIdade() {
        assertEquals(EnumFaixaEtaria.FILHOTE, service.classificarFaixaEtaria(pet(EnumEspecie.CACHORRO, HOJE.minusMonths(5)), HOJE));
        assertEquals(EnumFaixaEtaria.ADULTO, service.classificarFaixaEtaria(pet(EnumEspecie.CACHORRO, HOJE.minusYears(3)), HOJE));
        assertEquals(EnumFaixaEtaria.IDOSO, service.classificarFaixaEtaria(pet(EnumEspecie.CACHORRO, HOJE.minusYears(8)), HOJE));
        assertEquals(EnumFaixaEtaria.ADULTO, service.classificarFaixaEtaria(pet(EnumEspecie.GATO, HOJE.minusYears(8)), HOJE));
        assertEquals(EnumFaixaEtaria.IDOSO, service.classificarFaixaEtaria(pet(EnumEspecie.HAMSTER, HOJE.minusYears(2)), HOJE));
    }

    @Test
    void petSemConsultasRealizadasFicaSemHistoricoEPrecisaDeContato() {
        ResumoSaudePet resumo = service.calcular(pet(EnumEspecie.CACHORRO, HOJE.minusMonths(5)), List.of());

        assertEquals(EnumSituacaoSaude.SEM_HISTORICO, resumo.situacao());
        assertNull(resumo.dataUltimaConsultaRealizada());
        assertTrue(resumo.precisaDeContato());
    }

    @Test
    void idosoComCheckupSemestralVencidoFicaAtrasado() {
        Pet gatoIdoso = pet(EnumEspecie.GATO, HOJE.minusYears(12));
        List<Consulta> historico = List.of(consulta(HOJE.minusMonths(8), EnumStatusConsulta.REALIZADA));

        ResumoSaudePet resumo = service.calcular(gatoIdoso, historico);

        assertEquals(6, resumo.intervaloRecomendadoMeses());
        assertEquals(EnumSituacaoSaude.ATRASADO, resumo.situacao());
        assertEquals(100, resumo.percentualCiclo());
    }

    @Test
    void adultoComCheckupVencendoEmMenosDe30DiasFicaEmAtencao() {
        Pet adulto = pet(EnumEspecie.CACHORRO, HOJE.minusYears(3));
        List<Consulta> historico = List.of(consulta(HOJE.minusDays(350), EnumStatusConsulta.REALIZADA));

        assertEquals(EnumSituacaoSaude.ATENCAO, service.calcular(adulto, historico).situacao());
    }

    @Test
    void adultoComCheckupRecenteFicaEmDia() {
        Pet adulto = pet(EnumEspecie.CACHORRO, HOJE.minusYears(3));
        List<Consulta> historico = List.of(
                consulta(HOJE.minusMonths(2), EnumStatusConsulta.REALIZADA),
                consulta(HOJE.minusMonths(1), EnumStatusConsulta.CANCELADA));

        ResumoSaudePet resumo = service.calcular(adulto, historico);

        assertEquals(EnumSituacaoSaude.EM_DIA, resumo.situacao());
        assertEquals(HOJE.minusMonths(2).plusMonths(12), resumo.dataProximaConsultaSugerida());
        assertEquals(1, resumo.consultasRealizadas());
    }

    @Test
    void consultaFuturaAgendadaTiraPetAtrasadoDoRadar() {
        Pet adulto = pet(EnumEspecie.CACHORRO, HOJE.minusYears(5));
        List<Consulta> historico = List.of(
                consulta(HOJE.plusDays(10), EnumStatusConsulta.AGENDADA),
                consulta(HOJE.minusMonths(20), EnumStatusConsulta.REALIZADA));

        ResumoSaudePet resumo = service.calcular(adulto, historico);

        assertEquals(EnumSituacaoSaude.ATRASADO, resumo.situacao());
        assertTrue(resumo.possuiConsultaAgendada());
        assertFalse(resumo.precisaDeContato());
        assertTrue(resumo.recomendacao().startsWith("Consulta já agendada"));
    }

    @Test
    void deveDescreverIdadeEmAnosEMeses() {
        assertEquals("3 anos e 2 meses", PlanoPreventivoService.descreverIdade(HOJE.minusYears(3).minusMonths(2), HOJE));
        assertEquals("1 ano", PlanoPreventivoService.descreverIdade(HOJE.minusYears(1), HOJE));
        assertEquals("5 meses", PlanoPreventivoService.descreverIdade(HOJE.minusMonths(5), HOJE));
        assertEquals("menos de 1 mês", PlanoPreventivoService.descreverIdade(HOJE.minusDays(10), HOJE));
    }

    private static Pet pet(EnumEspecie especie, LocalDate nascimento) {
        Pet pet = new Pet();
        pet.setId(1L);
        pet.setNome("Teste");
        pet.setEspecie(especie);
        pet.setDataNascimento(nascimento);
        return pet;
    }

    private static Consulta consulta(LocalDate data, EnumStatusConsulta status) {
        Consulta consulta = new Consulta();
        consulta.setDataConsulta(data);
        consulta.setHorario(LocalTime.of(10, 0));
        consulta.setTipo(EnumTipoConsulta.PREVENTIVA);
        consulta.setStatus(status);
        return consulta;
    }
}
