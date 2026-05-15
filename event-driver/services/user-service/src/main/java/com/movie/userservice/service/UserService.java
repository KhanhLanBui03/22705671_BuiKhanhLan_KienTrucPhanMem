package com.movie.userservice.service;

import com.movie.userservice.entity.User;
import com.movie.userservice.producer.UserProducer;
import com.movie.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserProducer userProducer;

    public UserService(UserRepository userRepository, UserProducer userProducer) {
        this.userRepository = userRepository;
        this.userProducer = userProducer;
    }

    public String register(String username, String password) {
        if (userRepository.existsById(username)) {
            return "User already exists";
        }
        User user = new User(username, password);
        userRepository.save(user);
        userProducer.sendUserRegistered(username);
        return "User registered successfully";
    }

    public boolean login(String username, String password) {
        Optional<User> user = userRepository.findById(username);
        return user.isPresent() && user.get().getPassword().equals(password);
    }
}
