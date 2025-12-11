package com.smartmes.service.impl;

import com.smartmes.dto.*;
import com.smartmes.entity.TokenBlacklist;
import com.smartmes.entity.User;
import com.smartmes.repository.TokenBlacklistRepository;
import com.smartmes.repository.UserRepository;
import com.smartmes.security.JwtTokenProvider;
import com.smartmes.service.AuditLogService;
import com.smartmes.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Authentication service implementation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 30;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request, String ipAddress) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("Login attempt for non-existent user: {}", request.getUsername());
                    return new BadCredentialsException("Invalid username or password");
                });

        // Check if account is locked
        if (user.isLocked()) {
            log.warn("Login attempt for locked account: {}", request.getUsername());
            throw new LockedException("Account is locked. Please try again later.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // Login successful - reset login attempts and update last login time
            userRepository.updateLoginSuccess(user.getUserId(), LocalDateTime.now());

            // Generate tokens
            String accessToken = jwtTokenProvider.generateAccessToken(request.getUsername());
            String refreshToken = jwtTokenProvider.generateRefreshToken(request.getUsername());

            // Log successful login
            auditLogService.logLogin(user.getUserId(), user.getUsername(), ipAddress, true);

            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtTokenProvider.getAccessTokenExpirationInSeconds())
                    .user(LoginResponse.UserInfo.builder()
                            .userId(user.getUserId())
                            .username(user.getUsername())
                            .realName(user.getRealName())
                            .role(user.getRole().name())
                            .team(user.getTeam())
                            .status(user.getStatus().name())
                            .build())
                    .build();

        } catch (BadCredentialsException e) {
            // Increment login attempts
            userRepository.incrementLoginAttempts(user.getUserId());
            int attempts = user.getLoginAttempts() + 1;

            if (attempts >= MAX_LOGIN_ATTEMPTS) {
                // Lock account
                LocalDateTime lockUntil = LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES);
                userRepository.lockAccount(user.getUserId(), lockUntil);
                log.warn("Account locked due to too many failed attempts: {}", request.getUsername());

                // Log failed login with lock
                auditLogService.logLogin(user.getUserId(), user.getUsername(), ipAddress, false);

                throw new LockedException("Account locked due to too many failed attempts. Please try again in " + LOCK_DURATION_MINUTES + " minutes.");
            }

            // Log failed login
            auditLogService.logLogin(user.getUserId(), user.getUsername(), ipAddress, false);

            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @Override
    public TokenRefreshResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // Validate refresh token
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        // Ensure it's a refresh token
        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new BadCredentialsException("Invalid token type");
        }

        // Check if token is blacklisted
        String tokenHash = jwtTokenProvider.hashToken(refreshToken);
        if (tokenBlacklistRepository.existsByTokenHash(tokenHash)) {
            throw new BadCredentialsException("Token has been revoked");
        }

        // Generate new access token
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        String newAccessToken = jwtTokenProvider.generateAccessToken(username);

        return TokenRefreshResponse.builder()
                .accessToken(newAccessToken)
                .expiresIn(jwtTokenProvider.getAccessTokenExpirationInSeconds())
                .build();
    }

    @Override
    @Transactional
    public void logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (token != null && jwtTokenProvider.validateToken(token)) {
            String tokenHash = jwtTokenProvider.hashToken(token);
            LocalDateTime expiresAt = jwtTokenProvider.getExpirationFromToken(token)
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            // Add token to blacklist
            TokenBlacklist blacklistEntry = new TokenBlacklist(tokenHash, expiresAt);
            tokenBlacklistRepository.save(blacklistEntry);

            // Log logout
            String username = jwtTokenProvider.getUsernameFromToken(token);
            userRepository.findByUsername(username).ifPresent(user ->
                    auditLogService.logLogout(user.getUserId(), user.getUsername())
            );
        }
    }

    @Override
    public UserDTO getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .role(user.getRole().name())
                .team(user.getTeam())
                .status(user.getStatus().name())
                .build();
    }

    @Override
    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        // Validate passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Old password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Log password change
        auditLogService.logPasswordChange(user.getUserId(), user.getUsername());
    }
}
