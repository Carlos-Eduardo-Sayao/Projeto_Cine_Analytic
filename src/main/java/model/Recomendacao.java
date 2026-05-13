package model;

/**
 * Resultado de uma recomendação: filme + score + justificativa. Imutável.
 */
public final class Recomendacao {

    private final Filme filme;
    private final double score;
    private final String justificativa;

    public Recomendacao(Filme filme, double score, String justificativa) {
        this.filme = filme;
        this.score = score;
        this.justificativa = justificativa;
    }

    public Filme getFilme() { return filme; }
    public double getScore() { return score; }
    public String getJustificativa() { return justificativa; }

    @Override
    public String toString() {
        return "Recomendacao{filme=" + filme.getTitulo() +
               ", score=" + String.format("%.1f", score) + "}";
    }
}
