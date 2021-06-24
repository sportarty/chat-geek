package ru.gb.chat.server;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class ListAuthServiceTest {

    @BeforeEach
    void setUp() {
        System.out.println("Start");
    }

    @AfterEach
    void tearDown() {
        System.out.println("End");
    }

    @Test
    @DisplayName("Check nick")
    void findByLoginAndPassword() {
        User user1 = new User("log1","pass1","nick1");
        assertSame("nick", user1.getNickname());
    }

    @Test
    void findByLoginOrNick() {
        User user1 = new User("log1","pass1","nick1");
        assertNotNull(user1);
    }



    @Test
    void save() {
        User user1 = new User("log1","pass1","nick1");
        assertNull(user1);
    }

    @Test
    void remove() {
        User user1 = new User("log1","pass1","nick1");
        assertSame("nick1", user1.getNickname());
    }

    @Test
    @Disabled("Метод ещё не доработан")
    void findById(){

    }

}