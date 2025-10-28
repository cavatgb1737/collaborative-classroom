package com.classroom.notifications_service.service;

import com.classroom.notifications_service.model.AssignmentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private final JavaMailSender mail;

    public void sendEmails(AssignmentEvent event, String body, String subject){

        if(event.getStudentEmails() != null){
            for(String email: event.getStudentEmails()){
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("cavatgb@gmail.com");
                message.setTo(email);
                message.setText(body);
                message.setSubject(subject);
                mail.send(message);
            }
        }

    }

}
