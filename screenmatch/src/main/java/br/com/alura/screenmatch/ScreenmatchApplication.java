package br.com.alura.screenmatch;

import br.com.alura.screenmatch.main.MainAntigo;
import br.com.alura.screenmatch.main.MainNovo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ScreenmatchApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        //O MainAntigo é a classe que foi utilizada para estudo
        //ela é mais desorganizada e nem utiliza funções para separar o código
        //MainAntigo main = new MainAntigo();
        MainNovo main = new MainNovo();
        main.consultarAPI();
    }
}
