import cncs.academy.ess.model.User;
import cncs.academy.ess.repository.memory.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserRepositoryTest {
    private InMemoryUserRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
    }

    private User createUser() {
        byte[] pwd = "password".getBytes();
        byte[] salt = new byte[16];
        return new User("username", pwd, salt);
    }

    @Test
    void saveAndFindById_ShouldReturnSavedUser() {
        // Arrange
        User user = createUser();

        // Act
        int id = repository.save(user);
        User savedUser = repository.findById(id);

        // Assert
        assertEquals(user.getUsername(), savedUser.getUsername());
        assertArrayEquals(user.getPassword(), savedUser.getPassword());
        assertArrayEquals(user.getSalt(), savedUser.getSalt());
    }

    @Test
    void findById_ShouldReturnNull(){
        assertNull(repository.findById(1));
    }

    @Test
    void findUserByUsername_ShouldReturnCorrectUser(){
        User user = createUser();
        repository.save(user);

        User user_search = repository.findByUsername("username");

        assertEquals("username", user_search.getUsername());
        assertArrayEquals(user.getPassword(), user_search.getPassword());
        assertArrayEquals(user.getSalt(), user_search.getSalt());
    }

    @Test
    void findUserByUsername_ShouldReturnNull(){
        User user = createUser();
        repository.save(user);

        User user_search = repository.findByUsername("aaaaaaaaaaa");

        assertNull(user_search);
    }

    @Test
    void findAll_ShouldReturnAllUsers(){
        User user1 = createUser();
        User user2 = createUser();
        User user3 = createUser();
        repository.save(user1);
        repository.save(user2);
        repository.save(user3);

        List<User> users = repository.findAll();
        assertEquals(3, users.size());
    }

    @Test
    void deleteById_ShouldDeleteUser(){
        User user = createUser();
        int id = repository.save(user);

        repository.deleteById(id);

        assertNull(repository.findById(id));
    }


}