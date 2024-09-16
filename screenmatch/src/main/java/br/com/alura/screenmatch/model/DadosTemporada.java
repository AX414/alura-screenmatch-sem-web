package br.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosTemporada(@JsonAlias("Season") Integer temporada,
                             @JsonAlias("Episodes") List<DadosEpisodios> episodios) {

    public DadosTemporada {
        System.out.println("\n< Temporada: " + temporada + " >");
        for (DadosEpisodios x : episodios) {
            System.out.println(x);
        }
    }

    @Override
    public Integer temporada() {
        return temporada;
    }

    @Override
    public List<DadosEpisodios> episodios() {
        return episodios;
    }
}
