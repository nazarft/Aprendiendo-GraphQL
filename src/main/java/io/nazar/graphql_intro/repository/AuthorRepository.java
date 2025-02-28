package io.nazar.graphql_intro.repository;

import io.nazar.graphql_intro.domain.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Integer> {

}
