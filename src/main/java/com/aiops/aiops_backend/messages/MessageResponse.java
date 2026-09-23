package com.aiops.aiops_backend.messages;

public record MessageResponse(Long id, String role, String content) {

    public static MessageResponse from(Message message) {
        return new MessageResponse(message.getId(), message.getRole(), message.getContent());
    }
}