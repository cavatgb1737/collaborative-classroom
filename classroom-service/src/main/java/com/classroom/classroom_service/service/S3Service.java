package com.classroom.classroom_service.service;

import com.classroom.classroom_service.model.Classroom;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.bucket.name}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;

    public Classroom uploadFile(MultipartFile file, Classroom classroom) throws Exception{

        String key = UUID.randomUUID() + "#" + file.getOriginalFilename();

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build(), RequestBody.fromBytes(file.getBytes())
        );


        classroom.setImageBytes(downloadFile(key));

        return classroom;

    }

    public byte[] downloadFile(String key){

        if(key == null) return null;

        ResponseBytes<GetObjectResponse> objectAsBytes = s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build());

        return objectAsBytes.asByteArray();
    }

}