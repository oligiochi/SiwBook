package it.uniroma3.siwbooks.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Recensione {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false)
    private Books libro; // relazione: molti a uno (molte recensioni per un libro)

    @Min(0)
    @Max(5)
    private int stelle; // valore da 0 a 5

    @Size(max = 1000)
    private String commento;

    // --- Getter e Setter ---

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
}
