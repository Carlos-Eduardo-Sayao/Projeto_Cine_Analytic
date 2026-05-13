package exception;

/** Lançada quando o perfil do usuário não está completo para gerar recomendações. */
public class PerfilIncompletoException extends RuntimeException {
    public PerfilIncompletoException(String mensagem) {
        super(mensagem);
    }
}
