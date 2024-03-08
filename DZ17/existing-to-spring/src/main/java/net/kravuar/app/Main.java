package net.kravuar.app;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        final ApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class);

        // Cached service
        SomeService service = ctx.getBean(SomeService.class);

        // Cache Miss
        service.calculate(2, 2, 1, () -> System.out.println("Invoked"));

        // Cache Hit
        service.calculate(2, 2, 2, () -> System.out.println("Wont be invoked"));

        // Cache Miss
        service.calculate(2, 1, 4, () -> System.out.println("Invoked"));
    }
}