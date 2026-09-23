package com.aiops.aiops_backend.messages;

import java.util.List;

public record MessagesPage(List<MessageResponse> content, int page, int size, long totalElements) {
}