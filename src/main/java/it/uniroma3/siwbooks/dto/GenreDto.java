package it.uniroma3.siwbooks.dto;

import it.uniroma3.siwbooks.models.Genere;

public class GenreDto {
    private String genereName;
    private Long id;
    private boolean selected;
    private int count;

    public GenreDto(boolean selected, int count, String genereName, Long id) {
        this.selected  = selected;
        this.count     = count;
        this.genereName= genereName;
        this.id        = id;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getGenereName() {
        return genereName;
    }

    public void setGenereName(String genereName) {
        this.genereName = genereName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
