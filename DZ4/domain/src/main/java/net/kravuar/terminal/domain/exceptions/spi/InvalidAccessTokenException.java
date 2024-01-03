package net.kravuar.terminal.domain.exceptions.spi;

import lombok.Getter;

/**
 * Exception thrown when the authorization of an operation fails due to an invalid access token.
 */
@Getter
public class InvalidAccessTokenException extends Exception {
    /**
     * The access token associated with the failed authorization attempt.
     */
    private final String accessToken;

    /**
     * Constructs an InvalidAccessTokenException with the specified account ID and access token.
     *
     * @param accessToken The access token associated with the failed authorization attempt.
     */
    public InvalidAccessTokenException(String accessToken) {
        super(String.format("Failed to authenticate operation with access token %s", accessToken));
        this.accessToken = accessToken;
    }
}
