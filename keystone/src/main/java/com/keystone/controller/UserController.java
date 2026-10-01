package com.keystone.controller;

import com.keystone.entity.User;
import com.keystone.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {

        if (userService.emailExists(user.getEmail())) {
            return ResponseEntity.badRequest().build();
        }

        User savedUser = userService.saveUser(user);

        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/{email}")
    public ResponseEntity<User> getUserByEmail(
            @PathVariable String email) {

        Optional<User> user = userService.getUserByEmail(email);

        return user.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/manager-test")
    @PreAuthorize("hasRole('MANAGER')")
    public String managerTest() {
        return "Manager access granted!";
    }
}