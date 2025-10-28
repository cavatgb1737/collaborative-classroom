package com.classroom.user_service.service;

import com.classroom.user_service.model.User;
import com.classroom.user_service.model.UserEvent;
import com.classroom.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void createUser(UserEvent event){
        userRepository.findByEmail(event.getEmail()).map(existing -> {
            existing.setName(event.getName());
            existing.setImageUrl(event.getImageUrl());
            return userRepository.save(existing);
        }).orElseGet(() -> userRepository.save(User.builder()
                .email(event.getEmail())
                .name(event.getName())
                .imageUrl(event.getImageUrl()).build()));

    }

    public List<User> getUsers(){
        return userRepository.findAll();
    }

    public User getUser(String email){
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("user not found with email " + email));
    }

}
