package com.bobisonfire.lab4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserRepository users;

    @Autowired
    public UserController(UserRepository users) {
        this.users = users;
    }

    @GetMapping("/{id}")
    public User getUser(
            @PathVariable long id,
            @RequestParam(name = "userName", required = false) String userName,
            @RequestParam(name = "password", required = false) String password)
    {
        Optional<User> user = users.findById(id);
        if (user.isPresent())
            return user.get();

        User u = new User(userName, password);
        users.save(u);
        return u;
    }
}
