package it.uniroma3.siwbooks.repository;

import it.uniroma3.siwbooks.models.Autore;
import it.uniroma3.siwbooks.models.Books;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface BookRepository extends CrudRepository<Books, Long> {
    @Query("SELECT b FROM Books b WHERE b.author.id = :authorId OR :authorId IN (SELECT a.id FROM b.coautori a)")
    public List<Books> findByAuthorOrCoauthor(@Param("authorId") Long authorId);
    public List<Books> findByTitle(String title);
    public List<Books> findByAuthor(Autore author);
    @Query("SELECT b FROM Books b WHERE FUNCTION('YEAR', b.releaseDate) = :year")
    List<Books> findByYear(@Param("year") int year);
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Books b WHERE b.title = :title AND FUNCTION('YEAR', b.releaseDate) = :year")
    boolean existsByTitleAndYear(@Param("title") String title, @Param("year") int year);
    @Query("SELECT DISTINCT b FROM Books b JOIN b.recensioni r WHERE r.stelle = :stelle")
    List<Books> findBooksByRating(@Param("stelle") int stelle);
    @Query("SELECT b FROM Books b JOIN b.recensioni r GROUP BY b ORDER BY AVG(r.stelle) DESC")
    List<Books> findAllBooksOrderByAverageRatingDesc();
    @Query("SELECT b FROM Books b JOIN b.recensioni r GROUP BY b ORDER BY AVG(r.stelle) ASC")
    List<Books> findAllBooksOrderByAverageRatingAsc();
}
