package com.hirecrux_backend.service.impl;

import com.hirecrux_backend.repository.UserRepository;
import com.hirecrux_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

}
