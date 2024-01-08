package net.kravuar;

import net.kravuar.exceptions.AccountNotFoundException;
import net.kravuar.terminal.domain.card.CardDetails;
import net.kravuar.exceptions.InvalidAccessTokenException;

public interface AccountService {

    /**
     * Checks if an account exists based on the provided card details.
     *
     * @param cardDetails The card details to check for account existence.
     * @return {@code true} if the account exists, {@code false} otherwise.
     */
    boolean exists(CardDetails cardDetails);

    /**
     * Retrieves account information along with the balance using the provided access token.
     *
     * @param accessToken The access token used to fetch account information.
     * @return An instance of {@code AccountInfoWithBalance} containing account details and balance.
     * @throws InvalidAccessTokenException If the provided access token is invalid.
     */
    AccountInfoWithBalance getAccountInfo(String accessToken) throws InvalidAccessTokenException;

    /**
     * Converts {@code CardDetails} instance to string representation.
     *
     * @param cardDetails card details which will be converted to access token.
     * @return {@code String} representing card details
     * @throws AccountNotFoundException If the account associated with the card details is not found.
     */
    String toToken(CardDetails cardDetails);

    /**
     * Parses access token and constructs {@code CardDetails} from it.
     *
     * @param accessToken string representation of an access token.
     * @return {@code CardDetails} parsed from provided token
     * @throws InvalidAccessTokenException If the provided access token is invalid.
     * @throws AccountNotFoundException If the account associated with the card details is not found.
     */
    CardDetails toDetails(String accessToken) throws InvalidAccessTokenException;
}
