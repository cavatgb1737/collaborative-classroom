package com.classroom.user_service.controller;

import com.classroom.user_service.model.User;
import com.classroom.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/viewall")
    public ResponseEntity<List<User>> getUsers(){
        return ResponseEntity.ok(userService.getUsers());
    }
    @GetMapping("/view")
    public ResponseEntity<User> getUsers(Authentication auth){
        String email = auth.getName();
        return ResponseEntity.ok(userService.getUser(email));
    }
}
