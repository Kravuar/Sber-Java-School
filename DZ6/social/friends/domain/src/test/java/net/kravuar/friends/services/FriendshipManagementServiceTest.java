package net.kravuar.friends.services;

import net.kravuar.friends.model.Friendship;
import net.kravuar.friends.model.exceptions.AccountNotFoundException;
import net.kravuar.friends.model.exceptions.FriendshipBlockedException;
import net.kravuar.friends.model.exceptions.FriendshipRequestAlreadySentException;
import net.kravuar.friends.ports.out.AccountExistenceCheckPort;
import net.kravuar.friends.ports.out.FriendshipPersistencePort;
import net.kravuar.friends.ports.out.FriendshipRetrievalPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendshipManagementServiceTest {

    @Mock
    private FriendshipRetrievalPort retrievalPort;

    @Mock
    private FriendshipPersistencePort persistencePort;

    @Mock
    private AccountExistenceCheckPort existenceCheckPort;

    @InjectMocks
    private FriendshipManagementService managementService;

    @Test
    void sendFriendRequest_ValidRequest_ReturnsPendingFriendship() {
        // given
        long fromUserId = 1L;
        long toUserId = 2L;

        when(retrievalPort.findByParticipantIds(fromUserId, toUserId)).thenReturn(Optional.empty());
        when(persistencePort.save(any(Friendship.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(existenceCheckPort.exists(fromUserId)).thenReturn(true);
        when(existenceCheckPort.exists(toUserId)).thenReturn(true);

        // when
        var result = managementService.sendFriendRequest(fromUserId, toUserId);

        // then
        assertNotNull(result);
        assertEquals(fromUserId, result.getUserId());
        assertEquals(toUserId, result.getFriendId());
        assertEquals(Friendship.FriendshipStatus.PENDING, result.getStatus());

        verify(persistencePort, times(1)).save(any(Friendship.class));
    }

    @Test
    void sendFriendRequest_RequestAlreadySent_ThrowsFriendshipRequestAlreadySentException() {
        // given
        long fromUserId = 1L;
        long toUserId = 2L;

        var alreadySentFriendship = new Friendship(
                fromUserId,
                toUserId,
                Friendship.FriendshipStatus.PENDING
        );

        when(retrievalPort.findByParticipantIds(fromUserId, toUserId)).thenReturn(Optional.of(alreadySentFriendship));
        when(existenceCheckPort.exists(fromUserId)).thenReturn(true);
        when(existenceCheckPort.exists(toUserId)).thenReturn(true);

        // when & then
        assertThrows(FriendshipRequestAlreadySentException.class, () -> managementService.sendFriendRequest(fromUserId, toUserId));
        verifyNoInteractions(persistencePort);
    }

    @Test
    void sendFriendRequest_WithNonExistingUser_ThrowsAccountNotFoundException() {
        // given
        long fromUserId = 1L;
        long toUserId = 2L;

        when(existenceCheckPort.exists(fromUserId)).thenReturn(false);

        // when & then
        assertThrows(AccountNotFoundException.class, () -> managementService.sendFriendRequest(fromUserId, toUserId));
        verifyNoInteractions(persistencePort);
    }

    @Test
    void sendFriendRequest_FromBlockedUser_ThrowsFriendshipBlockedException() {
        // given
        long blockedUserId = 1L;
        long toUserId = 2L;

        var blockedFriendship = new Friendship(
                toUserId, // reversed
                blockedUserId,
                Friendship.FriendshipStatus.BLOCKED
        );

        when(existenceCheckPort.exists(blockedUserId)).thenReturn(true);
        when(existenceCheckPort.exists(toUserId)).thenReturn(true);
        when(retrievalPort.findByParticipantIds(toUserId, blockedUserId)).thenReturn(Optional.of(blockedFriendship));
        when(retrievalPort.findByParticipantIds(blockedUserId, toUserId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(FriendshipBlockedException.class, () -> managementService.sendFriendRequest(blockedUserId, toUserId));
        verifyNoInteractions(persistencePort);
    }

    @Test
    void acceptFriendRequest_PendingFriendship_SuccessfullyAccepts() {
        // given
        long fromUserId = 1L;
        long toUserId = 2L;

        var pendingFriendship = new Friendship(
                fromUserId,
                toUserId,
                Friendship.FriendshipStatus.PENDING
        );

        when(retrievalPort.findByParticipantIds(fromUserId, toUserId)).thenReturn(Optional.of(pendingFriendship));

        // when
        assertDoesNotThrow(() -> managementService.acceptFriendRequest(fromUserId, toUserId));

        // then
        assertEquals(Friendship.FriendshipStatus.ACCEPTED, pendingFriendship.getStatus());
        verify(persistencePort, times(1)).save(pendingFriendship);
    }

    @Test
    void acceptFriendRequest_NonPendingFriendship_ThrowsIllegalStateException() {
        // given
        long fromUserId = 1L;
        long toUserId = 2L;

        var acceptedFriendship = new Friendship(
                fromUserId,
                toUserId,
                Friendship.FriendshipStatus.ACCEPTED
        );

        when(retrievalPort.findByParticipantIds(fromUserId, toUserId)).thenReturn(Optional.of(acceptedFriendship));

        // when & then
        assertThrows(IllegalStateException.class, () -> managementService.acceptFriendRequest(fromUserId, toUserId));
        verifyNoInteractions(persistencePort);
    }

    @Test
    void declineFriendRequest_PendingFriendship_SuccessfullyDeclines() {
        // given
        long fromUserId = 1L;
        long toUserId = 2L;

        var pendingFriendship = new Friendship(
                fromUserId,
                toUserId,
                Friendship.FriendshipStatus.PENDING
        );

        when(retrievalPort.findByParticipantIds(fromUserId, toUserId)).thenReturn(Optional.of(pendingFriendship));

        // when
        assertDoesNotThrow(() -> managementService.declineFriendRequest(fromUserId, toUserId));

        // then
        verify(persistencePort, times(1)).delete(pendingFriendship);
    }

    @Test
    void declineFriendRequest_NonPendingFriendship_ThrowsIllegalStateException() {
        // given
        long fromUserId = 1L;
        long toUserId = 2L;

        var acceptedFriendship = new Friendship(
                fromUserId,
                toUserId,
                Friendship.FriendshipStatus.ACCEPTED
        );

        when(retrievalPort.findByParticipantIds(fromUserId, toUserId)).thenReturn(Optional.of(acceptedFriendship));

        // when
        assertThrows(IllegalStateException.class, () -> managementService.declineFriendRequest(fromUserId, toUserId));

        // then
        verifyNoInteractions(persistencePort);
    }

    @Test
    void cancelFriendship_AcceptedFriendship_SuccessfullyCancels() {
        // given
        long fromUserId = 1L;
        long toUserId = 2L;

        var acceptedFriendship = new Friendship(
                fromUserId,
                toUserId,
                Friendship.FriendshipStatus.ACCEPTED
        );

        when(retrievalPort.findByParticipantIds(fromUserId, toUserId)).thenReturn(Optional.of(acceptedFriendship));

        // when
        assertDoesNotThrow(() -> managementService.cancelFriendship(fromUserId, toUserId));

        // then
        verify(persistencePort, times(1)).delete(acceptedFriendship);
    }

    @Test
    void cancelFriendship_NonAcceptedFriendship_ThrowsIllegalStateException() {
        // given
        long fromUserId = 1L;
        long toUserId = 2L;

        var pendingFriendship = new Friendship(
                fromUserId,
                toUserId,
                Friendship.FriendshipStatus.PENDING
        );

        when(retrievalPort.findByParticipantIds(fromUserId, toUserId)).thenReturn(Optional.of(pendingFriendship));

        // when & then
        assertThrows(IllegalStateException.class, () -> managementService.cancelFriendship(fromUserId, toUserId));
        verifyNoInteractions(persistencePort);
    }

    @Test
    void blockUser_NoFriendshipExists_CreateBlockedFriendship() {
        // given
        long fromUserId = 1L;
        long blockedUserId = 2L;

        when(retrievalPort.findByParticipantIds(anyLong(), anyLong())).thenReturn(Optional.empty());

        // when
        assertDoesNotThrow(() -> managementService.blockUser(fromUserId, blockedUserId));

        // then
        verify(persistencePort, times(1)).save(any(Friendship.class));
    }

    @Test
    void blockUser_DirectFriendshipExist_ThrowsFriendshipNotFoundException() {
        // given
        long fromUserId = 1L;
        long blockedUserId = 2L;

        var existingDirectFriendship = new Friendship(
                fromUserId,
                blockedUserId,
                Friendship.FriendshipStatus.PENDING // anything
        );

        when(retrievalPort.findByParticipantIds(blockedUserId, fromUserId)).thenReturn(Optional.empty());
        when(retrievalPort.findByParticipantIds(fromUserId, blockedUserId)).thenReturn(Optional.of(existingDirectFriendship));

        // when & then
        assertDoesNotThrow(() -> managementService.blockUser(fromUserId, blockedUserId));
        assertEquals(Friendship.FriendshipStatus.BLOCKED, existingDirectFriendship.getStatus());
        verify(persistencePort, times(1)).save(existingDirectFriendship);
    }

    @Test
    void blockUser_ReversedFriendshipExist_ThrowsFriendshipNotFoundException() {
        // given
        long blockedFromUserId = 1L;
        long blockedUserId = 2L;

        var existingReversedFriendship = new Friendship(
                blockedUserId,
                blockedFromUserId,
                Friendship.FriendshipStatus.PENDING // anything
        );

        when(retrievalPort.findByParticipantIds(blockedUserId, blockedFromUserId)).thenReturn(Optional.of(existingReversedFriendship));
        when(retrievalPort.findByParticipantIds(blockedFromUserId, blockedUserId)).thenReturn(Optional.empty());

        // when & then
        assertDoesNotThrow(() -> managementService.blockUser(blockedFromUserId, blockedUserId));
        verify(persistencePort, times(1)).delete(existingReversedFriendship);
        verify(persistencePort, times(1)).save(any(Friendship.class));
    }
}
