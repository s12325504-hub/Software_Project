package domain;

public class user {
    private String username;
    private String password;
    private String role; 
    private String email; 

    public user(String username, String password, String role) {
        this(username, password, role, username + "@example.com"); 
    }
    public user(String username, String password, String role, String email) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
 
    public String getRole() {
        return role;
    }

    // 🟢 Getter and Setter للإيميل
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}