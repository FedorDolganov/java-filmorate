package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Film addFilm(Film film);

    void updateFilm(long id, Film film);

    Collection<Film> getAllFilms();

    Film getFilm(long id);

    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);

    List<Film> getListFilms(int count);

    Collection<Genre> getAllGenres();

    Genre getGenreById(long id);

    Collection<MPA> getAllMPA();

    MPA getMPAById(long id);

}
