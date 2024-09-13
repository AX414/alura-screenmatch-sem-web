package br.com.alura.screenmatch.service;

public interface IConverteDados {
    //Retorna o tipo genérico de alguma coisa com o <T> T
    <T> T obterDados(String json, Class<T> classe);
}
