package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@ToString
public class Film implements Comparable<Film> {

    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private Set<Long> likes;
    private LinkedHashSet<Genre> genres;
    private MPA mpa;

    @Override
    public int compareTo(Film film) {
        return film.getLikes().size() - this.getLikes().size();
    }
}
