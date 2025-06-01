package it.uniroma3.siwbooks.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;

@Entity
public class Genere {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    private String genere;
    @NotNull
    @ManyToMany(mappedBy = "generi")
    private List<Books> libri;
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getGenere() {
        return genere;
    }

    public void setGenere(String genere) {
        this.genere = genere;
    }

    public List<Books> getLibri() {
        return libri;
    }

    public void setLibri(List<Books> libri) {
        this.libri = libri;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Genere genere1 = (Genere) o;
        return Objects.equals(genere, genere1.genere);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(genere);
    }
}
