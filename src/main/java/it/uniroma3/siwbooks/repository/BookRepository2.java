package it.uniroma3.siwbooks.repository;

import it.uniroma3.siwbooks.models.Autore;
import it.uniroma3.siwbooks.models.Books;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface BookRepository2 extends CrudRepository<Books, Long> {
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
    @Modifying
    @Transactional
    @Query(nativeQuery=true, value = "INSERT into book_authors (authors_id, books_id) VALUES (:authorId, :bookId)")
    public void addAuthorToBook(@Param("authorId") Long authorId, @Param("bookId") Long bookId);

    @Modifying
    @Transactional
    @Query(nativeQuery=true, value = "DELETE from book_authors where authors_id= :authorId AND books_id= :bookId")
    public void removeAuthorFromBook(@Param("authorId") Long authorId, @Param("bookId") Long bookId);

    @Query(nativeQuery=true, value = "SELECT * from book b where lower(b.title) LIKE lower(concat(:title,'%'))")
    public Iterable<Books> findBookByTitleStartingWith(@Param("title") String title);
}
