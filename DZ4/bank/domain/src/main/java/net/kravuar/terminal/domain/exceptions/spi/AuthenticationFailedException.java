package net.kravuar.terminal.domain.exceptions.spi;

import lombok.Getter;

/**
 * Thrown to indicate that authentication failed due to the absence of an account associated with
 * the provided access token.
 */
@Getter
public class AuthenticationFailedException extends RuntimeException {

    private final String accessToken;

    /**
     * Constructs an AuthenticationFailedException with the specified access token.
     *
     * @param accessToken The access token that failed authentication.
     */
    public AuthenticationFailedException(String accessToken) {
        super("Authentication failed for the provided access token.");
        this.accessToken = accessToken;
    }

    /**
     * Constructs an AuthenticationFailedException with the specified access token and a cause.
     *
     * @param accessToken The access token that failed authentication.
     * @param cause       The cause of the authentication failure.
     */
    public AuthenticationFailedException(String accessToken, Throwable cause) {
        this(accessToken);
        this.initCause(cause);
    }
}

