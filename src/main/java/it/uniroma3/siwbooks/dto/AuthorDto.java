package it.uniroma3.siwbooks.dto;

import it.uniroma3.siwbooks.models.Books;

import java.time.LocalDate;
import java.util.List;

public class AuthorDto {

    private Long id;

    private String nome;
    private String cognome;
    private String nationality;

    private String dateOfBirthStr;
    private String dateOfDeathStr;

    private LocalDate dateOfBirth;
    private LocalDate dateOfDeath;

    private List<Books> books;

    // Riferimento all'ImageDto esistente
    private ImageDto images;

    // === GETTER E SETTER ===

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getDateOfBirthStr() {
        return dateOfBirthStr;
    }

    public void setDateOfBirthStr(String dateOfBirthStr) {
        this.dateOfBirthStr = dateOfBirthStr;
    }

    public String getDateOfDeathStr() {
        return dateOfDeathStr;
    }

    public void setDateOfDeathStr(String dateOfDeathStr) {
        this.dateOfDeathStr = dateOfDeathStr;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDate getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(LocalDate dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
    }

    public List<Books> getBooks() {
        return books;
    }

    public void setBooks(List<Books> books) {
        this.books = books;
    }

    public ImageDto getImages() {
        return images;
    }

    public void setImages(ImageDto images) {
        this.images = images;
    }
}
