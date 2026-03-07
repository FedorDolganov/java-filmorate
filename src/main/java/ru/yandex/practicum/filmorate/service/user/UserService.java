package ru.yandex.practicum.filmorate.service.user;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
public class UserService {

    private InMemoryUserStorage userStorage;

    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUser(long userId) {
        if (!userStorage.containsUser(userId)) {
            throw new NotFoundException("Пользователь не найден по указанному ID");
        }

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

        userStorage.addUser(user);

        return user;
    }

    public User updateUser(User user) {
        if (userStorage.containsUser(user.getId())) {
            if (user.getFriends() == null) {
                user.setFriends(new HashSet<>());
            }

            userStorage.updateUser(user.getId(), user);
            return user;
        } else {
            throw new ValidationException("Пользователь не найден по указанному ID");
        }
    }

    public User addFriend(long userId, long friendId) {
        if (!userStorage.containsUser(userId)) {
            throw new NotFoundException("Пользователь не найден по указанному ID");
        }

        if (!userStorage.containsUser(friendId)) {
            throw new NotFoundException("Друг не найден по указанному ID");
        }

        userStorage.addFriend(userId, friendId);

        return userStorage.getUser(userId);
    }

    public User removeFriend(long userId, long friendId) {
        if (!userStorage.containsUser(userId)) {
            throw new NotFoundException("Пользователь не найден по указанному ID");
        }

        if (!userStorage.containsUser(friendId)) {
            throw new NotFoundException("Друг не найден по указанному ID");
        }

        userStorage.removeFriend(userId, friendId);

        return userStorage.getUser(userId);
    }

    public List<User> getAllFriends(long userId) {
        if (!userStorage.containsUser(userId)) {
            throw new NotFoundException("Пользователь не найден по указанному ID");
        }

        return userStorage.getAllFriends(userId);
    }

    public List<User> getCommonFriends(long userId, long friendId) {
        return userStorage.getCommonFriends(userId, friendId);
    }

}
