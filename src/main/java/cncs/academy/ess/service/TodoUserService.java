package cncs.academy.ess.service;

import cncs.academy.ess.model.User;
import cncs.academy.ess.repository.UserRepository;

import java.util.Arrays;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;

public class TodoUserService {
    private final UserRepository repository;
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    public TodoUserService(UserRepository userRepository) {
        this.repository = userRepository;
    }
    public User addUser(String username, String password) throws  Exception  {
        byte[] salt = generateSalt();
        byte[] hashedPassword = hashPassword(password, salt);
        User user = new User(username, hashedPassword, salt);
        int id = repository.save(user);
        user.setId(id);
        return user;
    }
    public User getUser(int id) {
        return repository.findById(id);
    }

    public void deleteUser(int id) {
        repository.deleteById(id);
    }

    public String login(String username, String inputPassword) throws Exception {
        User user = repository.findByUsername(username);
        if (user == null) {
            return null;
        }
        byte[] inputHashedPassword = hashPassword(inputPassword, user.getSalt());
        if (Arrays.equals(user.getPassword(),inputHashedPassword)) {
            return createAuthToken(user);
        }
        return null;
    }

    private String createAuthToken(User user) {
        return "Bearer " + user.getUsername();
    }

    private static byte[] hashPassword(String password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return factory.generateSecret(spec).getEncoded();
    }
    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

}
