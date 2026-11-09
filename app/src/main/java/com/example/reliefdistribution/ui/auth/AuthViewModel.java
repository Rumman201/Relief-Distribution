package com.example.reliefdistribution.ui.auth;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.reliefdistribution.User;
import com.example.reliefdistribution.data.model.Resource;
import com.example.reliefdistribution.data.repository.AuthRepository;
import com.example.reliefdistribution.data.repository.FirebaseAuthRepository;
import com.example.reliefdistribution.data.repository.RepositoryCallback;

/**
 * ViewModel managing authentication state and user session verification.
 */
public class AuthViewModel extends ViewModel {

    private final AuthRepository authRepository;

    private final MutableLiveData<Resource<User>> loginState = new MutableLiveData<>();
    private final MutableLiveData<Resource<User>> registerState = new MutableLiveData<>();
    private final MutableLiveData<Resource<User>> sessionState = new MutableLiveData<>();

    /**
     * Default constructor for Android Architecture Components ViewModelProvider.
     */
    public AuthViewModel() {
        this(new FirebaseAuthRepository());
    }

    /**
     * Constructor for dependency injection and unit testing.
     *
     * @param authRepository Repository providing authentication operations.
     */
    public AuthViewModel(@NonNull AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public LiveData<Resource<User>> getLoginState() {
        return loginState;
    }

    public LiveData<Resource<User>> getRegisterState() {
        return registerState;
    }

    public LiveData<Resource<User>> getSessionState() {
        return sessionState;
    }

    /**
     * Executes user login and verifies that the authenticated user matches the selected role.
     *
     * @param email        User email address.
     * @param password     User password.
     * @param selectedRole Selected role ("Distributor" or "Survivor").
     */
    public void login(@NonNull String email, @NonNull String password, @NonNull String selectedRole) {
        loginState.setValue(Resource.loading(null));

        authRepository.login(email, password, new RepositoryCallback<String>() {
            @Override
            public void onSuccess(String userId) {
                authRepository.getUserProfile(userId, new RepositoryCallback<User>() {
                    @Override
                    public void onSuccess(User user) {
                        if (user != null && selectedRole.equals(user.getRole())) {
                            loginState.setValue(Resource.success(user));
                        } else {
                            authRepository.signOut();
                            loginState.setValue(Resource.error("Selected role does not match account role", null));
                        }
                    }

                    @Override
                    public void onError(Exception exception) {
                        authRepository.signOut();
                        loginState.setValue(Resource.error("User profile not found", null));
                    }
                });
            }

            @Override
            public void onError(Exception exception) {
                String errorMsg = exception.getMessage() != null ? exception.getMessage() : "Authentication failed";
                loginState.setValue(Resource.error(errorMsg, null));
            }
        });
    }

    /**
     * Registers a new user with credentials and profile details.
     */
    public void register(@NonNull String firstName, @NonNull String lastName, @NonNull String email,
                         @NonNull String phone, @NonNull String role, @NonNull String password) {
        registerState.setValue(Resource.loading(null));

        authRepository.register(firstName, lastName, email, phone, role, password, new RepositoryCallback<User>() {
            @Override
            public void onSuccess(User user) {
                registerState.setValue(Resource.success(user));
            }

            @Override
            public void onError(Exception exception) {
                String errorMsg = exception.getMessage() != null ? exception.getMessage() : "Registration failed";
                registerState.setValue(Resource.error(errorMsg, null));
            }
        });
    }

    /**
     * Verifies if a persistent user session exists.
     */
    public void checkSession() {
        sessionState.setValue(Resource.loading(null));

        authRepository.checkUserSession(new RepositoryCallback<User>() {
            @Override
            public void onSuccess(User user) {
                sessionState.setValue(Resource.success(user));
            }

            @Override
            public void onError(Exception exception) {
                authRepository.signOut();
                String errorMsg = exception.getMessage() != null ? exception.getMessage() : "No active session";
                sessionState.setValue(Resource.error(errorMsg, null));
            }
        });
    }

    /**
     * Signs out the user and clears authentication state.
     */
    public void signOut() {
        authRepository.signOut();
        loginState.setValue(null);
        sessionState.setValue(null);
    }
}

