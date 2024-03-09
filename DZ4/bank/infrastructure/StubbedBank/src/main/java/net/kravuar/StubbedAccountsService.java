package net.kravuar;

import lombok.RequiredArgsConstructor;
import net.kravuar.exceptions.AccountNotFoundException;
import net.kravuar.exceptions.InvalidAccessTokenException;
import net.kravuar.terminal.domain.card.CardDetails;

import java.util.Map;

@RequiredArgsConstructor
public class StubbedAccountsService implements AccountService {
    private final Map<CardDetails, AccountInfoWithBalance> accounts;

    // For demonstration purposes
    public Map<CardDetails, AccountInfoWithBalance> getDB() {
        return accounts;
    }

    @Override
    public boolean exists(CardDetails cardDetails) {
        return accounts.containsKey(cardDetails);
    }

    @Override
    public AccountInfoWithBalance getAccountInfo(String accessToken) throws InvalidAccessTokenException {
        var cardDetails = toDetails(accessToken);
        return accounts.get(cardDetails);
    }

    @Override
    public String toToken(CardDetails cardDetails) {
        if (!exists(cardDetails))
            throw new AccountNotFoundException(cardDetails);
        return String.valueOf(cardDetails.id());
    }

    @Override
    public CardDetails toDetails(String accessToken) throws InvalidAccessTokenException {
        try {
            var id = Long.parseLong(accessToken);
            var cardDetails = new CardDetails(id);
            if (!exists(cardDetails))
                throw new AccountNotFoundException(cardDetails);
            return cardDetails;
        } catch (NumberFormatException e) {
            throw new InvalidAccessTokenException(accessToken, e);
        }
    }
}
