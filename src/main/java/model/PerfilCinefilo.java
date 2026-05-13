package model;

import exception.DuracaoInvalidaException;
import exception.PesoInvalidoException;
import model.enums.ClassificacaoEtaria;
import model.enums.Genero;
import model.enums.Idioma;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Representa as preferências cinematográficas de um usuário.
 */
public class PerfilCinefilo {

    private final Map<Genero, Double> pesosGenero;
    private int duracaoMinima;
    private int duracaoMaxima;
    private ClassificacaoEtaria classificacaoMaxima;
    private final List<Idioma> idiomasAceitos;
    private final List<String> historico;
    private final Map<String, Integer> notas;

    public PerfilCinefilo() {
        this.pesosGenero = new HashMap<>();
        this.idiomasAceitos = new ArrayList<>();
        this.historico = new ArrayList<>();
        this.notas = new HashMap<>();
        this.duracaoMinima = 0;
        this.duracaoMaxima = Integer.MAX_VALUE;
        this.classificacaoMaxima = ClassificacaoEtaria.DEZOITO;
    }

    /**
     * Define o peso de preferência para um gênero.
     *
     * @param genero gênero a configurar
     * @param peso   valor entre 0.0 e 1.0
     * @throws PesoInvalidoException se o peso estiver fora do intervalo [0.0, 1.0]
     */
    public void setPeso(Genero genero, double peso) {
        if (peso < 0.0 || peso > 1.0) {
            throw new PesoInvalidoException(
                "Peso inválido para " + genero + ": " + peso + ". Deve estar entre 0.0 e 1.0."
            );
        }
        pesosGenero.put(genero, peso);
    }

    public double getPeso(Genero genero) {
        return pesosGenero.getOrDefault(genero, 0.5);
    }

    /**
     * Define a faixa de duração preferida.
     *
     * @throws DuracaoInvalidaException se mínimo for maior que máximo
     */
    public void setFaixaDuracao(int minima, int maxima) {
        if (minima > maxima) {
            throw new DuracaoInvalidaException(
                "Duração mínima (" + minima + ") não pode ser maior que a máxima (" + maxima + ")."
            );
        }
        this.duracaoMinima = minima;
        this.duracaoMaxima = maxima;
    }

    public int getDuracaoMinima() { return duracaoMinima; }
    public int getDuracaoMaxima() { return duracaoMaxima; }

    public void setClassificacaoMaxima(ClassificacaoEtaria classificacao) {
        this.classificacaoMaxima = classificacao;
    }

    public ClassificacaoEtaria getClassificacaoMaxima() { return classificacaoMaxima; }

    public void adicionarIdioma(Idioma idioma) {
        if (!idiomasAceitos.contains(idioma)) {
            idiomasAceitos.add(idioma);
        }
    }

    public List<Idioma> getIdiomasAceitos() {
        return Collections.unmodifiableList(idiomasAceitos);
    }

    /**
     * Marca um filme como assistido no histórico.
     *
     * @param idFilme identificador do filme
     */
    public void marcarComoAssistido(String idFilme) {
        if (!historico.contains(idFilme)) {
            historico.add(idFilme);
        }
    }

    public boolean jaAssistiu(String idFilme) {
        return historico.contains(idFilme);
    }

    public List<String> getHistorico() {
        return Collections.unmodifiableList(historico);
    }

    /**
     * Adiciona uma nota para um filme.
     *
     * @param idFilme identificador do filme
     * @param nota    valor entre 1 e 5
     * @throws IllegalArgumentException se a nota estiver fora de [1, 5]
     */
    public void adicionarNota(String idFilme, int nota) {
        if (nota < 1 || nota > 5) {
            throw new IllegalArgumentException(
                "Nota inválida: " + nota + ". Deve estar entre 1 e 5."
            );
        }
        notas.put(idFilme, nota);
    }

    public Integer getNotaPara(String idFilme) {
        return notas.get(idFilme);
    }

    public Map<String, Integer> getNotas() {
        return Collections.unmodifiableMap(notas);
    }

    public boolean isNotificacoesAtivas() {
        return true;
    }
}
