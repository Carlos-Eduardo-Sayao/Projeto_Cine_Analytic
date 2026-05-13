package service;

import model.Filme;
import model.enums.ClassificacaoEtaria;
import model.enums.Genero;
import model.enums.Idioma;

import java.util.List;


public class CatalogoMock implements CatalogoFilmesAPI {

    @Override
    public List<Filme> buscarTodos() {
        return List.of(
            new Filme("F01", "Duna: Parte Dois",      2024, 166, List.of(Genero.FICCAO_CIENTIFICA, Genero.DRAMA),                ClassificacaoEtaria.QUATORZE,  Idioma.INGLES,     92),
            new Filme("F02", "Ela (Her)",              2013, 126, List.of(Genero.FICCAO_CIENTIFICA, Genero.DRAMA, Genero.ROMANCE), ClassificacaoEtaria.DEZESSEIS, Idioma.INGLES,     78),
            new Filme("F03", "O Iluminado",            1980, 146, List.of(Genero.TERROR),                                         ClassificacaoEtaria.DEZOITO,   Idioma.INGLES,     88),
            new Filme("F04", "Interestelar",           2014, 169, List.of(Genero.FICCAO_CIENTIFICA, Genero.DRAMA),                ClassificacaoEtaria.DOZE,      Idioma.INGLES,     95),
            new Filme("F05", "Tropa de Elite",         2007, 115, List.of(Genero.ACAO, Genero.DRAMA),                            ClassificacaoEtaria.DEZOITO,   Idioma.PORTUGUES,  80),
            new Filme("F06", "Click",                  2006, 107, List.of(Genero.COMEDIA, Genero.DRAMA),                         ClassificacaoEtaria.DOZE,      Idioma.INGLES,     65),
            new Filme("F07", "A Chegada",              2016, 116, List.of(Genero.FICCAO_CIENTIFICA, Genero.DRAMA),               ClassificacaoEtaria.DOZE,      Idioma.INGLES,     84),
            new Filme("F08", "Matrix",                 1999, 136, List.of(Genero.FICCAO_CIENTIFICA, Genero.ACAO),                ClassificacaoEtaria.QUATORZE,  Idioma.INGLES,     96),
            new Filme("F09", "Blade Runner 2049",      2017, 164, List.of(Genero.FICCAO_CIENTIFICA, Genero.DRAMA),               ClassificacaoEtaria.DEZESSEIS, Idioma.INGLES,     80),
            new Filme("F10", "Parasita",               2019, 132, List.of(Genero.DRAMA, Genero.TERROR),                         ClassificacaoEtaria.DEZESSEIS, Idioma.JAPONES,    91),
            new Filme("F11", "Whiplash",               2014,  107, List.of(Genero.DRAMA),                                        ClassificacaoEtaria.QUATORZE,  Idioma.INGLES,     87),
            new Filme("F12", "Gravidade",              2013,  91, List.of(Genero.FICCAO_CIENTIFICA, Genero.DRAMA),               ClassificacaoEtaria.DOZE,      Idioma.INGLES,     78),
            new Filme("F13", "Oppenheimer",            2023, 180, List.of(Genero.DRAMA),                                         ClassificacaoEtaria.DEZESSEIS, Idioma.INGLES,     93),
            new Filme("F14", "O Senhor dos Anéis",     2001, 178, List.of(Genero.ACAO, Genero.DRAMA),                           ClassificacaoEtaria.DOZE,      Idioma.INGLES,     97),
            new Filme("F15", "Divertida Mente",        2015,  95, List.of(Genero.COMEDIA, Genero.DRAMA),                        ClassificacaoEtaria.LIVRE,     Idioma.INGLES,     88),
            new Filme("F16", "Amadeus",                1984, 160, List.of(Genero.DRAMA),                                         ClassificacaoEtaria.DOZE,      Idioma.INGLES,     85),
            new Filme("F17", "Pulp Fiction",           1994, 154, List.of(Genero.DRAMA, Genero.ACAO),                           ClassificacaoEtaria.DEZOITO,   Idioma.INGLES,     92),
            new Filme("F18", "Corra!",                 2017, 104, List.of(Genero.TERROR, Genero.DRAMA),                         ClassificacaoEtaria.DEZESSEIS, Idioma.INGLES,     79),
            new Filme("F19", "La La Land",             2016, 128, List.of(Genero.ROMANCE, Genero.DRAMA),                        ClassificacaoEtaria.LIVRE,     Idioma.INGLES,     81),
            new Filme("F20", "Vingadores: Ultimato",   2019, 181, List.of(Genero.ACAO, Genero.FICCAO_CIENTIFICA),               ClassificacaoEtaria.DOZE,      Idioma.INGLES,     98),
            new Filme("F21", "Clube da Luta",          1999, 139, List.of(Genero.DRAMA, Genero.ACAO),                           ClassificacaoEtaria.DEZOITO,   Idioma.INGLES,     88),
            new Filme("F22", "Cidadão Kane",           1941, 119, List.of(Genero.DRAMA),                                         ClassificacaoEtaria.LIVRE,     Idioma.INGLES,     82),
            new Filme("F23", "O Poderoso Chefão",      1972, 175, List.of(Genero.DRAMA, Genero.ACAO),                           ClassificacaoEtaria.DEZESSEIS, Idioma.INGLES,     96),
            new Filme("F24", "Meu Malvado Favorito",   2010,  95, List.of(Genero.COMEDIA),                                      ClassificacaoEtaria.LIVRE,     Idioma.INGLES,     76),
            new Filme("F25", "O Labirinto do Fauno",   2006, 118, List.of(Genero.DRAMA, Genero.TERROR),                         ClassificacaoEtaria.DEZESSEIS, Idioma.ESPANHOL,   83),
            new Filme("F26", "Amélie Poulain",         2001, 122, List.of(Genero.ROMANCE, Genero.COMEDIA),                      ClassificacaoEtaria.DOZE,      Idioma.FRANCES,    87),
            new Filme("F27", "Rashomon",               1950,  88, List.of(Genero.DRAMA),                                         ClassificacaoEtaria.LIVRE,     Idioma.JAPONES,    80),
            new Filme("F28", "Coringa",                2019, 122, List.of(Genero.DRAMA),                                         ClassificacaoEtaria.DEZESSEIS, Idioma.INGLES,     85),
            new Filme("F29", "Bacurau",                2019, 131, List.of(Genero.DRAMA, Genero.TERROR),                         ClassificacaoEtaria.DEZESSEIS, Idioma.PORTUGUES,  79),
            new Filme("F30", "Narradores de Javé",     2003, 100, List.of(Genero.COMEDIA, Genero.DRAMA),                        ClassificacaoEtaria.DOZE,      Idioma.PORTUGUES,  72),
            new Filme("F31", "Central do Brasil",      1998, 110, List.of(Genero.DRAMA),                                         ClassificacaoEtaria.DOZE,      Idioma.PORTUGUES,  84),
            new Filme("F32", "Tenet",                  2020, 150, List.of(Genero.FICCAO_CIENTIFICA, Genero.ACAO),               ClassificacaoEtaria.DOZE,      Idioma.INGLES,     74)
        );
    }
}
