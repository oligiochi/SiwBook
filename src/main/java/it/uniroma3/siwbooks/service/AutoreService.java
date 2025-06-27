package it.uniroma3.siwbooks.service;

import it.uniroma3.siwbooks.models.Autore;
import it.uniroma3.siwbooks.repository.AutoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
//@Transactional
public class AutoreService {

    @Autowired
    private AutoreRepository repository;

    public Autore findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Iterable<Autore> findAll() {
        return repository.findAll();
    }

    public List<Autore> findByNome(String nome) {
        return repository.findByNome(nome);
    }

    public List<Autore> findByCognome(String cognome) {
        return repository.findByCognome(cognome);
    }

    public List<Autore> findAllByOrderByCognomeAsc() {
        return repository.findAllByOrderByCognomeAsc();
    }

    public Autore findByNomeAndCognome(String nome, String cognome) {
        return repository.findByNomeAndCognome(nome, cognome);
    }

    public boolean existsByNomeAndCognome(String nome, String cognome) {
        return repository.existsByNomeAndCognome(nome, cognome);
    }

    public Autore save(Autore autore) {
        return repository.save(autore);
    }

    public void delete(Autore autore) {
        repository.delete(autore);
    }

    public List<Autore> searchByTerm(String term) {
        return repository.searchByTerm(term);
    }
}
