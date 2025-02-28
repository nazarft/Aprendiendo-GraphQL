package io.nazar.graphql_intro.repository;

import io.nazar.graphql_intro.domain.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Integer> {
}
