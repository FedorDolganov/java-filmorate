package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    void addFilm(Film film);

    void updateFilm(long id, Film film);

    Collection<Film> getAllFilms();

    Film getFilm(long id);

    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);

    List<Film> getListFilms(int count);

    boolean containsFilm(long filmId);

}
