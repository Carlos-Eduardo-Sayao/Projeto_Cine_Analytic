package model.enums;

/**
 * Enum com as classificações etárias possíveis para um filme.
 */
public enum ClassificacaoEtaria {
    LIVRE(0),
    DEZ(10),
    DOZE(12),
    QUATORZE(14),
    DEZESSEIS(16),
    DEZOITO(18);

    private final int idadeMinima;

    ClassificacaoEtaria(int idadeMinima) {
        this.idadeMinima = idadeMinima;
    }

    /** Retorna a idade mínima necessária para esta classificação. */
    public int getIdadeMinimal() {
        return idadeMinima;
    }
}
