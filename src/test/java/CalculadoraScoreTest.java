import model.Filme;
import model.PerfilCinefilo;
import model.Recomendacao;
import model.enums.ClassificacaoEtaria;
import model.enums.Genero;
import model.enums.Idioma;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import service.CalculadoraScore;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unitario")
@DisplayName("Testes de CalculadoraScore")
class CalculadoraScoreTest {

    private CalculadoraScore calculadora;
    private PerfilCinefilo perfil;

    @BeforeEach
    void setUp() {
        calculadora = new CalculadoraScore();
        perfil = new PerfilCinefilo();
        perfil.setPeso(Genero.FICCAO_CIENTIFICA, 1.0);
        perfil.setPeso(Genero.DRAMA, 1.0);
        perfil.setPeso(Genero.COMEDIA, 0.5);
        perfil.setPeso(Genero.TERROR, 0.0);
        perfil.setFaixaDuracao(90, 150);
        perfil.adicionarIdioma(Idioma.INGLES);
    }

    @Nested
    @DisplayName("Componente de Gênero")
    class ComponenteGenero {

        @Test
        @DisplayName("deve_RetornarCem_Quando_TodosGenerosAmados")
        void deve_RetornarCem_Quando_TodosGenerosAmados() {
            Filme filme = criarFilme("FT", List.of(Genero.FICCAO_CIENTIFICA, Genero.DRAMA), 120, 80);

            double componenteGenero = calculadora.calcularComponenteGenero(filme, perfil);

            assertEquals(100.0, componenteGenero, 0.01);
        }

        @Test
        @DisplayName("deve_RetornarScoreBaixo_Quando_GeneroNaoPreferido")
        void deve_RetornarScoreBaixo_Quando_GeneroNaoPreferido() {
            perfil.setPeso(Genero.COMEDIA, 0.2);
            Filme filme = criarFilme("FT", List.of(Genero.COMEDIA), 120, 80);

            double componenteGenero = calculadora.calcularComponenteGenero(filme, perfil);

            assertTrue(componenteGenero < 50.0);
        }

        @ParameterizedTest
        @CsvSource({
            "1.0, 1.0, 100.0",
            "0.5, 0.5, 50.0",
            "0.0, 0.5, 25.0"
        })
        @DisplayName("deve_CalcularMediaPonderadaDosGeneros")
        void deve_CalcularMediaPonderadaDosGeneros(double pesoFC, double pesoDrama, double esperado) {
            perfil.setPeso(Genero.FICCAO_CIENTIFICA, pesoFC);
            perfil.setPeso(Genero.DRAMA, pesoDrama);
            Filme filme = criarFilme("FT", List.of(Genero.FICCAO_CIENTIFICA, Genero.DRAMA), 120, 80);

            double resultado = calculadora.calcularComponenteGenero(filme, perfil);

            assertEquals(esperado, resultado, 0.01);
        }
    }

    @Nested
    @DisplayName("Componente de Duração")
    class ComponenteDuracao {

        @Test
        @DisplayName("deve_RetornarCem_Quando_FilmeDentroDaFaixaDuracao")
        void deve_RetornarCem_Quando_FilmeDentroDaFaixaDuracao() {
            Filme filme = criarFilme("FT", List.of(Genero.DRAMA), 120, 80);

            double resultado = calculadora.calcularComponenteDuracao(filme, perfil);

            assertEquals(100.0, resultado, 0.01);
        }

        @Test
        @DisplayName("deve_ReducirScore_Quando_DuracaoAcimaDoMaximo")
        void deve_ReducirScore_Quando_DuracaoAcimaDoMaximo() {
            Filme filme = criarFilme("FT", List.of(Genero.DRAMA), 180, 80); // 30 min acima do máx 150

            double resultado = calculadora.calcularComponenteDuracao(filme, perfil);

            assertTrue(resultado < 100.0);
        }
    }

    @Nested
    @DisplayName("Score Final")
    class ScoreFinal {

        @Test
        @DisplayName("deve_NaoPassarDeCem_NuncaFicarNegativo")
        void deve_NaoPassarDeCem_NuncaFicarNegativo() {
            Filme filmeBom  = criarFilme("F1", List.of(Genero.FICCAO_CIENTIFICA), 120, 100);
            Filme filmeRuim = criarFilme("F2", List.of(Genero.COMEDIA), 300, 0);

            Recomendacao recBom  = calculadora.calcular(filmeBom, perfil);
            Recomendacao recRuim = calculadora.calcular(filmeRuim, perfil);

            assertTrue(recBom.getScore() <= 100.0);
            assertTrue(recRuim.getScore() >= 0.0);
        }

        @Test
        @DisplayName("deve_GerarJustificativa_Quando_CalculaScore")
        void deve_GerarJustificativa_Quando_CalculaScore() {
            Filme filme = criarFilme("FT", List.of(Genero.FICCAO_CIENTIFICA), 120, 80);

            Recomendacao resultado = calculadora.calcular(filme, perfil);

            assertNotNull(resultado.getJustificativa());
            assertFalse(resultado.getJustificativa().isBlank());
        }
    }

    private Filme criarFilme(String id, List<Genero> generos, int duracao, int popularidade) {
        return new Filme(id, "Filme Teste", 2020, duracao,
                generos, ClassificacaoEtaria.DOZE, Idioma.INGLES, popularidade);
    }
}
