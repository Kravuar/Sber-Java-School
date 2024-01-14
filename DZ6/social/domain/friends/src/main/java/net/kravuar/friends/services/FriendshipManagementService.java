package net.kravuar.friends.services;

import lombok.RequiredArgsConstructor;
import net.kravuar.friends.model.Friendship;
import net.kravuar.friends.model.exceptions.AccountNotFoundException;
import net.kravuar.friends.model.exceptions.FriendshipNotFoundException;
import net.kravuar.friends.model.exceptions.FriendshipRequestAlreadySentException;
import net.kravuar.friends.ports.in.FriendshipManagementUseCase;
import net.kravuar.friends.ports.out.AccountExistenceCheckPort;
import net.kravuar.friends.ports.out.FriendshipPersistencePort;
import net.kravuar.friends.ports.out.FriendshipRetrievalPort;

@RequiredArgsConstructor
public class FriendshipManagementService implements FriendshipManagementUseCase {
    private final FriendshipRetrievalPort friendshipRetrievalPort;
    private final FriendshipPersistencePort friendshipPersistencePort;
    private final AccountExistenceCheckPort accountExistenceCheckPort;

    @Override
    public Friendship sendFriendRequest(long fromUserId, long toUserId) {
        if (friendshipRetrievalPort.findByParticipantIds(fromUserId, toUserId).isPresent())
            throw new FriendshipRequestAlreadySentException(fromUserId, toUserId);

        if (accountExistenceCheckPort.exists(fromUserId))
            throw new AccountNotFoundException(fromUserId);
        if (accountExistenceCheckPort.exists(toUserId))
            throw new AccountNotFoundException(toUserId);

        Friendship friendship = new Friendship(
                fromUserId,
                toUserId,
                Friendship.FriendshipStatus.PENDING
        );
        return friendshipPersistencePort.save(friendship);
    }

    @Override
    public void acceptFriendRequest(long fromUserId, long toUserId) {
        Friendship friendship = friendshipRetrievalPort
                .findByParticipantIds(fromUserId, toUserId)
                .orElseThrow(() -> new FriendshipNotFoundException(fromUserId, toUserId));

        if (friendship.getStatus() != Friendship.FriendshipStatus.PENDING)
            throw new IllegalStateException("Friendship should be in a PENDING status.");

        friendship.setStatus(Friendship.FriendshipStatus.ACCEPTED);
        friendshipPersistencePort.save(friendship);
    }

    @Override
    public void declineFriendRequest(long fromUserId, long toUserId) {
        Friendship friendship = friendshipRetrievalPort
                .findByParticipantIds(fromUserId, toUserId)
                .orElseThrow(() -> new FriendshipNotFoundException(fromUserId, toUserId));

        if (friendship.getStatus() != Friendship.FriendshipStatus.PENDING)
            throw new IllegalStateException("Friendship should be in a PENDING status.");

        friendshipPersistencePort.delete(friendship);
    }

    @Override
    public void cancelFriendship(long fromUserId, long toUserId) {
        Friendship friendship = friendshipRetrievalPort
                .findByParticipantIds(fromUserId, toUserId)
                .orElseThrow(() -> new FriendshipNotFoundException(fromUserId, toUserId));

        if (friendship.getStatus() != Friendship.FriendshipStatus.ACCEPTED)
            throw new IllegalStateException("Friendship should be in a ACCEPTED status.");

        friendshipPersistencePort.delete(friendship);
    }

    @Override
    public void blockUser(long fromUserId, long blockedUserId) {
        Friendship friendship = friendshipRetrievalPort
                .findByParticipantIds(fromUserId, blockedUserId)
                .orElseThrow(() -> new FriendshipNotFoundException(fromUserId, blockedUserId));

        friendship.setStatus(Friendship.FriendshipStatus.BLOCKED);
        friendshipPersistencePort.save(friendship);
    }
}