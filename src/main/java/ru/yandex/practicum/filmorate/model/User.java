package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.enums.FriendConfirmation;

import java.time.LocalDate;
import java.util.HashMap;

@Builder
@Data
public class User {

    private long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private HashMap<Long, FriendConfirmation> friends;

}
