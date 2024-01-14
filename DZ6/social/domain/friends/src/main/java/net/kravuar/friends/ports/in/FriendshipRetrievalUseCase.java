package net.kravuar.friends.ports.in;

import net.kravuar.friends.model.Friendship;

import java.util.List;

/**
 * Interface defining the use case for retrieving friendships.
 */
public interface FriendshipRetrievalUseCase {

    /**
     * Lists all friends of a given user.
     *
     * @param userId The ID of the user.
     * @return {@code List} of {@link Friendship} representing the user's friends.
     */
    List<Friendship> listFriends(long userId);

    /**
     * Lists all pending friend requests received by a user.
     *
     * @param userId The ID of the receiver user.
     * @return {@code List} of {@link Friendship} representing pending friend requests to the user.
     */
    List<Friendship> listPendingTo(long userId);

    /**
     * Lists all pending friend requests sent by a user.
     *
     * @param userId The ID of the sender user.
     * @return {@code List} of {@link Friendship} representing pending friend requests from the user.
     */
    List<Friendship> listPendingFrom(long userId);
}
