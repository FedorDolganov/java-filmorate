package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.relational.core.sql.In;
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
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

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
        List<Film> films = jdbc.query(
                "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, f.mpa, COUNT(DISTINCT l.user_id) AS likes_count FROM films f LEFT JOIN likes l ON f.id = l.film_id GROUP BY f.id, f.name, f.description, f.releaseDate, f.duration, f.mpa ORDER BY likes_count DESC LIMIT 10",
                filmMapper
        );

        Map<Long, Set<Genre>> allGenres = getMapGenresIds(films);
        Map<Long, Set<Long>> allLikes = getMapLikes(films);
        Map<Long, MPA> allMPAs = getMapMPAs(films);

        films.forEach(film -> {
            film.setGenres(new LinkedHashSet<>(allGenres.getOrDefault(film.getId(), new HashSet<>())));
            film.setLikes(allLikes.getOrDefault(film.getId(), new HashSet<>()));
            film.setMpa(allMPAs.getOrDefault(film.getId(), MPA.builder().id(1).name("Боевик").build()));
        });

        return films;
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

    private Map<Long, Set<Genre>> getMapGenresIds(List<Film> films) {
        String ids = films.stream()
                .map(u -> String.valueOf(u.getId()))
                .collect(Collectors.joining(","));

        return jdbc.query(
                "SELECT fg.film_id as filmId, fg.genre_id as id, g.genre as name FROM filmGenres fg JOIN genres g ON fg.genre_id = g.id WHERE fg.film_id IN (" + ids + ") ORDER BY fg.film_id, g.id",
                rs -> {
                    Map<Long, Set<Genre>> map = new HashMap<>();
                    while (rs.next()) {
                        map.computeIfAbsent(rs.getLong("filmId"), k -> new HashSet<>()).add(Genre.builder().id(rs.getInt("id")).name(rs.getString("name")).build());
                    }
                    return map;
                }
        );
    }

    private Map<Long, Set<Long>> getMapLikes(List<Film> films) {
        String ids = films.stream()
                .map(u -> String.valueOf(u.getId()))
                .collect(Collectors.joining(","));

        return jdbc.query(
                "SELECT film_id, user_id FROM likes WHERE film_id IN (" + ids + ")",
                rs -> {
                    Map<Long, Set<Long>> map = new HashMap<>();
                    while (rs.next()) {
                        map.computeIfAbsent(rs.getLong("film_id"), k -> new HashSet<>()).add(rs.getLong("user_id"));
                    }
                    return map;
                }
        );
    }

    private Map<Long, MPA> getMapMPAs(List<Film> films) {
        String ids = films.stream()
                .map(u -> String.valueOf(u.getId()))
                .collect(Collectors.joining(","));

        return jdbc.query(
                "SELECT f.id as filmId, m.id as mpaid, m.mpa as mpaname FROM films f JOIN mpas m ON m.id = f.mpa WHERE f.id IN (" + ids + ")",
                rs -> {
                    Map<Long, MPA> map = new HashMap<>();
                    while (rs.next()) {
                        map.put(rs.getLong("filmId"), MPA.builder().id(rs.getInt("mpaid")).name(rs.getString("mpaname")).build());
                    }
                    return map;
                }
        );
    }

    private void saveGenres(long filmId, LinkedHashSet<Genre> genres) {
        if (genres == null) {
            return;
        }

        if (genres.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO filmGenres(film_id, genre_id) VALUES (?, ?)";

        List<Object[]> batchArgs = new ArrayList<>();

        for (Genre genre : genres) {
            batchArgs.add(new Object[]{filmId, genre.getId()});
        }

        jdbc.batchUpdate(sql, batchArgs);
    }

}
