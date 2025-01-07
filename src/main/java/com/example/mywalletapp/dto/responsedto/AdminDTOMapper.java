package com.example.mywalletapp.dto.responsedto;

import com.example.mywalletapp.model.User;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class AdminDTOMapper implements Function<User, UserSignUpResponse> {
    @Override
    public UserSignUpResponse apply(User user){
        return UserSignUpResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .middleName(user.getMiddleName())
                .email(user.getEmail())
                .dateOfBirth(user.getDateOfBirth())
                .bvn(user.getBvn())
                .address(user.getAddress())
                .createdAt(user.getCreatedAt())
                .build();
    }


}