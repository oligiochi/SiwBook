package it.uniroma3.siwbooks.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
@Entity
public class Books {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @NotNull
    private String title;
    @NotNull
    @ManyToMany
    private List<Autore> author;
    @ManyToMany
    private List<Genere> generi;
    @NotNull
    private LocalDateTime releaseDate;
    @Lob
    private byte[] pdf;
    @OneToMany
    private List<Image> images;

    private String imageType; // es. "image/jpeg"
    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Recensione> recensioni;

    public List<Genere> getGeneri() {
        return generi;
    }

    public void setGeneri(List<Genere> generi) {
        this.generi = generi;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public List<Recensione> getRecensioni() {
        return recensioni;
    }

    public void setRecensioni(List<Recensione> recensioni) {
        this.recensioni = recensioni;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Autore> getAuthor() {
        return author;
    }

    public void setAuthor(List<Autore> author) {
        this.author = author;
    }

    public LocalDateTime getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDateTime releaseDate) {
        this.releaseDate = releaseDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte[] getPdf() {
        return pdf;
    }

    public void setPdf(byte[] pdf) {
        this.pdf = pdf;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Books books = (Books) o;
        return Objects.equals(title, books.title) && Objects.equals(releaseDate, books.releaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, releaseDate);
    }
}
