package it.uniroma3.siwbooks.controller;

import it.uniroma3.siwbooks.dto.AuthorDto;
import it.uniroma3.siwbooks.dto.BookInfoDto;
import it.uniroma3.siwbooks.dto.ImageDto;
import it.uniroma3.siwbooks.models.Autore;
import it.uniroma3.siwbooks.models.Books;
import it.uniroma3.siwbooks.service.AutoreService;
import it.uniroma3.siwbooks.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Book;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class AutoriController {

    @Autowired
    private AutoreService autoreService;

    @Autowired
    private BookService bookService;

    @RequestMapping("/authors")
    public String showAutori(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(required = false) String search,
                             @RequestParam(defaultValue = "nameAsc") String sort,
                             Model model) {

        List<Autore> all = (search == null || search.isBlank())
                ? (List<Autore>) autoreService.findAll()
                : autoreService.searchByTerm(search);

        // Ordina in base alla richiesta
        switch (sort) {
            case "nameAsc" -> all.sort(Comparator.comparing(Autore::getNome));
            case "nameDesc" -> all.sort(Comparator.comparing(Autore::getNome).reversed());
            case "surnameAsc" -> all.sort(Comparator.comparing(Autore::getCognome));
            case "surnameDesc" -> all.sort(Comparator.comparing(Autore::getCognome).reversed());
            case "libriCount" -> all.sort(Comparator.comparing((Autore a) -> a.getLibri() != null ? a.getLibri().size() : 0).reversed());
            case "birthYearAsc" -> all.sort(Comparator.comparing(Autore::getDateOfBirth));
            case "birthYearDesc" -> all.sort(Comparator.comparing(Autore::getDateOfBirth).reversed());
            default -> all.sort(Comparator.comparing(Autore::getCognome)); // fallback
        }

        // Paginazione manuale
        Pageable pageable = PageRequest.of(page, 4);
        int total = all.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);
        List<Autore> content = (start <= end) ? all.subList(start, end) : List.of();

        Page<Autore> autorePage = new PageImpl<>(content, pageable, total);
        model.addAttribute("page", autorePage);
        model.addAttribute("sort", sort);
        return "autors";
    }
    @RequestMapping("/author/{id}")
    public String showAutori(@PathVariable("id") Long id,
                             @RequestParam(defaultValue = "0") int page,
                             Model model) {
        Autore autore=autoreService.findById(id);
        List<Books> opere=autore.getLibri();
        Pageable pageable = PageRequest.of(page, 4);
        int total = opere.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);
        List<Books> content = (start <= end) ? opere.subList(start, end) : List.of();

        Page<Books> bookPage = new PageImpl<>(content, pageable, total);
        List<BookInfoDto> dtos = buildBookInfo(bookPage);
        model.addAttribute("autore", autore);
        model.addAttribute("books", dtos);
        model.addAttribute("page", bookPage);
        model.addAttribute("totalCount", bookPage.getTotalElements());
        return "autore";
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
    @GetMapping("admin/deleteAuthor/{author_id}")
    public String removeAuthor(@PathVariable Long author_id) {
        autoreService.deleteAuthor(author_id);
        return "redirect:/authors";
    }

    @GetMapping("/admin/author/add")
    public String showCreateForm(Model model) {
        AuthorDto autore = new AuthorDto();
        model.addAttribute("autore", autore);
        model.addAttribute("booksList", bookService.findAll());
        List<Long> selectedBookIds=new ArrayList<>();

        model.addAttribute("selectedBookIds", selectedBookIds);
        model.addAttribute("formAction", "/admin/author/add");
        model.addAttribute("isEditMode", false);
        return "authorForm";
    }

    @PostMapping("/admin/author/add")
    public String createAuthor(
            @Valid @ModelAttribute("autore") AuthorDto authorDto,
            BindingResult bindingResult,
            @RequestParam(value = "bookIds", required = false) String bookIds,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            Model model
    ) {

        if (bindingResult.hasErrors()) {
            // Ricarica dati necessari per la view
            model.addAttribute("booksList", bookService.findAll());
            return "authorForm";  // nome della view Thymeleaf del form autore
        }

        try {
            // Parsing date (se il binding non lo fa già)
            if (authorDto.getDateOfBirthStr() != null && !authorDto.getDateOfBirthStr().isEmpty()) {
                authorDto.setDateOfBirth(LocalDate.parse(authorDto.getDateOfBirthStr()));
            }
            if (authorDto.getDateOfDeathStr() != null && !authorDto.getDateOfDeathStr().isEmpty()) {
                authorDto.setDateOfDeath(LocalDate.parse(authorDto.getDateOfDeathStr()));
            }

            // Caricamento libri associati (se presenti)
            List<Long> bookIdList = bookIds == null || bookIds.isEmpty()
                    ? List.of()
                    : Arrays.stream(bookIds.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            Iterable<Books> books = bookService.findByIds(bookIdList);
            List<Books> booksList = new ArrayList<>();
            books.forEach(booksList::add);
            authorDto.setBooks(booksList);

            // Salvataggio autore con immagine
            autoreService.saveAuthor(authorDto, imageFile);

            return "redirect:/authors";  // reindirizza lista autori dopo salvataggio

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Errore durante la creazione dell'autore: " + e.getMessage());
            model.addAttribute("booksList", bookService.findAll());
            return "authorForm";
        }
    }

    @GetMapping("/admin/author/edit/{id}")
    public String showEditForm(
            @PathVariable("id") Long id,
            Model model
    ) {
        // Carica l'entità Autore
        Autore existing = autoreService.findByIdOp(id)
                .orElseThrow(() -> new NoSuchElementException("Autore non trovato: " + id));;

        // Converte in DTO
        AuthorDto dto = new AuthorDto();
        dto.setId(existing.getId());
        dto.setNome(existing.getNome());
        dto.setCognome(existing.getCognome());
        dto.setNationality(existing.getNationality());
        if (existing.getDateOfBirth() != null) {
            dto.setDateOfBirth(existing.getDateOfBirth().toLocalDate());
            dto.setDateOfBirthStr(existing.getDateOfBirth().toLocalDate().toString());
        }
        if (existing.getDateOfDeath() != null) {
            dto.setDateOfDeath(existing.getDateOfDeath().toLocalDate());
            dto.setDateOfDeathStr(existing.getDateOfDeath().toLocalDate().toString());
        }
        dto.setBooks(existing.getLibri());
        if (existing.getImages() != null) {
            // se hai un solo ImageDto
            dto.setImages(new ImageDto(
                    existing.getImages().getId(),
                    existing.getImages().getFileName(),
                    existing.getImages().getOriginalFileName(),
                    "/images/"+existing.getImages().getId(),
                    existing.getImages().getFileSize(),
                    existing.getImages().getContentType()
            ));
        }

        // Prepara la lista degli ID già selezionati
        List<Long> selectedBookIds = dto.getBooks().stream()
                .map(Books::getId)
                .toList();

        model.addAttribute("autore", dto);
        model.addAttribute("booksList", bookService.findAll());
        model.addAttribute("selectedBookIds", selectedBookIds);
        model.addAttribute("formAction", "/admin/author/edit/" + id);
        model.addAttribute("isEditMode", true);
        return "authorForm";
    }

    // --- POST per salvare le modifiche ---
    @PostMapping("/admin/author/edit/{id}")
    public String updateAuthor(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("autore") AuthorDto authorDto,
            BindingResult bindingResult,
            @RequestParam(value = "bookIds", required = false) String bookIds,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("booksList", bookService.findAll());
            model.addAttribute("selectedBookIds",
                    bookIds == null ? List.of() :
                            Arrays.stream(bookIds.split(",")).map(Long::parseLong).toList()
            );
            model.addAttribute("formAction", "/admin/author/edit/" + id);
            model.addAttribute("isEditMode", true);
            return "authorForm";
        }

        try {
            // Parsing date
            if (authorDto.getDateOfBirthStr() != null && !authorDto.getDateOfBirthStr().isEmpty()) {
                authorDto.setDateOfBirth(LocalDate.parse(authorDto.getDateOfBirthStr()));
            }
            if (authorDto.getDateOfDeathStr() != null && !authorDto.getDateOfDeathStr().isEmpty()) {
                authorDto.setDateOfDeath(LocalDate.parse(authorDto.getDateOfDeathStr()));
            }

            // Libri associati
            List<Long> bookIdList = bookIds == null || bookIds.isEmpty()
                    ? List.of()
                    : Arrays.stream(bookIds.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .toList();
            Iterable<Books> booksIt = bookService.findByIds(bookIdList);
            List<Books> booksList = new ArrayList<>();
            booksIt.forEach(booksList::add);
            authorDto.setBooks(booksList);

            // Chiama il service che già gestisce create/update
            autoreService.saveAuthor(authorDto, imageFile);

            return "redirect:/authors";  // o dove elenchi gli autori

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Errore durante l'aggiornamento: " + e.getMessage());
            model.addAttribute("booksList", bookService.findAll());
            model.addAttribute("selectedBookIds",
                    bookIds == null ? List.of() :
                            Arrays.stream(bookIds.split(",")).map(Long::parseLong).toList()
            );
            model.addAttribute("formAction", "/admin/author/edit/" + id);
            model.addAttribute("isEditMode", true);
            return "authorForm";
        }
    }

}
