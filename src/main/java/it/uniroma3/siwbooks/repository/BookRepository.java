package it.uniroma3.siwbooks.repository;

import it.uniroma3.siwbooks.models.Autore;
import it.uniroma3.siwbooks.models.Books;
import it.uniroma3.siwbooks.models.Genere;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends CrudRepository<Books, Long> {

    // Ricerca per termine (titolo, autore, descrizione, genere)
    @Query("SELECT DISTINCT b FROM Books b " +
            "LEFT JOIN b.coautori ca " +
            "WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(b.author.nome) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(b.author.cognome) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(ca.nome) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(ca.cognome) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(b.generi) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Books> searchByTerm(@Param("searchTerm") String searchTerm);


    // Ricerca per anno di pubblicazione (convertito da LocalDateTime)
    @Query("SELECT b FROM Books b WHERE YEAR(b.releaseDate) = :year")
    List<Books> findByPublishedYear(@Param("year") Integer year);

    // Ricerca per range di anni (convertito da LocalDateTime)
    @Query("SELECT b FROM Books b WHERE YEAR(b.releaseDate) BETWEEN :startYear AND :endYear")
    List<Books> findByPublishedYearBetween(@Param("startYear") Integer startYear,
                                          @Param("endYear") Integer endYear);

    // Ricerca per autore
    List<Books> findByAuthor(Autore author);

    // Ricerca per genere
    List<Books> findByGeneriContainingIgnoreCase(List<Genere> generi);

    // Libri più popolari
    @Query("SELECT b FROM Books b JOIN b.recensioni r GROUP BY b ORDER BY AVG(r.stelle) DESC")
    List<Books> findPopularBooks();

    // Libri più recenti
    @Query("SELECT b FROM Books b ORDER BY b.releaseDate DESC")
    List<Books> findRecentBooks();

    // Conta libri per genere
    @Query("SELECT b.generi, COUNT(b) FROM Books b GROUP BY b.generi")
    List<Object[]> countBooksByGenre();

    // Conta libri per anno
    @Query("SELECT YEAR(b.releaseDate), COUNT(b) FROM Books b GROUP BY YEAR(b.releaseDate) ORDER BY YEAR(b.releaseDate) DESC")
    List<Object[]> countBooksByYear();

    // Statistiche globali
    @Query("SELECT COUNT(DISTINCT b), AVG(r.stelle), MAX(b.releaseDate), MIN(b.releaseDate) FROM Books b JOIN b.recensioni r")
    Object[] getBookStatistics();

}