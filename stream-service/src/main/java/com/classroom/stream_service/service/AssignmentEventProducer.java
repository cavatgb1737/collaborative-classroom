package com.classroom.stream_service.service;

import com.classroom.stream_service.model.AssignmentEvent;
import com.classroom.stream_service.model.PostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AssignmentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishAssignmentCreated(AssignmentEvent event){
        kafkaTemplate.send("notification-topic", event)
                .whenComplete((result, ex) -> {
                    if(ex != null){
                        log.error("Failed to publish assignment created event.");
                    }
                    else{
                        log.info("Published assignment created event for {}", event.getClassroomId());
                    }
                });
    }

    public void publishPostCreated(PostEvent event){
        kafkaTemplate.send("notification-topic", event)
                .whenComplete((result, ex) -> {
                    if(ex != null){
                        log.error("Failed to publish assignment created event.");
                    }
                    else{
                        log.info("Published assignment created event for {}", event.getClassroomId());
                    }
                });
    }
}
