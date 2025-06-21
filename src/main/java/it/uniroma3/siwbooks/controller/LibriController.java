package it.uniroma3.siwbooks.controller;

import it.uniroma3.siwbooks.dto.BookInfoDto;
import it.uniroma3.siwbooks.dto.GenreDto;
import it.uniroma3.siwbooks.models.Books;
import it.uniroma3.siwbooks.models.Genere;
import it.uniroma3.siwbooks.models.Recensione;
import it.uniroma3.siwbooks.models.Utente;
import it.uniroma3.siwbooks.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class LibriController {

    @Autowired
    private GenereService genereService;
    @Autowired
    private BookService bookService;
    @Autowired
    private AutoreService autoreService;
    @Autowired
    private RecensioneService RecensioneService;
    @Autowired
    private UtenteService userService;

    @GetMapping("/books")
    public String list(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(name = "genre", defaultValue = "-1") Long genreID,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "DATE_DESC") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        Sort sort = this.buildSort(sortBy);
        Pageable pageable = PageRequest.of(page, 8, sort);

        Page<Books> bookPage = bookService.searchBooks(
                searchTerm, genreID, startDate, endDate, sort, pageable);

        // Trasforma direttamente in DTO

        List<BookInfoDto> dtos=buildBookInfo(bookPage);
        model.addAttribute("books", dtos);
        model.addAttribute("page", bookPage);
        model.addAttribute("totalCount", bookPage.getTotalElements());
        model.addAttribute("genere", buildGenreList(genreID));
        model.addAttribute("sortBy", sortBy);

        return "books";
    }

    @GetMapping("/book/{id}")
    public String getBook(
            @PathVariable("id") Long id,
            Model model) {

        // 1) prendi tutte le recensioni del libro
        List<Recensione> allReviews = RecensioneService.getBookReviews(id);

        // 2) utente corrente
        Utente currentUser = userService.getCurrentUser();

        List<Recensione> sortedReviews = allReviews.stream()
                .sorted(Comparator.comparing(
                        r -> !(currentUser != null && r.getAuthor().equals(currentUser))
                ))
                .collect(Collectors.toList());

        boolean hasUserReview = !sortedReviews.isEmpty() && sortedReviews.getFirst().getAuthor().equals(currentUser);

        // 4) aggiungi al modello
        model.addAttribute("book",        bookService.findById(id));
        model.addAttribute("hasUserReview",  hasUserReview);
        model.addAttribute("bookReviews",  sortedReviews);

        return "book";
    }


    @GetMapping("/book/{id}/cover")
    public ResponseEntity<byte[]> getBookCover(@PathVariable Long id) {
        Books book = bookService.findById(id);
        if (book == null || book.getCoverImage() == null) {
            return ResponseEntity.notFound().build();
        }
        byte[] data = book.getCoverImage().getData();
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // o rileva dinamicamente
                .body(data);
    }

    @GetMapping("/book/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("authors", autoreService.findAll()); // List<Author>
        model.addAttribute("genres", genereService.findAll());   // List<Genre>
        return "FormBook"; // oppure il nome effettivo del file Thymeleaf
    }


    private Sort buildSort(String sortBy) {
        return switch(sortBy) {
            case "DATE_ASC"   -> Sort.by("releaseDate").ascending();
            case "TITLE"      -> Sort.by("title").ascending();
            case "TITLE_DESC" -> Sort.by("title").descending();
            case "RATING"     -> Sort.by("popularity").descending();  // verifica che 'popularity' esista!
            default           -> Sort.by("releaseDate").descending();
        };
    }

    private List<BookInfoDto> buildBookInfo(Page<Books> bookPage) {
        return bookPage.getContent().stream()
                .map(book -> new BookInfoDto(
                        book.getId(),
                        book.getTitle(),
                        book.getReleaseDate(),
                        bookService.AvgStar(book) // metodo sul service che riceve Books
                ))
                .collect(Collectors.toList());
    }

    private List<GenreDto> buildGenreList(Long selectedGenreId) {
        var all = genereService.findAll();
        return all.stream()
                .map(g -> new GenreDto(
                        g.getId().equals(selectedGenreId),
                        genereService.countLibriByGenere(g),
                        g.getGenere(),
                        g.getId()
                ))
                .collect(Collectors.toList());
    }
}
