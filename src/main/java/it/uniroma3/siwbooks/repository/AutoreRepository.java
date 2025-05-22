package it.uniroma3.siwbooks.repository;

import it.uniroma3.siwbooks.models.Autore;
import org.springframework.data.repository.CrudRepository;
import java.util.List;


interface AutoreRepository extends CrudRepository<it.uniroma3.siwbooks.models.Autore, Long> {
    public boolean existsByNomeAndCognome(String nome, String cognome);
    public Autore findByNomeAndCognome(String nome, String cognome);
    public List<Autore> findAllByOrderByCognomeAsc();
    public List<Autore> findByNome(String nome);
    public List<Autore> findByCognome(String cognome);

}
