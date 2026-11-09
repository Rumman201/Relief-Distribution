package com.example.reliefdistribution.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * Generic wrapper representing UI and data state with status, payload, and optional message.
 * @param <T> Payload data type
 */
public class Resource<T> {

    public enum Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    @NonNull
    private final Status status;

    @Nullable
    private final T data;

    @Nullable
    private final String message;

    private Resource(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> Resource<T> success(@Nullable T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> error(@NonNull String message, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, message);
    }

    public static <T> Resource<T> error(@NonNull String message) {
        return new Resource<>(Status.ERROR, null, message);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, null);
    }

    public static <T> Resource<T> loading() {
        return new Resource<>(Status.LOADING, null, null);
    }

    @NonNull
    public Status getStatus() {
        return status;
    }

    @Nullable
    public T getData() {
        return data;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    public boolean isError() {
        return status == Status.ERROR;
    }

    public boolean isLoading() {
        return status == Status.LOADING;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resource<?> resource = (Resource<?>) o;
        return status == resource.status &&
                Objects.equals(data, resource.data) &&
                Objects.equals(message, resource.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, data, message);
    }

    @NonNull
    @Override
    public String toString() {
        return "Resource{" +
                "status=" + status +
                ", data=" + data +
                ", message='" + message + '\'' +
                '}';
    }
}

