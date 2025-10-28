package com.classroom.user_service.grpc;

import com.classroom.user_service.model.User;
import com.classroom.user_service.repository.UserRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import userservice.UserServiceOuterClass;

@GrpcService
@RequiredArgsConstructor
public class UserServiceGrpc extends userservice.UserServiceGrpc.UserServiceImplBase {

    private final UserRepository userRepository;

    @Override
    public void retrieveUserData(UserServiceOuterClass.RetrieveUserDataReq request, StreamObserver<UserServiceOuterClass.RetrieveUserDataRes> responseObserver){

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User could not be found with email " + request.getEmail()));

        UserServiceOuterClass.RetrieveUserDataRes response = UserServiceOuterClass.RetrieveUserDataRes.newBuilder()
                .setImageUrl(user.getImageUrl())
                .setName(user.getName())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }
}
