package it.uniroma3.siwbooks.controller;

import it.uniroma3.siwbooks.dto.BookInfoDto;
import it.uniroma3.siwbooks.models.Autore;
import it.uniroma3.siwbooks.models.Books;
import it.uniroma3.siwbooks.service.AutoreService;
import it.uniroma3.siwbooks.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
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
    @GetMapping("authors/form")
    public String showCreateForm(Model model) {
        model.addAttribute("autore", new Autore());
        model.addAttribute("booksList", bookService.findAll());
        model.addAttribute("formAction", "/authors");
        model.addAttribute("isEditMode", false);
        return "authorForm";
    }
}
