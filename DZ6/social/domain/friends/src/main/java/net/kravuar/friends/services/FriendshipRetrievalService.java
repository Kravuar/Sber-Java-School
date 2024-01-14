package net.kravuar.friends.services;

import lombok.RequiredArgsConstructor;
import net.kravuar.friends.model.Friendship;
import net.kravuar.friends.ports.in.FriendshipRetrievalUseCase;
import net.kravuar.friends.ports.out.FriendshipRetrievalPort;

import java.util.List;

@RequiredArgsConstructor
public class FriendshipRetrievalService implements FriendshipRetrievalUseCase {
    private final FriendshipRetrievalPort friendshipRetrievalPort;

    @Override
    public List<Friendship> listFriends(long userId) {
        return friendshipRetrievalPort.findAllByUserId(userId);
    }

    @Override
    public List<Friendship> listPendingTo(long userId) {
        return friendshipRetrievalPort.findPendingTo(userId);
    }

    @Override
    public List<Friendship> listPendingFrom(long userId) {
        return friendshipRetrievalPort.findPendingFrom(userId);
    }
}
