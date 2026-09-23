package com.aiops.aiops_backend.messages;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String role;

    @Column(nullable = false, length = 1000)
    private String content;

    protected Message() {
    }

    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public Long getId(){
        return id;
    }

    public String getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }

    public void update(String role, String content) {
        this.role = role;
        this.content = content;
    }
}
