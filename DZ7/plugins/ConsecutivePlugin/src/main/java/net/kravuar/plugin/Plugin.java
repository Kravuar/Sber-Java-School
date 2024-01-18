package net.kravuar.plugin;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class Plugin implements RockPaperScissorsPlugin {
    private final Deque<Option> list = new LinkedList<>(List.of(Option.values()));

    @Override
    public Option act() {
        var next = list.pollFirst();
        list.addLast(next);
        return next;
    }

    @Override
    public String getName() {
        return "Consecutive";
    }
}