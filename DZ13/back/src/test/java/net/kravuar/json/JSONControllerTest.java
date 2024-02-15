package net.kravuar.json;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = JSONController.class)
class JSONControllerTest {
    @Autowired
    private WebTestClient client;

    @MockBean
    private AccountRepository repository;


    @Test
    void givenJsonArray_whenStream_ThenStreamOfNdjson() {
        // given
        List<Map<String, Object>> ndjson = new ArrayList<>(2);
        ndjson.add(Map.of());
        ndjson.add(Map.of());

        // when & then
        client.post()
                .uri("/json/stream-ndjson/0")
                .contentType(MediaType.APPLICATION_NDJSON)
                .bodyValue(ndjson)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
                .expectBodyList(Map.class)
                .hasSize(2);
    }

    @Test
    void givenUsersInRepo_WhenStreamUsersAsNdjson_ThenStreamUsersAsNdjson() {
        // given
        List<Account> accounts = List.of(
                mock(Account.class),
                mock(Account.class)
        );
        when(repository.findAll()).thenReturn(Flux.fromIterable(accounts));

        // when & then
        client.get()
                .uri("/json/stream-ndjson/users/0")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
                .expectBodyList(Account.class)
                .hasSize(accounts.size());
    }

    // Test for streaming SSE users with delay
    @Test
    void givenUsersInRepo_WhenStreamUsersAsSse_ThenStreamUsersAsSse() {
        // given
        List<Account> accounts = List.of(
                mock(Account.class),
                mock(Account.class)
        );
        when(repository.findAll()).thenReturn(Flux.fromIterable(accounts));

        // when & then
        client.get()
                .uri("/json/stream-sse/users/0")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
                .expectBodyList(Account.class)
                .hasSize(2);
    }
}