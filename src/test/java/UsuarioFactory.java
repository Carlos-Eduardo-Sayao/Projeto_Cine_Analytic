import model.PerfilCinefilo;
import model.Usuario;
import model.enums.ClassificacaoEtaria;
import model.enums.Genero;
import model.enums.Idioma;

/**
 * Fábrica de usuários para uso nos testes.
 */
public class UsuarioFactory {

    /** Cria o perfil da Maria exatamente como descrito no enunciado do projeto. */
    public static Usuario maria() {
        PerfilCinefilo perfil = new PerfilCinefilo();
        perfil.setPeso(Genero.FICCAO_CIENTIFICA, 0.9);
        perfil.setPeso(Genero.DRAMA, 0.6);
        perfil.setPeso(Genero.COMEDIA, 0.5);
        perfil.setPeso(Genero.TERROR, 0.0);
        perfil.setPeso(Genero.ROMANCE, 0.4);
        perfil.setFaixaDuracao(90, 150);
        perfil.setClassificacaoMaxima(ClassificacaoEtaria.DEZESSEIS);
        perfil.adicionarIdioma(Idioma.PORTUGUES);
        perfil.adicionarIdioma(Idioma.INGLES);
        perfil.marcarComoAssistido("F04"); // Interestelar
        perfil.marcarComoAssistido("F08"); // Matrix
        perfil.adicionarNota("F09", 5); // Blade Runner 2049
        perfil.adicionarNota("F32", 2); // Star Wars IX (usamos Tenet como substituto)
        return new Usuario("Maria", 28, perfil, true);
    }

    /** Usuário simples sem notificações ativas, para testes de notificação. */
    public static Usuario joao() {
        PerfilCinefilo perfil = new PerfilCinefilo();
        perfil.setPeso(Genero.ACAO, 0.8);
        perfil.setPeso(Genero.DRAMA, 0.7);
        perfil.setFaixaDuracao(80, 160);
        perfil.setClassificacaoMaxima(ClassificacaoEtaria.DEZOITO);
        perfil.adicionarIdioma(Idioma.PORTUGUES);
        perfil.adicionarIdioma(Idioma.INGLES);
        return new Usuario("João", 25, perfil, false);
    }
}
