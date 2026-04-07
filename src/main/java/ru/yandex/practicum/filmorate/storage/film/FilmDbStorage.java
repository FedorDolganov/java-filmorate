package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.mappers.MpaMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.*;

@Component("dbFilm")
@Repository
public class FilmDbStorage implements FilmStorage {

    private long lastID = 1;
    private final JdbcTemplate jdbc;
    private final FilmMapper filmMapper;
    private final MpaMapper mpaMapper;
    private final GenreMapper genreMapper;

    public FilmDbStorage(JdbcTemplate jdbc, FilmMapper filmMapper, MpaMapper mpaMapper, GenreMapper genreMapper) {
        this.jdbc = jdbc;
        this.filmMapper = filmMapper;
        this.mpaMapper = mpaMapper;
        this.genreMapper = genreMapper;

        try {
            this.lastID = jdbc.queryForObject(
                    "SELECT MAX(id) FROM films",
                    Long.class
            ) + 1;
        } catch (NullPointerException ignored) {

        }
    }

    @Override
    public Film addFilm(Film film) {
        jdbc.update(
                "INSERT INTO films(id, name, description, releaseDate, duration, mpa) VALUES (?, ?, ?, ?, ?, ?)",
                lastID, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId()
        );

        film.setId(lastID);

        saveGenres(lastID, film.getGenres());

        lastID++;

        return film;
    }

    @Override
    public void updateFilm(long id, Film film) {
        jdbc.update(
                "UPDATE films SET name = ?, description = ?, releaseDate = ?, duration = ?, mpa = ? WHERE id = ?",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), id
        );

        saveGenres(id, film.getGenres());
    }

    @Override
    public Collection<Film> getAllFilms() {
        return jdbc.query(
                "SELECT * FROM films",
                filmMapper
        ).stream()
                .peek(film -> {
                    film.setLikes(getLikes(film.getId()));
                    film.setGenres(getGenres(film.getId()));
                    film.setMpa(getMPAById(film.getMpa().getId()));
                })
                .toList();
    }

    @Override
    public Film getFilm(long id) {
        try {
            Film film = jdbc.queryForObject(
                    "SELECT * FROM films WHERE id = ?",
                    filmMapper,
                    id
            );

            film.setLikes(getLikes(id));
            film.setGenres(getGenres(film.getId()));
            film.setMpa(getMPAById(film.getMpa().getId()));

            return film;
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Фильм с таким индефикатором не найден!");
        }
    }

    @Override
    public void addLike(long filmId, long userId) {
        jdbc.update(
                "INSERT INTO likes(film_id, user_id) VALUES (?, ?)",
                filmId, userId
        );
    }

    @Override
    public void removeLike(long filmId, long userId) {
        jdbc.update(
                "DELETE FROM likes WHERE film_id = ? AND user_id = ?",
                filmId, userId
        );
    }

    @Override
    public List<Film> getListFilms(int count) {
        return jdbc.query(
                "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, f.mpa, COUNT(DISTINCT l.user_id) AS likes_count FROM films f LEFT JOIN likes l ON f.id = l.film_id GROUP BY f.id, f.name, f.description, f.releaseDate, f.duration, f.mpa ORDER BY likes_count DESC LIMIT 10",
                filmMapper
        ).stream()
                .peek(film -> {
                    film.setLikes(getLikes(film.getId()));
                    film.setGenres(getGenres(film.getId()));
                    film.setMpa(getMPAById(film.getMpa().getId()));
                })
                .toList();
    }

    @Override
    public Collection<Genre> getAllGenres() {
        return jdbc.query(
                "SELECT * FROM genres",
                genreMapper
        );
    }

    @Override
    public Genre getGenreById(long id) {
        try {
            Genre genre = jdbc.queryForObject(
                    "SELECT * FROM genres WHERE id = ?",
                    genreMapper,
                    id
            );

            if (genre == null) {
                throw new NotFoundException("Жанр с таким индефикатором не найден!");
            }

            return genre;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Жанр с таким индефикатором не найден!");
        }
    }

    @Override
    public Collection<MPA> getAllMPA() {
        return jdbc.query(
                "SELECT * FROM mpas",
                mpaMapper
        );
    }

    @Override
    public MPA getMPAById(long id) {
        try {
            return jdbc.queryForObject(
                    "SELECT * FROM mpas WHERE id = ?",
                    mpaMapper,
                    id
            );
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("MPA с таким индефикатором не найден!");
        }
    }

    private Set<Long> getLikes(long id) {
        return new HashSet<>(jdbc.query(
                "SELECT user_id FROM likes WHERE film_id = ?",
                (rs, rowNum) -> rs.getLong("user_id"),
                id
        ));
    }

    private LinkedHashSet<Genre> getGenres(long id) {
        LinkedList<Genre> genres = new LinkedList<>(jdbc.query(
                "SELECT g.id, g.genre FROM genres g JOIN filmGenres fg ON g.id = fg.genre_id WHERE fg.film_id = ?",
                genreMapper,
                id
        ));

        genres.sort(null);

        return new LinkedHashSet<>(genres);
    }

    private void saveGenres(long filmId, LinkedHashSet<Genre> genres) {
        if (genres == null) {
            return;
        }

        if (genres.isEmpty()) {
            return;
        }

        for (Genre genre : genres) {
            jdbc.update(
                    "INSERT INTO filmGenres(film_id, genre_id) VALUES (?, ?)",
                    filmId, genre.getId()
            );
        }
    }

}
