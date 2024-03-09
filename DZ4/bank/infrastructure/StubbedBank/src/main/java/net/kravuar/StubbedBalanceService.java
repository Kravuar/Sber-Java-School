package net.kravuar;

import lombok.RequiredArgsConstructor;
import net.kravuar.exceptions.InvalidAccessTokenException;
import net.kravuar.terminal.domain.exceptions.spi.AuthenticationFailedException;
import net.kravuar.terminal.domain.exceptions.spi.InsufficientFundsException;
import net.kravuar.terminal.spi.BalanceService;

@RequiredArgsConstructor
public class StubbedBalanceService implements BalanceService {
    private final AccountService accountService;

    @Override
    public double getBalance(String accessToken) throws AuthenticationFailedException {
        try {
            var accountInfo = accountService.getAccountInfo(accessToken);
            return accountInfo.getBalance();
        } catch (InvalidAccessTokenException e) {
            throw new AuthenticationFailedException(accessToken, e);
        }
    }

    @Override
    public double deposit(String accessToken, double amount) throws AuthenticationFailedException {
        try {
            var accountInfo = accountService.getAccountInfo(accessToken);
            accountInfo.setBalance(accountInfo.getBalance() + amount);
            return accountInfo.getBalance();
        } catch (InvalidAccessTokenException e) {
            throw new AuthenticationFailedException(accessToken, e);
        }
    }

    @Override
    public double withdraw(String accessToken, double amount) throws AuthenticationFailedException, InsufficientFundsException {
        try {
            var accountInfo = accountService.getAccountInfo(accessToken);
            if (accountInfo.getBalance() < amount)
                throw new InsufficientFundsException(amount - accountInfo.getBalance());
            accountInfo.setBalance(accountInfo.getBalance() - amount);
            return accountInfo.getBalance();
        } catch (InvalidAccessTokenException e) {
            throw new RuntimeException(e);
        }
    }
}
