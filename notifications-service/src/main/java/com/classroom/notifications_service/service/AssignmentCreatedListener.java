package com.classroom.notifications_service.service;
import com.classroom.notifications_service.model.AssignmentEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AssignmentCreatedListener {


    private static final Logger log = LoggerFactory.getLogger(AssignmentCreatedListener.class);
    private final EmailSenderService senderService;

    @KafkaListener(
            topics = "notification-topic",
            groupId = "assignment-notification-group",
            containerFactory = "assignmentKafkaListenerContainerFactory"
    )
    public void consumeAssignmentCreated(AssignmentEvent event){

        log.info("Received assignment created event for processing: {}", event);


        String body = """      
                You have receieved a new assignment called %s.
                This assignment is due on %s.
                Click here to view the assignment %s.
                """.formatted(event.getName(), event.getDueDate(), event.getDocsLink());

        String subject = "New classroom assignment posted: ";

        senderService.sendEmails(event, body, subject);



    }

}
