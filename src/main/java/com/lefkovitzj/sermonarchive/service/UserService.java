package com.lefkovitzj.sermonarchive.service;

import com.lefkovitzj.sermonarchive.entity.User;
import com.lefkovitzj.sermonarchive.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> listAllUsers() {
        return userRepository.findAll();
    }
    public List<User> searchUserByName(String name) {
        return userRepository.findByUsernameContaining(name);
    }
    public User searchUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }
    public User getByName(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public void addUser(User user) {
        userRepository.save(user);
    }
    @Transactional
    public void updateUser(User user) {
        userRepository.save(user);
    }
}
