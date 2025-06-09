package it.uniroma3.siwbooks.service;

import it.uniroma3.siwbooks.models.Autore;
import it.uniroma3.siwbooks.models.Books;
import it.uniroma3.siwbooks.repository.BookRepository2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Transactional
public class BookService {

    @Autowired
    private BookRepository2 bookRepository2;

    public Books findById(Long id) {
        return bookRepository2.findById(id).orElse(null);
    }

    public Iterable<Books> findAll() {
        return bookRepository2.findAll();
    }

    public List<Books> findByTitle(String title) {
        return bookRepository2.findByTitle(title);
    }

    public List<Books> findByAuthor(Autore author) {
        return bookRepository2.findByAuthor(author);
    }

    public List<Books> findByAuthorOrCoauthor(Long authorId) {
        return bookRepository2.findByAuthorOrCoauthor(authorId);
    }

    public List<Books> findByYear(int year) {
        return bookRepository2.findByYear(year);
    }

    public boolean existsByTitleAndYear(String title, int year) {
        return bookRepository2.existsByTitleAndYear(title, year);
    }

    public Iterable<Books> findByTitleStartingWith(String title) {
        return bookRepository2.findBookByTitleStartingWith(title);
    }

    public List<Books> findBooksByRating(int stelle) {
        return bookRepository2.findBooksByRating(stelle);
    }

    public List<Books> findAllBooksOrderByAverageRatingDesc() {
        return bookRepository2.findAllBooksOrderByAverageRatingDesc();
    }

    public List<Books> findAllBooksOrderByAverageRatingAsc() {
        return bookRepository2.findAllBooksOrderByAverageRatingAsc();
    }

    public Books save(Books book) {
        return bookRepository2.save(book);
    }

    public void delete(Books book) {
        bookRepository2.delete(book);
    }


}