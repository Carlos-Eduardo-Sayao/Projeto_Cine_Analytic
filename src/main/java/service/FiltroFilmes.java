package service;

import model.Filme;
import model.PerfilCinefilo;
import model.enums.Genero;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Aplica regras de exclusão absolutas sobre o catálogo de filmes.
 * Lógica pura — não deve ser mockada nos testes.
 */
public class FiltroFilmes {

    /**
     * Filtra a lista de filmes removendo os que violam regras do perfil.
     *
     * @param filmes lista de filmes do catálogo
     * @param perfil preferências do usuário
     * @return lista de filmes aceitos; nunca null
     */
    public List<Filme> filtrar(List<Filme> filmes, PerfilCinefilo perfil) {
        if (filmes == null || filmes.isEmpty()) {
            return Collections.emptyList();
        }

        return filmes.stream()
                .filter(filme -> !isJaAssistido(filme, perfil))
                .filter(filme -> !isAcimaClassificacao(filme, perfil))
                .filter(filme -> !isIdiomaRecusado(filme, perfil))
                .filter(filme -> !isGeneroRejeitado(filme, perfil))
                .collect(Collectors.toList());
    }

    /** Regra 1: excluir filmes já assistidos. */
    boolean isJaAssistido(Filme filme, PerfilCinefilo perfil) {
        return perfil.jaAssistiu(filme.getId());
    }

    /** Regra 2: excluir filmes acima da classificação etária máxima do perfil. */
    boolean isAcimaClassificacao(Filme filme, PerfilCinefilo perfil) {
        return filme.getClassificacao().getIdadeMinimal() >
               perfil.getClassificacaoMaxima().getIdadeMinimal();
    }

    /** Regra 3: excluir filmes em idioma não aceito. */
    boolean isIdiomaRecusado(Filme filme, PerfilCinefilo perfil) {
        return !perfil.getIdiomasAceitos().contains(filme.getIdioma());
    }

    /** Regra 4: excluir filmes cujo gênero principal tem peso 0.0. */
    boolean isGeneroRejeitado(Filme filme, PerfilCinefilo perfil) {
        return filme.getGeneros().stream()
                .anyMatch(genero -> perfil.getPeso(genero) == 0.0);
    }
}
