package service;

import model.Recomendacao;
import model.Usuario;

import java.util.List;

/**
 * Interface para envio de notificações push ao usuário.
 * Em produção, integra com Firebase ou OneSignal. Nos testes, é mockada.
 */
public interface NotificadorPush {
    /**
     * Envia notificação push informando que as recomendações estão prontas.
     *
     * @param usuario       usuário destinatário
     * @param recomendacoes recomendações geradas
     */
    void enviar(Usuario usuario, List<Recomendacao> recomendacoes);
}
