package net.kravuar.friends.model.exceptions;

public class FriendshipRequestAlreadySentException extends FriendshipException {
    public FriendshipRequestAlreadySentException(Long fromUserId, Long toUserId) {
        super(String.format("Friendship request already sent from user %d to user %d.", fromUserId, toUserId));
    }
}