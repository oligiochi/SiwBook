package it.uniroma3.siwbooks.service;

import it.uniroma3.siwbooks.models.Books;
import it.uniroma3.siwbooks.models.Recensione;
import it.uniroma3.siwbooks.models.Utente;
import it.uniroma3.siwbooks.repository.RecensioneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Transactional
public class RecensioneService {

    @Autowired
    private RecensioneRepository recensioneRepository;

    public Recensione findById(Long id) {
        return recensioneRepository.findById(id).orElse(null);
    }

    public Iterable<Recensione> findAll() {
        return recensioneRepository.findAll();
    }

    public List<Recensione> findByLibro(Books libro) {
        return recensioneRepository.findByLibro(libro);
    }

    public List<Recensione> findByStelle(int stelle) {
        return recensioneRepository.findByStelle(stelle);
    }

    public boolean existsByLibro(Books libro) {
        return recensioneRepository.existsByLibro(libro);
    }

    public Recensione save(Recensione recensione) {
        return recensioneRepository.save(recensione);
    }

    public void delete(Recensione recensione) {
        recensioneRepository.delete(recensione);
    }

    public List<Recensione> getBookReviews(Long bookId) {
        return recensioneRepository.findByLibro_Id(bookId);
    }

    public boolean userHasAlreadyReviewed(Books book, Utente currentUser) {
        return recensioneRepository.existsByLibroAndAuthor(book,currentUser);
    }
}
