package it.uniroma3.siwbooks.service;

import it.uniroma3.siwbooks.models.Books;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

import it.uniroma3.siwbooks.models.Image;
import it.uniroma3.siwbooks.repository.ImageRepository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ImageService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");

    @Autowired
    private ImageRepository imageRepository;

    /**
     * Crea un'immagine dal MultipartFile caricato
     */
    public Image createImage(MultipartFile file) throws IOException, IllegalArgumentException {
        // Validazioni
        validateFile(file);

        // Legge i dati del file come byte array
        byte[] imageData = file.getBytes();

        // Crea e salva l'entità Image
        Image image = new Image();
        image.setFileName(generateUniqueFilename(file.getOriginalFilename()));
        image.setOriginalFileName(file.getOriginalFilename());
        image.setData(imageData);
        image.setFileSize(file.getSize());
        image.setContentType(file.getContentType());
        image.setUploadDate(LocalDateTime.now());

        return imageRepository.save(image);
    }

    /**
     * Valida il file caricato
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File non può essere vuoto");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File troppo grande. Massimo 5MB consentiti");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("Nome file non valido");
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Formato file non supportato. Formati consentiti: " +
                    String.join(", ", ALLOWED_EXTENSIONS));
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Il file deve essere un'immagine");
        }
    }

    /**
     * Estrae l'estensione del file
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    /**
     * Genera un nome file unico usando timestamp e UUID
     */
    private String generateUniqueFilename(String originalFilename) {
        String extension = getFileExtension(originalFilename).toLowerCase();
        long timestamp = System.currentTimeMillis();
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return timestamp + "_" + uuid + "." + extension;
    }

    /**
     * Elimina un'immagine dal database
     */
    public void deleteImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Immagine non trovata"));

        // Elimina solo il record dal database (non c'è più file fisico)
        imageRepository.delete(image);
    }

    /**
     * Trova un'immagine per ID
     */
    public Image findById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Immagine non trovata"));
    }

    /**
     * Ottiene il contenuto di un'immagine come byte array
     */
    public byte[] getImageContent(Long imageId) {
        Image image = findById(imageId);
        return image.getData();
    }

    /**
     * Crea multiple immagini da un array di MultipartFile
     */
    public List<Image> createImages(MultipartFile[] files) {
        List<Image> images = new ArrayList<>();

        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    try {
                        Image image = createImage(file);
                        images.add(image);
                    } catch (Exception e) {
                        // Log dell'errore, ma continua con gli altri file
                        System.err.println("Errore nel caricamento del file " +
                                file.getOriginalFilename() + ": " + e.getMessage());
                    }
                }
            }
        }

        return images;
    }

    // Aggiungi questi metodi al tuo ImageService esistente

    /**
     * Salva un'immagine già esistente (per aggiornamenti)
     */
    public Image saveImage(Image image) {
        return imageRepository.save(image);
    }

    /**
     * Trova immagini per book ID
     */
    public List<Image> findByBookId(Long bookId) {
        return imageRepository.findByBookId(bookId);
    }

    /**
     * Elimina tutte le immagini di un libro
     */
    @Transactional
    public void deleteImagesByBookId(Long bookId) {
        imageRepository.deleteByBookId(bookId);
    }

    /**
     * Associa immagini a un libro
     */
    @Transactional
    public void associateImagesToBook(List<Long> imageIds, Long bookId, BookService bookService) {
        Books book = bookService.findById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Libro non trovato");
        }

        for (Long imageId : imageIds) {
            Image image = findById(imageId);
            image.setBook(book);
            saveImage(image);
        }
    }

    /**
     * Trova immagini orfane (senza libro associato)
     */
    public List<Image> findOrphanImages() {
        return imageRepository.findByBookIsNull();
    }

    /**
     * Elimina immagini orfane più vecchie di X giorni
     */
    @Transactional
    public void cleanupOrphanImages(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        List<Image> orphanImages = imageRepository.findByBookIsNullAndUploadDateBefore(cutoffDate);

        imageRepository.deleteAll(orphanImages);
    }
}