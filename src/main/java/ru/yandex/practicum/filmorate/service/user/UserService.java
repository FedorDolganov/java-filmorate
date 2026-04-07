package ru.yandex.practicum.filmorate.service.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

@Service
@AllArgsConstructor
public class UserService {

    private UserDbStorage userStorage;


    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUser(long userId) {
        return userStorage.getUser(userId);
    }

    public User createUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Неверные данные почты");
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Неверные данные логина");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Неверные данные дня рождения");
        }

        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }

        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        User userDB = userStorage.getUser(user.getId());

        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }

        userStorage.updateUser(userDB.getId(), user);

        return user;
    }

    public User addFriend(long userId, long friendId) {
        userStorage.getUser(userId);

        userStorage.getUser(friendId);

        userStorage.addFriend(userId, friendId);

        return userStorage.getUser(userId);
    }

    public User removeFriend(long userId, long friendId) {
        userStorage.getUser(userId);

        userStorage.getUser(friendId);

        userStorage.removeFriend(userId, friendId);

        return userStorage.getUser(userId);
    }

    public List<User> getAllFriends(long userId) {
        userStorage.getUser(userId);

        return userStorage.getAllFriends(userId);
    }

    public List<User> getCommonFriends(long userId, long friendId) {
        return userStorage.getCommonFriends(userId, friendId);
    }

}
