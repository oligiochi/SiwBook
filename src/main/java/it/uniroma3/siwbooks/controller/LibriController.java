package it.uniroma3.siwbooks.controller;

import it.uniroma3.siwbooks.controller.validator.BookValidator;
import it.uniroma3.siwbooks.dto.BookInfoDto;
import it.uniroma3.siwbooks.dto.GenreDto;
import it.uniroma3.siwbooks.models.*;
import it.uniroma3.siwbooks.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
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
    @Autowired
    private ImageService imageService;

    @InitBinder("book")
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("releaseDate", "images", "author", "generi");
    }

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
        List<Long> imageIds=bookService.getImageSummariesForBook(id);
        if(!imageIds.isEmpty()) {
            imageIds.removeFirst();
        }
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
        model.addAttribute("imageIds",  imageIds);
        return "book";
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
                          BindingResult bindingResult,

                          @RequestParam(value = "releaseDate", required = false) String releaseDateString,
                          @RequestParam(value = "images", required = false) MultipartFile[] imagesParam,
                          @RequestParam(value = "author", required = false) String authorsCsv,
                          @RequestParam(value = "generi", required = false) String genresCsv,

                          RedirectAttributes redirectAttributes,
                          Model model) {

        // Gestione manuale di releaseDate
        if (releaseDateString != null && !releaseDateString.isBlank()) {
            try {
                LocalDate date = LocalDate.parse(releaseDateString);
                book.setReleaseDate(date.atStartOfDay());
            } catch (DateTimeParseException e) {
                bindingResult.rejectValue("releaseDate", "error.date", "Formato data non valido");
            }
        } else {
            bindingResult.rejectValue("releaseDate", "error.required", "Data di pubblicazione obbligatoria");
        }

        // Gestione manuale degli autori
        List<Autore> selectedAuthors = new ArrayList<>();
        if (authorsCsv != null && !authorsCsv.isBlank()) {
            try {
                selectedAuthors = Arrays.stream(authorsCsv.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(Long::parseLong)
                        .map(autoreService::findById)
                        .collect(Collectors.toList());
                book.setAuthor(selectedAuthors);
            } catch (NumberFormatException e) {
                bindingResult.rejectValue("author", "error.format", "Formato autori non valido");
            }
        } else {
            bindingResult.rejectValue("author", "error.required", "Almeno un autore è obbligatorio");
        }

        // Gestione manuale dei generi
        List<Genere> selectedGenres = new ArrayList<>();
        if (genresCsv != null && !genresCsv.isBlank()) {
            try {
                selectedGenres = Arrays.stream(genresCsv.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(Long::parseLong)
                        .map(genereService::findById)
                        .collect(Collectors.toList());
                book.setGeneri(selectedGenres);
            } catch (NumberFormatException e) {
                bindingResult.rejectValue("generi", "error.format", "Formato generi non valido");
            }
        } else {
            bindingResult.rejectValue("generi", "error.required", "Almeno un genere è obbligatorio");
        }

        // Gestione manuale delle immagini
        List<Image> images = new ArrayList<>();
        if (imagesParam != null && imagesParam.length > 0) {
            for (MultipartFile file : imagesParam) {
                if (!file.isEmpty()) {
                    try {
                        // Qui devi implementare la logica per salvare l'immagine
                        // e creare l'oggetto Image
                        Image image = imageService.createImage(file);
                        images.add(image);
                    } catch (Exception e) {
                        bindingResult.rejectValue("images", "error.upload", "Errore nel caricamento delle immagini");
                        break;
                    }
                }
            }
        }
        book.setImages(images);

        // Valida il book solo sui campi che non gestisci manualmente
        bookValidator.validate(book, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("authors", autoreService.findAll());
            model.addAttribute("genres", genereService.findAll());
            model.addAttribute("book", book);
            return "FormBook";
        }

        if (!isIsAdmin() && false) {
            redirectAttributes.addFlashAttribute("error", "Non puoi Creare un libro. Solo admin!");
            return "redirect:/";
        }

        // Salva il libro
        bookService.save(book);
        redirectAttributes.addFlashAttribute("success", "Libro aggiunto con successo!");

        return "redirect:/books";
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