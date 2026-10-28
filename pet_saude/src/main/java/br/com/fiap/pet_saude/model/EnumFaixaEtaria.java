package br.com.fiap.pet_saude.model;

/**
 * Faixa etária do pet e o intervalo recomendado entre consultas preventivas.
 * Filhotes passam por várias consultas no primeiro ano (vacinas, vermifugação);
 * adultos saudáveis precisam de check-up anual; idosos, semestral.
 */
public enum EnumFaixaEtaria {

    FILHOTE("Filhote", 3),
    ADULTO("Adulto", 12),
    IDOSO("Idoso", 6);

    private final String descricao;
    private final int intervaloRecomendadoMeses;

    EnumFaixaEtaria(String descricao, int intervaloRecomendadoMeses) {
        this.descricao = descricao;
        this.intervaloRecomendadoMeses = intervaloRecomendadoMeses;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getIntervaloRecomendadoMeses() {
        return intervaloRecomendadoMeses;
    }
}
