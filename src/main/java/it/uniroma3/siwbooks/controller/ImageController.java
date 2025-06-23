package it.uniroma3.siwbooks.controller;

import it.uniroma3.siwbooks.models.Image;
import it.uniroma3.siwbooks.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@Controller
@RequestMapping("/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    /**
     * Serve un'immagine tramite il suo ID
     */
    @GetMapping("/{imageId}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long imageId) {
        try {
            Image image = imageService.findById(imageId);
            byte[] imageContent = image.getData();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(image.getContentType()));
            headers.setContentLength(imageContent.length);
            headers.setCacheControl(CacheControl.maxAge(Duration.ofDays(30)));

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
}
