package net.kravuar.friends.ports.out;

import net.kravuar.friends.model.Friendship;

public interface FriendshipManagementPort {
    Friendship save(Friendship friendship);
    void delete(Friendship friendship);
}
