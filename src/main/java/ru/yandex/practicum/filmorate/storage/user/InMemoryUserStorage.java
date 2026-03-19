package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.enums.FriendConfirmation;
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
        HashMap<Long, FriendConfirmation> friendFriends = users.get(userId).getFriends();
        HashMap<Long, FriendConfirmation> friends = users.get(userId).getFriends();

        if (friendFriends.containsKey(userId)) {
            if (friendFriends.get(userId).equals(FriendConfirmation.NOT_CONFIRMED)) {
                friendFriends.put(userId, FriendConfirmation.CONFIRMED);
                friends.put(friendId, FriendConfirmation.CONFIRMED);
            }
        } else {
            friends.put(friendId, FriendConfirmation.NOT_CONFIRMED);
        }
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        HashMap<Long, FriendConfirmation> friendFriends = users.get(userId).getFriends();
        HashMap<Long, FriendConfirmation> friends = users.get(userId).getFriends();

        friends.remove(friendId);
        friendFriends.remove(userId);
    }

    @Override
    public boolean containsUser(long filmId) {
        return users.containsKey(filmId);
    }

    @Override
    public boolean containsUserFriend(long userId, long friendId) {
        return users.get(userId).getFriends().containsKey(friendId);
    }

    @Override
    public List<User> getAllFriends(long userId) {
        List<User> friends = new ArrayList<>();

        for (long friendId : users.get(userId).getFriends().keySet()) {
            friends.add(users.get(friendId));
        }

        return friends;
    }

    @Override
    public List<User> getCommonFriends(long userId, long friendId) {
        List<User> commonFriends = new ArrayList<>();

        for (long userFriendId : users.get(userId).getFriends().keySet()) {
            if (users.get(friendId).getFriends().containsKey(userFriendId)) {
                commonFriends.add(users.get(userFriendId));
            }
        }

        return commonFriends;
    }
}
