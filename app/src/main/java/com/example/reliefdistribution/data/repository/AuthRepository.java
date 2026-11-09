package com.example.reliefdistribution.data.repository;

import com.example.reliefdistribution.User;

/**
 * Repository interface defining authentication and user profile operations.
 */
public interface AuthRepository {

    /**
     * Authenticates a user with email and password.
     *
     * @param email    User email.
     * @param password User password.
     * @param callback Callback delivering the authenticated user ID or error.
     */
    void login(String email, String password, RepositoryCallback<String> callback);

    /**
     * Registers a new user account and saves the initial user profile.
     *
     * @param firstName User first name.
     * @param lastName  User last name.
     * @param email     User email.
     * @param phone     User phone number.
     * @param role      User role (e.g. "Distributor" or "Survivor").
     * @param password  User password.
     * @param callback  Callback delivering the created User entity or error.
     */
    void register(String firstName, String lastName, String email, String phone,
                  String role, String password, RepositoryCallback<User> callback);

    /**
     * Retrieves the user profile record for a given user ID.
     *
     * @param userId   Firebase Auth UID.
     * @param callback Callback delivering the User object or error.
     */
    void getUserProfile(String userId, RepositoryCallback<User> callback);

    /**
     * Checks if a persistent user session exists and fetches their profile.
     *
     * @param callback Callback delivering the logged-in User profile or error.
     */
    void checkUserSession(RepositoryCallback<User> callback);

    /**
     * Signs out the current user session from Firebase Authentication.
     */
    void signOut();

    /**
     * Returns the currently authenticated user UID, or null if unauthenticated.
     */
    String getCurrentUserId();

    /**
     * Returns true if a user is currently signed in.
     */
    boolean isUserLoggedIn();
}

