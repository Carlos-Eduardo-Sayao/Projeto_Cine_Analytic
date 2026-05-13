package service;

import model.Recomendacao;
import model.Usuario;

import java.util.List;

/**
 * Interface de persistência do histórico do usuário.
 * Em produção, acessa banco de dados. Nos testes, é mockada.
 */
public interface HistoricoUsuarioRepository {
    /**
     * Registra a lista de recomendações feitas ao usuário.
     *
     * @param usuario        usuário que recebeu as recomendações
     * @param recomendacoes  lista de recomendações geradas
     */
    void registrarRecomendacao(Usuario usuario, List<Recomendacao> recomendacoes);

    /**
     * Busca o histórico de filmes assistidos pelo usuário.
     *
     * @param usuario usuário consultado
     * @return lista de IDs de filmes assistidos
     */
    List<String> buscarHistorico(Usuario usuario);
}
