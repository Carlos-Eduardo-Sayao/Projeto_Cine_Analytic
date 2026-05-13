package util;

/**
 * Interface para sorteio de inteiros aleatórios.
 * Mockável nos testes para garantir determinismo.
 */
public interface GeradorAleatorio {
    /**
     * Sorteia um inteiro entre min (inclusivo) e max (exclusivo).
     *
     * @param min valor mínimo (inclusivo)
     * @param max valor máximo (exclusivo)
     * @return inteiro sorteado
     */
    int sortearInteiro(int min, int max);
}
