package com.hirecrux_backend.service;

import com.hirecrux_backend.dto.request.ApplicationRequest;
import com.hirecrux_backend.dto.response.ApplicationResponse;

public interface ApplicationService {
ApplicationResponse createApplication(ApplicationRequest request);

}
