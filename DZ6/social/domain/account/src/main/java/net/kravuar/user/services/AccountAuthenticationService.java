package net.kravuar.user.services;

import lombok.RequiredArgsConstructor;
import net.kravuar.user.model.Account;
import net.kravuar.user.model.AccountAuthenticationRequest;
import net.kravuar.user.model.AccountRegistrationRequest;
import net.kravuar.user.model.exceptions.AccountExistsException;
import net.kravuar.user.model.exceptions.AccountNotFoundException;
import net.kravuar.user.model.exceptions.AuthenticationException;
import net.kravuar.user.ports.in.AccountAuthenticationUseCase;
import net.kravuar.user.ports.out.AccountPersistencePort;
import net.kravuar.user.ports.out.AccountRetrievalPort;
import net.kravuar.user.ports.out.PasswordEncoderPort;

@RequiredArgsConstructor
public class AccountAuthenticationService implements AccountAuthenticationUseCase {
    private final PasswordEncoderPort passwordEncoderPort;
    private final AccountRetrievalPort retrievalPort;
    private final AccountPersistencePort persistencePort;

    @Override
    public Account authenticate(AccountAuthenticationRequest request) {
        Account account = retrievalPort.findByUsername(request.username())
                .orElseThrow(() -> new AccountNotFoundException(request.username()));

        if (!passwordEncoderPort.encode(request.password()).equals(account.getPasswordEncoded()))
            throw new AuthenticationException(request);

        return account;
    }

    @Override
    public Account register(AccountRegistrationRequest request) {
        if (retrievalPort.findByUsername(request.username()).isPresent())
            throw new AccountExistsException(request);

        Account newAccount = Account.withoutId(
                request.firstName(),
                request.secondName(),
                request.username(),
                passwordEncoderPort.encode(request.password())
        );
        return persistencePort.save(newAccount);
    }
}
