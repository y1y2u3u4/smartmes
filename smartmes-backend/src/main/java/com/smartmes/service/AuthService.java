package com.smartmes.service;

import com.smartmes.dto.*;

/**
 * Authentication service interface
 */
public interface AuthService {

    /**
     * Authenticate user and return tokens
     */
    LoginResponse login(LoginRequest request, String ipAddress);

    /**
     * Refresh access token using refresh token
     */
    TokenRefreshResponse refreshToken(RefreshTokenRequest request);

    /**
     * Logout user and invalidate token
     */
    void logout(String token);

    /**
     * Get current user information
     */
    UserDTO getCurrentUser(String username);

    /**
     * Change user password
     */
    void changePassword(String username, ChangePasswordRequest request);
}
