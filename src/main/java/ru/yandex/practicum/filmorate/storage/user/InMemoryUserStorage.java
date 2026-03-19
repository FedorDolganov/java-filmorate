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
        HashSet<Long> friendFriends = users.get(userId).getFriends();
        HashSet<Long> friends = users.get(userId).getFriends();

        if (friendFriends.contains(userId)) {
            friendFriends.add(userId);
            friends.add(friendId);
        } else {
            friends.add(friendId);
        }
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        HashSet<Long> friendFriends = users.get(userId).getFriends();
        HashSet<Long> friends = users.get(userId).getFriends();

        friends.remove(friendId);
        friendFriends.remove(userId);
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
