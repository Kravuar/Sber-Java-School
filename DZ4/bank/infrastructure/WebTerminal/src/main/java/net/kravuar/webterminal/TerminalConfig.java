package net.kravuar.webterminal;

import net.kravuar.*;
import net.kravuar.terminal.api.Terminal;
import net.kravuar.terminal.api.TerminalImpl;
import net.kravuar.terminal.domain.card.CardDetails;
import net.kravuar.terminal.spi.BalanceService;
import net.kravuar.terminal.spi.PinValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.HashMap;

@Configuration
class TerminalConfig {
    @Bean
    AccountService accountService() {
        var db = new HashMap<CardDetails, AccountInfoWithBalance>();
        db.put(new CardDetails(1L), new AccountInfoWithBalance(1L, 99.9));
        db.put(new CardDetails(2L), new AccountInfoWithBalance(2L, 43509d));
        db.put(new CardDetails(3L), new AccountInfoWithBalance(3L, 1d));
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
                Duration.ofSeconds(30),
                Duration.ofSeconds(30),
                Duration.ofSeconds(60)
        );
    }
}
