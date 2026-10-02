

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
                

     
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return user; 
    }
}