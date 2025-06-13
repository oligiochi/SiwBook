package it.uniroma3.siwbooks.controller;

import it.uniroma3.siwbooks.dto.BookInfoDto;
import it.uniroma3.siwbooks.dto.GenreDto;
import it.uniroma3.siwbooks.models.Books;
import it.uniroma3.siwbooks.models.Genere;
import it.uniroma3.siwbooks.service.AutoreService;
import it.uniroma3.siwbooks.service.BookService;
import it.uniroma3.siwbooks.service.BookService2;
import it.uniroma3.siwbooks.service.GenereService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/books")
public class LibriController {

    @Autowired
    private GenereService genereService;
    @Autowired
    private BookService bookService;
    @Autowired
    private AutoreService autoreService;

    @GetMapping
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
