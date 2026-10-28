package br.com.fiap.pet_saude.dto;

import java.util.List;

import br.com.fiap.pet_saude.model.Consulta;

/** Indicadores exibidos na home da equipe da clínica. */
public record PainelAdmin(
        long totalTutores,
        long totalPetsAtivos,
        long consultasAgendadas,
        List<Consulta> agendaDeHoje,
        List<PetComResumo> radarPrevencao
) {
}
