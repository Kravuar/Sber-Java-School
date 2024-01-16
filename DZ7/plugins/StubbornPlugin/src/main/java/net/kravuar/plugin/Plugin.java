package net.kravuar.plugin;

public class Plugin implements RockPaperScissorsPlugin {

    @Override
    public Option act() {
        return Option.PAPER;
    }

    @Override
    public String getName() {
        return "Stubborn";
    }
}
