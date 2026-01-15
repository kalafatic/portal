package com.kalafatic.web;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @GetMapping("/greeting/{username}/{password}")
    public String getGreeting(@PathVariable("username") String username, @PathVariable("password") String password) {
        return UserBean.getStringForLanguage(username, password, "language");
    }
}
