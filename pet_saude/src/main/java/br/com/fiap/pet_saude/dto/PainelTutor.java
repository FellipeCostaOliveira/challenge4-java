package br.com.fiap.pet_saude.dto;

import java.util.List;

import br.com.fiap.pet_saude.model.Consulta;

/** Informações exibidas na home do tutor. */
public record PainelTutor(
        List<PetComResumo> meusPets,
        List<Consulta> proximasConsultas
) {
}
