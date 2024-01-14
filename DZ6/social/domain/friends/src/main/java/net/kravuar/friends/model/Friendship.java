package net.kravuar.friends.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class Friendship {
    private final Long userId;
    private final Long friendId;
    @Setter private FriendshipStatus status;

    public enum FriendshipStatus {
        PENDING,
        ACCEPTED,
        BLOCKED
    }
}

