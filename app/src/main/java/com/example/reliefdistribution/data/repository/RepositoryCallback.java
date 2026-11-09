package com.example.reliefdistribution.data.repository;

/**
 * Generic callback interface for asynchronous repository operations.
 *
 * @param <T> The type of the expected result data.
 */
public interface RepositoryCallback<T> {
    /**
     * Invoked when the repository operation succeeds.
     *
     * @param result The result payload.
     */
    void onSuccess(T result);

    /**
     * Invoked when the repository operation fails.
     *
     * @param exception The exception causing the failure.
     */
    void onError(Exception exception);
}

