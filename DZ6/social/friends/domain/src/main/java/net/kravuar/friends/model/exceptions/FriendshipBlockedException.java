package net.kravuar.friends.model.exceptions;

public class FriendshipBlockedException extends FriendshipException {
    public FriendshipBlockedException(long fromUserId, long toUserId) {
        super(String.format("Friendship blocked from user %d to user %d.", fromUserId, toUserId));
    }
}