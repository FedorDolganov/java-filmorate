package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {

    User addUser(User user);

    void updateUser(long id, User user);

    Collection<User> getAllUsers();

    User getUser(long id);

    void addFriend(long userId, long friendId);

    void removeFriend(long userId, long friendId);

    List<User> getAllFriends(long userId);

    List<User> getCommonFriends(long userId, long friendId);

}
