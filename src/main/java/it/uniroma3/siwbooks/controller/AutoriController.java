package it.uniroma3.siwbooks.controller;

import it.uniroma3.siwbooks.models.Autore;
import it.uniroma3.siwbooks.service.AutoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;

@Controller
public class AutoriController {

    @Autowired
    private AutoreService autoreService;

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
}
