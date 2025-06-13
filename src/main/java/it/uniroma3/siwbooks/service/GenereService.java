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
    public Genere findById(Long id) {
        return genereRepository.findById(id).orElse(null);
    }
    public Genere findByGenere(String nomeGenere) {
        return genereRepository.findByGenere(nomeGenere).orElse(null);
    }

    public Genere save(Genere genere) {
        return genereRepository.save(genere);
    }
    public void delete(Genere genere) {
        genereRepository.delete(genere);
    }

    public List<Genere> findAll() {
        return (List<Genere>) genereRepository.findAll();
    }

    public int countLibriByGenere(Genere genere) {
        return genere.getLibri().size();
    }
}

