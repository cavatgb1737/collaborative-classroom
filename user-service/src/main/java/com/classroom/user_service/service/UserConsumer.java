package com.classroom.user_service.service;

import com.classroom.user_service.model.User;
import com.classroom.user_service.model.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserConsumer {

    private final UserService userService;

    @KafkaListener(topics = "user-topic", groupId = "user-service-group")
    public void consume(UserEvent event){
        log.info("Received user object for processing: {}", event);
        userService.createUser(event);
    }

}
