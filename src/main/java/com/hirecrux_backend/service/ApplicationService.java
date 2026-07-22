package com.hirecrux_backend.service;

import com.hirecrux_backend.dto.request.ApplicationRequest;
import com.hirecrux_backend.dto.request.UpdateApplicationRequest;
import com.hirecrux_backend.dto.response.ApplicationResponse;

import java.util.List;

public interface ApplicationService {
ApplicationResponse createApplication(ApplicationRequest request);
ApplicationResponse getApplicationById(Integer applicationId);
ApplicationResponse updateApplicationStatus(Integer applicationId, UpdateApplicationRequest request);
List<ApplicationResponse> getApplicationsByJob(Integer jobId);

}
