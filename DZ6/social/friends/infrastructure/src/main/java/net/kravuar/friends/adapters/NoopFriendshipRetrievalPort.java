package net.kravuar.friends.adapters;

import net.kravuar.friends.model.Friendship;
import net.kravuar.friends.ports.out.FriendshipRetrievalPort;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class NoopFriendshipRetrievalPort implements FriendshipRetrievalPort {
    private final Logger log = LogManager.getLogger(NoopFriendshipRetrievalPort.class);

    @Override
    public Optional<Friendship> findByParticipantIds(long fromUserId, long toUserId) {
        log.info(
                "Finding Friendship by participant ids: fromUserId={}, toUserId={}",
                fromUserId,
                toUserId
        );
        return Optional.empty();
    }

    @Override
    public List<Friendship> findAllByUserId(long userId) {
        log.info(
                "Finding all Friendships by userId={}",
                userId
        );
        return Collections.emptyList();
    }

    @Override
    public List<Friendship> findPendingTo(long userId) {
        log.info(
                "Finding pending Friendships to userId={}",
                userId
        );
        return Collections.emptyList();
    }

    @Override
    public List<Friendship> findPendingFrom(long userId) {
        log.info(
                "Finding pending Friendships from userId={}",
                userId
        );
        return Collections.emptyList();
    }
}