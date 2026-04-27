package com.lefkovitzj.sermonarchive;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/auth")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public List<User> getUsers() {
        return userService.listAllUsers();
    }

    @GetMapping("/search")
    public List<User> getUsersByUsername(@RequestParam String username) {
        return userService.searchUserByName(username);
    }

    @GetMapping("/user")
    public User getUserById(@RequestParam Integer id) {
        return userService.searchUserById(id);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addUser(@RequestBody User newUser) {
        userService.addUser(newUser);
        return ResponseEntity.ok("User " + newUser.getUsername() + " (" + newUser.getEmail() + ") was added successfully.");
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody User updatedUser) {
        userService.updateUser(updatedUser);
        return ResponseEntity.ok("User " + updatedUser.getUsername() + " was updated successfully.");
    }
}
