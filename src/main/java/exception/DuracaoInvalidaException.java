package exception;

/** Lançada quando a duração mínima é maior que a duração máxima no perfil. */
public class DuracaoInvalidaException extends RuntimeException {
    public DuracaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
