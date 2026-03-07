package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{filmId}")
    public Film getFilm(@PathVariable long filmId) {
        return filmService.getFilm(filmId);
    }

    @GetMapping("/popular")
    public LinkedList<Film> getPopularFilms(@RequestParam int count) {
        return filmService.getPopularFilms(count);
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
         return filmService.updateFilm(film);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Film addLike(@PathVariable long filmId, @PathVariable long userId) {
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Film removeLike(@PathVariable long filmId, @PathVariable long userId) {
        return filmService.removeLike(filmId, userId);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> validationError(final ValidationException e) {
        return Map.of(
                "error", "Ошибка валидации",
                "errorMessage", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFoundError(final NotFoundException e) {
        return Map.of(
                "error", "Элемент не найден",
                "errorMessage", e.getMessage()
        );
    }

}