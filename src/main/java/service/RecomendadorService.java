package service;

import exception.PerfilIncompletoException;
import model.Filme;
import model.Recomendacao;
import model.Usuario;
import util.GeradorAleatorio;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class RecomendadorService {

    private final CatalogoFilmesAPI catalogo;
    private final HistoricoUsuarioRepository historico;
    private final NotificadorPush notificador;
    private final GeradorAleatorio gerador;
    private final CalculadoraScore calculadora;
    private final FiltroFilmes filtro;

    
    public RecomendadorService(CatalogoFilmesAPI catalogo,
                                HistoricoUsuarioRepository historico,
                                NotificadorPush notificador,
                                GeradorAleatorio gerador,
                                CalculadoraScore calculadora,
                                FiltroFilmes filtro) {
        this.catalogo     = catalogo;
        this.historico    = historico;
        this.notificador  = notificador;
        this.gerador      = gerador;
        this.calculadora  = calculadora;
        this.filtro       = filtro;
    }

    
    public List<Recomendacao> recomendar(Usuario usuario, int topN) {
        validarPerfil(usuario);

        List<Filme> todosFilmes = buscarCatalogoComResiliencia();
        if (todosFilmes.isEmpty()) {
            return Collections.emptyList();
        }

        List<Filme> filmesFiltrados = filtro.filtrar(todosFilmes, usuario.getPerfil());
        if (filmesFiltrados.isEmpty()) {
            return Collections.emptyList();
        }

        List<Recomendacao> recomendacoes = calcularScores(filmesFiltrados, usuario);
        List<Recomendacao> ordenadas     = ordenar(recomendacoes);
        List<Recomendacao> topLista      = ordenadas.stream().limit(topN).collect(Collectors.toList());

        historico.registrarRecomendacao(usuario, topLista);

        if (usuario.isNotificacoesAtivas()) {
            notificador.enviar(usuario, topLista);
        }

        return topLista;
    }

    
    public Optional<Recomendacao> recomendarAleatorio(Usuario usuario) {
        List<Filme> todosFilmes = buscarCatalogoComResiliencia();

        List<Filme> filmesFiltrados = filtro.filtrar(todosFilmes, usuario.getPerfil());
        if (filmesFiltrados.isEmpty()) {
            return Optional.empty();
        }

        int indice = gerador.sortearInteiro(0, filmesFiltrados.size());
        Filme filmeSorteado = filmesFiltrados.get(indice);
        Recomendacao recomendacao = new Recomendacao(filmeSorteado, 0, "Surpreenda-me!");

        historico.registrarRecomendacao(usuario, List.of(recomendacao));
        return Optional.of(recomendacao);
    }

    private void validarPerfil(Usuario usuario) {
        if (usuario.getPerfil() == null) {
            throw new PerfilIncompletoException("O usuário " + usuario.getNome() + " não possui perfil configurado.");
        }
        if (usuario.getPerfil().getIdiomasAceitos().isEmpty()) {
            throw new PerfilIncompletoException("O perfil deve ter ao menos um idioma aceito.");
        }
    }

    private List<Filme> buscarCatalogoComResiliencia() {
        try {
            return catalogo.buscarTodos();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private List<Recomendacao> calcularScores(List<Filme> filmes, Usuario usuario) {
        return filmes.stream()
                .map(filme -> calculadora.calcular(filme, usuario.getPerfil()))
                .collect(Collectors.toList());
    }

    private List<Recomendacao> ordenar(List<Recomendacao> recomendacoes) {
        return recomendacoes.stream()
                .sorted(Comparator
                        .comparingDouble(Recomendacao::getScore).reversed()
                        .thenComparingInt((Recomendacao r) -> r.getFilme().getPopularidade()).reversed()
                        .thenComparingInt(r -> gerador.sortearInteiro(0, 100)))
                .collect(Collectors.toList());
    }
}
