package ru.yandex.practicum.filmorate.service.film;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
public class FilmService {

    private InMemoryFilmStorage filmStorage;
    private InMemoryUserStorage userStorage;

    public FilmService(InMemoryFilmStorage filmStorage, InMemoryUserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilm(long filmId) {
        if (!filmStorage.containsFilm(filmId)) {
            throw new NotFoundException("Фильм не найден по указанному ID");
        }

        return filmStorage.getFilm(filmId);
    }

    public Film createFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Неверные данные имени фильма");
        }

        if (film.getDescription() == null || film.getDescription().isBlank() || film.getDescription().length() > 200) {
            throw new ValidationException("Неверные данные описания фильма");
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

        filmStorage.addFilm(film);

        return film;
    }

    public Film updateFilm(Film film) {
        if (filmStorage.containsFilm(film.getId())) {
            if (film.getLikes() == null) {
                film.setLikes(new HashSet<>());
            }

            filmStorage.updateFilm(film.getId(), film);
            return film;
        } else {
            throw new NotFoundException("Фильм не найден по указанному ID");
        }
    }

    public LinkedList<Film> getPopularFilms(int count) {
        return filmStorage.getListFilms(count);
    }

    public Film addLike(long filmId, long userId) {
        if (!userStorage.containsUser(userId)) {
            throw new NotFoundException("Пользователь не найден по указанному ID");
        }

        if (!filmStorage.containsFilm(filmId)) {
            throw new NotFoundException("Фильм не найден по указанному ID");
        }

        filmStorage.addLike(filmId, userId);

        return filmStorage.getFilm(filmId);
    }

    public Film removeLike(long filmId, long userId) {
        if (!userStorage.containsUser(userId)) {
            throw new NotFoundException("Пользователь не найден по указанному ID");
        }

        if (!filmStorage.containsFilm(filmId)) {
            throw new NotFoundException("Фильм не найден по указанному ID");
        }

        filmStorage.removeLike(filmId, userId);

        return filmStorage.getFilm(filmId);
    }

}
