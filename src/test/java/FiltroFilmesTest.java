import model.Filme;
import model.PerfilCinefilo;
import model.enums.ClassificacaoEtaria;
import model.enums.Genero;
import model.enums.Idioma;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import service.FiltroFilmes;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unitario")
@DisplayName("Testes de FiltroFilmes")
class FiltroFilmesTest {

    private FiltroFilmes filtro;
    private PerfilCinefilo perfil;

    @BeforeEach
    void setUp() {
        filtro = new FiltroFilmes();
        perfil = new PerfilCinefilo();
        perfil.setPeso(Genero.FICCAO_CIENTIFICA, 0.9);
        perfil.setPeso(Genero.DRAMA, 0.7);
        perfil.setPeso(Genero.TERROR, 0.0);
        perfil.setClassificacaoMaxima(ClassificacaoEtaria.DEZESSEIS);
        perfil.adicionarIdioma(Idioma.INGLES);
        perfil.adicionarIdioma(Idioma.PORTUGUES);
    }

    @Test
    @DisplayName("deve_RemoverFilme_Quando_JaFoiAssistido")
    void deve_RemoverFilme_Quando_JaFoiAssistido() {
        Filme filme = criarFilme("F01", List.of(Genero.DRAMA), ClassificacaoEtaria.DOZE, Idioma.INGLES);
        perfil.marcarComoAssistido("F01");

        List<Filme> resultado = filtro.filtrar(List.of(filme), perfil);

        assertFalse(resultado.contains(filme));
    }

    @Test
    @DisplayName("deve_RemoverFilme_Quando_ClassificacaoAcimaDoMaximo")
    void deve_RemoverFilme_Quando_ClassificacaoAcimaDoMaximo() {
        Filme filme = criarFilme("F02", List.of(Genero.DRAMA), ClassificacaoEtaria.DEZOITO, Idioma.INGLES);

        List<Filme> resultado = filtro.filtrar(List.of(filme), perfil);

        assertFalse(resultado.contains(filme));
    }

    @Test
    @DisplayName("deve_RemoverFilme_Quando_IdiomaRecusado")
    void deve_RemoverFilme_Quando_IdiomaRecusado() {
        Filme filme = criarFilme("F03", List.of(Genero.DRAMA), ClassificacaoEtaria.DOZE, Idioma.JAPONES);

        List<Filme> resultado = filtro.filtrar(List.of(filme), perfil);

        assertFalse(resultado.contains(filme));
    }

    @Test
    @DisplayName("deve_RemoverFilme_Quando_GeneroComPesoZero")
    void deve_RemoverFilme_Quando_GeneroComPesoZero() {
        Filme filmeDeTerror = criarFilme("F04", List.of(Genero.TERROR), ClassificacaoEtaria.DEZESSEIS, Idioma.INGLES);

        List<Filme> resultado = filtro.filtrar(List.of(filmeDeTerror), perfil);

        assertFalse(resultado.contains(filmeDeTerror));
    }

    @Test
    @DisplayName("deve_RetornarListaVazia_Quando_CatalogoVazio")
    void deve_RetornarListaVazia_Quando_CatalogoVazio() {
        List<Filme> resultado = filtro.filtrar(Collections.emptyList(), perfil);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("deve_ManterFilme_Quando_PassaEmTodasAsRegras")
    void deve_ManterFilme_Quando_PassaEmTodasAsRegras() {
        Filme filmeValido = criarFilme("F05", List.of(Genero.DRAMA), ClassificacaoEtaria.DOZE, Idioma.INGLES);

        List<Filme> resultado = filtro.filtrar(List.of(filmeValido), perfil);

        assertTrue(resultado.contains(filmeValido));
    }

    @ParameterizedTest
    @CsvSource({
        "DEZ,    DEZESSEIS, true",
        "DEZESSEIS, DEZESSEIS, true",
        "DEZOITO, DEZESSEIS, false"
    })
    @DisplayName("deve_AceitarOuRejeitarFilme_ConforreClassificacao")
    void deve_AceitarOuRejeitarFilme_ConforreClassificacao(
            String classificacaoFilme, String classificacaoMaxPerfil, boolean esperadoAceito) {

        ClassificacaoEtaria classFilme   = ClassificacaoEtaria.valueOf(classificacaoFilme);
        ClassificacaoEtaria classMaxPerfil = ClassificacaoEtaria.valueOf(classificacaoMaxPerfil);

        perfil.setClassificacaoMaxima(classMaxPerfil);
        Filme filme = criarFilme("FX", List.of(Genero.DRAMA), classFilme, Idioma.INGLES);

        List<Filme> resultado = filtro.filtrar(List.of(filme), perfil);

        assertEquals(esperadoAceito, resultado.contains(filme));
    }

    private Filme criarFilme(String id, List<Genero> generos,
                              ClassificacaoEtaria classificacao, Idioma idioma) {
        return new Filme(id, "Filme " + id, 2020, 120, generos, classificacao, idioma, 70);
    }
}
