package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
public class MPAController {

    private FilmService filmService;

    public MPAController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<MPA> getAllMPA() {
        return filmService.getAllMPA();
    }

    @GetMapping("/{id}")
    public MPA getMPA(@PathVariable long id) {
        return filmService.getMPA(id);
    }

}