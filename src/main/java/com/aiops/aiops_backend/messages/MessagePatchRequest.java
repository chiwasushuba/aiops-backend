package com.aiops.aiops_backend.messages;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MessagePatchRequest(
        @Pattern(regexp = "USER|ASSISTANT") @Size(max = 100) String role,
        @Pattern(regexp = "(?s).*\\S.*", message = "must not be blank") @Size(max = 1000) String content
) {
    @AssertTrue(message = "role or content must be provided")
    public boolean isUpdateProvided() {
        return role != null || content != null;
    }
}
