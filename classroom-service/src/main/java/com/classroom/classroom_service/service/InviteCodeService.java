package com.classroom.classroom_service.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class InviteCodeService {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int CODE_LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int MAX_TRIES = 8;

    public static String generateRandomCode(){

        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < CODE_LENGTH; i++){
            int idx = RANDOM.nextInt(ALPHABET.length());
            sb.append(ALPHABET.charAt(idx));
        }

        return sb.toString();
    }
}
