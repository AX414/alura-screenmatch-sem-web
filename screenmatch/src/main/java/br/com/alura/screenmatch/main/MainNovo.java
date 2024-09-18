package br.com.alura.screenmatch.main;

import br.com.alura.screenmatch.model.DadosEpisodios;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;
import ch.qos.logback.core.encoder.JsonEscapeUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class MainNovo {

    private Scanner ler = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";

    public void consultarAPI() {
        System.out.println("\nDigite o nome da série para efetuar a consulta: ");
        var nome = ler.nextLine();
        var json = consumoAPI.obterDados(ENDERECO + nome.replace(" ", "+") + API_KEY);

        //Apresenta dados da série
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        if (dados.titulo() == null) {
            System.out.println("\nNenhum resultado encontrado.");
        } else {
            List<DadosTemporada> temporadas = new ArrayList<DadosTemporada>();
            List<DadosEpisodios> todosEpisodiosDeTodasTemporadas = new ArrayList<DadosEpisodios>();
            List<Episodio> episodios = new ArrayList<Episodio>();

            int op = 0;
            do {
                apresentarMenuDeOpcoes();
                System.out.println("\n\nSelecione uma das opções do menu para prosseguir: ");
                op = ler.nextInt();
                ler.nextLine();

                switch (op) {
                    case 1:
                        temporadas = apresentandoTodosOsEpisodiosETemporadas(temporadas, json, nome, dados);
                        break;
                    case 2:
                        //Não roda se a lista de temporadas não estiver populada
                        if (temporadas.isEmpty())
                            System.out.println("\nNão é possível acessar essa opção ainda (acesse as opções anteriores primeiro).");
                        else
                            apresentandoTodosOsTitulos(temporadas);
                        break;
                    case 3:
                        //Não roda se a lista de temporadas não estiver populada
                        if (temporadas.isEmpty())
                            System.out.println("\nNão é possível acessar essa opção ainda (acesse as opções anteriores primeiro).");
                        else
                            todosEpisodiosDeTodasTemporadas = utilizandoStreamsELambdas(temporadas);
                        break;
                    case 4:
                        //Não roda se não tiver a lista de todos os eps e todas as temporadas populadas
                        if (temporadas.isEmpty() || todosEpisodiosDeTodasTemporadas.isEmpty())
                            System.out.println("\nNão é possível acessar essa opção ainda (acesse as opções anteriores primeiro).");
                        else
                            apresentandoDezMaisAvaliados(todosEpisodiosDeTodasTemporadas);
                        break;
                    case 5:
                        if (temporadas.isEmpty())
                            System.out.println("\nNão é possível acessar essa opção ainda (acesse as opções anteriores primeiro).");
                        else
                            episodios = apresentandoEpisodiosETemporadasPorConstrutor(temporadas);
                        break;
                    case 6:
                        if (temporadas.isEmpty() || episodios.isEmpty())
                            System.out.println("\nNão é possível acessar essa opção ainda (acesse as opções anteriores primeiro).");
                        else
                            apresentarEpisodiosAPartirDeUmAno(episodios);
                        break;
                    case 7:
                        if (temporadas.isEmpty() || episodios.isEmpty())
                            System.out.println("\nNão é possível acessar essa opção ainda (acesse as opções anteriores primeiro).");
                        else
                            apresentarTemporadaPorEpisodio(episodios);
                        break;
                    case 8:
                        if (temporadas.isEmpty() || episodios.isEmpty())
                            System.out.println("\nNão é possível acessar essa opção ainda (acesse as opções anteriores primeiro).");
                        else
                            apresentarAvaliacoesPorTemporada(episodios);
                        break;
                    case 9:
                        if (temporadas.isEmpty() || episodios.isEmpty())
                            System.out.println("\nNão é possível acessar essa opção ainda (acesse as opções anteriores primeiro).");
                        else
                            apresentarEstatisticas(episodios);
                        break;
                    case 0:
                        System.out.println("\nEncerrando o programa.");
                        break;
                    default:
                        System.out.println("\nOpção inválida.");
                        break;
                }
            } while (op != 0);

        }
    }

    public void apredendoSobreStreams() {
        {
            //Aprendendo sobre Stream
            List<String> letras = Arrays.asList("A", "B", "C", "D", "E");
            //Ordenando o fluxo de dados e realizando uma ordenação
            System.out.println("\nOrdenando em ordem alfabética a lista: ");
            letras.stream()
                    .sorted()
                    .forEach(System.out::println);

            //É possível limitar o fluxo de dados também
            System.out.println("\nApresentando apenas 3 items da lista:");
            letras.stream()
                    .limit(3)
                    .forEach(System.out::println);

            //Também podemos utilizar filtros e realizar diversas
            // operações intermediárias e encadeadas
            System.out.println("\nApresentando a letra B em minúscula:");
            letras.stream()
                    .limit(3)
                    .filter(l -> l.startsWith("B"))
                    .map(l -> l.toLowerCase())
                    .forEach(System.out::println);
        }
    }

    public void apresentarMenuDeOpcoes() {
        System.out.println("\n<Menu>");
        System.out.println("1 - Apresentar todos os episódios e temporadas.");
        System.out.println("2 - Apresentar todos os títulos de episódios.");
        System.out.println("3 - Apresentar utilizando streams e lambdas.");
        System.out.println("4 - Apresentar os dez mais avaliados.");
        System.out.println("5 - Apresentar episódios e temporadas por um construtor personalizado.");
        System.out.println("6 - Apresentar episódios a partir de um ano.");
        System.out.println("7 - Apresentar temporada por um episódio.");
        System.out.println("8 - Apresentar média de avaliações por temporada.");
        System.out.println("9 - Apresentar estatísticas");
        System.out.println("0 - Encerrar Programa.");
    }

    //1
    public List<DadosTemporada> apresentandoTodosOsEpisodiosETemporadas(List<DadosTemporada> temporadas, String json, String nome, DadosSerie dados) {
        for (int i = 1; i <= dados.totalTemporadas(); i++) {
            json = consumoAPI.obterDados(ENDERECO + nome.replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        return temporadas;
    }

    //2
    public void apresentandoTodosOsTitulos(List<DadosTemporada> temporadas) {
        // Apresentar todos os títulos (forma não convencional)
        //        for(int i=0; i<dados.totalTemporadas();i++){
        //            List<DadosEpisodios> epsTemporada = temporadas.get(i).episodios();
        //            for(int j=0; j<epsTemporada.size();j++){
        //                System.out.println(epsTemporada.get(j).titulo());
        //            }
        //        }

        // Apresentar todos os títulos (forma convencional)
        System.out.println("\n\nApresentando todos os títulos de episódios:");
        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println("| " + e.titulo() + " |")));

        /*
         OBS.: As -> são chamadas de lambdas (funções anônimas)
         elas são uma maneira de definir funções que são
         frequentemente usadas uma única vez, direto no
         local onde serão usadas.
        */
    }

    //3
    public List<DadosEpisodios> utilizandoStreamsELambdas(List<DadosTemporada> temporadas) {
        //Utilizando Streams e lambdas para trabalhar com as temporadas
        System.out.println("\n\nUtilizando streams e lambdas: ");
        List<DadosEpisodios> todosEpisodiosDeTodasTemporadas = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());
        //.toList();

        //Adicionando um episódio extra na coleção
        //todosEpisodiosDeTodasTemporadas.add(new DadosEpisodios("teste", 3, "9.0", "2020-01-01"));
        todosEpisodiosDeTodasTemporadas.forEach(System.out::println);

        /*
        OBS.: O toList nos dá uma lista imutável, não é possível realizar alterações.
        Em contrapartida, o Collectors nos apresenta uma coleção que é passiva de mudanças futuras.
        */

        return todosEpisodiosDeTodasTemporadas;
    }

    //4
    public void apresentandoDezMaisAvaliados(List<DadosEpisodios> todosEpisodiosDeTodasTemporadas) {
        System.out.println("\nApresentando os 10 episódios mais bem avaliados:");
        todosEpisodiosDeTodasTemporadas.stream()
                //Retirando os N/A
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                //Peek ajuda a dar uma olhadinha para ver se é o que estávamos esperando
                //.peek(e-> System.out.println("\nPrimeiro filtro (N/A): "+e))
                .sorted(Comparator.comparing(DadosEpisodios::avaliacao).reversed())
                //.peek(e-> System.out.println("\nOrdenação: "+e))
                .limit(10)
                //.peek(e-> System.out.println("\nLimite: "+e))
                .map(e -> e.titulo().toUpperCase())
                //.peek(e-> System.out.println("\nMapeamento: "+e))
                .forEach(e -> System.out.println("* " + e));
    }

    //5
    public List<Episodio> apresentandoEpisodiosETemporadasPorConstrutor(List<DadosTemporada> temporadas) {
        System.out.println("\nApresentando episódios e temporadas por meio de um construtor de episódios da classe Episódio:");
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios()
                        .stream()
                        .map(e -> new Episodio(t.temporada(), e))
                ).collect(Collectors.toList());
        episodios.forEach(System.out::println);
        return episodios;
    }

    //6
    public void apresentarEpisodiosAPartirDeUmAno(List<Episodio> episodios) {

        System.out.println("\nA partir de que ano você quer ver estes episódios?");
        var ano = ler.nextInt();
        ler.nextLine();

        System.out.println("\nApresentando episódios a partir do ano de " + ano + ": ");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyy");
        LocalDate dataBusca = LocalDate.of(ano, 1, 1);

        boolean nenhumResultado = episodios.stream()
                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
                .peek(e -> System.out.println("| Temporada: " + e.getTemporada() +
                        " | Episódio: " + e.getNumeroEpisodio() +
                        " | Título: " + e.getTitulo() +
                        " | Avaliação: " + e.getAvaliacao() +
                        " | Data de Lançamento: " + e.getDataLancamento().format(dtf) +
                        " |"))
                .count() == 0;

        if (nenhumResultado) {
            System.out.println("Nenhum episódio encontrado a partir do ano " + ano + ".");
        }
    }

    //7
    public void apresentarTemporadaPorEpisodio(List<Episodio> episodios) {
        System.out.println("\nDigite o título de um episódio: ");
        var tituloEpisodio = ler.nextLine();
        //Optional é utilizado quando os valores podem ou não serem retornados
        Optional<Episodio> episodioBuscado = episodios.stream()
                .filter(e -> e.getTitulo().contains(tituloEpisodio))
                .findFirst();

        //Procura se existe
        episodioBuscado.ifPresent(episodio -> System.out.println("\nEpisódio encontrado, ele pertence à temporada: " + episodio.getTemporada()));
        if (episodioBuscado.isEmpty()) {
            System.out.println("\nEpisódio não encontrado.");
        }
    }

    //8
    public void apresentarAvaliacoesPorTemporada(List<Episodio> episodios) {
        //Apresenta a média de avaliação por temporada
        Map<Integer, Double> avaliacaoPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() != null && e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));
        System.out.println("* " + avaliacaoPorTemporada);
    }

    //9
    public void apresentarEstatisticas(List<Episodio> episodios) {
        //Apresenta estatísticas
        System.out.println("\nApresenta Estatísticas do Double Summary Statistics: ");
        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() != null && e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
        System.out.println("* " + est);

        //Não é necessário somar todas as avaliações
        //imprimimos apenas o necessário
        System.out.println("\n\nApresentando as estatísticas que eu quero:"
                + "\n* Média: " + est.getAverage()
                + "\n* Menor Avaliação: " + est.getMin()
                + "\n* Melhor Avaliação: " + est.getMax()
                + "\n* Quantidade de episódios avaliados: " + est.getCount());
    }

}