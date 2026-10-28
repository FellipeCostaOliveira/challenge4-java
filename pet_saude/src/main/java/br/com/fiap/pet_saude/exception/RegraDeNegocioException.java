package br.com.fiap.pet_saude.exception;

/** Lançada quando uma regra de negócio impede a operação (ex.: conflito de agenda, senha divergente). */
public class RegraDeNegocioException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}
