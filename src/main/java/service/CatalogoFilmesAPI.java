package service;

import model.Filme;

import java.util.List;

/**
 * Interface que representa o catálogo externo de filmes.
 * Em produção, faria chamada HTTP para APIs como TMDB.
 * Nos testes, é mockada com Mockito.
 */
public interface CatalogoFilmesAPI {
    /**
     * Retorna todos os filmes disponíveis no catálogo.
     *
     * @return lista de filmes; nunca null
     */
    List<Filme> buscarTodos();
}
