package net.kravuar;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kravuar.terminal.domain.exceptions.spi.InsufficientFundsException;
import net.kravuar.terminal.domain.exceptions.spi.InvalidAccessTokenException;
import net.kravuar.terminal.spi.BalanceService;

import java.util.Map;

@RequiredArgsConstructor
public class StubbedBalanceService implements BalanceService {
    /**
     * The underlying {@code Map} associates account IDs (long as in the demo CardDetails from domain) with account balances.
     */
    @Getter
    private final Map<Long, Double> accounts;
    private final CardDetailsToAccessTokenMapper accessTokenMapper;

    @Override
    public double getBalance(String accessToken) throws InvalidAccessTokenException {
        var id = accessTokenMapper.toDetails(accessToken).id();
        return accounts.get(id);
    }

    @Override
    public double deposit(String accessToken, double amount) throws InvalidAccessTokenException {
        var id = accessTokenMapper.toDetails(accessToken).id();
        return accounts.merge(id, amount, Double::sum);
    }

    @Override
    public double withdraw(String accessToken, double amount) throws InvalidAccessTokenException, InsufficientFundsException {
        var id = accessTokenMapper.toDetails(accessToken).id();
        var currentAmount = accounts.get(id);
        if (currentAmount < amount)
            throw new InsufficientFundsException(amount);
        return accounts.merge(id, -amount, Double::sum);
    }
}
