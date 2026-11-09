package com.example.reliefdistribution;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.reliefdistribution.data.repository.FirebaseConnectionRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FirebaseConnectionRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private DatabaseReference connectedRef;

    @Captor
    private ArgumentCaptor<ValueEventListener> listenerCaptor;

    private FirebaseConnectionRepository connectionRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        connectionRepository = new FirebaseConnectionRepository(connectedRef);
    }

    @Test
    public void startMonitoring_listensAndEmitsState() {
        connectionRepository.startMonitoring();
        verify(connectedRef).addValueEventListener(listenerCaptor.capture());

        ValueEventListener listener = listenerCaptor.getValue();

        // Simulate connection established
        DataSnapshot snapshotConnected = mock(DataSnapshot.class);
        when(snapshotConnected.getValue(Boolean.class)).thenReturn(Boolean.TRUE);
        listener.onDataChange(snapshotConnected);
        assertTrue(Boolean.TRUE.equals(connectionRepository.isConnected().getValue()));

        // Simulate connection dropped
        DataSnapshot snapshotDisconnected = mock(DataSnapshot.class);
        when(snapshotDisconnected.getValue(Boolean.class)).thenReturn(Boolean.FALSE);
        listener.onDataChange(snapshotDisconnected);
        assertFalse(Boolean.TRUE.equals(connectionRepository.isConnected().getValue()));
    }

    @Test
    public void stopMonitoring_removesListener() {
        connectionRepository.startMonitoring();
        verify(connectedRef).addValueEventListener(listenerCaptor.capture());

        connectionRepository.stopMonitoring();
        verify(connectedRef).removeEventListener(listenerCaptor.getValue());
    }
}

