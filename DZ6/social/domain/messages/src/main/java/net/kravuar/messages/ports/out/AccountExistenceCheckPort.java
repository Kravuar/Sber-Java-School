package net.kravuar.messages.ports.out;

public interface AccountExistenceCheckPort {
    boolean exists(long accountId);
}
