package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Builder
@Data
public class Film implements Comparable<Film> {

    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private Set<Long> likes;

    @Override
    public int compareTo(Film film) {
        return film.getLikes().size() - this.getLikes().size();
    }
}
