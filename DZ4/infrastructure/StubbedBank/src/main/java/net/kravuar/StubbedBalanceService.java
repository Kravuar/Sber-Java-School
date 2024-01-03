package net.kravuar;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kravuar.terminal.domain.card.CardDetails;
import net.kravuar.terminal.domain.exceptions.spi.InsufficientFundsException;
import net.kravuar.terminal.domain.exceptions.spi.InvalidAccessTokenException;
import net.kravuar.terminal.spi.BalanceService;

import java.util.Map;

@RequiredArgsConstructor
public class StubbedBalanceService implements BalanceService {
    /**
     * The underlying {@code Map} associates {@code CardDetails} with account balances.
     */
    @Getter
    private final Map<CardDetails, Double> accounts;
    private final CardDetailsToAccessTokenMapper accessTokenMapper;

    @Override
    public double getBalance(String accessToken) throws InvalidAccessTokenException {
        var cardDetails = accessTokenMapper.toDetails(accessToken);
        return accounts.get(cardDetails);
    }

    @Override
    public double deposit(String accessToken, double amount) throws InvalidAccessTokenException {
        var cardDetails = accessTokenMapper.toDetails(accessToken);
        return accounts.merge(cardDetails, amount, Double::sum);
    }

    @Override
    public double withdraw(String accessToken, double amount) throws InvalidAccessTokenException, InsufficientFundsException {
        var cardDetails = accessTokenMapper.toDetails(accessToken);
        var currentAmount = accounts.get(cardDetails);
        if (currentAmount < amount)
            throw new InsufficientFundsException(amount - currentAmount);
        return accounts.merge(cardDetails, -amount, Double::sum);
    }
}
