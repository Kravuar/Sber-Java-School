package net.kravuar;

import net.kravuar.terminal.domain.card.CardDetails;
import net.kravuar.terminal.domain.exceptions.spi.InvalidAccessTokenException;

/**
 * Mapper based on ID only.
 */
public class IdBasedCardDetailsToAccessTokenMapperImpl implements CardDetailsToAccessTokenMapper {
    @Override
    public String toToken(CardDetails cardDetails) {
        return String.valueOf(cardDetails.id());
    }

    @Override
    public CardDetails toDetails(String token) throws InvalidAccessTokenException {
        try {
            var id = Long.parseLong(token);
            return new CardDetails(id);
        } catch (NumberFormatException e) {
            var wrapped = new InvalidAccessTokenException(token);
            wrapped.initCause(e);
            throw wrapped;
        }
    }
}
