package it.uniroma3.siwbooks.repository;

import it.uniroma3.siwbooks.models.Genere;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GenereRepository extends CrudRepository<Genere, Long> {
    public Optional<Genere> findByGenere(String genere);

    @Query("SELECT COUNT(b) FROM Genere g JOIN g.libri b WHERE g.id = :genereId")
    int countLibriByGenereId(@Param("genereId") Long genereId);

}
