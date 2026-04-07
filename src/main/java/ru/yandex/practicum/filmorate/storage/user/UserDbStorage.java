package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.UserMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component("dbUser")
@Repository
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbc;
    private final UserMapper userMapper;

    private long lastID = 1;

    public UserDbStorage(JdbcTemplate jdbc, UserMapper userMapper) {
        this.jdbc = jdbc;
        this.userMapper = userMapper;

        try {
            this.lastID = jdbc.queryForObject(
                    "SELECT MAX(id) FROM users",
                    Long.class
            ) + 1;
        } catch (NullPointerException ignored) {

        }
    }

    @Override
    public User addUser(User user) {
        jdbc.update(
                "INSERT INTO users(id, email, login, name, birthday) VALUES (?, ?, ?, ?, ?)",
                lastID, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday()
        );

        user.setId(lastID);

        lastID++;

        return user;
    }

    @Override
    public void updateUser(long id, User user) {
        jdbc.update(
                "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), id
        );
    }

    @Override
    public Collection<User> getAllUsers() {
        return jdbc.query(
                        "SELECT * FROM users",
                        userMapper
                ).stream()
                .peek(user -> {
                    user.setFriends(getFriendsIds(user.getId()));
                })
                .toList();
    }

    @Override
    public User getUser(long id) {
        try {
            User user = jdbc.queryForObject(
                    "SELECT * FROM users WHERE id = ?",
                    userMapper,
                    id
            );

            user.setFriends(getFriendsIds(user.getId()));

            return user;
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Фильм с таким индефикатором не найден!");
        }
    }

    @Override
    public void addFriend(long userId, long friendId) {
        jdbc.update(
                "INSERT INTO friends(user_id, friend_id) VALUES (?, ?)",
                userId, friendId
        );
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        jdbc.update(
                "DELETE FROM friends WHERE user_id = ? AND friend_id = ?",
                userId, friendId
        );
    }

    @Override
    public List<User> getAllFriends(long userId) {
        List<User> users = jdbc.query(
                "SELECT u.id, u.name, u.login, u.email, u.birthday FROM friends f JOIN users u ON u.id = f.friend_id WHERE f.user_id = ?",
                userMapper,
                userId
        );

        Map<Long, Set<Long>> mapFriends = getMapFriendsIds(users);

        users.forEach(user ->
                user.setFriends(mapFriends.getOrDefault(user.getId(), new HashSet<>()))
        );

        return users;
    }

    @Override
    public List<User> getCommonFriends(long userId, long friendId) {
        List<User> users = jdbc.query(
                "SELECT u.id, u.name, u.login, u.email, u.birthday FROM friends f1 JOIN friends f2 ON f1.friend_id = f2.friend_id JOIN users u ON u.id = f1.friend_id WHERE f1.user_id = ? AND f2.user_id = ?",
                userMapper,
                userId, friendId
        );

        Map<Long, Set<Long>> mapFriends = getMapFriendsIds(users);

        users.forEach(user ->
                user.setFriends(mapFriends.getOrDefault(user.getId(), new HashSet<>()))
        );

        return users;
    }

    private Map<Long, Set<Long>> getMapFriendsIds(List<User> users) {
        String ids = users.stream()
                .map(u -> String.valueOf(u.getId()))
                .collect(Collectors.joining(","));

        return jdbc.query(
                "SELECT user_id, friend_id FROM friends WHERE user_id IN (" + ids + ")",
                rs -> {
                    Map<Long, Set<Long>> map = new HashMap<>();
                    while (rs.next()) {
                        map.computeIfAbsent(rs.getLong("user_id"), k -> new HashSet<>()).add(rs.getLong("friend_id"));
                    }
                    return map;
                }
        );
    }

    private Set<Long> getFriendsIds(long userId) {
        return new HashSet<>(
                jdbc.query(
                        "SELECT friend_id FROM friends WHERE user_id = ?",
                        (rs, rowNum) -> rs.getLong("friend_id"),
                        userId
                )
        );
    }

}
