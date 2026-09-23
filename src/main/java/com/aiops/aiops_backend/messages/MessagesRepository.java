package com.aiops.aiops_backend.messages;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface MessagesRepository extends JpaRepository<Message, Long> {
    Page<Message> findAllByOrderByIdAsc(Pageable pageable);
}

