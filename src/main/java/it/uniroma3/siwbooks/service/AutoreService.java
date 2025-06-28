package it.uniroma3.siwbooks.service;

import it.uniroma3.siwbooks.dto.AuthorDto;
import it.uniroma3.siwbooks.models.Autore;
import it.uniroma3.siwbooks.models.Books;
import it.uniroma3.siwbooks.models.Image;
import it.uniroma3.siwbooks.repository.AutoreRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
//@Transactional
public class AutoreService {

    @Autowired
    private AutoreRepository repository;

    @Autowired
    private ImageService imageStorageService;

    public Autore findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Iterable<Autore> findAll() {
        return repository.findAll();
    }

    public List<Autore> findByNome(String nome) {
        return repository.findByNome(nome);
    }

    public List<Autore> findByCognome(String cognome) {
        return repository.findByCognome(cognome);
    }

    public List<Autore> findAllByOrderByCognomeAsc() {
        return repository.findAllByOrderByCognomeAsc();
    }

    public Autore findByNomeAndCognome(String nome, String cognome) {
        return repository.findByNomeAndCognome(nome, cognome);
    }

    public boolean existsByNomeAndCognome(String nome, String cognome) {
        return repository.existsByNomeAndCognome(nome, cognome);
    }

    public Autore save(Autore autore) {
        return repository.save(autore);
    }

    public void delete(Autore autore) {
        repository.delete(autore);
    }

    public List<Autore> searchByTerm(String term) {
        return repository.searchByTerm(term);
    }

    @Transactional
    public void deleteAuthor(Long id) {
        repository.deleteAllBookFromAuthor(id);
        repository.deleteById(id);
    }

    public void saveAuthor(AuthorDto dto, MultipartFile imageFile) throws IOException {
        Autore author = new Autore();

        // Carica autore esistente se presente
        if (dto.getId() != null) {
            author = repository.findById(dto.getId())
                    .orElse(new Autore());
        }

        author.setNome(dto.getNome());
        author.setCognome(dto.getCognome());
        author.setNationality(dto.getNationality());
        author.setDateOfBirth(dto.getDateOfBirth().atStartOfDay());

        if (dto.getDateOfDeath() != null) {
            author.setDateOfDeath(dto.getDateOfDeath().atStartOfDay());
        }

        author.setLibri(dto.getBooks());

        // Associa autore ai libri
        for (Books libro : dto.getBooks()) {
            libro.getAuthor().add(author);
        }

        // Salva autore prima di associare l'immagine
        Autore savedAuthor = repository.save(author);

        // Salva immagine se presente
        if (imageFile != null && !imageFile.isEmpty()) {
            Image img = new Image();
            img.setContentType(imageFile.getContentType());

            String cleaned = StringUtils.cleanPath(Objects.requireNonNull(imageFile.getOriginalFilename()));
            img.setFileName(cleaned);
            img.setOriginalFileName(cleaned);
            img.setFileSize(imageFile.getSize());
            img.setUploadDate(LocalDateTime.now());
            img.setData(imageFile.getBytes());

            // Salva immagine
            Image savedImage = imageStorageService.saveImage(img);

            // Aggiorna autore con l'immagine salvata
            savedAuthor.setImages(savedImage);
            repository.save(savedAuthor); // Salva di nuovo con l'immagine
        }
    }

}
