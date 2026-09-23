package com.aiops.aiops_backend.messages;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MessageRequest(
        @NotBlank @Pattern(regexp = "USER|ASSISTANT") @Size(max = 100) String role,
        @NotBlank @Size(max = 1000) String content
) {
}
