package net.kravuar.friends;

import lombok.RequiredArgsConstructor;
import net.kravuar.friends.model.Friendship;
import net.kravuar.friends.model.exceptions.AccountNotFoundException;
import net.kravuar.friends.model.exceptions.FriendshipBlockedException;
import net.kravuar.friends.model.exceptions.FriendshipNotFoundException;
import net.kravuar.friends.model.exceptions.FriendshipRequestAlreadySentException;
import net.kravuar.friends.ports.in.FriendshipManagementUseCase;
import net.kravuar.friends.ports.out.AccountExistenceCheckPort;
import net.kravuar.friends.ports.out.FriendshipPersistencePort;
import net.kravuar.friends.ports.out.FriendshipRetrievalPort;

import java.util.Optional;

@RequiredArgsConstructor
public class FriendshipManagementFacade implements FriendshipManagementUseCase {
    private final FriendshipRetrievalPort friendshipRetrievalPort;
    private final FriendshipPersistencePort friendshipPersistencePort;
    private final AccountExistenceCheckPort accountExistenceCheckPort;

    @Override
    public Friendship sendFriendRequest(long fromUserId, long toUserId) {
        if (!accountExistenceCheckPort.exists(fromUserId))
            throw new AccountNotFoundException(fromUserId);
        if (!accountExistenceCheckPort.exists(toUserId))
            throw new AccountNotFoundException(toUserId);

        if (friendshipRetrievalPort.findByParticipantIds(fromUserId, toUserId).isPresent())
            throw new FriendshipRequestAlreadySentException(fromUserId, toUserId);
        var maybeReversedFriendship = friendshipRetrievalPort.findByParticipantIds(toUserId, fromUserId);
        if (maybeReversedFriendship.isPresent())
            if (maybeReversedFriendship.get().getStatus() == Friendship.FriendshipStatus.BLOCKED)
                throw new FriendshipBlockedException(toUserId, fromUserId);
            else {
                var reversedFriendship = maybeReversedFriendship.get();
                reversedFriendship.setStatus(Friendship.FriendshipStatus.ACCEPTED);
                friendshipPersistencePort.save(reversedFriendship);
                return reversedFriendship;
            }

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
        Optional<Friendship> maybeReversedFriendship = friendshipRetrievalPort
                .findByParticipantIds(blockedUserId, fromUserId);
        maybeReversedFriendship.ifPresent(friendshipPersistencePort::delete);

        Friendship blockedFriendship = friendshipRetrievalPort
                .findByParticipantIds(fromUserId, blockedUserId)
                .orElse(new Friendship(
                        fromUserId,
                        blockedUserId,
                        Friendship.FriendshipStatus.BLOCKED
                ));

        blockedFriendship.setStatus(Friendship.FriendshipStatus.BLOCKED);
        friendshipPersistencePort.save(blockedFriendship);
    }
}