package com.aiops.aiops_backend.messages;

import java.net.URI;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
public class MessagesController {
    private final MessagesService service;

    public MessagesController(MessagesService service) {
        this.service = service;
    }

    @GetMapping
    public MessagesPage list(@RequestParam(defaultValue = "0") @Min(0) int page,
                             @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return service.list(page, size);
    }

    @GetMapping("/{id}")
    public MessageResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public ResponseEntity<MessageResponse> create(@Valid @RequestBody MessageRequest request) {
        MessageResponse message = service.create(request);
        return ResponseEntity.created(URI.create("/api/messages/" + message.id())).body(message);
    } 

    @PutMapping("/{id}")
    public MessageResponse update(@PathVariable Long id, @Valid @RequestBody MessageRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}")
    public MessageResponse partialUpdate(@PathVariable Long id, @Valid @RequestBody MessagePatchRequest request) {
        return service.partialUpdate(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}

