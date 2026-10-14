
package com.hirecrux_backend.service;

import com.hirecrux_backend.dto.response.AssessmentResultsResponse;

import java.util.List;

public interface AssessmentResultsService {

    List<AssessmentResultsResponse> getAssessmentResults(
            Integer assessmentId
    );
}
