package br.com.fiap.pet_saude.model;

public enum EnumPapel {

    ADMIN("Administrador (equipe da clínica)"),
    TUTOR("Tutor do pet");

    private final String descricao;

    EnumPapel(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
