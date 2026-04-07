package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class Genre implements Comparable<Genre> {

    private long id;
    private String name;

    @Override
    public int compareTo(Genre genre) {
        return (int) (this.id - genre.getId());
    }

}
