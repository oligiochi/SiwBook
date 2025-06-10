package it.uniroma3.siwbooks.service;

import it.uniroma3.siwbooks.models.Books;
import it.uniroma3.siwbooks.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
public class BookService {

    @Autowired
    BookRepository bookRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

    // Ricerca semplice per termine
    public List<Books> searchByTerm(String searchTerm) {
        if (!StringUtils.hasText(searchTerm)) {
            return Collections.emptyList();
        }
        return bookRepository.searchByTerm(searchTerm);
    }

    // Ricerca per anno (int)
    public List<Books> findByPublishedYear(LocalDateTime year) {
        return bookRepository.findByPublishedDateTime(year);
    }

    // Ricerca per range di anni (int)
    public List<Books> findByPublishedYearBetween(LocalDateTime startYear, LocalDateTime endYear) {
        return bookRepository.findByReleaseDateTimeBetween(startYear, endYear);
    }

    // Ricerca combinata: searchTerm + filtro range date + ordinamento base
    public List<Books> searchWithFilters(String searchTerm, String startDateStr, String endDateStr, String sortBy) {
        return null;
    }
}
