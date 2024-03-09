package net.kravuar.friends;

import lombok.RequiredArgsConstructor;
import net.kravuar.friends.model.Friendship;
import net.kravuar.friends.model.exceptions.AccountNotFoundException;
import net.kravuar.friends.ports.in.FriendshipRetrievalUseCase;
import net.kravuar.friends.ports.out.AccountExistenceCheckPort;
import net.kravuar.friends.ports.out.FriendshipRetrievalPort;

import java.util.List;

@RequiredArgsConstructor
public class FriendshipRetrievalFacade implements FriendshipRetrievalUseCase {
    private final FriendshipRetrievalPort friendshipRetrievalPort;
    private final AccountExistenceCheckPort accountExistenceCheckPort;

    @Override
    public List<Friendship> listFriends(long userId) {
        checkIfExistsOrElseThrow(userId);
        return friendshipRetrievalPort.findAllByUserId(userId);
    }

    @Override
    public List<Friendship> listPendingTo(long userId) {
        checkIfExistsOrElseThrow(userId);
        return friendshipRetrievalPort.findPendingTo(userId);
    }

    @Override
    public List<Friendship> listPendingFrom(long userId) {
        checkIfExistsOrElseThrow(userId);
        return friendshipRetrievalPort.findPendingFrom(userId);
    }

    private void checkIfExistsOrElseThrow(long userId) {
        if (accountExistenceCheckPort.exists(userId))
            throw new AccountNotFoundException(userId);
    }
}
