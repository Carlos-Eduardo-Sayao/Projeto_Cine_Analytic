# 🎬 CineAnalytic — Recomendador de Filmes por Perfil

> *"Análise inteligente. Cinema sob medida."*

Sistema de recomendação de filmes baseado em perfil cinéfilo, desenvolvido como projeto da disciplina **Teste de Software 2026** na Universidade Católica do Salvador (UCSAL). Implementa filtragem por regras de negócio, cálculo de score ponderado com justificativa textual, ranqueamento com desempate por popularidade, modo "Surpreenda-me" e cobertura completa de testes unitários usando **JUnit 5** e **Mockito**.

[![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)](https://openjdk.org/projects/jdk/17/)
[![Maven](https://img.shields.io/badge/Maven-3.x-C71A36?logo=apachemaven)](https://maven.apache.org/)
[![JUnit](https://img.shields.io/badge/JUnit-5.10.2-25A162?logo=junit5)](https://junit.org/junit5/)
[![Mockito](https://img.shields.io/badge/Mockito-5.11.0-78A641)](https://site.mockito.org/)
[![JaCoCo](https://img.shields.io/badge/JaCoCo-0.8.11-D11A2A)](https://www.jacoco.org/)
[![Tests](https://img.shields.io/badge/tests-37%20passing-success)](#-execução-dos-testes)

---

## 📋 Sumário

- [Visão geral](#-visão-geral)
- [Como funciona — exemplo de uso](#-como-funciona--exemplo-de-uso)
- [Diagrama de classes](#-diagrama-de-classes)
- [Diagrama de sequência](#-diagrama-de-sequência)
- [Estrutura do projeto](#-estrutura-do-projeto)
- [Stack técnica](#-stack-técnica)
- [Como rodar](#-como-rodar)
- [Execução dos testes](#-execução-dos-testes)
- [Cobertura JaCoCo](#-cobertura-jacoco)
- [Cenários de teste implementados](#-cenários-de-teste-implementados)
- [Decisões de design](#-decisões-de-design)
- [Autores](#-autores)

---

## 🎯 Visão geral

O sistema recomenda filmes para um usuário com base no seu **perfil cinéfilo**:

- 🎭 Gêneros preferidos (com pesos de `0.0` a `1.0`)
- ⏱️ Faixa de duração ideal (mínima e máxima em minutos)
- 🔞 Classificação etária aceitável
- 🌍 Idiomas aceitos
- 📜 Histórico do que já assistiu (IDs de filmes)
- ⭐ Notas dadas para outros filmes (escala de 1 a 5)

O `RecomendadorService` cruza o perfil com um catálogo de filmes e devolve uma **lista ranqueada** dos melhores candidatos, **com justificativa textual** para cada recomendação.

A mecânica central é:

1. **Validar** o perfil do usuário (lança `PerfilIncompletoException` se inválido)
2. **Filtrar** filmes que violam regras absolutas (4 regras)
3. **Pontuar** cada filme restante com score `0–100` ponderado por 4 componentes
4. **Ranquear** por score (desempate por popularidade e, em último caso, sorteio aleatório)
5. **Registrar** no histórico e disparar notificação push (se ativada pelo usuário)

---

## 🎬 Como funciona — exemplo de uso

**Cenário:** Maria, 28 anos, cria seu perfil e quer ver o que o sistema recomenda para hoje à noite.

### Entrada — Perfil da Maria

| Campo | Valor |
|---|---|
| Nome / Idade | Maria, 28 anos |
| Duração preferida | 90 a 150 minutos |
| Classificação máxima | 16 anos (DEZESSEIS) |
| Idiomas aceitos | Português, Inglês |
| Já assistiu | F04 (Interestelar), F08 (Matrix) |
| Notas dadas | F09 (Blade Runner 2049): ★★★★★ \| F32: ★★ |
| Notificações | Ativas |

**Pesos por gênero:**

| Gênero | Peso |
|---|---|
| Ficção Científica | 0.9 |
| Drama | 0.6 |
| Comédia | 0.5 |
| Romance | 0.4 |
| Terror | 0.0 *(rejeitado)* |

### Passo 1 — Validação do perfil

`RecomendadorService.validarPerfil()` confirma:
- ✅ Perfil não é null
- ✅ Pelo menos um idioma aceito está configurado

Se algum falhar → **`PerfilIncompletoException`**.

### Passo 2 — Busca do catálogo (com resiliência)

`CatalogoMock.buscarTodos()` retorna **32 filmes**. Em caso de exceção da API (em produção, HTTP), o `RecomendadorService` captura o erro e devolve `Collections.emptyList()`.

### Passo 3 — Filtragem (4 regras)

`FiltroFilmes.filtrar()` aplica as regras:

- ❌ **F04 Interestelar** — já assistiu (`isJaAssistido`)
- ❌ **F08 Matrix** — já assistiu
- ❌ **F03 O Iluminado** — classificação `18 > 16` (`isAcimaClassificacao`)
- ❌ **F10 Parasita** — idioma JAPONES não aceito (`isIdiomaRecusado`)
- ❌ Qualquer filme com gênero TERROR — peso `0.0` (`isGeneroRejeitado`)

### Passo 4 — Cálculo de score

Cada filme restante é avaliado pela `CalculadoraScore.calcular()`, que retorna uma `Recomendacao` contendo:
- **Filme**
- **Score** entre `0.0` e `100.0`, ponderado em 4 componentes
- **Justificativa textual** gerada com base no componente de gênero

### Passo 5 — Ranqueamento

Ordenado em **3 níveis**:

1. **Score descendente** (`Comparator.comparingDouble(Recomendacao::getScore).reversed()`)
2. **Popularidade descendente** em caso de empate
3. **Sorteio aleatório** via `GeradorAleatorio.sortearInteiro(0, 100)` no último caso

### Passo 6 — Registro e notificação

- `historico.registrarRecomendacao(maria, top5)` — persiste para evitar repetição
- Se `usuario.isNotificacoesAtivas()` → `notificador.enviar(maria, top5)`

### Saída final

Maria recebe um `List<Recomendacao>` com até 5 itens. Cada item traz: filme, score numérico e justificativa textual personalizada.

---

## 🧮 A fórmula de score

```
score = (componenteGenero       × 0.50)
      + (componenteDuracao      × 0.20)
      + (componentePopularidade × 0.15)
      + (componenteAfinidade    × 0.15)
```

| Componente | Peso | Cálculo |
|---|---|---|
| **Gênero** | 50% | Média dos pesos dos gêneros do filme no perfil × 100 |
| **Duração** | 20% | `100` se dentro da faixa preferida; reduz com penalidade `(desvio/30) × 20` para cada minuto fora |
| **Popularidade** | 15% | Valor direto do filme (já é 0–100) |
| **Afinidade histórica** | 15% | Média das notas dadas pelo usuário × 20 (fallback `50.0` se sem histórico) |

Score final é sempre limitado: `Math.min(100, Math.max(0, score))`.

### Geração de justificativa

A `CalculadoraScore.gerarJustificativa()` produz um texto baseado no componente de gênero:

| Faixa | Justificativa |
|---|---|
| `>= 70` | *"os gêneros combinam muito bem com seu perfil"* |
| `40 – 70` | *"os gêneros têm boa compatibilidade com seu perfil"* |
| `< 40` | *"pode ser uma descoberta interessante para você"* |

E sempre adiciona: `". Popularidade: X/100."`

---

## 📐 Diagrama de classes

![Diagrama de Classes](docs/diagrama_classes.png)

**Convenções visuais:**

- 🟢 **Verde (service):** classes concretas de orquestração e lógica pura — `RecomendadorService`, `CalculadoraScore`, `FiltroFilmes`
- 🟡 **Amarelo (model):** objetos de domínio — `Filme` e `Recomendacao` são **imutáveis** (`final`)
- 🔵 **Azul (interfaces):** fronteiras com o mundo externo, **mockadas nos testes** — `CatalogoFilmesAPI`, `HistoricoUsuarioRepository`, `NotificadorPush`, `GeradorAleatorio`
- 🟣 **Roxo (enums):** valores fixos — `Genero` (7 valores), `ClassificacaoEtaria` (6 valores), `Idioma` (5 valores)
- 🔴 **Vermelho (exception):** exceções tipadas — `PesoInvalidoException`, `DuracaoInvalidaException`, `PerfilIncompletoException`

**Pontos arquiteturais importantes:**

- `RecomendadorService` recebe **todas as 6 dependências por construtor** (injeção de dependências)
- `Filme` e `Recomendacao` são **imutáveis** — todos os campos `final`, sem setters
- `Filme` implementa `equals`/`hashCode` baseados apenas no `id`
- `CalculadoraScore` tem **4 constantes nomeadas** para os pesos (`PESO_GENERO = 0.50` etc.) — sem números mágicos
- `FiltroFilmes` aplica **4 regras** de filtragem independentes (assistido / classificação / idioma / gênero rejeitado)

---

## 🔄 Diagrama de sequência

![Diagrama de Sequência](docs/diagrama_sequencia.png)

O diagrama cobre **dois fluxos**:

### Fluxo Principal — `recomendar(usuario, topN)`

1. Usuário chama `service.recomendar(maria, 5)`
2. `validarPerfil()` — verifica perfil completo (fluxo `alt` lança `PerfilIncompletoException`)
3. `buscarCatalogoComResiliencia()` chama `CatalogoFilmesAPI.buscarTodos()`
4. Se o catálogo lança exceção → captura e devolve `Collections.emptyList()` (fluxo `alt`)
5. Aplica as 4 regras do `FiltroFilmes`
6. Se filtrados está vazio → devolve lista vazia
7. Para cada filme filtrado, chama `CalculadoraScore.calcular()` que retorna `Recomendacao`
8. Ordena por score DESC, popularidade DESC e sorteio aleatório (último critério)
9. Limita ao `topN`
10. Persiste via `HistoricoUsuarioRepository.registrarRecomendacao()`
11. Se `usuario.isNotificacoesAtivas()` → dispara `NotificadorPush.enviar()`
12. Devolve `List<Recomendacao>` ao usuário

### Fluxo Alternativo — `recomendarAleatorio(usuario)` ("Surpreenda-me")

1. Usuário chama `service.recomendarAleatorio(maria)`
2. Mesma busca + filtragem
3. Se vazio → `Optional.empty()`
4. Sorteia um índice via `GeradorAleatorio.sortearInteiro(0, tamanhoLista)`
5. Cria `Recomendacao(filme, 0, "Surpreenda-me!")`
6. Registra no histórico
7. Devolve `Optional.of(recomendacao)`

---

## 📂 Estrutura do projeto

```
cineanalytic/
├── pom.xml                                   ← configuração Maven
├── README.md
├── docs/
│   ├── diagrama_classes.png
│   └── diagrama_sequencia.png
└── src/
    ├── main/java/
    │   ├── Main.java                         ← ponto de entrada executável
    │   ├── exception/
    │   │   ├── DuracaoInvalidaException.java
    │   │   ├── PerfilIncompletoException.java
    │   │   └── PesoInvalidoException.java
    │   ├── model/
    │   │   ├── Filme.java                    ← imutável (final + equals por id)
    │   │   ├── PerfilCinefilo.java
    │   │   ├── Recomendacao.java             ← imutável
    │   │   ├── Usuario.java                  ← imutável
    │   │   └── enums/
    │   │       ├── ClassificacaoEtaria.java  ← 6 valores
    │   │       ├── Genero.java               ← 7 valores
    │   │       └── Idioma.java               ← 5 valores
    │   ├── service/
    │   │   ├── CalculadoraScore.java         ← lógica pura (gera justificativa)
    │   │   ├── CatalogoFilmesAPI.java        ← «interface»
    │   │   ├── CatalogoMock.java             ← impl. hardcoded (32 filmes)
    │   │   ├── FiltroFilmes.java             ← lógica pura (4 regras)
    │   │   ├── HistoricoUsuarioRepository.java ← «interface»
    │   │   ├── NotificadorPush.java          ← «interface»
    │   │   └── RecomendadorService.java      ← orquestrador (6 dependências)
    │   └── util/
    │       └── GeradorAleatorio.java         ← «interface»
    └── test/java/
        ├── CalculadoraScoreTest.java         ← 7 testes (com @Nested)
        ├── FilmeTest.java                    ← 3 testes
        ├── FiltroFilmesTest.java             ← 7 testes
        ├── PerfilCinefiloTest.java           ← 9 testes
        ├── RecomendadorServiceTest.java      ← 11 testes (com @Nested)
        └── UsuarioFactory.java               ← factory (maria + joão)
```

**Total:** 19 classes em `main/` + 5 classes em `test/` (incluindo factory) = **24 arquivos Java + 37 testes**

---

## 🛠️ Stack técnica

| Tecnologia | Versão | Uso |
|---|---|---|
| **Java** | 17 | Linguagem base |
| **Maven** | 3.x | Gerenciamento de dependências e build |
| **JUnit Jupiter** | 5.10.2 | Framework de testes — `@Test`, `@DisplayName`, `@BeforeEach`, `@Nested`, `@Tag` |
| **Mockito JUnit Jupiter** | 5.11.0 | Anotações `@Mock`, `@Spy`, `@ExtendWith(MockitoExtension.class)` + `ArgumentCaptor` |
| **Maven Surefire** | 3.2.5 | Plugin Maven para execução dos testes |
| **JaCoCo** | 0.8.11 | Relatório de cobertura de testes |

**Group ID:** `br.cineanalytic`
**Artifact ID:** `cineanalytic`
**Version:** `1.0-SNAPSHOT`

---

## 🚀 Como rodar

### Pré-requisitos

- **JDK 17** ou superior — verifique com `java -version`
- **Maven 3.6+** (opcional se usar Eclipse com Maven embutido) — `mvn -version`
- **Eclipse IDE**, **IntelliJ IDEA** ou outro IDE com suporte a Maven

### Opção 1 — Via Eclipse

1. Clone o repositório:
   ```bash
   git clone https://github.com/[seu-usuario]/cineanalytic.git
   ```
2. No Eclipse: **File → Import → Maven → Existing Maven Projects**
3. Selecione a pasta `cineanalytic` e clique em **Finish**
4. Aguarde o Maven baixar as dependências (`Maven Dependencies` aparecerá na árvore)
5. Para rodar a demonstração: **botão direito em `Main.java` → Run As → Java Application**

### Opção 2 — Via terminal

```bash
git clone https://github.com/[seu-usuario]/cineanalytic.git
cd cineanalytic
mvn clean compile

# rodar a demonstração
mvn exec:java -Dexec.mainClass="Main"
```

### Saída esperada da demonstração

```
═══════════════════════════════════════════════
       CineAnalytic — Sistema de Recomendação
═══════════════════════════════════════════════
Usuário: Maria (28 anos)

>>> Top 5 recomendações para Maria:
-----------------------------------------------
1. A Chegada                     | Score:  87.5
   Recomendamos 'A Chegada' porque os gêneros combinam muito bem com seu perfil. Popularidade: 84/100.

2. Duna: Parte Dois              | Score:  82.1
   Recomendamos 'Duna: Parte Dois' porque os gêneros combinam muito bem com seu perfil. Popularidade: 92/100.

...

[Histórico] 5 recomendação(ões) registrada(s) para Maria
[Push] Notificação enviada para Maria com 5 sugestão(ões).

-----------------------------------------------
>>> Modo 'Surpreenda-me':
    Blade Runner 2049 (2017)
═══════════════════════════════════════════════
```

---

## 🧪 Execução dos testes

### Via Eclipse

**Botão direito no projeto → Run As → JUnit Test**

A aba JUnit abre mostrando os 37 testes agrupados por classe, com `@DisplayName` legível.

### Via terminal

```bash
mvn test
```

### Resultado esperado

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running CalculadoraScoreTest                  — 7 tests
[INFO] Running FilmeTest                             — 3 tests
[INFO] Running FiltroFilmesTest                      — 7 tests
[INFO] Running PerfilCinefiloTest                    — 9 tests
[INFO] Running RecomendadorServiceTest               — 11 tests
[INFO]
[INFO] Results:
[INFO] Tests run: 37, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] BUILD SUCCESS
```

---

## 📊 Cobertura JaCoCo

Após rodar `mvn test`, o relatório de cobertura JaCoCo fica disponível em:

```
target/site/jacoco/index.html
```

Abra no navegador para ver a cobertura linha-a-linha por pacote e classe.

**Cobertura atingida:**

| Pacote | Cobertura |
|--------|-----------|
| `service/` | **> 80%** ✅ |
| `model/` | **> 80%** ✅ |
| `exception/` | 100% (exceções instanciadas em testes de validação) |
| `util/` | 100% (interface coberta via mock) |

---

## ✅ Cenários de teste implementados

**37 testes no total**, divididos por classe:

### `FilmeTest` — 3 testes

| ID | Cenário |
|----|---------|
| F01 | `deve_CriarFilme_ComTodosAtributosPreenchidos` |
| F02 | `deve_ConsiderarIguais_QuandoMesmoId` |
| F03 | `deve_ConsiderarDiferentes_QuandoIdsDistintos` |

### `PerfilCinefiloTest` — 9 testes

| ID | Cenário |
|----|---------|
| P01 | `deve_CriarPerfil_ComPesosValidos` |
| P02 | `deve_LancarExcecao_Quando_PesoMaiorQueUm` (`PesoInvalidoException`) |
| P03 | `deve_LancarExcecao_Quando_PesoMenorQueZero` (`PesoInvalidoException`) |
| P04 | `deve_LancarExcecao_Quando_DuracaoMinimaEhMaiorQueMaxima` (`DuracaoInvalidaException`) |
| P05 | `deve_AceitarFaixaDuracao_QuandoMinimaIgualMaxima` |
| P06 | `deve_LancarExcecao_Quando_NotaForaDoIntervalo` |
| P07 | `deve_MarcarFilmeAssistido_EAparecerNoHistorico` |
| P08 | `deve_RetornarNull_Quando_FilmeNuncaAvaliado` |
| P09 | `deve_RetornarNota_Quando_FilmeAvaliado` |

### `CalculadoraScoreTest` — 7 testes (organizados em `@Nested`)

| Grupo | ID | Cenário |
|-------|----|---------|
| **Componente de Gênero** | C01 | `deve_RetornarCem_Quando_TodosGenerosAmados` |
| | C02 | `deve_RetornarScoreBaixo_Quando_GeneroNaoPreferido` |
| | C03 | `deve_CalcularMediaPonderadaDosGeneros` |
| **Componente de Duração** | C04 | `deve_RetornarCem_Quando_FilmeDentroDaFaixaDuracao` |
| | C05 | `deve_ReducirScore_Quando_DuracaoAcimaDoMaximo` |
| **Score Final** | C06 | `deve_NaoPassarDeCem_NuncaFicarNegativo` |
| | C07 | `deve_GerarJustificativa_Quando_CalculaScore` |

### `FiltroFilmesTest` — 7 testes

| ID | Cenário |
|----|---------|
| FT01 | `deve_RemoverFilme_Quando_JaFoiAssistido` |
| FT02 | `deve_RemoverFilme_Quando_ClassificacaoAcimaDoMaximo` |
| FT03 | `deve_RemoverFilme_Quando_IdiomaRecusado` |
| FT04 | `deve_RemoverFilme_Quando_GeneroComPesoZero` |
| FT05 | `deve_RetornarListaVazia_Quando_CatalogoVazio` |
| FT06 | `deve_ManterFilme_Quando_PassaEmTodasAsRegras` |
| FT07 | `deve_AceitarOuRejeitarFilme_ConformeClassificacao` |

### `RecomendadorServiceTest` — 11 testes (com Mockito + `@Nested`)

| Grupo | ID | Cenário | Recurso Mockito |
|-------|----|---------|-----------------|
| **Principais** | RS01 | `deve_RetornarTopN_Quando_ExistemFilmesSuficientes` | `when().thenReturn()` |
| | RS02 | `deve_OrdenarPorScoreDesc_Quando_RecomendacaoTemMultiplosFilmes` | `when().thenReturn()` |
| | RS03 | `deve_RetornarListaVazia_Quando_CatalogoEstaVazio` | `when().thenReturn(emptyList())` |
| | RS04 | `deve_NaoDerrubar_Quando_CatalogoLancaExcecao` | `when().thenThrow()` + `verify(never())` |
| | RS05 | `deve_ChamarRegistrarRecomendacao_Apos_Recomendar` | `verify(times(1))` |
| | RS06 | `deve_ChamarNotificador_Quando_PushEstaHabilitado` | `verify(times(1))` |
| | RS07 | `deve_NaoChamarNotificador_Quando_PushEstaDesligado` | `verify(never())` |
| **Surpreenda-me** | RS08 | `deve_RetornarFilme_Quando_SurpreendaMeComFilmesFiltrados` | `when().thenReturn()` |
| | RS09 | `deve_RetornarVazio_Quando_SurpreendaMeSemFilmes` | `Optional.empty()` |
| **ArgumentCaptor** | RS10 | `deve_RegistrarCorretamente_AsRecomendacoesGeradas` | **`ArgumentCaptor`** |
| **Spy** | RS11 | `deve_ChamarCalculadora_UmaVezPorFilmeFiltrado` | **`@Spy`** + `verify(times(N))` |

---

## 🧠 Decisões de design

### O que mockamos e por quê

| Dependência | Anotação | Razão para mockar |
|-------------|----------|-------------------|
| `CatalogoFilmesAPI` | `@Mock` | Em produção é HTTP (TMDB/OMDB). Teste não pode depender de internet. |
| `HistoricoUsuarioRepository` | `@Mock` | Em produção é banco de dados. Não queremos persistir em testes. |
| `NotificadorPush` | `@Mock` | Em produção é Firebase/OneSignal. Disparar push real em testes é spam. |
| `GeradorAleatorio` | `@Mock` | Sem mock, desempate aleatório vira *flaky test*. |
| `CalculadoraScore` | `@Spy` | Instância real, mas observada — permite contar chamadas com `verify` |

### O que NÃO mockamos

| Classe | Razão para NÃO mockar |
|--------|------------------------|
| `FiltroFilmes` | Lógica pura. Usamos instância real (`new FiltroFilmes()`) no `@BeforeEach`. |
| `Filme`, `Recomendacao`, `PerfilCinefilo`, `Usuario` | Objetos de domínio. Usamos `new`. |
| Enums (`Genero`, `Idioma`, `ClassificacaoEtaria`) | São valores constantes. Mockar enum não faz sentido. |

### Decisão arquitetural: `CalculadoraScore` como `@Spy`

Uma decisão interessante foi usar `@Spy` (não `@Mock`) na `CalculadoraScore`:

- **`@Mock`** substituiria a calculadora por um objeto vazio. Não testaria a fórmula real.
- **`@Spy`** envolve uma instância real, permitindo:
  - Que a fórmula execute normalmente (validando a lógica matemática)
  - Que possamos contar quantas vezes `calcular()` foi invocado (`verify(times(N))`)

Isso é especialmente útil no teste `deve_ChamarCalculadora_UmaVezPorFilmeFiltrado`, que prova: para 2 filmes filtrados, a calculadora deve ser chamada exatamente 2 vezes.

### Padrões adotados

- **AAA (Arrange-Act-Assert):** todo teste tem três blocos visualmente separados
- **Nomenclatura `deve_X_Quando_Y()`:** padrão obrigatório para legibilidade
- **`@DisplayName` em TODOS os testes** — relatório fica humanizado em português
- **`@Nested` para agrupar contextos relacionados** (ex: "Modo Surpreenda-me", "ArgumentCaptor", "Spy na CalculadoraScore")
- **`@Tag("unitario")`** em todas as classes de teste para futura segmentação de execução
- **`@BeforeEach` para setup limpo** — cada teste começa do zero
- **Constantes nomeadas (`PESO_GENERO = 0.50`)** — sem números mágicos no código
- **`Collections.emptyList()` em vez de `null`** — APIs nunca retornam `null` para coleções
- **Resiliência em I/O:** `buscarCatalogoComResiliencia()` captura exceções e devolve lista vazia

---

## 👥 Autores

- **Ryan Ribeiro de Oliveira**
- **Guilherme Froes**
- **Carlos Eduardo Sayão**

**Disciplina:** Teste de Software 2026
**Instituição:** Universidade Católica do Salvador (UCSAL)
**Data:** Maio de 2026

---

## 📄 Licença

Projeto acadêmico — uso educacional.

---

<div align="center">

**CineAnalytic** — *Análise inteligente. Cinema sob medida.*

</div>
