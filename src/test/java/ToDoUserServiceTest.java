import cncs.academy.ess.model.User;
import cncs.academy.ess.repository.memory.InMemoryUserRepository;
import cncs.academy.ess.service.TodoUserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import static org.junit.jupiter.api.Assertions.*;

public class ToDoUserServiceTest {

    private TodoUserService service;
    private InMemoryUserRepository repository;
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    @BeforeEach
    void setUp() {
        repository = mock(InMemoryUserRepository.class);
        service = new TodoUserService(repository);
    }

    @Test
    void login_ShouldReturnValidJWTTokenWhenCredentialsMatch() throws Exception{
        String username = "Alberto";
        String password = "Alberto123";

        byte[] salt = new byte[16];
        byte[] hashPassword = hashPassword(password,salt);

        User user = new User(153,username,hashPassword,salt);
        when(repository.findByUsername(username)).thenReturn(user);

        String tokenJWT = service.login(username,password);
        String[] token_split = tokenJWT.split(" ");

        assertEquals("Bearer", token_split[0]);

        DecodedJWT decodedToken = JWT.decode(token_split[1]);
        assertEquals("o back-end", decodedToken.getIssuer());
        assertEquals("Alberto", decodedToken.getClaim("username").asString());
        assertEquals(153, decodedToken.getClaim("id").asInt());
    }

    private static byte[] hashPassword(String password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return factory.generateSecret(spec).getEncoded();
    }

}
