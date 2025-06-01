package it.uniroma3.siwbooks.service;

import it.uniroma3.siwbooks.models.Books;
import it.uniroma3.siwbooks.models.Genere;
import it.uniroma3.siwbooks.repository.GenereRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class GenereService {
    @Autowired
    private GenereRepository genereRepository;

    public List<Books> getLibriPerGenere(String nomeGenere) {
        return genereRepository.findByGenere(nomeGenere)
                .map(Genere::getLibri)
                .orElse(Collections.emptyList());
    }
}

