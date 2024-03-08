package net.kravuar.app;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
class HeavyService implements SomeService {

    @Override
    public String[] calculate(int x, int y, int iAmIgnored, Runnable callback) {
        // Callback to track method invocation
        callback.run();

        // ArrayList for some amount of resizes
        List<String> strings = new ArrayList<>(1);

        for (int i = 0; i < x; ++i)
            for (int j = 0; j < y; ++j) {
                for (int k = 0; k < iAmIgnored; ++k)
                    System.out.println("Doing some irrelevant side effects, not affecting result");
                strings.add(String.format("Heavy %d:%d", i, j));
            }

        return strings.toArray(new String[0]);
    }
}
