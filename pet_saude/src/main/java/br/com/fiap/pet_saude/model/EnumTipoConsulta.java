package br.com.fiap.pet_saude.model;

public enum EnumTipoConsulta {

    PREVENTIVA("Preventiva / check-up"),
    TERAPEUTICA("Terapêutica"),
    BEM_ESTAR("Bem-estar"),
    EMERGENCIA("Emergência"),
    RETORNO("Retorno");

    private final String descricao;

    EnumTipoConsulta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
