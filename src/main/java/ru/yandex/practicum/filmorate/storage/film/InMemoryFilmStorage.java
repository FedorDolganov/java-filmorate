package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage{

    private final HashMap<Long, Film> films = new HashMap<>();
    private long lastID = 1;


    @Override
    public void addFilm(Film film) {
        film.setId(lastID);

        lastID++;

        films.put(film.getId(), film);
    }

    @Override
    public void updateFilm(long id, Film film) {
        films.put(id, film);
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public Film getFilm(long id) {
        return films.get(id);
    }

    @Override
    public void addLike(long filmId, long userId) {
        Set<Long> likes = films.get(filmId).getLikes();

        likes.add(userId);

        films.get(filmId).setLikes(likes);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        Set<Long> likes = films.get(filmId).getLikes();

        likes.remove(userId);

        films.get(filmId).setLikes(likes);
    }

    @Override
    public LinkedList<Film> getListFilms(int count) {
        LinkedList<Film> sortedFilms = new LinkedList<>(new ArrayList<>(films.values()).subList(0, Math.min(films.size(), count)));

        sortedFilms.sort(null);

        return sortedFilms;
    }

    @Override
    public boolean containsFilm(long filmId) {
        return films.containsKey(filmId);
    }
}
