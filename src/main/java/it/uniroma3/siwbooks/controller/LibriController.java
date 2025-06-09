package it.uniroma3.siwbooks.controller;

import it.uniroma3.siwbooks.service.AutoreService;
import it.uniroma3.siwbooks.service.BookService;
import it.uniroma3.siwbooks.service.GenereService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/books")
class LibriController {

    @Autowired
    private GenereService genereService;

    @Autowired
    private BookService bookService;

    @Autowired
    private AutoreService autoreService;



}
