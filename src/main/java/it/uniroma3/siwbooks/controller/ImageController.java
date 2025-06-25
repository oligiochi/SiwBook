package it.uniroma3.siwbooks.controller;

import it.uniroma3.siwbooks.dto.ImageDto;
import it.uniroma3.siwbooks.models.Books;
import it.uniroma3.siwbooks.models.Image;
import it.uniroma3.siwbooks.service.BookService;
import it.uniroma3.siwbooks.service.ImageService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;

import java.io.IOException;
import java.time.Duration;

@Controller
@RequestMapping("/images")
public class ImageController {

    @Autowired
    private ImageService imageService;
    @Autowired
    private BookService bookService;

    /**
     * Serve un'immagine tramite il suo ID
     */
    @Transactional(readOnly = true)
    @GetMapping("/{imageId}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long imageId) {
        try {
            Image image = imageService.findById(imageId);
            byte[] imageContent = image.getData();

            HttpHeaders headers = getHeaders(image, imageContent);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(imageContent);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Elimina un'immagine
     */
    @DeleteMapping("/{imageId}")
    @ResponseBody
    public ResponseEntity<String> deleteImage(@PathVariable Long imageId) {
        try {
            imageService.deleteImage(imageId);
            return ResponseEntity.ok("Immagine eliminata con successo");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @Transactional(readOnly = true)
    @GetMapping("/book/{id}/cover")
    public ResponseEntity<byte[]> getBookCover(@PathVariable Long id) {
        Books book = bookService.findById(id);
        Image cover = (book != null) ? book.getCoverImage() : null;

        if (cover == null || cover.getData() == null) {
            try {
                ClassPathResource placeholder = new ClassPathResource("static/images/book_cover_placeholder2.png");
                byte[] data = placeholder.getInputStream().readAllBytes();
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(data);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        
        //genera heder
        HttpHeaders headers = getHeaders(cover, cover.getData());

        return ResponseEntity.ok()
                .headers(headers)
                .body(cover.getData());
    }

    private HttpHeaders getHeaders(Image image, byte[] data) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(image.getContentType()));
        headers.setContentLength(data.length);

        // Disattiva completamente la cache
        headers.setCacheControl(CacheControl.noStore());
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return headers;
    }

    // Aggiungi questi metodi al tuo ImageController esistente

    /**
     * Upload multiple immagini (AJAX)
     */
    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<?> uploadImages(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "bookId", required = false) Long bookId) {

        try {
            List<Image> uploadedImages = imageService.createImages(files);

            // Se c'è un bookId, associa le immagini al libro
            if (bookId != null) {
                Books book = bookService.findById(bookId);
                if (book != null) {
                    uploadedImages.forEach(img -> {
                        img.setBook(book);
                        imageService.saveImage(img); // Dovrai aggiungere questo metodo
                    });
                }
            }

            // Ritorna i dati delle immagini per il frontend
            List<ImageDto> imageDtos = uploadedImages.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "images", imageDtos,
                    "message", "Immagini caricate con successo"
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", "Errore durante l'upload: " + e.getMessage()
                    ));
        }
    }

    /**
     * Ottieni tutte le immagini di un libro
     */
    @GetMapping("/book/{bookId}/images")
    @ResponseBody
    public ResponseEntity<List<ImageDto>> getBookImages(@PathVariable Long bookId) {
        try {
            List<Image> images = imageService.findByBookId(bookId);
            List<ImageDto> imageDtos = images.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(imageDtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Upload temporaneo (senza associazione a libro)
     */
    @PostMapping("/upload/temp")
    @ResponseBody
    public ResponseEntity<?> uploadTempImages(@RequestParam("files") MultipartFile[] files) {
        return uploadImages(files, null);
    }

    // DTO Helper method
    private ImageDto convertToDto(Image image) {
        return new ImageDto(
                image.getId(),
                image.getFileName(),
                image.getOriginalFileName(),
                "/images/" + image.getId(), // URL per visualizzare l'immagine
                image.getFileSize(),
                image.getContentType()
        );
    }

}
