package net.kravuar.arena;

import net.kravuar.plugin.RockPaperScissorsPlugin;

import java.nio.file.Path;
import java.util.Optional;

public class Arena {
    private final int roundsPerBattle;
    private final PluginIterator pluginIterator;
    private RockPaperScissorsPlugin firstOpponent;

    public Arena(Path pluginsDirPath, int roundsPerBattle) {
        if (roundsPerBattle % 2 != 1)
            throw new IllegalArgumentException("Rounds per battle should be odd and greater than 1.");

        this.pluginIterator = new PluginIterator(pluginsDirPath);
        this.roundsPerBattle = roundsPerBattle;
        if (!pluginIterator.hasNext())
            throw new IllegalArgumentException("Directory doesn't contain any plugins.");
        this.firstOpponent = pluginIterator.next();
    }

    /**
     * Proceeds rock-paper-scissors battle by loading new opponent
     * and making it fight against previous winner (or the first opponent if the game just started)
     *
     * @return {@code Optional} containing Arena winner or {@code Optional.empty()} if the Arena haven't finished yet.
     */
    public Optional<RockPaperScissorsPlugin> proceedBattle() {
        if (!pluginIterator.hasNext())
            return Optional.of(firstOpponent);

        var secondOpponent = pluginIterator.next();

        int firstWinCount = 0;
        int secondWinCount = 0;
        for (int i = 0; i < roundsPerBattle; ++i) {
            var firstOpponentOption = firstOpponent.act();
            var secondOpponentOption = secondOpponent.act();

            switch (RockPaperScissorsPlugin.Option.Outcome.getOutcome(firstOpponentOption, secondOpponentOption)) {
                case WIN -> firstWinCount++;
                case DEFEAT -> secondWinCount++;
                case TIE -> {/*continue*/}
            }
        }
        if (firstWinCount < secondWinCount)
            firstOpponent = secondOpponent;
        return Optional.empty();
    }
}
