package com.aiops.aiops_backend.messages;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessagesService {

    private final MessagesRepository repository;

    public MessagesService(MessagesRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public MessageResponse get(Long id) {
        return MessageResponse.from(findMessage(id));
    }

    @Transactional(readOnly = true)
    public MessagesPage list(int page, int size) {
        Page<MessageResponse> messages = repository.findAllByOrderByIdAsc(PageRequest.of(page, size))
                .map(MessageResponse::from);
        return new MessagesPage(messages.getContent(), page, size, messages.getTotalElements());
    }

    @Transactional
    public MessageResponse create(MessageRequest request) {
        Message message = new Message(request.role().trim(), request.content().trim());
        return MessageResponse.from(repository.saveAndFlush(message));
    }

    @Transactional
    public MessageResponse update(Long id, MessageRequest request) {
        Message message = findMessage(id);
        message.update(request.role().trim(), request.content().trim());
        repository.flush();
        return MessageResponse.from(message);
    }

    @Transactional
    public MessageResponse partialUpdate(Long id, MessagePatchRequest request) {
        Message message = findMessage(id);
        String role = request.role() == null ? message.getRole() : request.role().trim();
        String content = request.content() == null ? message.getContent() : request.content().trim();
        message.update(role, content);
        repository.flush();
        return MessageResponse.from(message);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findMessage(id));
    }

    private Message findMessage(Long id) {
        return repository.findById(id)
                .orElseThrow(MessageNotFoundException::new);
    }
}

class MessageNotFoundException extends RuntimeException {
}


