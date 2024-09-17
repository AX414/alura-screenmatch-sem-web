package br.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosSerie(@JsonAlias("Title") String titulo,
                         @JsonAlias("totalSeasons") Integer totalTemporadas,
                         @JsonAlias("imdbRating") String avaliacao,
                         @JsonProperty("imdbVotes") String votos) {

    public DadosSerie {
        if (titulo != null) {
            System.out.println("\n| Série: " + titulo + " | Temporadas: " + totalTemporadas + " |");
        }
    }

    @Override
    public String titulo() {
        return titulo;
    }

    @Override
    public Integer totalTemporadas() {
        return totalTemporadas;
    }

    @Override
    public String avaliacao() {
        return avaliacao;
    }

    @Override
    public String votos() {
        return votos;
    }
}