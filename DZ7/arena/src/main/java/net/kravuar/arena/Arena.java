package net.kravuar.arena;

import net.kravuar.plugin.RockPaperScissorsPlugin;

import java.nio.file.Path;

public class Arena {
    private final int roundsPerBattle;
    private final PluginIterator pluginIterator;
    private RockPaperScissorsPlugin firstParticipant;

    public Arena(Path pluginsDirPath, int roundsPerBattle) {
        if (roundsPerBattle % 2 != 1)
            throw new IllegalArgumentException("Rounds per battle should be odd and greater than 1.");

        this.pluginIterator = new PluginIterator(pluginsDirPath);
        this.roundsPerBattle = roundsPerBattle;
        if (!pluginIterator.hasNext())
            throw new IllegalArgumentException("Directory doesn't contain any plugins.");
        this.firstParticipant = pluginIterator.next();
    }

    /**
     * Proceeds rock-paper-scissors battle by loading new participant
     * and making it fight against previous winner (or the first participant if the game just started)
     *
     * @return {@link BattleScore}.
     * @throws IllegalStateException If the was no participants left.
     */
    public BattleScore proceedBattle() {
        if (hasWinner())
            throw new IllegalStateException("No participants left.");

        var secondParticipant = pluginIterator.next();

        int firstWinCount = 0;
        int secondWinCount = 0;
        for (int i = 0; i < roundsPerBattle; ++i) {
            var firstParticipantOption = firstParticipant.act();
            var secondParticipantOption = secondParticipant.act();

            switch (RockPaperScissorsPlugin.Option.Outcome.getOutcome(firstParticipantOption, secondParticipantOption)) {
                case WIN -> firstWinCount++;
                case DEFEAT -> secondWinCount++;
                case TIE -> --i; /*continue*/
            }
        }

        var battleScore = new BattleScore(
                firstParticipant.getName(),
                secondParticipant.getName(),
                firstWinCount,
                secondWinCount
        );

        if (firstWinCount < secondWinCount)
            firstParticipant = secondParticipant;

        return battleScore;
    }

    public boolean hasWinner() {
        return !pluginIterator.hasNext();
    }

    /**
     * Retrieves winner name.
     *
     * @return {@code String} Winner name.
     * @throws IllegalStateException If the arena hasn't finished yet.
     */
    public String getWinnerName() {
        if (!hasWinner())
            throw new IllegalStateException("The arena hasn't finished yet.");
        return firstParticipant.getName();
    }

    public record BattleScore(
            String firstParticipantName,
            String secondParticipantName,
            int firstParticipantScore,
            int secondParticipantScore
    ) {}
}
