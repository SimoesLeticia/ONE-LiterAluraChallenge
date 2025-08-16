package com.leticia.literalura.runner;

import com.leticia.literalura.domain.Author;
import com.leticia.literalura.domain.Book;
import com.leticia.literalura.service.CatalogService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class AppRunner implements CommandLineRunner {

    private final CatalogService catalog;
    private final Scanner scanner = new Scanner(System.in);

    public AppRunner(CatalogService catalog) {
        this.catalog = catalog;
    }

    @Override
    public void run(String... args) {
        boolean running = true;
        while (running) {
            printMenu();
            String opt = scanner.nextLine().trim();
            switch (opt) {
                case "1" -> buscarLivroPorTitulo();
                case "2" -> listarLivros();
                case "3" -> listarAutores();
                case "4" -> listarAutoresVivosNoAno();
                case "5" -> listarLivrosPorIdioma();
                case "0" -> running = false;
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
        }
        System.out.println("Encerrando, até mais!");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("Escolha o número de sua opção:");
        System.out.println("1 - Buscar livro pelo título");
        System.out.println("2 - Listar livros registrados");
        System.out.println("3 - Listar autores registrados");
        System.out.println("4 - Listar autores vivos em um determinado ano");
        System.out.println("5 - Listar livros em um determinado idioma");
        System.out.println("0 - Sair");
        System.out.print("> ");
    }

    private void buscarLivroPorTitulo() {
        System.out.print("Informe o título do livro: ");
        String title = scanner.nextLine().trim();
        try {
            catalog.importBookByTitle(title);
        } catch (Exception e) {
            System.out.println("Falha ao buscar/persistir o livro: " + e.getMessage());
        }
    }

    private void listarLivros() {
        List<Book> books = catalog.listBooks();
        if (books.isEmpty()) {
            System.out.println("Nenhum livro encontrado.");
        } else {
            books.forEach(b -> {
                System.out.println("----- LIVRO -----");
                System.out.println("Título: " + b.getTitle());
                System.out.println("Autor: " + (b.getAuthor() != null ? b.getAuthor().getName() : "-"));
                System.out.println("Idioma: " + (b.getLanguage() != null ? b.getLanguage() : "-"));
                System.out.println("Número de downloads: " + (b.getDownloads() != null ? b.getDownloads() : "-"));
                System.out.println("-----------------");
            });
        }
    }

    private void listarAutores() {
        List<Author> authors = catalog.listAuthors();
        if (authors.isEmpty()) {
            System.out.println("Nenhum autor encontrado.");
        } else {
            authors.forEach(a -> {
                System.out.println("-----------------");
                System.out.println("Autor: " + a.getName());
                System.out.println("Ano de Nascimento: " + (a.getBirthYear() != null ? a.getBirthYear() : "-"));
                System.out.println("Ano de Falecimento: " + (a.getDeathYear() != null ? a.getDeathYear() : "-"));
                System.out.print("Livros: [");
                String titles = a.getBooks().stream().map(Book::getTitle).reduce((x, y) -> x + ", " + y).orElse("");
                System.out.println(titles + "]");
                System.out.println("-----------------");
            });
        }
    }

    private void listarAutoresVivosNoAno() {
        try {
            System.out.print("Informe o ano: ");
            int year = Integer.parseInt(scanner.nextLine().trim());
            List<Author> authors = catalog.listAuthorsAliveInYear(year);
            if (authors.isEmpty()) {
                System.out.println("Nenhum autor encontrado vivo em " + year + ".");
            } else {
                authors.forEach(a -> {
                    System.out.println("-----------------");
                    System.out.println("Autor: " + a.getName());
                    System.out.println("Ano de Nascimento: " + (a.getBirthYear() != null ? a.getBirthYear() : "-"));
                    System.out.println("Ano de Falecimento: " + (a.getDeathYear() != null ? a.getDeathYear() : "-"));
                    System.out.print("Livros: [");
                    String titles = a.getBooks().stream().map(Book::getTitle).reduce((x, y) -> x + ", " + y).orElse("");
                    System.out.println(titles + "]");
                    System.out.println("-----------------");
                });
            }
        } catch (NumberFormatException e) {
            System.out.println("Ano inválido.");
        }
    }

    private void listarLivrosPorIdioma() {
        System.out.println("Insira o idioma para realizar a busca:");
        System.out.println("es - espanhol");
        System.out.println("en - inglês");
        System.out.println("fr - francês");
        System.out.println("pt - português");
        System.out.print("> ");
        String lang = scanner.nextLine().trim().toLowerCase();
        List<Book> books = catalog.listBooksByLanguage(lang);
        if (books.isEmpty()) {
            System.out.println("Não existe livro nesse idioma no nosso banco de dados.");
        } else {
            books.forEach(b -> {
                System.out.println("----- LIVRO -----");
                System.out.println("Título: " + b.getTitle());
                System.out.println("Autor: " + (b.getAuthor() != null ? b.getAuthor().getName() : "-"));
                System.out.println("Idioma: " + (b.getLanguage() != null ? b.getLanguage() : "-"));
                System.out.println("Número de downloads: " + (b.getDownloads() != null ? b.getDownloads() : "-"));
                System.out.println("-----------------");
            });
            long count = catalog.countBooksByLanguage(lang);
            System.out.println("Total de livros no idioma '" + lang + "': " + count);
        }
    }
}
