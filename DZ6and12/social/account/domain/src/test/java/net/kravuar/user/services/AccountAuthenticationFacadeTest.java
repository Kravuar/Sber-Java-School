package net.kravuar.user.services;

import net.kravuar.user.AccountAuthenticationFacade;
import net.kravuar.user.model.Account;
import net.kravuar.user.model.AccountAuthenticationRequest;
import net.kravuar.user.model.AccountRegistrationRequest;
import net.kravuar.user.model.exceptions.AccountExistsException;
import net.kravuar.user.model.exceptions.AccountNotFoundException;
import net.kravuar.user.model.exceptions.IncorrectPasswordException;
import net.kravuar.user.ports.out.AccountPersistencePort;
import net.kravuar.user.ports.out.AccountRetrievalPort;
import net.kravuar.user.ports.out.PasswordEncoderPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountAuthenticationFacadeTest {

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @Mock
    private AccountRetrievalPort retrievalPort;

    @Mock
    private AccountPersistencePort persistencePort;

    @InjectMocks
    private AccountAuthenticationFacade authenticationService;

    @Test
    void authenticate_ValidCredentials_ReturnsAuthenticatedAccount() {
        // given
        String username = "testUser";
        String password = "password";

        var mockAccount = mock(Account.class);

        when(retrievalPort.findByUsername(username)).thenReturn(Optional.of(mockAccount));
        when(passwordEncoderPort.encode(password)).thenReturn("encodedPassword");
        when(mockAccount.getPasswordEncoded()).thenReturn("encodedPassword");

        var request = new AccountAuthenticationRequest(
                username,
                password
        );

        // when
        var authenticatedAccount = authenticationService.authenticate(request);

        // then
        assertEquals(mockAccount, authenticatedAccount);
        verify(retrievalPort, times(1)).findByUsername(username);
    }

    @Test
    void authenticate_AccountNotFound_ThrowsAccountNotFoundException() {
        // given
        String username = "nonExistentUser";

        when(retrievalPort.findByUsername(username)).thenReturn(Optional.empty());

        var request = new AccountAuthenticationRequest(
                username,
                "anyPassword"
        );

        // when & then
        assertThrows(AccountNotFoundException.class, () -> authenticationService.authenticate(request));
        verify(retrievalPort, times(1)).findByUsername(username);
    }

    @Test
    void authenticate_IncorrectPassword_ThrowsIncorrectPasswordException() {
        // given
        String username = "testUser";
        String password = "differentPassword";

        var mockAccount = mock(Account.class);

        when(retrievalPort.findByUsername(username)).thenReturn(Optional.of(mockAccount));
        when(passwordEncoderPort.encode(password)).thenReturn("differentEncodedPassword");
        when(mockAccount.getPasswordEncoded()).thenReturn("encodedPassword");

        var request = new AccountAuthenticationRequest(
                username,
                password
        );

        // when & Assert
        assertThrows(IncorrectPasswordException.class, () -> authenticationService.authenticate(request));
        verify(retrievalPort, times(1)).findByUsername(username);
    }

    @Test
    void register_NewAccount_SuccessfullyRegistersAndReturnsAccount() {
        // given
        String firstName = "bebebe";
        String secondName = "bababa";
        String username = "user";
        String password = "password";

        var registeredAccount = Account.withoutId(
                firstName,
                secondName,
                username,
                "omitted"
        );
        when(retrievalPort.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoderPort.encode(password)).thenReturn("encodedPassword");
        when(persistencePort.save(any(Account.class))).thenReturn(registeredAccount);

        var request = new AccountRegistrationRequest(
                firstName,
                secondName,
                username,
                password
        );

        // when
        Account result = authenticationService.register(request);

        // then
        assertNotNull(result);
        assertEquals(request.firstName(), result.getFirstName());
        assertEquals(request.secondName(), result.getSecondName());
        assertEquals(request.username(), result.getUsername());
        verify(retrievalPort, times(1)).findByUsername(username);
        verify(persistencePort, times(1)).save(any(Account.class));
    }

    @Test
    void register_AccountAlreadyExists_ThrowsAccountExistsException() {
        // given
        String existingUsername = "existingUser";
        var request = new AccountRegistrationRequest(
                "Bebebe",
                "Bababa",
                existingUsername,
                "password"
        );

        var existingAccount = mock(Account.class);
        when(retrievalPort.findByUsername(existingUsername)).thenReturn(Optional.of(existingAccount));

        // when & then
        assertThrows(AccountExistsException.class, () -> authenticationService.register(request));
        verify(retrievalPort, times(1)).findByUsername(existingUsername);
        verifyNoInteractions(persistencePort);
    }
}
