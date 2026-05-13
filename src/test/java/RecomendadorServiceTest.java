import model.Filme;
import model.Recomendacao;
import model.Usuario;
import model.enums.ClassificacaoEtaria;
import model.enums.Genero;
import model.enums.Idioma;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import service.CalculadoraScore;
import service.CatalogoFilmesAPI;
import service.FiltroFilmes;
import service.HistoricoUsuarioRepository;
import service.NotificadorPush;
import service.RecomendadorService;
import util.GeradorAleatorio;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Tag("unitario")
@DisplayName("Testes de RecomendadorService")
@ExtendWith(MockitoExtension.class)
class RecomendadorServiceTest {

    @Mock private CatalogoFilmesAPI catalogo;
    @Mock private HistoricoUsuarioRepository historico;
    @Mock private NotificadorPush notificador;
    @Mock private GeradorAleatorio gerador;

    @Spy  private CalculadoraScore calculadora;
    private FiltroFilmes filtro;
    private RecomendadorService service;
    private Usuario maria;

    @BeforeEach
    void setUp() {
        filtro  = new FiltroFilmes();
        service = new RecomendadorService(catalogo, historico, notificador, gerador, calculadora, filtro);
        maria   = UsuarioFactory.maria();
    }

    // ---- Filmes auxiliares para os testes ----

    private Filme filmeAltoScore() {
        return new Filme("FA", "Filme Alto",  2020, 120,
                List.of(Genero.FICCAO_CIENTIFICA, Genero.DRAMA),
                ClassificacaoEtaria.DOZE, Idioma.INGLES, 95);
    }

    private Filme filmeMedioScore() {
        return new Filme("FM", "Filme Medio", 2020, 120,
                List.of(Genero.COMEDIA),
                ClassificacaoEtaria.DOZE, Idioma.INGLES, 70);
    }

    private Filme filmeBaixoScore() {
        return new Filme("FB", "Filme Baixo", 2020, 120,
                List.of(Genero.ROMANCE),
                ClassificacaoEtaria.DOZE, Idioma.INGLES, 50);
    }

    // ---- Testes ----

    @Test
    @DisplayName("deve_RetornarTopN_Quando_ExistemFilmesSuficientes")
    void deve_RetornarTopN_Quando_ExistemFilmesSuficientes() {
        // Arrange
        when(catalogo.buscarTodos()).thenReturn(List.of(filmeAltoScore(), filmeMedioScore(), filmeBaixoScore()));
        when(gerador.sortearInteiro(anyInt(), anyInt())).thenReturn(0);

        // Act
        List<Recomendacao> resultado = service.recomendar(maria, 2);

        // Assert
        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("deve_OrdenarPorScoreDesc_Quando_RecomendacaoTemMultiplosFilmes")
    void deve_OrdenarPorScoreDesc_Quando_RecomendacaoTemMultiplosFilmes() {
        // Arrange
        when(catalogo.buscarTodos()).thenReturn(List.of(filmeBaixoScore(), filmeAltoScore(), filmeMedioScore()));
        when(gerador.sortearInteiro(anyInt(), anyInt())).thenReturn(0);

        // Act
        List<Recomendacao> resultado = service.recomendar(maria, 3);

        // Assert
        assertTrue(resultado.get(0).getScore() >= resultado.get(1).getScore());
        assertTrue(resultado.get(1).getScore() >= resultado.get(2).getScore());
    }

    @Test
    @DisplayName("deve_RetornarListaVazia_Quando_CatalogoEstaVazio")
    void deve_RetornarListaVazia_Quando_CatalogoEstaVazio() {
        // Arrange
        when(catalogo.buscarTodos()).thenReturn(Collections.emptyList());

        // Act
        List<Recomendacao> resultado = service.recomendar(maria, 5);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("deve_NaoDerrubar_Quando_CatalogoLancaExcecao")
    void deve_NaoDerrubar_Quando_CatalogoLancaExcecao() {
        // Arrange
        when(catalogo.buscarTodos()).thenThrow(new RuntimeException("API offline"));

        // Act & Assert
        assertDoesNotThrow(() -> {
            List<Recomendacao> resultado = service.recomendar(maria, 5);
            assertTrue(resultado.isEmpty());
        });

        verify(notificador, never()).enviar(any(), any());
    }

    @Test
    @DisplayName("deve_ChamarRegistrarRecomendacao_Apos_Recomendar")
    void deve_ChamarRegistrarRecomendacao_Apos_Recomendar() {
        // Arrange
        when(catalogo.buscarTodos()).thenReturn(List.of(filmeAltoScore()));
        when(gerador.sortearInteiro(anyInt(), anyInt())).thenReturn(0);

        // Act
        service.recomendar(maria, 5);

        // Assert
        verify(historico, times(1)).registrarRecomendacao(eq(maria), anyList());
    }

    @Test
    @DisplayName("deve_ChamarNotificador_Quando_PushEstaHabilitado")
    void deve_ChamarNotificador_Quando_PushEstaHabilitado() {
        // Arrange — maria tem notificações ativas
        when(catalogo.buscarTodos()).thenReturn(List.of(filmeAltoScore()));
        when(gerador.sortearInteiro(anyInt(), anyInt())).thenReturn(0);

        // Act
        service.recomendar(maria, 5);

        // Assert
        verify(notificador, times(1)).enviar(eq(maria), anyList());
    }

    @Test
    @DisplayName("deve_NaoChamarNotificador_Quando_PushEstaDesligado")
    void deve_NaoChamarNotificador_Quando_PushEstaDesligado() {
        // Arrange — joão tem notificações desativadas
        Usuario joao = UsuarioFactory.joao();
        when(catalogo.buscarTodos()).thenReturn(List.of(filmeAltoScore()));
        when(gerador.sortearInteiro(anyInt(), anyInt())).thenReturn(0);

        // Act
        service.recomendar(joao, 5);

        // Assert
        verify(notificador, never()).enviar(any(), any());
    }

    @Nested
    @DisplayName("Modo Surpreenda-me")
    class SurpreendaMe {

        @Test
        @DisplayName("deve_RetornarFilme_Quando_SurpreendaMeComFilmesFiltrados")
        void deve_RetornarFilme_Quando_SurpreendaMeComFilmesFiltrados() {
            // Arrange
            when(catalogo.buscarTodos()).thenReturn(List.of(filmeAltoScore(), filmeMedioScore()));
            when(gerador.sortearInteiro(0, 2)).thenReturn(1);

            // Act
            Optional<Recomendacao> resultado = service.recomendarAleatorio(maria);

            // Assert
            assertTrue(resultado.isPresent());
        }

        @Test
        @DisplayName("deve_RetornarVazio_Quando_SurpreendaMeSemFilmes")
        void deve_RetornarVazio_Quando_SurpreendaMeSemFilmes() {
            // Arrange
            when(catalogo.buscarTodos()).thenReturn(Collections.emptyList());

            // Act
            Optional<Recomendacao> resultado = service.recomendarAleatorio(maria);

            // Assert
            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    @DisplayName("ArgumentCaptor")
    class ArgumentCaptorTestes {

        @Test
        @DisplayName("deve_RegistrarCorretamente_AsRecomendacoesGeradas")
        void deve_RegistrarCorretamente_AsRecomendacoesGeradas() {
            // Arrange
            when(catalogo.buscarTodos()).thenReturn(List.of(filmeAltoScore(), filmeMedioScore()));
            when(gerador.sortearInteiro(anyInt(), anyInt())).thenReturn(0);

            // Act
            service.recomendar(maria, 2);

            // Assert via ArgumentCaptor
            ArgumentCaptor<List<Recomendacao>> captor = ArgumentCaptor.forClass(List.class);
            verify(historico).registrarRecomendacao(eq(maria), captor.capture());

            List<Recomendacao> registradas = captor.getValue();
            assertAll(
                () -> assertFalse(registradas.isEmpty()),
                () -> assertNotNull(registradas.get(0).getFilme()),
                () -> assertTrue(registradas.get(0).getScore() >= 0)
            );
        }
    }

    @Nested
    @DisplayName("Spy na CalculadoraScore")
    class SpyCalculadora {

        @Test
        @DisplayName("deve_ChamarCalculadora_UmaVezPorFilmeFiltrado")
        void deve_ChamarCalculadora_UmaVezPorFilmeFiltrado() {
            // Arrange — 2 filmes passam no filtro da Maria
            when(catalogo.buscarTodos()).thenReturn(List.of(filmeAltoScore(), filmeMedioScore()));
            when(gerador.sortearInteiro(anyInt(), anyInt())).thenReturn(0);

            // Act
            service.recomendar(maria, 5);

            // Assert — spy confirma que calcular() foi chamado para cada filme filtrado
            verify(calculadora, times(2)).calcular(any(Filme.class), any());
        }
    }
}
