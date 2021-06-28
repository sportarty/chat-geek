package ru.gb.chat.server;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

//@Mock пока тяжеловато будет

class DBAuthServiceTest {
    @BeforeEach
    void setUp() {
        System.out.println("Start");
    }

    @AfterEach
    void tearDown() {
        System.out.println("End");
    }

    @Test
    @DisplayName("Тестирование метода поиска пользователя по логину и паролю")
    void findByLoginAndPassword() {
        User user = new User("login","password","nickname");
        assertAll("Test user",
                () -> assertEquals("login",user.getLogin()),
                () -> assertEquals("password",user.getPassword()),
                () -> assertEquals("nickname",user.getNickname()));
    }

    @Test
    void findByLoginOrNick() {
        User user = new User("login","password","nickname");
        assertNotNull(user);
    }

    @Test
    void save() {
        User user = new User("login","password","nickname");
        assertNotNull(user);
    }

    @Test
    void remove() {
        User user = new User("login","password","nickname");
        assertNotNull(user);
    }

    @Test
    @Disabled("Методе еще не написан, тестирование отложено")
    void changeNick() {

    }
}