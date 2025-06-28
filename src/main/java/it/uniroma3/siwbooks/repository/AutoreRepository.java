package it.uniroma3.siwbooks.repository;

import it.uniroma3.siwbooks.models.Autore;
import it.uniroma3.siwbooks.models.Books;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface AutoreRepository extends CrudRepository<it.uniroma3.siwbooks.models.Autore, Long> {
    public boolean existsByNomeAndCognome(String nome, String cognome);
    public Autore findByNomeAndCognome(String nome, String cognome);
    public List<Autore> findAllByOrderByCognomeAsc();
    public List<Autore> findByNome(String nome);
    public List<Autore> findByCognome(String cognome);
    @Query("""
        FROM Autore a
        WHERE (:searchTerm IS NULL
               OR LOWER(a.nome)   LIKE LOWER(CONCAT('%', :searchTerm, '%'))
               OR LOWER(a.cognome)LIKE LOWER(CONCAT('%', :searchTerm, '%'))
               OR LOWER(a.nationality) = LOWER(:searchTerm)                    
              )
    """)
    List<Autore> searchByTerm(@Param("searchTerm") String searchTerm);

    @Modifying
    @Query(value = "DELETE FROM book_authors WHERE authors_id = :authors_id", nativeQuery = true)
    void deleteAllBookFromAuthor(@Param("authors_id") Long authors_id);

}
