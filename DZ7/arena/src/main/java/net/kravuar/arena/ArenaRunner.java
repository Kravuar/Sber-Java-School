package net.kravuar.arena;

import java.nio.file.Path;
import java.util.stream.IntStream;

public class ArenaRunner implements Runnable {
    private final Arena arena;

    public ArenaRunner(Path pluginDirectoryPath, int roundsPerBattle) {
        this.arena = new Arena(pluginDirectoryPath, roundsPerBattle);
    }

    @Override
    public void run() {
        while (!arena.hasWinner()) {
            var battleScore = arena.proceedBattle();
            IntStream.range(0, battleScore.history().size())
                            .forEach(round -> {
                                var first = battleScore.history().get(round).firstOpponentOption();
                                var second = battleScore.history().get(round).secondOpponentOption();
                                System.out.printf("Round %d: %s - %s.%n", round, first, second);
                            });
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
