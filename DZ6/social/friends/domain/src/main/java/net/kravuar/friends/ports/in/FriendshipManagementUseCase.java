package net.kravuar.friends.ports.in;

import net.kravuar.friends.model.Friendship;
import net.kravuar.friends.model.exceptions.AccountNotFoundException;
import net.kravuar.friends.model.exceptions.FriendshipBlockedException;
import net.kravuar.friends.model.exceptions.FriendshipNotFoundException;
import net.kravuar.friends.model.exceptions.FriendshipRequestAlreadySentException;

public interface FriendshipManagementUseCase {

    /**
     * Sends a friend request from one user to another.
     * If there's a pending reversed friend request - it will be accepted
     * instead of creating a new one.
     *
     * @param fromUserId The ID of the user sending the request.
     * @param toUserId The ID of the user receiving the request.
     * @return The created {@link Friendship} object representing the pending request.
     * @throws FriendshipRequestAlreadySentException If a friendship (or request) between the users already exists.
     * @throws AccountNotFoundException If the account of either user is not found.
     * @throws FriendshipBlockedException If the receiving user blocked the sending user.
     */
    Friendship sendFriendRequest(long fromUserId, long toUserId);

    /**
     * Accepts a friend request between two users.
     *
     * @param fromUserId The ID of the user sending the request.
     * @param toUserId The ID of the user receiving the request.
     * @throws FriendshipNotFoundException If the friendship request between the users is not found.
     * @throws IllegalStateException If the friendship is not in a {@link Friendship.FriendshipStatus#PENDING} status.
     */
    void acceptFriendRequest(long fromUserId, long toUserId);

    /**
     * Declines friend request.
     *
     * @param fromUserId The ID of the user sending the request.
     * @param toUserId The ID of the user receiving the request.
     * @throws IllegalStateException If the friendship is not in a {@link Friendship.FriendshipStatus#PENDING} status.
     */
    void declineFriendRequest(long fromUserId, long toUserId);

    /**
     * Cancels friendship.
     *
     * @param fromUserId The ID of the sender of the friendship request.
     * @param toUserId The ID of the receiver of the friendship request.
     * @throws IllegalStateException If the friendship is not in a {@link Friendship.FriendshipStatus#ACCEPTED} status.
     */
    void cancelFriendship(long fromUserId, long toUserId);

    /**
     * Blocks a user. Will cancel the reversed request, if exists.
     *
     * @param fromUserId The ID of the user initiating the block.
     * @param blockedUserId The ID of the user being blocked.
     * @throws FriendshipNotFoundException If the friendship between the users is not found.
     */
    void blockUser(long fromUserId, long blockedUserId);
}
