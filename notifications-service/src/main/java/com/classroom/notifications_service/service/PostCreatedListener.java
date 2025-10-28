package com.classroom.notifications_service.service;

import com.classroom.notifications_service.model.PostEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostCreatedListener {

    private final WebsocketService websocketService;

    @KafkaListener(
            topics = "notification-topic",
            groupId = "post-notification-group",
            containerFactory = "postKafkaListenerContainerFactory"
    )
    public void consumePostCreatedMessage(PostEvent event){
        websocketService.sendPostCreatedMessage(event);
    }
}
