package service;

import model.Recomendacao;
import model.Usuario;

import java.util.List;


public interface HistoricoUsuarioRepository {
    
    void registrarRecomendacao(Usuario usuario, List<Recomendacao> recomendacoes);

    
    List<String> buscarHistorico(Usuario usuario);
}
