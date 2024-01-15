package net.kravuar.friends.adapters;

import net.kravuar.friends.model.Friendship;
import net.kravuar.friends.ports.out.FriendshipPersistencePort;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NoopFriendshipPersistencePort implements FriendshipPersistencePort {
    private final Logger log = LogManager.getLogger(NoopFriendshipPersistencePort.class);

    @Override
    public Friendship save(Friendship friendship) {
        log.info(
                "Friendship save operation: userId={}, friendId={}, status={}",
                friendship.getUserId(),
                friendship.getFriendId(),
                friendship.getStatus()
        );
        return null;
    }

    @Override
    public void delete(Friendship friendship) {
        log.info(
                "Friendship delete operation: userId={}, friendId={}, status={}",
                friendship.getUserId(),
                friendship.getFriendId(),
                friendship.getStatus()
        );
    }
}