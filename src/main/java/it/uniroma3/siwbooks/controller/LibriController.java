package it.uniroma3.siwbooks.controller;

import it.uniroma3.siwbooks.controller.validator.BookValidator;
import it.uniroma3.siwbooks.dto.BookInfoDto;
import it.uniroma3.siwbooks.dto.GenreDto;
import it.uniroma3.siwbooks.models.*;
import it.uniroma3.siwbooks.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static it.uniroma3.siwbooks.authentication.SecurityUtil.isIsAdmin;

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
    @Autowired
    private BookValidator bookValidator;

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
        model.addAttribute("book", new Books());
        return "FormBook"; // oppure il nome effettivo del file Thymeleaf
    }

    @PostMapping("/book/add")
    public String addBook(@ModelAttribute("book") Books book,
                          BindingResult bindingResult,                                // ← DEVE STARE SUBITO DOPO book

                          @RequestParam(value = "releaseDate", required = false)
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                              LocalDate releaseDateParam,

                          @RequestParam(value = "images", required = false)
                              MultipartFile[] imagesParam,

                          @RequestParam(value = "authors", required = false)
                              String authorsCsv,

                          @RequestParam(value = "genres", required = false)
                              String genresCsv,

                          RedirectAttributes redirectAttributes,
                          Model model) {

        // Valida il book (senza images e releaseDate automatici)
        bookValidator.validate(book, bindingResult);

        if (bindingResult.hasErrors() && false) {
            model.addAttribute("authors", autoreService.findAll());
            model.addAttribute("genres", genereService.findAll());
            model.addAttribute("book", book);
            List<String> errorMessages = new ArrayList<>();

// 1. Errori di campo, con nome del campo
            bindingResult.getFieldErrors().forEach(fe -> {
                String field = fe.getField();
                String msg   = fe.getDefaultMessage();
                errorMessages.add(String.format("%s: %s", field, msg));
            });

// 2. Errori globali (ObjectError)
            bindingResult.getGlobalErrors().forEach(oe -> {
                errorMessages.add(oe.getDefaultMessage());
            });

            model.addAttribute("errors", errorMessages);
            return "FormBook";
        } else if (!isIsAdmin()) {
            redirectAttributes.addFlashAttribute("error", "Non puoi Creare un libro. Solo admin!");
        }
        // Imposta releaseDate manualmente
        book.setReleaseDate(releaseDateParam.atStartOfDay());
        List<Autore> selectedAuthors = new ArrayList<>();
        if (authorsCsv != null && !authorsCsv.isBlank()) {
            selectedAuthors = Arrays.stream(authorsCsv.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .map(autoreService::findById)
                    .toList();
        }
        List<Genere> selectedGenres = new ArrayList<>();
        if (genresCsv != null && !genresCsv.isBlank()) {
            selectedGenres=Arrays.stream(genresCsv.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .map(genereService::findById)
                    .toList();
        }


        System.out.println("authorsCsv: " + authorsCsv);
        System.out.println("genresCsv: " + genresCsv);
        System.out.println("releaseDateParam: " + releaseDateParam);

        return "redirect:/";
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
/*





            // Ora puoi usare releaseDate e images come vuoi
            bookService.registerBook(book, imagesParam, selectedAuthors);

            redirectAttributes.addFlashAttribute("success", "Libro aggiunto con successo!");
            return "redirect:/book/" + book.getId();
        }

        return "redirect:/book";
 */