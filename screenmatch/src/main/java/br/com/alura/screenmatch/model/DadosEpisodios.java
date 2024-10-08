package br.com.alura.screenmatch.model;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosEpisodios(@JsonAlias("Title") String titulo,
                             @JsonAlias("Episode") Integer numeroEpisodio,
                             @JsonAlias("imdbRating") String avaliacao,
                             @JsonProperty("Released") String dataLancamento) {

    @Override
    public Integer numeroEpisodio() {
        return numeroEpisodio;
    }

    @Override
    public String titulo() {
        return titulo;
    }

    @Override
    public String avaliacao() {
        return avaliacao;
    }

    @Override
    public String dataLancamento() { return dataLancamento; }

    @Override
    public String toString() {
        return "| Episódio: " + numeroEpisodio +
                " | Título: " + titulo +
                " | Avaliação: " + avaliacao +
                " | Data de Lançamento: " + dataLancamento +
                " |";
    }
}
