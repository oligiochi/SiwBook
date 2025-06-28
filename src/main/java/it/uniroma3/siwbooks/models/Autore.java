package it.uniroma3.siwbooks.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Autore {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @NotNull
    private String nome;
    @NotNull
    private String cognome;

    @ManyToMany(mappedBy = "author")
    private List<Books>Libri;

    @NotNull
    @Past(message = "La Data deve essere al passato")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime dateOfBirth;

    @Past(message = "La Data deve essere al passato")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime dateOfDeath;
    @NotNull
    private String nationality;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    @JoinColumn(name = "Autore_id")
    private Image images;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Autore autore = (Autore) o;
        return Objects.equals(nome, autore.nome) && Objects.equals(cognome, autore.cognome) && Objects.equals(dateOfBirth, autore.dateOfBirth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, cognome, dateOfBirth);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public List<Books> getLibri() {
        return Libri;
    }

    public void setLibri(List<Books> libri) {
        Libri = libri;
    }

    public LocalDateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDateTime getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(LocalDateTime dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public Image getImages() {
        return images;
    }

    public void setImages(Image images) {
        this.images = images;
    }
}
