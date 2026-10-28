package br.com.fiap.pet_saude.dto;

import br.com.fiap.pet_saude.model.Pet;

/** Par pet + resumo de saúde, usado nos cards da home e no Radar de Prevenção. */
public record PetComResumo(Pet pet, ResumoSaudePet resumo) {
}
