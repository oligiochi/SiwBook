package it.uniroma3.siwbooks.repository;

import it.uniroma3.siwbooks.models.Autore;
import it.uniroma3.siwbooks.models.Books;
import it.uniroma3.siwbooks.models.Genere;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends CrudRepository<Books, Long> {

    // Ricerca per termine (titolo, autore, descrizione, genere)
    @Query("""
        SELECT DISTINCT b 
        FROM Books b
        LEFT JOIN b.author ca
        LEFT JOIN b.generi g
        WHERE (:searchTerm IS NULL
               OR LOWER(b.title)   LIKE LOWER(CONCAT('%', :searchTerm, '%'))
               OR LOWER(ca.nome)   LIKE LOWER(CONCAT('%', :searchTerm, '%'))
               OR LOWER(ca.cognome)LIKE LOWER(CONCAT('%', :searchTerm, '%'))
               OR LOWER(g.genere) = LOWER(:searchTerm)                    
              )
    """)
    List<Books> searchByTerm(@Param("searchTerm") String searchTerm);


    // Ricerca per anno di pubblicazione (convertito da LocalDateTime)
    @Query("SELECT b FROM Books b WHERE YEAR(b.releaseDate) = :year")
    List<Books> findByPublishedDateTime(@Param("year") LocalDateTime Data);

    // Ricerca per range di anni (convertito da LocalDateTime)
    @Query("SELECT b FROM Books b WHERE b.releaseDate BETWEEN :startDateTime AND :endDateTime")
    List<Books> findByReleaseDateTimeBetween(@Param("startDateTime") LocalDateTime start, @Param("endDateTime")   LocalDateTime end);

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

    // Libri ordine Alfabetico
    @Query("SELECT b FROM Books b ORDER BY b.title ASC")
    List<Books> findAllBooksOrderByTitleAsc();

    // Conta libri per anno
    @Query("SELECT YEAR(b.releaseDate), COUNT(b) FROM Books b GROUP BY YEAR(b.releaseDate) ORDER BY YEAR(b.releaseDate) DESC")
    List<Object[]> countBooksByYear();

    // Statistiche globali
    @Query("SELECT COUNT(DISTINCT b), AVG(r.stelle), MAX(b.releaseDate), MIN(b.releaseDate) FROM Books b JOIN b.recensioni r")
    Object[] getBookStatistics();

    @Query("SELECT b FROM Books b WHERE LOWER(REPLACE(b.title, ' ', '')) LIKE LOWER(CONCAT('%', REPLACE(:title, ' ', ''), '%'))")
    List<Books> searchByTitleIgnoringSpaces(@Param("title") String title);

    @Query("SELECT i.id FROM Image i WHERE i.book.id = :bookId")
    List<Long> findImageIdsByBookId(@Param("bookId") Long bookId);


}