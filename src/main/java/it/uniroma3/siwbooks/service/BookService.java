package it.uniroma3.siwbooks.service;

import it.uniroma3.siwbooks.models.Autore;
import it.uniroma3.siwbooks.models.Books;
import it.uniroma3.siwbooks.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
//@Transactional
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public Books findById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public Iterable<Books> findAll() {
        return bookRepository.findAll();
    }

    public List<Books> findByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    public List<Books> findByAuthor(Autore author) {
        return bookRepository.findByAuthor(author);
    }

    public List<Books> findByAuthorOrCoauthor(Long authorId) {
        return bookRepository.findByAuthorOrCoauthor(authorId);
    }

    public List<Books> findByYear(int year) {
        return bookRepository.findByYear(year);
    }

    public boolean existsByTitleAndYear(String title, int year) {
        return bookRepository.existsByTitleAndYear(title, year);
    }

    public List<Books> findBooksByRating(int stelle) {
        return bookRepository.findBooksByRating(stelle);
    }

    public List<Books> findAllBooksOrderByAverageRatingDesc() {
        return bookRepository.findAllBooksOrderByAverageRatingDesc();
    }

    public List<Books> findAllBooksOrderByAverageRatingAsc() {
        return bookRepository.findAllBooksOrderByAverageRatingAsc();
    }

    public Books save(Books book) {
        return bookRepository.save(book);
    }

    public void delete(Books book) {
        bookRepository.delete(book);
    }
}