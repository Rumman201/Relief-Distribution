package com.example.reliefdistribution;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.reliefdistribution.data.model.Resource;
import com.example.reliefdistribution.data.repository.AuthRepository;
import com.example.reliefdistribution.data.repository.RepositoryCallback;
import com.example.reliefdistribution.ui.auth.AuthViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class AuthViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private AuthRepository authRepository;

    @Captor
    private ArgumentCaptor<RepositoryCallback<String>> loginCallbackCaptor;

    @Captor
    private ArgumentCaptor<RepositoryCallback<User>> userCallbackCaptor;

    private AuthViewModel authViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        authViewModel = new AuthViewModel(authRepository);
    }

    @Test
    public void login_successful_emitsSuccess() {
        String email = "distributor@example.com";
        String password = "password123";
        String role = "Distributor";
        String userId = "uid-100";
        User user = new User(userId, "Test", "Distributor", email, "01700000000", role);

        authViewModel.login(email, password, role);

        // Verify repository was called
        verify(authRepository).login(eq(email), eq(password), loginCallbackCaptor.capture());

        // Simulate successful Firebase auth
        loginCallbackCaptor.getValue().onSuccess(userId);

        // Verify profile lookup
        verify(authRepository).getUserProfile(eq(userId), userCallbackCaptor.capture());

        // Simulate successful database retrieval
        userCallbackCaptor.getValue().onSuccess(user);

        // Verify ViewModel LiveData state
        Resource<User> result = authViewModel.getLoginState().getValue();
        assertNotNull(result);
        assertEquals(Resource.Status.SUCCESS, result.getStatus());
        assertEquals(user, result.getData());
    }

    @Test
    public void login_roleMismatch_emitsErrorAndSignsOut() {
        String email = "survivor@example.com";
        String password = "password123";
        String requestedRole = "Distributor"; // Mismatched role
        String actualRole = "Survivor";
        String userId = "uid-101";
        User user = new User(userId, "Test", "Survivor", email, "01800000000", actualRole);

        authViewModel.login(email, password, requestedRole);

        verify(authRepository).login(eq(email), eq(password), loginCallbackCaptor.capture());
        loginCallbackCaptor.getValue().onSuccess(userId);

        verify(authRepository).getUserProfile(eq(userId), userCallbackCaptor.capture());
        userCallbackCaptor.getValue().onSuccess(user);

        // Verify sign out was called because of role mismatch
        verify(authRepository).signOut();

        Resource<User> result = authViewModel.getLoginState().getValue();
        assertNotNull(result);
        assertEquals(Resource.Status.ERROR, result.getStatus());
        assertEquals("Selected role does not match account role", result.getMessage());
    }

    @Test
    public void login_authFailed_emitsError() {
        String email = "bad@example.com";
        String password = "wrongpassword";
        String role = "Distributor";

        authViewModel.login(email, password, role);

        verify(authRepository).login(eq(email), eq(password), loginCallbackCaptor.capture());
        loginCallbackCaptor.getValue().onError(new Exception("Invalid user credentials"));

        Resource<User> result = authViewModel.getLoginState().getValue();
        assertNotNull(result);
        assertEquals(Resource.Status.ERROR, result.getStatus());
        assertEquals("Invalid user credentials", result.getMessage());
    }

    @Test
    public void register_successful_emitsSuccess() {
        String fName = "John";
        String lName = "Doe";
        String email = "john@example.com";
        String phone = "01900000000";
        String role = "Survivor";
        String password = "password123";
        User registeredUser = new User("uid-200", fName, lName, email, phone, role);

        authViewModel.register(fName, lName, email, phone, role, password);

        verify(authRepository).register(eq(fName), eq(lName), eq(email), eq(phone), eq(role), eq(password), userCallbackCaptor.capture());
        userCallbackCaptor.getValue().onSuccess(registeredUser);

        Resource<User> result = authViewModel.getRegisterState().getValue();
        assertNotNull(result);
        assertEquals(Resource.Status.SUCCESS, result.getStatus());
        assertEquals(registeredUser, result.getData());
    }

    @Test
    public void register_failure_emitsError() {
        authViewModel.register("A", "B", "email@test.com", "012", "Distributor", "pass");

        verify(authRepository).register(any(), any(), any(), any(), any(), any(), userCallbackCaptor.capture());
        userCallbackCaptor.getValue().onError(new Exception("Email already exists"));

        Resource<User> result = authViewModel.getRegisterState().getValue();
        assertNotNull(result);
        assertEquals(Resource.Status.ERROR, result.getStatus());
        assertEquals("Email already exists", result.getMessage());
    }

    @Test
    public void checkSession_activeSession_emitsSuccess() {
        User existingUser = new User("uid-300", "Logged", "In", "user@test.com", "013", "Distributor");

        authViewModel.checkSession();

        verify(authRepository).checkUserSession(userCallbackCaptor.capture());
        userCallbackCaptor.getValue().onSuccess(existingUser);

        Resource<User> result = authViewModel.getSessionState().getValue();
        assertNotNull(result);
        assertEquals(Resource.Status.SUCCESS, result.getStatus());
        assertEquals(existingUser, result.getData());
    }
}

