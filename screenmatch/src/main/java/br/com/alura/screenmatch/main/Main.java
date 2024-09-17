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

public class Main {

    private Scanner ler = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";

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

    public void consultarAPI() {

        System.out.println("\nDigite o nome da série para efetuar a consulta: ");
        var nome = ler.nextLine();

        var json = consumoAPI.obterDados(ENDERECO + nome.replace(" ", "+") + API_KEY);


        //Apresenta dados da série
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        if (dados.titulo() == null) {
            System.out.println("\nNenhum resultado encontrado.");
        } else {

            // Apresentando todos os episódios e temporadas
            List<DadosTemporada> temporadas = new ArrayList<>();
            for (int i = 1; i <= dados.totalTemporadas(); i++) {
                json = consumoAPI.obterDados(ENDERECO + nome.replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }

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

            //Utilizando Streams e lambdas para trabalhar com as temporadas
            System.out.println("\n\nUtilizando streams e lambdas: ");
            List<DadosEpisodios> todosEpisodiosDeTodasTemporadas = temporadas.stream()
                    .flatMap(t -> t.episodios().stream())
                    .collect(Collectors.toList());
            //.toList();

            //Adicionando um episódio extra na coleção
            todosEpisodiosDeTodasTemporadas.add(new DadosEpisodios("teste", 3, "9.0", "2020-01-01"));
            todosEpisodiosDeTodasTemporadas.forEach(System.out::println);

        /*
        OBS.: O toList nos dá uma lista imutável, não é possível realizar alterações.
        Em contrapartida, o Collectors nos apresenta uma coleção que é passiva de mudanças futuras.
        */

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
                    .map(e-> e.titulo().toUpperCase())
                    //.peek(e-> System.out.println("\nMapeamento: "+e))
                    .forEach(e-> System.out.println("* "+e));

            System.out.println("\nApresentando episódios e temporadas por meio de um construtor de episódios da classe Episódio:");
            List<Episodio> episodios = temporadas.stream()
                    .flatMap(t -> t.episodios()
                            .stream()
                            .map(e -> new Episodio(t.temporada(), e))
                    ).collect(Collectors.toList());
            episodios.forEach(System.out::println);

            System.out.println("\nA partir de que ano você quer ver estes episódios?");
            var ano = ler.nextInt();
            ler.nextLine();

            System.out.println("\nApresentando episódios a partir do ano de "+ano+ ": ");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyy");
            LocalDate dataBusca = LocalDate.of(ano, 1, 1);

            episodios.stream()
                    .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
                    .forEach(e -> System.out.println("| Temporada: " + e.getTemporada() +
                            " | Episódio: " + e.getNumeroEpisodio() +
                            " | Título: " + e.getTitulo() +
                            " | Avaliação: " + e.getAvaliacao() +
                            " | Data de Lançamento: " + e.getDataLancamento().format(dtf) +
                            " |"));


            System.out.println("\nDigite o título de um episódio: ");
            var tituloEpisodio = ler.nextLine();
            //Optional é utilizado quando os valores podem ou não serem retornados
            Optional<Episodio> episodioBuscado = episodios.stream()
                    .filter(e -> e.getTitulo().contains(tituloEpisodio))
                    .findFirst();

            //Procura se existe
            episodioBuscado.ifPresent(episodio -> System.out.println("\nEpisódio encontrado, ele pertence à temporada: " + episodio.getTemporada()));
            if(episodioBuscado.isEmpty()){
                System.out.println("\nEpisódio não encontrado.");
            }

            Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                    .collect(Collectors.groupingBy(Episodio::getTemporada,
                            Collectors.averagingDouble(Episodio::getAvaliacao)));
            System.out.println(avaliacoesPorTemporada);
        }
    }
}