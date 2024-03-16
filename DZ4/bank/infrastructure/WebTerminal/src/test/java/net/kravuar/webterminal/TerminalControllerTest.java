package net.kravuar.webterminal;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.kravuar.*;
import net.kravuar.terminal.api.Terminal;
import net.kravuar.terminal.api.TerminalImpl;
import net.kravuar.terminal.domain.card.CardDetails;
import net.kravuar.terminal.domain.exceptions.spi.InvalidCardDetailsException;
import net.kravuar.terminal.domain.exceptions.terminal.InvalidPinFormatException;
import net.kravuar.terminal.spi.BalanceService;
import net.kravuar.terminal.spi.PinValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
class TerminalControllerTest {
    @TestConfiguration
    static class TerminalTestConfig {
        @Bean
        AccountInfoWithBalance testAccount() {
            return new AccountInfoWithBalance(1L, 999.9);
        }

        @Bean
        AccountService accountService(AccountInfoWithBalance testAccount) {
            var db = new HashMap<CardDetails, AccountInfoWithBalance>();
            db.put(new CardDetails(testAccount.getId()), testAccount);
            return new StubbedAccountsService(db);
        }

        @Bean
        BalanceService balanceService(AccountService accountService) {
            return new StubbedBalanceService(accountService);
        }

        @Bean
        PinValidator pinValidator(AccountService accountService) {
            return new StubbedPinValidator(accountService);
        }

        @Bean
        Terminal terminal(BalanceService balanceService, PinValidator pinValidator) {
            return new TerminalImpl(
                    balanceService,
                    pinValidator,
                    3,
                    Duration.ofSeconds(10),
                    Duration.ofSeconds(5),
                    Duration.ofSeconds(20)
            );
        }
    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AccountInfoWithBalance testAccount;
    @Autowired
    private Terminal terminal;

    private void setTestAccountBalance(double balance) {
        testAccount.setBalance(balance);
    }

    private void startSessionWithValidCardAndPin() throws InvalidCardDetailsException, InvalidPinFormatException {
        terminal.startSession(
                new CardDetails(testAccount.getId()),
                new char[]{'0', '0', '0', StubbedPinValidator.SPECIAL_DIGIT}
        );
    }

    @AfterEach
    void clean() {
        setTestAccountBalance(0);
        terminal.endSession();
    }

    @Test
    public void givenActiveSession_WhenGetBalance_ThenReturnBalance() throws Exception {
        // Given
        setTestAccountBalance(1000.0);
        startSessionWithValidCardAndPin();

        // When & Then
        mockMvc.perform(get("/terminal/balance"))
                .andExpect(status().isOk())
                .andExpect(content().string("1000.0"));
    }

    @Test
    public void givenNoActiveSession_WhenGetBalance_ThenReturnUnauthorized() throws Exception {
        // Given (no active session)

        // When & Then
        mockMvc.perform(get("/terminal/balance"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenValidAmount_WhenDeposit_ThenReturnNewBalance() throws Exception {
        // Given
        setTestAccountBalance(1000.0);
        startSessionWithValidCardAndPin();
        double depositAmount = 500.0;

        // When & Then
        mockMvc.perform(post("/terminal/deposit/{amount}", depositAmount))
                .andExpect(status().isOk())
                .andExpect(content().string("1500.0"));
    }

    @Test
    public void givenInvalidAmount_WhenDeposit_ThenReturnBadRequest() throws Exception {
        // Given
        startSessionWithValidCardAndPin();
        double invalidAmount = 501.0; // not divisible by 100

        // When & Then
        mockMvc.perform(post("/terminal/deposit/{amount}", invalidAmount))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenSufficientBalance_WhenWithdraw_ThenReturnNewBalance() throws Exception {
        // Given
        setTestAccountBalance(1000.0);
        startSessionWithValidCardAndPin();
        double withdrawalAmount = 500.0;

        // When & Then
        mockMvc.perform(post("/terminal/withdraw/{amount}", withdrawalAmount))
                .andExpect(status().isOk())
                .andExpect(content().string("500.0"));
    }

    @Test
    public void givenInsufficientBalance_WhenWithdraw_ThenReturnBadRequest() throws Exception {
        // Given
        setTestAccountBalance(500.0);
        startSessionWithValidCardAndPin();
        double withdrawalAmount = 600.0;

        // When & Then
        mockMvc.perform(post("/terminal/withdraw/{amount}", withdrawalAmount))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenValidCardAndPin_WhenStartSession_ThenReturnTrue() throws Exception {
        // Given
        CardDetails validCardDetails = new CardDetails(testAccount.getId());
        char[] validPin = new char[] {'1', '2', '3', StubbedPinValidator.SPECIAL_DIGIT};

        // When & Then
        mockMvc.perform(post("/terminal/start-session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new TerminalController.StartSessionRequest(validCardDetails, validPin))))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void givenInvalidCard_WhenStartSession_ThenReturnBadRequest() throws Exception {
        // Given
        CardDetails invalidCardDetails = new CardDetails(-999L);
        char[] validPin = new char[] {'1', '2', '3', StubbedPinValidator.SPECIAL_DIGIT};

        // When & Then
        mockMvc.perform(post("/terminal/start-session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new TerminalController.StartSessionRequest(invalidCardDetails, validPin))))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenActiveSession_WhenEndSession_ThenReturnTrue() throws Exception {
        // Given
        startSessionWithValidCardAndPin();

        // When & Then
        mockMvc.perform(post("/terminal/end-session"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void givenNoActiveSession_WhenEndSession_ThenReturnFalse() throws Exception {
        // Given (no active session)

        // When & Then
        mockMvc.perform(post("/terminal/end-session"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    public void givenActiveSession_WhenHasActiveSession_ThenReturnTrue() throws Exception {
        // Given
        startSessionWithValidCardAndPin();

        // When & Then
        mockMvc.perform(get("/terminal/active-session"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void givenNoActiveSession_WhenHasActiveSession_ThenReturnFalse() throws Exception {
        // Given (no active session)

        // When & Then
        mockMvc.perform(get("/terminal/active-session"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}