package net.kravuar.plugin;

import java.util.Random;

public class Plugin implements RockPaperScissorsPlugin {
    private final Random random = new Random();
    private final Option[] options = Option.values();

    @Override
    public Option act() {
        return options[random.nextInt(options.length)];
    }

    @Override
    public String getName() {
        return "Random";
    }
}
