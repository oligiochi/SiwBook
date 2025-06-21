package it.uniroma3.siwbooks.service;

import it.uniroma3.siwbooks.models.Books;
import it.uniroma3.siwbooks.models.Recensione;
import it.uniroma3.siwbooks.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<Books> searchWithFilters(String searchTerm, LocalDateTime startDateStr, LocalDateTime endDateStr, String sortBy) {
        List<Books> booksTerms = searchByTerm(searchTerm);
        List<Books> booksYear=new ArrayList<>();
        if (startDateStr != null && endDateStr == null) {
            booksYear = findByPublishedYear(startDateStr);
        }else if (startDateStr != null && endDateStr != null) {
            booksYear = findByPublishedYearBetween(startDateStr, endDateStr);
        }

        return null;
    }

    public Page<Books> searchBooks(
            String searchTerm,
            Long genreId,
            LocalDate startDate,
            LocalDate endDate,
            Sort sort,
            Pageable pageable
    ) {
        // 1) Recupera tutti i libri o filtra per termine

        List<Books> all = new ArrayList<>();
        if (searchTerm != null && !searchTerm.isBlank()) {
            all = bookRepository.searchByTerm(searchTerm);
        } else {
            bookRepository.findAll().forEach(all::add);
        }

        // 2) Filtra per genere se richiesto
        if (genreId != null && genreId != -1) {
            all = all.stream()
                    .filter(b -> b.getGeneri().stream()
                            .anyMatch(g -> g.getId().equals(genreId)))
                    .collect(Collectors.toList());
        }

        // 3) Filtra per date se presenti
        if (startDate != null || endDate != null) {
            all = all.stream()
                    .filter(b -> {
                        LocalDate pub = b.getReleaseDate().toLocalDate();
                        boolean afterStart = (startDate == null) || !pub.isBefore(startDate);
                        boolean beforeEnd  = (endDate   == null) || !pub.isAfter(endDate);
                        return afterStart && beforeEnd;
                    })
                    .collect(Collectors.toList());
        }

        extracted(sort, all);

        // 5) Sotto-lista per paginazione
        int total = all.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);
        List<Books> content = start <= end
                ? all.subList(start, end)
                : List.of();

        // 6) Restituisci la Page
        return new PageImpl<>(content, pageable, total);
    }

    private void extracted(Sort sort, List<Books> all) {
        // 4) Applica l'ordinamento in memoria
        Comparator<Books> cmp = Comparator.comparing(Books::getReleaseDate).reversed();
        if (sort.isSorted()) {
            // esempio: supporta un singolo ordine; per più ordini estendi
            Sort.Order o = sort.iterator().next();
            String prop = o.getProperty();
            boolean asc = o.isAscending();
            switch(prop) {
                case "publicationDate":
                case "releaseDate":
                    cmp = Comparator.comparing(Books::getReleaseDate);
                    break;
                case "popularity":
                    cmp = Comparator.comparingDouble(b ->
                            b.getRecensioni().stream()
                                    .mapToInt(Recensione::getStelle)
                                    .average()
                                    .orElse(-1)
                    );

                    break;
                case "title":
                    cmp = Comparator.comparing(b -> b.getTitle().toLowerCase(), String.CASE_INSENSITIVE_ORDER);
                    break;
                default:
                    cmp = Comparator.comparing(Books::getReleaseDate);
            }
            if (!asc) cmp = cmp.reversed();
        }
        all.sort(cmp);
    }

    public Double AvgStar(Books book){
        List<Recensione> recensioni = book.getRecensioni();
        return (Double) recensioni.stream().mapToInt(Recensione::getStelle).average().orElse(-1);
    }
    public Double AvgStarbyID(Long Id){
        List<Recensione> recensioni = findById(Id).getRecensioni();
        return (Double) recensioni.stream().mapToInt(Recensione::getStelle).average().orElse(-1);
    }

    public Books findById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public boolean bookExistsByNameAndPublicationYear(String title, Integer publicationYear) {
        List<Books> books = bookRepository.searchByTitleIgnoringSpaces(title);
        for (Books book : books) {
            if (book.getReleaseDate().equals(publicationYear)) {
                return true;
            }
        }
        return false;
    }
}
