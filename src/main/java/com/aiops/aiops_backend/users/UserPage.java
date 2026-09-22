package com.aiops.aiops_backend.users;

import java.util.List;

public record UserPage(List<UserResponse> content, int page, int size, long totalElements) {
}
