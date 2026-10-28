package br.com.fiap.pet_saude.dto;

import java.time.LocalDate;

import br.com.fiap.pet_saude.model.Consulta;
import br.com.fiap.pet_saude.model.EnumFaixaEtaria;
import br.com.fiap.pet_saude.model.EnumSituacaoSaude;

/**
 * Resultado do Plano Preventivo de um pet: em que fase da vida ele está,
 * de quanto em quanto tempo deveria ir ao veterinário e se está em dia.
 *
 * @param proximaConsultaAgendada consulta futura já marcada (ou null)
 * @param percentualCiclo quanto do intervalo recomendado já passou (0 a 100)
 */
public record ResumoSaudePet(
        EnumFaixaEtaria faixaEtaria,
        String idadeDescricao,
        int intervaloRecomendadoMeses,
        int totalConsultas,
        long consultasRealizadas,
        long consultasPreventivasRealizadas,
        LocalDate dataUltimaConsultaRealizada,
        LocalDate dataProximaConsultaSugerida,
        Consulta proximaConsultaAgendada,
        EnumSituacaoSaude situacao,
        String recomendacao,
        int percentualCiclo
) {
    public boolean possuiConsultaAgendada() {
        return proximaConsultaAgendada != null;
    }

    /** Precisa de contato da clínica: situação crítica e nada agendado. */
    public boolean precisaDeContato() {
        return situacao.exigeAcao() && !possuiConsultaAgendada();
    }
}
