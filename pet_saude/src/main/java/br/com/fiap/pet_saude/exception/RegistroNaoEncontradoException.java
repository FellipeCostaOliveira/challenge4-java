package br.com.fiap.pet_saude.exception;

/** Lançada quando um registro buscado pelo id não existe. */
public class RegistroNaoEncontradoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RegistroNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
