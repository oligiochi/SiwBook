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
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.data.domain.Sort;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
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
    private RecensioneService recensioneService;
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
        Sort sort = buildSort(sortBy);
        Pageable pageable = PageRequest.of(page, 8, sort);

        Page<Books> bookPage = bookService.searchBooks(
                searchTerm, genreID, startDate, endDate, sort, pageable);

        List<BookInfoDto> dtos = buildBookInfo(bookPage);
        model.addAttribute("books", dtos);
        model.addAttribute("page", bookPage);
        model.addAttribute("totalCount", bookPage.getTotalElements());
        model.addAttribute("genere", buildGenreList(genreID));
        model.addAttribute("sortBy", sortBy);

        return "books";
    }

    @GetMapping("/book/{id}")
    public String getBook(@PathVariable("id") Long id, Model model) {
        Books book = bookService.findById(id);
        if (book == null) {
            return "redirect:/books";
        }

        // Ottieni recensioni ordinate
        List<Recensione> allReviews = recensioneService.getBookReviews(id);
        Utente currentUser = userService.getCurrentUser();

        List<Recensione> sortedReviews = sortReviewsByCurrentUser(allReviews, currentUser);
        boolean hasUserReview = hasCurrentUserReview(sortedReviews, currentUser);

        // Ottieni immagini del libro
        List<Long> imageIds = bookService.getImageSummariesForBook(id);
        if (!imageIds.isEmpty()) {
            imageIds.removeFirst();
        }

        model.addAttribute("book", book);
        model.addAttribute("hasUserReview", hasUserReview);
        model.addAttribute("bookReviews", sortedReviews);
        model.addAttribute("imageIds", imageIds);

        return "book";
    }

    @GetMapping("/admin/book/add")
    public String showAddBookForm(Model model) {
        if (!isIsAdmin()) {
            return "redirect:/";
        }

        prepareFormData(model, new Books(), null, false);
        model.addAttribute("formAction", "/admin/book/add");
        return "FormBook";
    }

    @PostMapping("/admin/book/add")
    public String addBook(@ModelAttribute("book") Books book,
                          BindingResult bindingResult,
                          @RequestParam(value = "releaseDate", required = false) String releaseDateString,
                          @RequestParam(value = "author", required = false) String authorsCsv,
                          @RequestParam(value = "generi", required = false) String genresCsv,
                          @RequestParam(value = "imageIds", required = false) String imageIds,
                          @RequestParam(value = "removeImageIds", required = false) List<Long> removeImageIds,
                          RedirectAttributes redirectAttributes,
                          Model model) {

        if (!isIsAdmin()) {
            redirectAttributes.addFlashAttribute("error", "Non hai i permessi per creare un libro");
            return "redirect:/";
        }

        try {
            // Validazione
            validateBookData(book, releaseDateString, authorsCsv, genresCsv, bindingResult);
            bookValidator.validate(book, bindingResult);

            if (bindingResult.hasErrors()) {
                prepareFormData(model, book, imageIds, false);
                model.addAttribute("formAction", "/admin/book/add");
                return "FormBook";
            }

            // Salva il libro
            bookService.save(book);

            // Gestisci le immagini
            handleImages(book.getId(), imageIds, removeImageIds);

            redirectAttributes.addFlashAttribute("success", "Libro aggiunto con successo!");
            return "redirect:/book/" + book.getId();

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Errore durante il salvataggio: " + e.getMessage());
            prepareFormData(model, book, imageIds, false);
            model.addAttribute("formAction", "/admin/book/add");
            return "FormBook";
        }
    }

    @GetMapping("/admin/deleteBook/{book_id}")
    public String removeBook(@PathVariable Long book_id, RedirectAttributes redirectAttributes) {
        if (!isIsAdmin()) {
            redirectAttributes.addFlashAttribute("error", "Non hai i permessi per eliminare un libro");
            return "redirect:/books";
        }

        try {
            bookService.deleteBook(book_id);
            redirectAttributes.addFlashAttribute("success", "Libro eliminato con successo!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Errore durante l'eliminazione del libro");
        }

        return "redirect:/books";
    }

    @GetMapping("/admin/editBook/{book_id}")
    public String editBook(@PathVariable Long book_id, Model model) {
        if (!isIsAdmin()) {
            return "redirect:/";
        }

        Books book = bookService.findById(book_id);
        if (book == null) {
            return "redirect:/books";
        }

        prepareFormData(model, book, null, true);

        // Carica le immagini esistenti per l'edit
        List<Image> bookImages = imageService.findByBookId(book_id);
        model.addAttribute("bookImages", bookImages);
        model.addAttribute("formAction", "/book/editBook/" + book_id);

        return "FormBook";
    }

    @PostMapping("/book/editBook/{book_id}")
    public String updateBook(
            @PathVariable Long book_id,
            @ModelAttribute("book") Books book,
            BindingResult bindingResult,
            @RequestParam(value = "releaseDate", required = false) String releaseDateString,
            @RequestParam(value = "author", required = false) String authorsCsv,
            @RequestParam(value = "generi", required = false) String genresCsv,
            @RequestParam(value = "imageIds", required = false) String imageIds,
            @RequestParam(value = "removeImageIdsCsv", required = false) String removeImageIdsCsv,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (!isIsAdmin()) {
            redirectAttributes.addFlashAttribute("error", "Non hai i permessi per modificare un libro");
            return "redirect:/";
        }

        try {
            // Assicurati che l'ID sia corretto
            book.setId(book_id);

            // Validazione
            validateBookData(book, releaseDateString, authorsCsv, genresCsv, bindingResult);

            if (bindingResult.hasErrors()) {
                prepareFormData(model, book, imageIds, true);
                model.addAttribute("formAction", "/book/editBook/" + book_id);
                return "FormBook";
            }

            // Aggiorna il libro
            bookService.update(book);

            // Gestisci le immagini
            handleImages(book.getId(), imageIds, removeImageIdsCsv);

            redirectAttributes.addFlashAttribute("success", "Libro aggiornato con successo!");
            return "redirect:/book/" + book.getId();

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Errore durante l'aggiornamento: " + e.getMessage());
            prepareFormData(model, book, imageIds, true);
            model.addAttribute("formAction", "/book/editBook/" + book_id);
            return "FormBook";
        }
    }

    // Metodi di utilità privati

    private Sort buildSort(String sortBy) {
        return switch(sortBy) {
            case "DATE_ASC"   -> Sort.by("releaseDate").ascending();
            case "TITLE"      -> Sort.by("title").ascending();
            case "TITLE_DESC" -> Sort.by("title").descending();
            case "RATING"     -> Sort.by("popularity").descending();
            default           -> Sort.by("releaseDate").descending();
        };
    }

    private List<BookInfoDto> buildBookInfo(Page<Books> bookPage) {
        return bookPage.getContent().stream()
                .map(book -> new BookInfoDto(
                        book.getId(),
                        book.getTitle(),
                        book.getReleaseDate(),
                        bookService.AvgStar(book)
                ))
                .collect(Collectors.toList());
    }

    private List<GenreDto> buildGenreList(Long selectedGenreId) {
        return genereService.findAll().stream()
                .map(g -> new GenreDto(
                        g.getId().equals(selectedGenreId),
                        genereService.countLibriByGenere(g),
                        g.getGenere(),
                        g.getId()
                ))
                .collect(Collectors.toList());
    }

    private List<Recensione> sortReviewsByCurrentUser(List<Recensione> reviews, Utente currentUser) {
        if (currentUser == null) {
            return reviews;
        }

        return reviews.stream()
                .sorted(Comparator.comparing(
                        r -> !r.getAuthor().equals(currentUser)
                ))
                .collect(Collectors.toList());
    }

    private boolean hasCurrentUserReview(List<Recensione> sortedReviews, Utente currentUser) {
        return currentUser != null &&
                !sortedReviews.isEmpty() &&
                sortedReviews.get(0).getAuthor().equals(currentUser);
    }

    private void validateBookData(Books book, String releaseDateString, String authorsCsv,
                                  String genresCsv, BindingResult bindingResult) {
        validateAndSetReleaseDate(book, releaseDateString, bindingResult);
        validateAndSetAuthors(book, authorsCsv, bindingResult);
        validateAndSetGenres(book, genresCsv, bindingResult);
    }

    private void validateAndSetReleaseDate(Books book, String releaseDateString, BindingResult bindingResult) {
        if (releaseDateString == null || releaseDateString.isBlank()) {
            bindingResult.rejectValue("releaseDate", "error.required", "Data di pubblicazione obbligatoria");
            return;
        }

        try {
            LocalDate date = LocalDate.parse(releaseDateString);
            book.setReleaseDate(date.atStartOfDay());
        } catch (DateTimeParseException e) {
            bindingResult.rejectValue("releaseDate", "error.date", "Formato data non valido");
        }
    }

    private void validateAndSetAuthors(Books book, String authorsCsv, BindingResult bindingResult) {
        if (authorsCsv == null || authorsCsv.isBlank()) {
            bindingResult.rejectValue("author", "error.required", "Almeno un autore è obbligatorio");
            return;
        }

        try {
            List<Long> authorIds = parseIds(authorsCsv);
            List<Autore> authors = authorIds.stream()
                    .map(autoreService::findById)
                    .filter(Objects::nonNull) // Filtra autori null
                    .collect(Collectors.toList());

            if (authors.isEmpty()) {
                bindingResult.rejectValue("author", "error.notfound", "Nessun autore valido trovato");
                return;
            }

            book.setAuthor(authors);
        } catch (NumberFormatException e) {
            bindingResult.rejectValue("author", "error.format", "Formato autori non valido");
        }
    }

    private void validateAndSetGenres(Books book, String genresCsv, BindingResult bindingResult) {
        if (genresCsv == null || genresCsv.isBlank()) {
            bindingResult.rejectValue("generi", "error.required", "Almeno un genere è obbligatorio");
            return;
        }

        try {
            List<Long> genreIds = parseIds(genresCsv);
            List<Genere> genres = genreIds.stream()
                    .map(genereService::findById)
                    .filter(Objects::nonNull) // Filtra generi null
                    .collect(Collectors.toList());

            if (genres.isEmpty()) {
                bindingResult.rejectValue("generi", "error.notfound", "Nessun genere valido trovato");
                return;
            }

            book.setGeneri(genres);
        } catch (NumberFormatException e) {
            bindingResult.rejectValue("generi", "error.format", "Formato generi non valido");
        }
    }

    private List<Long> parseIds(String csv) {
        if (csv == null || csv.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    private void handleImages(Long bookId, String imageIds, String removeImageIdsCsv) {
        // Associa nuove immagini
        if (imageIds != null && !imageIds.trim().isEmpty()) {
            List<Long> newImageIds = parseIds(imageIds);
            if (!newImageIds.isEmpty()) {
                imageService.associateImagesToBook(newImageIds, bookId, bookService);
            }
        }

        // Rimuovi immagini selezionate
        if (removeImageIdsCsv != null && !removeImageIdsCsv.isBlank()) {
            List<Long> imagesToRemove = parseIds(removeImageIdsCsv);
            imagesToRemove.forEach(imageId -> {
                try {
                    imageService.deleteImage(imageId);
                } catch (Exception e) {
                    // Log dell'errore ma continua con le altre immagini
                    System.err.println("Errore eliminando immagine " + imageId + ": " + e.getMessage());
                }
            });
        }
    }

    private void handleImages(Long bookId, String imageIds, List<Long> removeImageIds) {
        // Associa nuove immagini
        if (imageIds != null && !imageIds.trim().isEmpty()) {
            List<Long> newImageIds = parseIds(imageIds);
            if (!newImageIds.isEmpty()) {
                imageService.associateImagesToBook(newImageIds, bookId, bookService);
            }
        }

        // Rimuovi immagini selezionate
        if (removeImageIds != null && !removeImageIds.isEmpty()) {
            removeImageIds.forEach(imageId -> {
                try {
                    imageService.deleteImage(imageId);
                } catch (Exception e) {
                    System.err.println("Errore eliminando immagine " + imageId + ": " + e.getMessage());
                }
            });
        }
    }

    private void prepareFormData(Model model, Books book, String imageIds, boolean isEditMode) {
        model.addAttribute("authors", autoreService.findAll());
        model.addAttribute("genres", genereService.findAll());
        model.addAttribute("book", book);
        model.addAttribute("isEditMode", isEditMode);

        // Gestione immagini del libro per il form di aggiunta
        if (imageIds != null && !imageIds.trim().isEmpty()) {
            List<Long> imageIdList = parseIds(imageIds);
            if (!imageIdList.isEmpty()) {
                List<Image> bookImages = imageIdList.stream()
                        .map(imageService::findById)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                model.addAttribute("bookImages", bookImages);
            }
        }
    }
}