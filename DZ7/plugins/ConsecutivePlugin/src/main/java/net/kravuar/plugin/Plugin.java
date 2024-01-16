package net.kravuar.plugin;

import java.util.*;

public class Plugin implements RockPaperScissorsPlugin {
    private final Deque<Option> list = new LinkedList<>(List.of(Option.values()));

    @Override
    public Option act() {
        var next = list.pollFirst();
        list.addLast(next);
        return next;
    }
}