package it.uniroma3.siwbooks.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for book information displayed in the view.
 */
public class BookInfoDto {

    private Long id;
    private String title;
    private LocalDate releaseDate;
    private double star;

    public BookInfoDto(long id, String title, LocalDateTime releaseDate, Double star) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate.toLocalDate();
        this.star = star;
    }

    public BookInfoDto(Long id, String title, LocalDate releaseDate, double star) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.star = star;
    }

    public BookInfoDto(boolean id, String title, LocalDateTime releaseDate, Double aDouble) {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public double getStar() {
        return star;
    }

    public void setStar(double star) {
        this.star = star;
    }

    @Override
    public String toString() {
        return "BookInfoDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", releaseDate=" + releaseDate +
                ", star=" + star +
                '}';
    }
}
