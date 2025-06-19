package it.uniroma3.siwbooks.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Recensione {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false,cascade = CascadeType.MERGE)
    @JoinColumn(name = "book_id", nullable = false)
    private Books libro; // relazione: molti a uno (molte recensioni per un libro)

    @Min(1)
    @Max(5)
    private int stelle; // valore da 0 a 5
    private LocalDateTime data;

    @Size(max = 1000)
    @NotBlank
    private String commento;

    @Size(max = 100)
    @NotBlank
    private String titolo;

    @ManyToOne
    private Utente author;

    // --- Getter e Setter ---


    public Utente getAuthor() {
        return author;
    }

    public void setAuthor(Utente author) {
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Books getLibro() {
        return libro;
    }

    public void setLibro(Books libro) {
        this.libro = libro;
    }

    public int getStelle() {
        return stelle;
    }

    public void setStelle(int stelle) {
        this.stelle = stelle;
    }

    public String getCommento() {
        return commento;
    }

    public void setCommento(String commento) {
        this.commento = commento;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Recensione that)) return false;
        return Objects.equals(libro, that.libro) && Objects.equals(author, that.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(libro, author);
    }
}
