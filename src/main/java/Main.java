import model.PerfilCinefilo;
import model.Recomendacao;
import model.Usuario;
import model.enums.ClassificacaoEtaria;
import model.enums.Genero;
import model.enums.Idioma;
import service.*;
import util.GeradorAleatorio;

import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Ponto de entrada do sistema CineAnalytic.
 * Demonstra o fluxo completo: perfil → filtro → score → recomendação.
 */
public class Main {

    public static void main(String[] args) {

        // ── 1. Configurar dependências ────────────────────────────────────
        CatalogoFilmesAPI catalogo   = new CatalogoMock();
        HistoricoUsuarioRepository historico  = criarHistoricoSimples();
        NotificadorPush notificador  = criarNotificadorConsole();
        GeradorAleatorio gerador     = criarGeradorAleatorio();
        CalculadoraScore calculadora = new CalculadoraScore();
        FiltroFilmes filtro          = new FiltroFilmes();

        RecomendadorService service = new RecomendadorService(
                catalogo, historico, notificador, gerador, calculadora, filtro
        );

        // ── 2. Criar usuário com perfil ───────────────────────────────────
        PerfilCinefilo perfil = new PerfilCinefilo();
        perfil.setPeso(Genero.FICCAO_CIENTIFICA, 0.9);
        perfil.setPeso(Genero.DRAMA,             0.7);
        perfil.setPeso(Genero.COMEDIA,           0.5);
        perfil.setPeso(Genero.TERROR,            0.0); // não quer terror
        perfil.setPeso(Genero.ROMANCE,           0.4);
        perfil.setFaixaDuracao(90, 160);
        perfil.setClassificacaoMaxima(ClassificacaoEtaria.DEZESSEIS);
        perfil.adicionarIdioma(Idioma.PORTUGUES);
        perfil.adicionarIdioma(Idioma.INGLES);
        perfil.marcarComoAssistido("F04"); // já assistiu Interestelar
        perfil.marcarComoAssistido("F08"); // já assistiu Matrix
        perfil.adicionarNota("F09", 5);    // adorou Blade Runner 2049

        Usuario maria = new Usuario("Maria", 28, perfil, true);

        // ── 3. Gerar Top 5 recomendações ─────────────────────────────────
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("       CineAnalytic — Sistema de Recomendação  ");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("Usuário: " + maria.getNome() + " (" + maria.getIdade() + " anos)");
        System.out.println();

        System.out.println(">>> Top 5 recomendações para " + maria.getNome() + ":");
        System.out.println("-----------------------------------------------");

        List<Recomendacao> top5 = service.recomendar(maria, 5);

        if (top5.isEmpty()) {
            System.out.println("Nenhuma recomendação encontrada para este perfil.");
        } else {
            int posicao = 1;
            for (Recomendacao rec : top5) {
                System.out.printf("%d. %-30s | Score: %5.1f%n",
                        posicao++,
                        rec.getFilme().getTitulo(),
                        rec.getScore());
                System.out.println("   " + rec.getJustificativa());
                System.out.println();
            }
        }

        // ── 4. Modo Surpreenda-me ─────────────────────────────────────────
        System.out.println("-----------------------------------------------");
        System.out.println(">>> Modo 'Surpreenda-me':");
        Optional<Recomendacao> surpresa = service.recomendarAleatorio(maria);
        surpresa.ifPresentOrElse(
                rec -> System.out.println("    " + rec.getFilme().getTitulo()
                        + " (" + rec.getFilme().getAno() + ")"),
                ()  -> System.out.println("    Nenhum filme disponível.")
        );

        System.out.println("═══════════════════════════════════════════════");
    }

    // ── Implementações inline simples (sem banco, sem HTTP) ───────────────

    private static HistoricoUsuarioRepository criarHistoricoSimples() {
        return new HistoricoUsuarioRepository() {
            @Override
            public void registrarRecomendacao(Usuario usuario, List<Recomendacao> recs) {
                System.out.println("[Histórico] " + recs.size()
                        + " recomendação(ões) registrada(s) para " + usuario.getNome());
            }

            @Override
            public List<String> buscarHistorico(Usuario usuario) {
                return usuario.getPerfil().getHistorico();
            }
        };
    }

    private static NotificadorPush criarNotificadorConsole() {
        return (usuario, recs) ->
            System.out.println("[Push] Notificação enviada para " + usuario.getNome()
                    + " com " + recs.size() + " sugestão(ões).");
    }

    private static GeradorAleatorio criarGeradorAleatorio() {
        Random random = new Random();
        return (min, max) -> min + random.nextInt(max - min);
    }
}
