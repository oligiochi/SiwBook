package it.uniroma3.siwbooks.controller;

import it.uniroma3.siwbooks.models.Genere;
import it.uniroma3.siwbooks.service.AutoreService;
import it.uniroma3.siwbooks.service.BookService2;
import it.uniroma3.siwbooks.service.GenereService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/books")
class LibriController {

    @Autowired
    private GenereService genereService;

    @Autowired
    private BookService2 bookService2;

    @Autowired
    private AutoreService autoreService;

    @GetMapping
    public String list(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(name = "genre", required = false, defaultValue = "1") Long genreID,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "DATE_DESC") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        // ... la tua logica esistente

        // aggiungi la data di oggi al model
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("genere", categoriesGenerator(genreID));
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("sortBy", sortBy);
        return "books";
    }
    private List<Map<String,Object>> categoriesGenerator(Long genre){
        List<Genere> allGenres =  genereService.findAll();
        List<Map<String,Object>> categories = new ArrayList<>();
        Genere gen=genereService.findById(genre);
        for (Genere g : allGenres) {
            Map<String,Object> dto = new HashMap<>();
            dto.put("g",   g);                      // il nome del genere
            dto.put("count",    genereService.countLibriByGenere(g.getId()));  // quanti libri
            dto.put("selected", g.equals(gen));        // se è il filtro corrente
            categories.add(dto);
        }
        return categories;
    }


}
