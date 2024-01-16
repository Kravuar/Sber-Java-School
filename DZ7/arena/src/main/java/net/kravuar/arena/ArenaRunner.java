package net.kravuar.arena;

import java.nio.file.Path;

public class ArenaRunner implements Runnable{
    private final Arena arena;

    public ArenaRunner(Path pluginDirectoryPath, int roundsPerBattle) {
        this.arena = new Arena(pluginDirectoryPath, roundsPerBattle);
    }

    @Override
    public void run() {
        // TODO: battle unless winner is resolved.
    }
}
