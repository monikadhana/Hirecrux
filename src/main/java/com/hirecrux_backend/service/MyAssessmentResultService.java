
package com.hirecrux_backend.service;

import com.hirecrux_backend.dto.response.MyAssessmentResultResponse;

public interface MyAssessmentResultService {

    MyAssessmentResultResponse getMyResult(Integer attemptId);
}
