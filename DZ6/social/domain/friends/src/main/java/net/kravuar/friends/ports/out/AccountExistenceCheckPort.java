package net.kravuar.friends.ports.out;

public interface AccountExistenceCheckPort {
    boolean exists(long accountId);
}
