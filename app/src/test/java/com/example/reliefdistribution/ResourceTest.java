package com.example.reliefdistribution;

import com.example.reliefdistribution.data.model.Resource;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ResourceTest {

    @Test
    public void success_createsSuccessfulResource() {
        String payload = "Test Payload";
        Resource<String> resource = Resource.success(payload);

        assertEquals(Resource.Status.SUCCESS, resource.getStatus());
        assertEquals(payload, resource.getData());
        assertNull(resource.getMessage());
    }

    @Test
    public void error_createsErrorResource() {
        String errorMsg = "Something went wrong";
        Resource<String> resource = Resource.error(errorMsg, "Partial");

        assertEquals(Resource.Status.ERROR, resource.getStatus());
        assertEquals(errorMsg, resource.getMessage());
        assertEquals("Partial", resource.getData());
    }

    @Test
    public void loading_createsLoadingResource() {
        Resource<Integer> resource = Resource.loading(42);

        assertEquals(Resource.Status.LOADING, resource.getStatus());
        assertEquals(Integer.valueOf(42), resource.getData());
        assertNull(resource.getMessage());
    }
}

