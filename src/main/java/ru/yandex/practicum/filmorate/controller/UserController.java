package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private final HashMap<Long, User> users = new HashMap<>();


    @GetMapping
    public Collection<User> userGetMethod() {
        return users.values();
    }

    @PostMapping
    public User userPostMethod(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException(log, "Неверные данные создания пользователя: Неверные данные почты");
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException(log, "Неверные данные создания пользователя: Неверные данные логина");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException(log, "Неверные данные создания пользователя: Неверные данные дня рождения");
        }

        Random random = new Random();

        user.setId(random.nextInt(0, 999_999_999));
        log.info("Добавлен новый пользователь {}!", user.getName());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User userPutMethod(@RequestBody User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Пользователь {} обновлён!", user.getName());
            return user;
        } else {
            throw new ValidationException(log, "Неверные данные обновления пользователя: В базе данных нет пользователя с таким индефикатором");
        }
    }

}
