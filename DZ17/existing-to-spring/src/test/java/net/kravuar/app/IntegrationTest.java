package net.kravuar.app;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {Config.class})
class IntegrationTest {
    @Autowired
    ApplicationContext applicationContext;

    @Test
    void springConfigurationWorks() {
        // given
        AtomicInteger invocationCounter = new AtomicInteger();
        Runnable incrementCallback = invocationCounter::incrementAndGet;
        SomeService service = applicationContext.getBean(SomeService.class);

        // when
        // Cache Miss
        service.calculate(2, 2, 1, incrementCallback);
        // Cache Hit
        service.calculate(2, 2, 2, incrementCallback);
        // Cache Miss
        service.calculate(2, 1, 4, incrementCallback);

        // then
        assertEquals(2, invocationCounter.get());
    }
}
