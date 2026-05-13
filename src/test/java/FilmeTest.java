import model.Filme;
import model.enums.ClassificacaoEtaria;
import model.enums.Genero;
import model.enums.Idioma;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unitario")
@DisplayName("Testes de Filme")
class FilmeTest {

    private Filme filme;

    @BeforeEach
    void setUp() {
        filme = new Filme("F01", "A Chegada", 2016, 116,
                List.of(Genero.FICCAO_CIENTIFICA, Genero.DRAMA),
                ClassificacaoEtaria.DOZE, Idioma.INGLES, 84);
    }

    @Test
    @DisplayName("deve_CriarFilme_ComTodosAtributosPreenchidos")
    void deve_CriarFilme_ComTodosAtributosPreenchidos() {
        assertAll(
            () -> assertEquals("F01", filme.getId()),
            () -> assertEquals("A Chegada", filme.getTitulo()),
            () -> assertEquals(2016, filme.getAno()),
            () -> assertEquals(116, filme.getDuracao()),
            () -> assertEquals(ClassificacaoEtaria.DOZE, filme.getClassificacao()),
            () -> assertEquals(Idioma.INGLES, filme.getIdioma()),
            () -> assertEquals(84, filme.getPopularidade()),
            () -> assertNotNull(filme.getGeneros())
        );
    }

    @Test
    @DisplayName("deve_ConsiderarIguais_QuandoMesmoId")
    void deve_ConsiderarIguais_QuandoMesmoId() {
        Filme outroComMesmoId = new Filme("F01", "Outro Título", 2000, 100,
                List.of(Genero.COMEDIA), ClassificacaoEtaria.LIVRE, Idioma.INGLES, 50);

        assertEquals(filme, outroComMesmoId);
        assertEquals(filme.hashCode(), outroComMesmoId.hashCode());
    }

    @Test
    @DisplayName("deve_ConsiderarDiferentes_QuandoIdsDistintos")
    void deve_ConsiderarDiferentes_QuandoIdsDistintos() {
        Filme filmeDistinto = new Filme("F99", "Outro Filme", 2020, 90,
                List.of(Genero.DRAMA), ClassificacaoEtaria.LIVRE, Idioma.INGLES, 70);

        assertNotEquals(filme, filmeDistinto);
    }
}
