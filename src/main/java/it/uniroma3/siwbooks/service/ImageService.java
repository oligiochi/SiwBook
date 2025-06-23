package it.uniroma3.siwbooks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

import it.uniroma3.siwbooks.models.Image;
import it.uniroma3.siwbooks.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageService {

    private static final String UPLOAD_DIR = "uploads/images/";
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

        // Genera nome file unico
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename).toLowerCase();
        String uniqueFilename = generateUniqueFilename() + "." + fileExtension;

        // Crea directory se non esiste
        createUploadDirectoryIfNotExists();

        // Salva il file fisicamente
        String filePath = UPLOAD_DIR + uniqueFilename;
        Path destinationPath = Paths.get(filePath);
        Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

        // Crea e salva l'entità Image
        Image image = new Image();
        image.setFileName(uniqueFilename);
        image.setOriginalFileName(originalFilename);
        image.setFilePath(filePath);
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
    private String generateUniqueFilename() {
        long timestamp = System.currentTimeMillis();
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return timestamp + "_" + uuid;
    }

    /**
     * Crea la directory di upload se non esiste
     */
    private void createUploadDirectoryIfNotExists() throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
    }

    /**
     * Elimina un'immagine (file e record dal database)
     */
    public void deleteImage(Long imageId) throws IOException {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Immagine non trovata"));

        // Elimina il file fisico
        Path filePath = Paths.get(image.getFilePath());
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }

        // Elimina il record dal database
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
    public byte[] getImageContent(Long imageId) throws IOException {
        Image image = findById(imageId);
        Path filePath = Paths.get(image.getFilePath());

        if (!Files.exists(filePath)) {
            throw new IOException("File immagine non trovato: " + image.getFilePath());
        }

        return Files.readAllBytes(filePath);
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
}