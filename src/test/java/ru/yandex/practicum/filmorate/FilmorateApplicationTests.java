package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FilmorateApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserController userController;

    @Autowired
    private FilmController filmController;


    @Test
    void createUser_CreateUser() throws Exception {
        User user = User.builder()
                .email("mail@mail.ru")
                .login("dolore")
                .name("Nick Name")
                .birthday(LocalDate.of(1946, Month.AUGUST, 20))
                .build();

        MvcResult result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.login").value(user.getLogin()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.birthday").value(user.getBirthday().toString()))
                .andReturn();

        User createdUser = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
        assertThat(createdUser.getId()).isPositive();
    }

    @Test
    void createUser_InvalidLogin() {
        User user = User.builder()
                .login("dolore ullamco")
                .email("yandex@mail.ru")
                .birthday(LocalDate.of(1980, Month.AUGUST, 20))
                .build();

        assertThrows(ValidationException.class, () -> {
            userController.createUser(user);
        });
    }

    @Test
    void createUser_InvalidEmail() {
        User user = User.builder()
                .login("dolore")
                .email("mail.ru")
                .name("")
                .birthday(LocalDate.of(1980, Month.AUGUST, 20))
                .build();

        assertThrows(ValidationException.class, () -> {
            userController.createUser(user);
        });
    }

    @Test
    void createUser_FutureBirthday() {
        User user = User.builder()
                .login("dolore")
                .email("test@mail.ru")
                .name("")
                .birthday(LocalDate.of(2446, Month.AUGUST, 20))
                .build();

        assertThrows(ValidationException.class, () -> {
            userController.createUser(user);
        });
    }

    @Test
    void updateUser_UpdateUser() throws Exception {
        User user = User.builder()
                .email("mail@mail.ru")
                .login("dolore")
                .name("Nick Name")
                .birthday(LocalDate.of(1946, Month.AUGUST, 20))
                .build();

        MvcResult createResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        User createdUser = objectMapper.readValue(createResult.getResponse().getContentAsString(), User.class);

        user = User.builder()
                .id(createdUser.getId())
                .login("doloreUpdate")
                .name("est adipisicing")
                .email("mail@yandex.ru")
                .birthday(LocalDate.of(1976, Month.SEPTEMBER, 20))
                .build();

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdUser.getId()))
                .andExpect(jsonPath("$.email").value("mail@yandex.ru"))
                .andExpect(jsonPath("$.name").value("est adipisicing"))
                .andExpect(jsonPath("$.login").value("doloreUpdate"))
                .andExpect(jsonPath("$.birthday").value("1976-09-20"));
    }

    @Test
    void updateUser_WithUnknownId() throws Exception {
        User user = User.builder()
                .email("mail@mail.ru")
                .login("dolore")
                .name("Nick Name")
                .birthday(LocalDate.of(1946, Month.AUGUST, 20))
                .build();

        MvcResult createResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        User createdUser = objectMapper.readValue(createResult.getResponse().getContentAsString(), User.class);

        final User updateUser = User.builder()
                .id(createdUser.getId() + 1)
                .login("doloreUpdate")
                .name("est adipisicing")
                .email("mail@yandex.ru")
                .birthday(LocalDate.of(1976, Month.SEPTEMBER, 20))
                .build();


        assertThrows(ValidationException.class, () -> {
            userController.updateUser(updateUser);
        });
    }

    @Test
    void getAllUsers() throws Exception {
        User user = User.builder()
                .email("mail@mail.ru")
                .login("dolore")
                .name("Nick Name")
                .birthday(LocalDate.of(1946, Month.AUGUST, 20))
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    void createUser_WithEmptyName() throws Exception {
        User userWithEmptyName = User.builder()
                .login("common")
                .email("friend@common.ru")
                .birthday(LocalDate.of(2000, Month.AUGUST, 20))
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userWithEmptyName)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("friend@common.ru"))
                .andExpect(jsonPath("$.name").value("common"))
                .andExpect(jsonPath("$.login").value("common"))
                .andExpect(jsonPath("$.birthday").value("2000-08-20"));
    }

    @Test
    void createFilm_CreateFilm() throws Exception {
        Film film = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, Month.MARCH, 25))
                .duration(100)
                .build();

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(film.getName()))
                .andExpect(jsonPath("$.description").value(film.getDescription()))
                .andExpect(jsonPath("$.releaseDate").value(film.getReleaseDate().toString()))
                .andExpect(jsonPath("$.duration").value(film.getDuration()));
    }

    @Test
    void createFilm_WithEmptyName() {
        Film film = Film.builder()
                .name("")
                .description("Description")
                .releaseDate(LocalDate.of(1900, Month.MARCH, 25))
                .duration(200)
                .build();

        assertThrows(ValidationException.class, () -> {
            filmController.createFilm(film);
        });
    }

    @Test
    void createFilm_WithTooLongDescription_ShouldReturnBadRequest() {
        String longDescription = "Пятеро друзей (комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.";

        Film film = Film.builder()
                .name("Film name")
                .description(longDescription)
                .releaseDate(LocalDate.of(1900, Month.MARCH, 25))
                .duration(200)
                .build();

        assertThrows(ValidationException.class, () -> {
            filmController.createFilm(film);
        });
    }

    @Test
    void createFilm_InvalidReleaseDate() {
        Film film = Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1890, Month.MARCH, 25))
                .duration(200)
                .build();

        assertThrows(ValidationException.class, () -> {
            filmController.createFilm(film);
        });
    }

    @Test
    void createFilm_WithNegativeDuration() {
        Film film = Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1980, Month.MARCH, 25))
                .duration(-200)
                .build();

        assertThrows(ValidationException.class, () -> {
            filmController.createFilm(film);
        });
    }

    @Test
    void updateFilm_ShouldReturnUpdatedFilm() throws Exception {
        Film film = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, Month.MARCH, 25))
                .duration(100)
                .build();

        MvcResult createResult = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Film createdFilm = objectMapper.readValue(createResult.getResponse().getContentAsString(), Film.class);

        Film updatedFilm = Film.builder()
                .id(createdFilm.getId())
                .name("Film Updated")
                .description("New film update description")
                .releaseDate(LocalDate.of(1989, Month.APRIL, 17))
                .duration(190)
                .build();

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFilm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdFilm.getId()))
                .andExpect(jsonPath("$.name").value("Film Updated"))
                .andExpect(jsonPath("$.description").value("New film update description"))
                .andExpect(jsonPath("$.releaseDate").value("1989-04-17"))
                .andExpect(jsonPath("$.duration").value(190));
    }

    @Test
    void updateFilm_WithUnknownId() throws Exception {
        Film film = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, Month.MARCH, 25))
                .duration(100)
                .build();

        MvcResult createResult = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Film createdFilm = objectMapper.readValue(createResult.getResponse().getContentAsString(), Film.class);

        Film updatedFilm = Film.builder()
                .id(createdFilm.getId() + 1)
                .name("Film Updated")
                .description("New film update description")
                .releaseDate(LocalDate.of(1989, Month.APRIL, 17))
                .duration(190)
                .build();

        assertThrows(ValidationException.class, () -> {
            filmController.updateFilm(updatedFilm);
        });
    }

    @Test
    void getAllFilms() throws Exception {
        Film film = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, Month.MARCH, 25))
                .duration(100)
                .build();


        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists());
    }

}
