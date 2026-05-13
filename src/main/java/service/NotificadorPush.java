package service;

import model.Recomendacao;
import model.Usuario;

import java.util.List;


public interface NotificadorPush {
    
    void enviar(Usuario usuario, List<Recomendacao> recomendacoes);
}
