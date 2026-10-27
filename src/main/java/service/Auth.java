package service;

import domain.user;
import repository.userRepository;

public class Auth {
    private userRepository userRepository;

    public Auth(userRepository userRepository) {
        this.userRepository = userRepository;
    }

    public user login(String username, String password) {
        // البحث عن المستخدم
        user user = userRepository.findByUsername(username);

        // الشرط المفقود: التأكد من وجود المستخدم قبل قراءة كلمة المرور
        if (user == null) {
            throw new IllegalArgumentException("User not found with username: " + username);
        }

        // فحص مطابقة كلمة المرور
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return user; 
    }
}