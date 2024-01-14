package net.kravuar.user.services;

import lombok.RequiredArgsConstructor;
import net.kravuar.user.model.Account;
import net.kravuar.user.ports.in.AccountRetrievalUseCase;
import net.kravuar.user.ports.out.AccountRetrievalPort;

import java.util.Optional;

@RequiredArgsConstructor
public class AccountRetrievalService implements AccountRetrievalUseCase {
    private final AccountRetrievalPort retrievalPort;

    @Override
    public Optional<Account> findById(long id) {
        return retrievalPort.findById(id);
    }

    @Override
    public Optional<Account> findByUsername(String username) {
        return retrievalPort.findByUsername(username);
    }
}
