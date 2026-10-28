package br.com.fiap.pet_saude.exception;

/** Lançada quando o usuário logado tenta acessar um recurso que não lhe pertence. */
public class AcessoNegadoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}
