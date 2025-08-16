package com.leticia.literalura.service;

import com.leticia.literalura.domain.Author;
import com.leticia.literalura.domain.Book;
import com.leticia.literalura.dto.GutendexAuthorDTO;
import com.leticia.literalura.dto.GutendexBookDTO;
import com.leticia.literalura.repository.AuthorRepository;
import com.leticia.literalura.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class CatalogService {

    private final AuthorRepository authorRepo;
    private final BookRepository bookRepo;
    private final GutendexClient client;

    public CatalogService(AuthorRepository authorRepo, BookRepository bookRepo, GutendexClient client) {
        this.authorRepo = authorRepo;
        this.bookRepo = bookRepo;
        this.client = client;
    }

    @Transactional
    public void importBookByTitle(String title) {
        if (title == null || title.isBlank()) {
            System.out.println("Título inválido.");
            return;
        }
        if (bookRepo.existsByTitleIgnoreCase(title)) {
            System.out.println("Livro já existe no catálogo: " + title);
            return;
        }

        Optional<GutendexBookDTO> opt = client.searchFirstByTitle(title);
        if (opt.isEmpty()) {
            System.out.println("Nenhum resultado encontrado para: " + title);
            return;
        }

        GutendexBookDTO dto = opt.get();

        String lang = null;
        if (dto.getLanguages() != null && !dto.getLanguages().isEmpty()) {
            lang = dto.getLanguages().get(0);
        }
        Integer downloads = dto.getDownloadCount();

        Book book = new Book(dto.getTitle(), lang, downloads);

        // Apenas o primeiro autor
        Author author = null;
        if (dto.getAuthors() != null && !dto.getAuthors().isEmpty()) {
            GutendexAuthorDTO a = dto.getAuthors().get(0);
            author = authorRepo.findByNameIgnoreCase(a.getName())
                    .orElseGet(() -> authorRepo.save(new Author(a.getName(), a.getBirthYear(), a.getDeathYear())));
        }

        if (author != null) {
            book.setAuthor(author);
            author.getBooks().add(book);
        }

        bookRepo.save(book);

        System.out.println("----- LIVRO -----");
        System.out.println("Título: " + book.getTitle());
        System.out.println("Autor: " + (author != null ? author.getName() : "-"));
        System.out.println("Idioma: " + (book.getLanguage() != null ? book.getLanguage() : "-"));
        System.out.println("Número de downloads: " + (book.getDownloads() != null ? book.getDownloads() : "-"));
        System.out.println("-----------------");
    }

    @Transactional(readOnly = true)
    public List<Book> listBooks() {
        return bookRepo.findAll().stream()
                .sorted(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Author> listAuthors() {
        return authorRepo.findAll().stream()
                .sorted(Comparator.comparing(Author::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Author> listAuthorsAliveInYear(int year) {
        return authorRepo.findAliveInYear(year).stream()
                .sorted(Comparator.comparing(Author::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Book> listBooksByLanguage(String lang) {
        if (lang == null || lang.isBlank()) return List.of();
        return bookRepo.findByLanguageIgnoreCase(lang).stream()
                .sorted(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Transactional(readOnly = true)
    public long countBooksByLanguage(String lang) {
        if (lang == null || lang.isBlank()) return 0;
        return bookRepo.countByLanguageIgnoreCase(lang);
    }
}
