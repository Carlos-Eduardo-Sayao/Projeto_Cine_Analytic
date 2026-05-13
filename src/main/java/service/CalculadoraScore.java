package service;

import model.Filme;
import model.PerfilCinefilo;
import model.Recomendacao;
import model.enums.Genero;


public class CalculadoraScore {

    static final double PESO_GENERO       = 0.50;
    static final double PESO_DURACAO      = 0.20;
    static final double PESO_POPULARIDADE = 0.15;
    static final double PESO_AFINIDADE    = 0.15;

    
    public Recomendacao calcular(Filme filme, PerfilCinefilo perfil) {
        double componenteGenero       = calcularComponenteGenero(filme, perfil);
        double componenteDuracao      = calcularComponenteDuracao(filme, perfil);
        double componentePopularidade = calcularComponentePopularidade(filme);
        double componenteAfinidade    = calcularBonusAfinidade(filme, perfil);

        double score = (componenteGenero       * PESO_GENERO)
                     + (componenteDuracao      * PESO_DURACAO)
                     + (componentePopularidade * PESO_POPULARIDADE)
                     + (componenteAfinidade    * PESO_AFINIDADE);

        double scoreFinal = Math.min(100.0, Math.max(0.0, score));
        String justificativa = gerarJustificativa(filme, perfil, componenteGenero);

        return new Recomendacao(filme, scoreFinal, justificativa);
    }

    
    double calcularComponenteGenero(Filme filme, PerfilCinefilo perfil) {
        double somaPesos = filme.getGeneros().stream()
                .mapToDouble(perfil::getPeso)
                .sum();
        double media = somaPesos / filme.getGeneros().size();
        return media * 100.0;
    }

    
    double calcularComponenteDuracao(Filme filme, PerfilCinefilo perfil) {
        int duracao = filme.getDuracao();
        int minima  = perfil.getDuracaoMinima();
        int maxima  = perfil.getDuracaoMaxima();

        if (duracao >= minima && duracao <= maxima) {
            return 100.0;
        }

        int desvio = duracao < minima ? minima - duracao : duracao - maxima;
        double penalidade = (desvio / 30.0) * 20.0;
        return Math.max(0.0, 100.0 - penalidade);
    }

    
    double calcularComponentePopularidade(Filme filme) {
        return Math.min(100.0, filme.getPopularidade());
    }

    
    double calcularBonusAfinidade(Filme filme, PerfilCinefilo perfil) {
        if (perfil.getNotas().isEmpty()) {
            return 50.0;
        }

        double somaBonusGenero = filme.getGeneros().stream()
                .mapToDouble(genero -> calcularBonusPorGenero(genero, perfil))
                .average()
                .orElse(50.0);

        return somaBonusGenero;
    }

    private double calcularBonusPorGenero(Genero genero, PerfilCinefilo perfil) {
        return perfil.getNotas().values().stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(50.0) * 20.0; // (média 0–5 → 0–100)
    }

    private String gerarJustificativa(Filme filme, PerfilCinefilo perfil, double componenteGenero) {
        StringBuilder sb = new StringBuilder();
        sb.append("Recomendamos '").append(filme.getTitulo()).append("' porque ");

        if (componenteGenero >= 70) {
            sb.append("os gêneros combinam muito bem com seu perfil");
        } else if (componenteGenero >= 40) {
            sb.append("os gêneros têm boa compatibilidade com seu perfil");
        } else {
            sb.append("pode ser uma descoberta interessante para você");
        }

        sb.append(". Popularidade: ").append(filme.getPopularidade()).append("/100.");
        return sb.toString();
    }
}
