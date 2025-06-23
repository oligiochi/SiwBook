package it.uniroma3.siwbooks.repository;

import it.uniroma3.siwbooks.models.Image;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends CrudRepository<Image, Long> {

    /**
     * Trova tutte le immagini di un libro specifico
     */
    List<Image> findByBookId(Long bookId);

    /**
     * Trova immagini per nome file
     */
    Optional<Image> findByFileName(String fileName);

    /**
     * Elimina tutte le immagini di un libro
     */
    void deleteByBookId(Long bookId);

    /**
     * Conta il numero di immagini per un libro
     */
    long countByBookId(Long bookId);

    /**
     * Trova immagini caricate in un determinato periodo
     */
    List<Image> findByUploadDateBetween(LocalDateTime start, LocalDateTime end);
}
