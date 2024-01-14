package net.kravuar.user.services;

import lombok.RequiredArgsConstructor;
import net.kravuar.user.model.Account;
import net.kravuar.user.model.AccountAuthenticationRequest;
import net.kravuar.user.model.exceptions.AccountNotFoundException;
import net.kravuar.user.model.exceptions.AuthenticationException;
import net.kravuar.user.ports.in.AccountAuthenticationUseCase;
import net.kravuar.user.ports.out.AccountRetrievalPort;
import net.kravuar.user.ports.out.PasswordEncoderPort;

@RequiredArgsConstructor
public class AccountAuthenticationService implements AccountAuthenticationUseCase {
    private final PasswordEncoderPort passwordEncoderPort;
    private final AccountRetrievalPort retrievalPort;

    @Override
    public Account authenticate(AccountAuthenticationRequest request) {
        Account account = retrievalPort.findByUsername(request.username())
                .orElseThrow(() -> new AccountNotFoundException(request.username()));

        if (!passwordEncoderPort.encode(request.password()).equals(account.getPasswordEncoded()))
            throw new AuthenticationException(request);

        return account;
    }
}
