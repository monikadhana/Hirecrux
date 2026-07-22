package com.hirecrux_backend.dto.request;

import com.hirecrux_backend.enums.ApplicationStatus;
import lombok.Data;

@Data
public class UpdateApplicationRequest {
    private ApplicationStatus applicationStatus;
}
