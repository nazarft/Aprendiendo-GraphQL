package io.nazar.graphql_intro.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    public Book(Integer id, String title) {
        this.id = id;
        this.title = title;
    }

    public Book(String title) {
        this.title = title;
    }

    public Book() {
    }

    public Book(Integer id, String title, Author author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

    public Book(String title, Author author) {
        this.title = title;
        this.author = author;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}
