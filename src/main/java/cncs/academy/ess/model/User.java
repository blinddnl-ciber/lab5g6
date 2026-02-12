package cncs.academy.ess.model;

public class User {
    private int id;
    private final String username;
    private final byte[] password;
    private final byte[] salt;

    public User(int id, String username, byte[] password, byte[] salt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.salt = salt;
    }
    public User(String username, byte[] password, byte[] salt) {
        this.username = username;
        this.password = password;
        this.salt = salt;
    }

    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public byte[] getPassword() {
        return password;
    }
    public byte[] getSalt() { return salt; }
}

