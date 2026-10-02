package repository;

import domain.user;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class userRepository {
    private List<user> users = new ArrayList<>();

    public userRepository() {
    	user customer=new user("tala","tala12345","customer");
    	users.add(customer);
    	user manager=new user("reem","reem12345","manager");
       
        users.add(manager);
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