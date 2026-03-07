package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Long, User> users = new HashMap<>();
    private long lastID = 1;


    @Override
    public void addUser(User user) {
        user.setId(lastID);

        lastID++;

        users.put(user.getId(), user);
    }

    @Override
    public void updateUser(long id, User user) {
        users.put(id, user);
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User getUser(long id) {
        return users.get(id);
    }

    @Override
    public void addFriend(long userId, long friendId) {
        Set<Long> friends1 = users.get(userId).getFriends();

        friends1.add(friendId);

        users.get(userId).setFriends(friends1);


        Set<Long> friends2 = users.get(friendId).getFriends();

        friends2.add(userId);

        users.get(friendId).setFriends(friends2);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        Set<Long> friends1 = users.get(userId).getFriends();

        friends1.remove(friendId);

        users.get(userId).setFriends(friends1);


        Set<Long> friends2 = users.get(friendId).getFriends();

        friends2.remove(userId);

        users.get(friendId).setFriends(friends1);
    }

    @Override
    public boolean containsUser(long filmId) {
        return users.containsKey(filmId);
    }

    @Override
    public boolean containsUserFriend(long userId, long friendId) {
        return users.get(userId).getFriends().contains(friendId);
    }

    @Override
    public List<User> getAllFriends(long userId) {
        List<User> friends = new ArrayList<>();

        for (long friendId : users.get(userId).getFriends()) {
            friends.add(users.get(friendId));
        }

        return friends;
    }

    @Override
    public List<User> getCommonFriends(long userId, long friendId) {
        List<User> commonFriends = new ArrayList<>();

        for (long userFriendId : users.get(userId).getFriends()) {
            if (users.get(friendId).getFriends().contains(userFriendId)) {
                commonFriends.add(users.get(userFriendId));
            }
        }

        return commonFriends;
    }
}
