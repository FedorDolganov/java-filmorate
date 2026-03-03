package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final HashMap<Long, Film> films = new HashMap<>();


    @GetMapping
    public Collection<Film> filmGetMethod() {
        return films.values();
    }

    @PostMapping
    public Film filmPostMethod(@Valid @RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException(log, "Неверные данные создания фильма: Неверные данные имени фильма");
        }

        if (film.getDescription() == null || film.getDescription().isBlank() || film.getDescription().length() > 200) {
            throw new ValidationException(log, "Неверные данные создания фильма: Неверные данные описания фильма");
        }

        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException(log, "Неверные данные создания фильма: Неверные данные даты релиза фильма");
        }

        if (film.getDuration() <= 0) {
            throw new ValidationException(log, "Неверные данные создания фильма: Неверные данные продолжительности фильма");
        }

        film.setId(films.size() + 1);
        log.info("Добавлен новый фильм {}!", film.getName());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film filmPutMethod(@Valid @RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Фильм {} обновлён!", film.getName());
            return film;
        } else {
            throw new ValidationException(log, "Неверные данные обновления фильма: Фильм с нужным индефикатором не найден");
        }
    }

}