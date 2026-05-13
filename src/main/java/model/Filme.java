package model;

import model.enums.ClassificacaoEtaria;
import model.enums.Genero;
import model.enums.Idioma;

import java.util.List;
import java.util.Objects;

/**
 * Representa um filme do catálogo. Imutável por design.
 */
public final class Filme {

    private final String id;
    private final String titulo;
    private final int ano;
    private final int duracao;
    private final List<Genero> generos;
    private final ClassificacaoEtaria classificacao;
    private final Idioma idioma;
    private final int popularidade;

    public Filme(String id, String titulo, int ano, int duracao,
                 List<Genero> generos, ClassificacaoEtaria classificacao,
                 Idioma idioma, int popularidade) {
        this.id = id;
        this.titulo = titulo;
        this.ano = ano;
        this.duracao = duracao;
        this.generos = List.copyOf(generos);
        this.classificacao = classificacao;
        this.idioma = idioma;
        this.popularidade = popularidade;
    }

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public int getAno() { return ano; }
    public int getDuracao() { return duracao; }
    public List<Genero> getGeneros() { return generos; }
    public ClassificacaoEtaria getClassificacao() { return classificacao; }
    public Idioma getIdioma() { return idioma; }
    public int getPopularidade() { return popularidade; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Filme)) return false;
        Filme outro = (Filme) obj;
        return Objects.equals(id, outro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Filme{id='" + id + "', titulo='" + titulo + "'}";
    }
}
