package com.hirecrux_backend.service.impl;

import com.hirecrux_backend.repository.ApplicationRepository;
import com.hirecrux_backend.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {
    private final ApplicationRepository applicationRepository;
}
