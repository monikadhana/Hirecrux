package com.hirecrux_backend.service.impl;

import com.hirecrux_backend.dto.request.LoginRequestDto;
import com.hirecrux_backend.dto.request.RegisterRequestDto;
import com.hirecrux_backend.dto.response.LoginResponseDto;
import com.hirecrux_backend.dto.response.UserResponseDto;
import com.hirecrux_backend.entity.User;
import com.hirecrux_backend.enums.UserRole;
import com.hirecrux_backend.repository.UserRepository;
import com.hirecrux_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    @Override
    public UserResponseDto register(RegisterRequestDto request) {
        //checks email exist
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        //creating the user
        User user = new User(); //User is a entity so we manually create construction.

        //copying to entity
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPhone(request.getPhone());

        //set the isVerified as false and role as a candidate
        user.setIsVerified(false);
        user.setRole(UserRole.CANDIDATE);

        //save the user
        //why we are storing in savedUser? -- initially userId = null after repo.save(user) and then only it generate the id
        //so we want to save in savedUser
        User savedUser = userRepository.save(user);

        //Response
        UserResponseDto response = new UserResponseDto();

        //copy values from the entity to the response
        response.setUserId(savedUser.getUserId());
        response.setFullName(savedUser.getFullName());
        response.setEmail(savedUser.getEmail());
        response.setPhone(savedUser.getPhone());
        response.setIsVerified(savedUser.getIsVerified());
        response.setRole(savedUser.getRole());

        return response;
    }

    @Override
    public LoginResponseDto login(LoginRequestDto request){
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if(userOptional.isEmpty()){
            throw new RuntimeException("Invalid Email or password");
        }
        User user = userOptional.get();

        if (!user.getPassword().equals(request.getPassword())){
            throw new RuntimeException("Invalid Email or Password");
        }
        LoginResponseDto response = new LoginResponseDto();

        response.setUserId(user.getUserId());
        response.setFullName(user.getFullName());
        response.setRole(user.getRole());
        response.setEmail(user.getEmail());
        response.setIsVerified(user.getIsVerified());
        return response;
    }
}
