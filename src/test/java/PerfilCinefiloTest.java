import exception.DuracaoInvalidaException;
import exception.PesoInvalidoException;
import model.PerfilCinefilo;
import model.enums.ClassificacaoEtaria;
import model.enums.Genero;
import model.enums.Idioma;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unitario")
@DisplayName("Testes de PerfilCinefilo")
class PerfilCinefiloTest {

    private PerfilCinefilo perfil;

    @BeforeEach
    void setUp() {
        perfil = new PerfilCinefilo();
    }

    @Test
    @DisplayName("deve_CriarPerfil_ComPesosValidos")
    void deve_CriarPerfil_ComPesosValidos() {
        assertDoesNotThrow(() -> {
            perfil.setPeso(Genero.DRAMA, 0.8);
            perfil.setPeso(Genero.COMEDIA, 0.0);
            perfil.setPeso(Genero.TERROR, 1.0);
        });
    }

    @Test
    @DisplayName("deve_LancarExcecao_Quando_PesoMaiorQueUm")
    void deve_LancarExcecao_Quando_PesoMaiorQueUm() {
        assertThrows(PesoInvalidoException.class,
                () -> perfil.setPeso(Genero.ACAO, 1.5));
    }

    @Test
    @DisplayName("deve_LancarExcecao_Quando_PesoMenorQueZero")
    void deve_LancarExcecao_Quando_PesoMenorQueZero() {
        assertThrows(PesoInvalidoException.class,
                () -> perfil.setPeso(Genero.ACAO, -0.1));
    }

    @Test
    @DisplayName("deve_LancarExcecao_Quando_DuracaoMinimaEhMaiorQueMaxima")
    void deve_LancarExcecao_Quando_DuracaoMinimaEhMaiorQueMaxima() {
        assertThrows(DuracaoInvalidaException.class,
                () -> perfil.setFaixaDuracao(200, 100));
    }

    @Test
    @DisplayName("deve_AceitarFaixaDuracao_QuandoMinimaIgualMaxima")
    void deve_AceitarFaixaDuracao_QuandoMinimaIgualMaxima() {
        assertDoesNotThrow(() -> perfil.setFaixaDuracao(120, 120));
    }

    @Test
    @DisplayName("deve_LancarExcecao_Quando_NotaForaDoIntervalo")
    void deve_LancarExcecao_Quando_NotaForaDoIntervalo() {
        assertThrows(IllegalArgumentException.class,
                () -> perfil.adicionarNota("F01", 6));

        assertThrows(IllegalArgumentException.class,
                () -> perfil.adicionarNota("F01", 0));
    }

    @Test
    @DisplayName("deve_MarcarFilmeAssistido_EAparecerNoHistorico")
    void deve_MarcarFilmeAssistido_EAparecerNoHistorico() {
        perfil.marcarComoAssistido("F01");

        assertTrue(perfil.jaAssistiu("F01"));
        assertTrue(perfil.getHistorico().contains("F01"));
    }

    @Test
    @DisplayName("deve_RetornarNull_Quando_FilmeNuncaAvaliado")
    void deve_RetornarNull_Quando_FilmeNuncaAvaliado() {
        assertNull(perfil.getNotaPara("filme-nunca-avaliado"));
    }

    @Test
    @DisplayName("deve_RetornarNota_Quando_FilmeAvaliado")
    void deve_RetornarNota_Quando_FilmeAvaliado() {
        perfil.adicionarNota("F01", 4);

        assertNotNull(perfil.getNotaPara("F01"));
        assertEquals(4, perfil.getNotaPara("F01"));
    }
}
