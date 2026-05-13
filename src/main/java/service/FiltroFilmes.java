package service;

import model.Filme;
import model.PerfilCinefilo;
import model.enums.Genero;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class FiltroFilmes {

    
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

    
    boolean isJaAssistido(Filme filme, PerfilCinefilo perfil) {
        return perfil.jaAssistiu(filme.getId());
    }

    
    boolean isAcimaClassificacao(Filme filme, PerfilCinefilo perfil) {
        return filme.getClassificacao().getIdadeMinimal() >
               perfil.getClassificacaoMaxima().getIdadeMinimal();
    }

    
    boolean isIdiomaRecusado(Filme filme, PerfilCinefilo perfil) {
        return !perfil.getIdiomasAceitos().contains(filme.getIdioma());
    }

    
    boolean isGeneroRejeitado(Filme filme, PerfilCinefilo perfil) {
        return filme.getGeneros().stream()
                .anyMatch(genero -> perfil.getPeso(genero) == 0.0);
    }
}
