package com.leticia.literalura.repository;

import com.leticia.literalura.domain.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByNameIgnoreCase(String name);

    @Query("select a from Author a " +
           "where (a.birthYear is null or a.birthYear <= :year) " +
           "and (a.deathYear is null or a.deathYear >= :year)")
    List<Author> findAliveInYear(int year);
}
