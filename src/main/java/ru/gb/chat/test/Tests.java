package ru.gb.chat.test;

import org.junit.jupiter.api.*;
import ru.gb.chat.client.Controller;
import ru.gb.chat.server.ListAuthService;
import ru.gb.chat.server.User;

import static org.junit.jupiter.api.Assertions.*;

class Tests {

    User user = new User("TestSaveUser", "test123", "ttt");
    Controller controller = new Controller();

    @BeforeEach
    void setUp() {
        System.out.println("Start");
    }

    @AfterEach
    void tearDown() {
        System.out.println("End");
    }

    @Test
    @DisplayName("Users equals test")
    void equals() {
        User user = new User("test", "test", "test");
        assertTrue(user.equals(user));
        System.out.println("Equals test");
    }

    @RepeatedTest(3)
    @DisplayName("Instance test")
    void getInstance() {
        assertNotNull(ListAuthService.getInstance());
        System.out.println("Instance test");
    }

    @Test
    void findByLoginAndPassword() {
        ListAuthService.getInstance().save(user);
        assertNotNull(ListAuthService.getInstance().findByLoginAndPassword(user.getLogin(), user.getPassword()));
        System.out.println("Find by login and password test");
    }

    @Test
    @DisplayName("Save test")
    void save() {
        assertEquals(ListAuthService.getInstance().save(user), user);
        System.out.println("Save test");
    }

    @Test
    @DisplayName("Remove test")
    void remove() {
        assertEquals(ListAuthService.getInstance().remove(user), user);
        System.out.println("Remove test");
    }
}