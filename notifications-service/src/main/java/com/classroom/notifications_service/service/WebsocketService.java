package com.classroom.notifications_service.service;

import com.classroom.notifications_service.model.PostEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebsocketService {

    private final SimpMessagingTemplate simpMessagingTemplate;


    public void sendPostCreatedMessage(PostEvent event){
        simpMessagingTemplate.convertAndSend("/topic/classroom/" + event.getClassroomId(), event.getMessage());
    }

}
