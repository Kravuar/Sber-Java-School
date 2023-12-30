package net.kravuar;

import net.kravuar.terminal.domain.card.CardDetails;

/**
 * Bidirectional mapping from {@code CardDetails} to {@code String} representation.
 * Same mapper should be used for {@code BalanceService} and {@code PinValidator} instances.
 */
public interface CardDetailsToAccessTokenMapper {
    /**
     * Converts {@code CardDetails} instance to string representation.
     *
     * @param cardDetails card details which will be converted to access token.
     * @return {@code String} representing card details
     */
    String toToken(CardDetails cardDetails);

    /**
     * Parses access token and constructs {@code CardDetails} from it.
     *
     * @param token string representation of an access token.
     * @throws net.kravuar.terminal.domain.exceptions.spi.InvalidAccessTokenException if token is invalid.
     * @return {@code CardDetails} parsed from provided token
     */
    CardDetails toDetails(String token);
}
