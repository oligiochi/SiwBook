package it.uniroma3.siwbooks.controller;

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

    private HttpHeaders getHeaders(Image Image, byte[] data) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(Image.getContentType()));
        headers.setContentLength(data.length);
        headers.setCacheControl(CacheControl.maxAge(Duration.ofDays(30)));
        return headers;
    }
}
