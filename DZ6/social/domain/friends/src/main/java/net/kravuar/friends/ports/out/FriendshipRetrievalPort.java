package net.kravuar.friends.ports.out;

import net.kravuar.friends.model.Friendship;

import java.util.List;
import java.util.Optional;

public interface FriendshipRetrievalPort {
    Optional<Friendship> findByParticipantIds(long fromUserId, long toUserId);
    List<Friendship> findAllByUserId(long userId);
    List<Friendship> findPendingTo(long userId);
    List<Friendship> findPendingFrom(long userId);
}
