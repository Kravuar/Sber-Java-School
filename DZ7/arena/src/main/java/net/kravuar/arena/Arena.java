package net.kravuar.arena;

import net.kravuar.plugin.RockPaperScissorsPlugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Arena {
    private final int roundsToWinBattle;
    private final Iterator<RockPaperScissorsPlugin> pluginIterator;
    private RockPaperScissorsPlugin firstParticipant;

    public Arena(Iterator<RockPaperScissorsPlugin> pluginIterator, int roundsToWinBattle) {
        if (roundsToWinBattle % 2 != 1)
            throw new IllegalArgumentException("Rounds per battle should be odd and greater than 1.");

        this.pluginIterator = pluginIterator;
        this.roundsToWinBattle = roundsToWinBattle;
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

        List<RoundHistory> history = new ArrayList<>();
        int firstWinCount = 0;
        int secondWinCount = 0;
        while (firstWinCount != roundsToWinBattle && secondWinCount != roundsToWinBattle) {
            var firstParticipantOption = firstParticipant.act();
            var secondParticipantOption = secondParticipant.act();
            history.add(new RoundHistory(firstParticipantOption, secondParticipantOption));

            switch (firstParticipantOption.getOutcome(secondParticipantOption)) {
                case WIN -> firstWinCount++;
                case DEFEAT -> secondWinCount++;
                case TIE -> {/*continue*/}
            }
        }

        var battleScore = new BattleScore(
                firstParticipant.getName(),
                secondParticipant.getName(),
                firstWinCount,
                secondWinCount,
                history
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
            int secondParticipantScore,
            List<RoundHistory> history
    ) {}

    public record RoundHistory(
            RockPaperScissorsPlugin.Option firstOpponentOption,
            RockPaperScissorsPlugin.Option secondOpponentOption
    ) {}
}
