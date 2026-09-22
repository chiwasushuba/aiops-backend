package com.aiops.aiops_backend.users;

import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public UserResponse get(Long id) {
        return UserResponse.from(findUser(id));
    }

    @Transactional(readOnly = true)
    public UserPage list(int page, int size) {
        Page<UserResponse> users = repository.findAllByOrderByIdAsc(PageRequest.of(page, size))
                .map(UserResponse::from);
        return new UserPage(users.getContent(), page, size, users.getTotalElements());
    }

    @Transactional
    public UserResponse create(UserRequest request) {
        User user = new User(request.name().trim(), normalizeEmail(request.email()));
        try {
            return UserResponse.from(repository.saveAndFlush(user));
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateEmailException(exception);
        }
    }

    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        User user = findUser(id);
        user.update(request.name().trim(), normalizeEmail(request.email()));
        try {
            repository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateEmailException(exception);
        }
        return UserResponse.from(user);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findUser(id));
    }

    private User findUser(Long id) {
        return repository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}

class UserNotFoundException extends RuntimeException {
}

class DuplicateEmailException extends RuntimeException {
    DuplicateEmailException(Throwable cause) {
        super(cause);
    }
}
