package io.nazar.graphql_intro.controller;

import io.nazar.graphql_intro.domain.model.Author;
import io.nazar.graphql_intro.domain.model.Book;
import io.nazar.graphql_intro.domain.model.BookInput;
import io.nazar.graphql_intro.repository.AuthorRepository;
import io.nazar.graphql_intro.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class BookController {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    @Autowired
    public BookController(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    @QueryMapping
    public List<Book> books() {
        return bookRepository.findAll();
    }
    @QueryMapping
    public Book bookById(@Argument Integer id) {
        return bookRepository.findById(id).orElse(null);
    }
    @SchemaMapping
    public Author author(Book book) {
        return authorRepository.findById(book.getAuthor().getId()).orElse(null);
    }
    @MutationMapping
    Book addBook(@Argument BookInput book){
        Author author = authorRepository.findById(book.authorId()).orElse(null);
        Book b = new Book(book.title(), author);
        return bookRepository.save(b);
    }
    @MutationMapping
    Book updateBook(@Argument Integer id, @Argument BookInput book) {
        Author author = authorRepository.findById(book.authorId()).orElse(null);
        Book b = bookRepository.findById(id).orElse(null);
        b.setTitle(book.title());
        b.setAuthor(author);
        return bookRepository.save(b);
    }
}
