package net.kravuar.user.services;

import lombok.RequiredArgsConstructor;
import net.kravuar.user.model.Account;
import net.kravuar.user.model.AccountCreationRequest;
import net.kravuar.user.ports.in.AccountPersistenceUseCase;
import net.kravuar.user.ports.out.AccountPersistencePort;
import net.kravuar.user.ports.out.PasswordEncoderPort;

@RequiredArgsConstructor
public class AccountPersistenceService implements AccountPersistenceUseCase {
    private final AccountPersistencePort persistencePort;
    private final PasswordEncoderPort passwordEncoderPort;


    @Override
    public Account create(AccountCreationRequest request) {
        Account newAccount = Account.withoutId(
                request.firstName(),
                request.secondName(),
                request.username(),
                passwordEncoderPort.encode(request.password())
                );
        return persistencePort.save(newAccount);
    }

    @Override
    public void delete(long userId) {
        persistencePort.delete(userId);
    }

    @Override
    public void update(Account account) {
        if (account.getId() == null)
            throw new IllegalArgumentException("Account should have an id to be updated");
        persistencePort.save(account);
    }
}
