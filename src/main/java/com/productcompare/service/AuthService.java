package com.productcompare.service;

import com.productcompare.dto.AuthRequest;
import com.productcompare.dto.AuthResponse;
import com.productcompare.entity.UserSession;
import com.productcompare.entity.UserWorkspace;
import com.productcompare.repository.UserSessionRepository;
import com.productcompare.repository.UserWorkspaceRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
public class AuthService {

    private final UserWorkspaceRepository userWorkspaceRepository;
    private final UserSessionRepository userSessionRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserWorkspaceRepository userWorkspaceRepository,
            UserSessionRepository userSessionRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userWorkspaceRepository = userWorkspaceRepository;
        this.userSessionRepository = userSessionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResponse register(AuthRequest request) {
        String username = normalizeUsername(request.username());
        String password = validatePassword(request.password());

        if (userWorkspaceRepository.existsByUsername(username)) {
            throw new ResponseStatusException(CONFLICT, "Username already exists");
        }

        UserWorkspace workspace = new UserWorkspace();
        workspace.setUsername(username);
        workspace.setPasswordHash(passwordEncoder.encode(password));
        workspace.setCreatedAt(LocalDateTime.now());

        UserWorkspace saved = userWorkspaceRepository.save(workspace);
        return createSession(saved);
    }

    @Transactional
    public AuthResponse login(AuthRequest request) {
        String username = normalizeUsername(request.username());
        String password = validatePassword(request.password());

        UserWorkspace workspace = userWorkspaceRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Invalid username or password"));

        if (workspace.getPasswordHash() == null || !passwordEncoder.matches(password, workspace.getPasswordHash())) {
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid username or password");
        }

        return createSession(workspace);
    }

    @Transactional
    public AuthResponse me(String token) {
        UserWorkspace workspace = getWorkspaceByToken(token);
        return new AuthResponse(token, workspace.getUsername());
    }

    @Transactional
    public void logout(String token) {
        String normalizedToken = normalizeToken(token);
        userSessionRepository.deleteByToken(normalizedToken);
    }

    @Transactional
    public UserWorkspace getWorkspaceByToken(String token) {
        String normalizedToken = normalizeToken(token);
        UserSession session = userSessionRepository.findByToken(normalizedToken)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Unauthorized"));

        session.setLastAccessAt(LocalDateTime.now());
        userSessionRepository.save(session);
        return session.getWorkspace();
    }

    private AuthResponse createSession(UserWorkspace workspace) {
        String token = UUID.randomUUID().toString();
        UserSession session = new UserSession();
        session.setToken(token);
        session.setWorkspace(workspace);
        session.setCreatedAt(LocalDateTime.now());
        session.setLastAccessAt(LocalDateTime.now());
        userSessionRepository.save(session);
        return new AuthResponse(token, workspace.getUsername());
    }

    private String normalizeUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new ResponseStatusException(UNAUTHORIZED, "Username is required");
        }
        return username.trim().toLowerCase();
    }

    private String validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new ResponseStatusException(UNAUTHORIZED, "Password is required");
        }
        if (password.trim().length() < 6) {
            throw new ResponseStatusException(UNAUTHORIZED, "Password must be at least 6 characters");
        }
        return password.trim();
    }

    private String normalizeToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new ResponseStatusException(UNAUTHORIZED, "Missing auth token");
        }
        return token.trim();
    }
}
