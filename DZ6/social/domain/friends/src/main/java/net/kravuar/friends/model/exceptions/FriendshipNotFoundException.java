package net.kravuar.friends.model.exceptions;

public class FriendshipNotFoundException extends FriendshipException {
    public FriendshipNotFoundException(long firstUserId, long secondUserId) {
        super(String.format("Friendship between user %d and user %d not found.", firstUserId, secondUserId));
    }
}