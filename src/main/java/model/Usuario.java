package model;

/**
 * Representa um usuário do sistema CineAnalytic.
 */
public class Usuario {

    private final String nome;
    private final int idade;
    private final PerfilCinefilo perfil;
    private final boolean notificacoesAtivas;

    public Usuario(String nome, int idade, PerfilCinefilo perfil, boolean notificacoesAtivas) {
        this.nome = nome;
        this.idade = idade;
        this.perfil = perfil;
        this.notificacoesAtivas = notificacoesAtivas;
    }

    public String getNome() { return nome; }
    public int getIdade() { return idade; }
    public PerfilCinefilo getPerfil() { return perfil; }
    public boolean isNotificacoesAtivas() { return notificacoesAtivas; }
}
