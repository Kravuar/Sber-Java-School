package net.kravuar.friends.ports.out;

import net.kravuar.friends.model.Friendship;

public interface FriendshipPersistencePort {
    Friendship save(Friendship friendship);
    void delete(Friendship friendship);
}
