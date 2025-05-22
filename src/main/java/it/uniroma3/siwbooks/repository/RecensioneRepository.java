package it.uniroma3.siwbooks.repository;

import it.uniroma3.siwbooks.models.Books;
import it.uniroma3.siwbooks.models.Recensione;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RecensioneRepository extends CrudRepository<Recensione, Long> {
    // Trova tutte le recensioni di un libro specifico
    public List<Recensione> findByLibro(Books libro);

    // (Opzionale) Trova tutte le recensioni con un certo numero di stelle
    public List<Recensione> findByStelle(int stelle);

    // (Opzionale) Verifica se un libro ha almeno una recensione
    public boolean existsByLibro(Books libro);
}
