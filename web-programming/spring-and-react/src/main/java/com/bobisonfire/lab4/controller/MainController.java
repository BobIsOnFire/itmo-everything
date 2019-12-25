package com.bobisonfire.lab4.controller;

import com.bobisonfire.lab4.data.Role;
import com.bobisonfire.lab4.data.User;
import com.bobisonfire.lab4.data.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;

@Controller
public class MainController {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public MainController(UserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/register")
    public String register() {
        return "login";
    }

    @PostMapping("/register")
    public String addUser(User user) {
        User userFromDb = users.findByUsername(user.getUsername());

        if (userFromDb != null) {
            return "login?exists";
        }

        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setPassword( passwordEncoder.encode(user.getPassword()) );

        users.save(user);

        return "redirect:/";
    }
}
