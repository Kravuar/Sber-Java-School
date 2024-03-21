package net.kravuar.json;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/json")
@RequiredArgsConstructor
@CrossOrigin("*")
class JSONController {
    private final AccountRepository repository;

    @PostMapping(value = "/stream-ndjson/{delay}", produces = MediaType.APPLICATION_NDJSON_VALUE)
    Flux<Object> stream(@PathVariable("delay") long delayInMs, @RequestBody List<Object> ndjson) {
        return Flux.fromIterable(ndjson)
                .delayElements(Duration.ofMillis(delayInMs));
    }

    @GetMapping(value = "/stream-ndjson/users/{delay}", produces = MediaType.APPLICATION_NDJSON_VALUE)
    Flux<Account> stream(@PathVariable("delay") long delayInMs) {
        return repository.findAll()
                .delayElements(Duration.ofMillis(delayInMs));
    }

    @GetMapping(value = "/stream-sse/users/{delay}")
    Flux<ServerSentEvent<Account>> streamSSE(@PathVariable("delay") long delayInMs) {
        return repository.findAll()
                .map(account -> {
                            System.out.println(account);
                            var sse = ServerSentEvent.<Account>builder()
                                    .event("stream-sse-event")
                                    .data(account)
                                    .build();
                            System.out.println(sse);
                            return sse;
                        }
                )
                .delayElements(Duration.ofMillis(delayInMs));
    }
}

