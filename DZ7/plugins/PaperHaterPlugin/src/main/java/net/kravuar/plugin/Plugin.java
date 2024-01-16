package net.kravuar.plugin;

import java.util.Random;

public class Plugin implements RockPaperScissorsPlugin {
    private final Random random = new Random();
    private final Option[] withoutPaper = new Option[] {Option.ROCK, Option.SCISSORS};

    @Override
    public Option act() {
        return withoutPaper[random.nextInt(withoutPaper.length)];
    }

    @Override
    public String getName() {
        return "PaperHater";
    }
}