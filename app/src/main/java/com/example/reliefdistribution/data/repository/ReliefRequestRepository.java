package com.example.reliefdistribution.data.repository;

import com.example.reliefdistribution.data.model.ReliefRequest;

import java.util.List;

/**
 * Repository interface governing survivor emergency aid requests and distributor triage.
 */
public interface ReliefRequestRepository {

    /**
     * Creates and broadcasts a new survivor relief request.
     */
    void createRequest(ReliefRequest request, RepositoryCallback<ReliefRequest> callback);

    /**
     * Retrieves an individual aid request by its unique identifier.
     */
    void getRequest(String requestId, RepositoryCallback<ReliefRequest> callback);

    /**
     * Atomically transitions the request status (e.g. from PENDING to ACCEPTED) using a database transaction.
     */
    void updateRequestStatus(String requestId, String newStatus, String distributorId, RepositoryCallback<Void> callback);

    /**
     * Queries all aid requests submitted by a specific survivor.
     */
    void getRequestsForSurvivor(String userId, RepositoryCallback<List<ReliefRequest>> callback);

    /**
     * Queries pending aid requests awaiting distributor acceptance.
     */
    void getPendingRequests(RepositoryCallback<List<ReliefRequest>> callback);
}

