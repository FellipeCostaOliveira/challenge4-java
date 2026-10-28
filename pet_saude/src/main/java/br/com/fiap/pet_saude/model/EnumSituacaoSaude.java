package br.com.fiap.pet_saude.model;

/**
 * Situação do acompanhamento preventivo de um pet.
 * A prioridade ordena o Radar de Prevenção da clínica (menor = mais urgente).
 */
public enum EnumSituacaoSaude {

    ATRASADO("Check-up atrasado", 1),
    SEM_HISTORICO("Sem histórico", 2),
    ATENCAO("Check-up em breve", 3),
    EM_DIA("Em dia", 4);

    private final String descricao;
    private final int prioridade;

    EnumSituacaoSaude(String descricao, int prioridade) {
        this.descricao = descricao;
        this.prioridade = prioridade;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public boolean exigeAcao() {
        return this != EM_DIA;
    }
}
