package br.com.fiap.pet_saude.model;

/**
 * Espécies atendidas pela clínica.
 * <p>
 * Cada espécie carrega os marcos de idade usados pelo plano preventivo:
 * até quantos meses o animal é considerado filhote e a partir de quantos
 * anos passa a ser considerado idoso (sênior). São valores de referência
 * geral; a avaliação final é sempre do médico-veterinário.
 */
public enum EnumEspecie {

    CACHORRO("Cachorro", 12, 7),
    GATO("Gato", 12, 10),
    PASSARO("Pássaro", 12, 8),
    COELHO("Coelho", 6, 5),
    HAMSTER("Hamster", 3, 2),
    OUTRO("Outro", 12, 7);

    private final String descricao;
    private final int mesesAteAdulto;
    private final int anosAteIdoso;

    EnumEspecie(String descricao, int mesesAteAdulto, int anosAteIdoso) {
        this.descricao = descricao;
        this.mesesAteAdulto = mesesAteAdulto;
        this.anosAteIdoso = anosAteIdoso;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getMesesAteAdulto() {
        return mesesAteAdulto;
    }

    public int getAnosAteIdoso() {
        return anosAteIdoso;
    }
}
