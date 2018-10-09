package com.snj.controllers;

import com.snj.entities.User;
import com.snj.repositories.UserRepository;
import com.snj.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping(value = "/")
    public String index() {
        return "Hello World";
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public Iterable<User> listUser() {
        return userService.findAll();
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public void create(@RequestBody User user) {
        userService.save(user);
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable(value = "id") Long id) {
        userService.deleteUser(id);
        return "success";
    }


}
