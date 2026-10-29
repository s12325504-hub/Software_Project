package repository;

import domain.user;
import java.util.ArrayList;
import java.util.List;

public class userRepository {
    private List<user> users = new ArrayList<>();

    public userRepository() {
        // إضافة الإيميل لكل مستخدم
        user customer = new user("tala", "tala12345", "customer", "talasaadeh775@gmail.com");
        users.add(customer);
        user manager = new user("reem", "reem12345", "manager", "reem@example.com");
        users.add(manager);
    }

    public void save(user u) {
        if (u != null) {
            this.users.add(u);
        }
    }

    public user findByUsername(String username) {
        for (user userr : users) {
            if (userr.getUsername().equals(username)) {
                return userr;
            }
        }
        throw new IllegalArgumentException("User not found with username: " + username);
    }
}