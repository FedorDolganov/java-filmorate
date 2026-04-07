package ru.yandex.practicum.filmorate.service.film;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

@Service
@AllArgsConstructor
public class FilmService {

    private FilmDbStorage filmStorage;
    private UserDbStorage userStorage;


    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilm(long filmId) {
        filmStorage.getFilm(filmId);

        return filmStorage.getFilm(filmId);
    }

    public Film createFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Неверные данные имени фильма");
        }

        if (film.getDescription() == null || film.getDescription().isBlank() || film.getDescription().length() > 200) {
            throw new ValidationException("Неверные данные описания фильма");
        }

        if (film.getGenres() == null) {
            film.setGenres(new LinkedHashSet<>());
        }

        for (Genre genre : film.getGenres()) {
            filmStorage.getGenreById(genre.getId());
        }

        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Неверные данные даты релиза фильма");
        }

        if (film.getDuration() <= 0) {
            throw new ValidationException("Неверные данные продолжительности фильма");
        }

        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }

        filmStorage.getMPAById(film.getMpa().getId());

        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        filmStorage.getFilm(film.getId());

        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }

        filmStorage.updateFilm(film.getId(), film);

        return filmStorage.getFilm(film.getId());
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getListFilms(count);
    }

    public Film addLike(long filmId, long userId) {
        userStorage.getUser(userId);

        filmStorage.getFilm(filmId);

        filmStorage.addLike(filmId, userId);

        return filmStorage.getFilm(filmId);
    }

    public Film removeLike(long filmId, long userId) {
        userStorage.getUser(userId);

        filmStorage.getFilm(filmId);

        filmStorage.removeLike(filmId, userId);

        return filmStorage.getFilm(filmId);
    }

    public Collection<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    public Genre getGenre(long genreId) {
        return filmStorage.getGenreById(genreId);
    }

    public Collection<MPA> getAllMPA() {
        return filmStorage.getAllMPA();
    }

    public MPA getMPA(long id) {
        return filmStorage.getMPAById(id);
    }
}
