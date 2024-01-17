package net.kravuar.arena;

import java.nio.file.Path;

public class ArenaRunner implements Runnable {
    private final Arena arena;

    public ArenaRunner(Path pluginDirectoryPath, int roundsPerBattle) {
        this.arena = new Arena(pluginDirectoryPath, roundsPerBattle);
    }

    @Override
    public void run() {
        while (!arena.hasWinner()) {
            var battleScore = arena.proceedBattle();
            System.out.printf("Battle Score: {%s-%d; %s-%d}.%n",
                    battleScore.firstParticipantName(),
                    battleScore.firstParticipantScore(),
                    battleScore.secondParticipantName(),
                    battleScore.secondParticipantScore()
            );
        }
        System.out.printf("Winner: %s%n", arena.getWinnerName());
    }
}
