package net.kravuar.plugin;

import java.util.Map;
import java.util.stream.Stream;

public interface RockPaperScissorsPlugin {
    Option act();
    String getName();

    enum Option {
        ROCK,
        PAPER,
        SCISSORS;

        public Outcome getOutcome(Option opponentOption) {
            return Outcome.getOutcome(this, opponentOption);
        }

        public enum Outcome {
            DEFEAT(Map.of(
                    PAPER, SCISSORS,
                    SCISSORS, ROCK,
                    ROCK, PAPER
            )),
            WIN(Map.of(
                    SCISSORS, PAPER,
                    ROCK, SCISSORS,
                    PAPER, ROCK
            )),

            TIE(Map.of(
                    SCISSORS, SCISSORS,
                    ROCK, ROCK,
                    PAPER, PAPER
            ));

            private final Map<Option, Option> combinations;

            Outcome(Map<Option, Option> combinations) {
                this.combinations = combinations;
            }

            public static Outcome getOutcome(Option firstParticipantOption, Option secondParticipantOption) {
                return Stream.of(values())
                        .filter(outcome -> outcome.isOutcome(firstParticipantOption, secondParticipantOption))
                        .findFirst().orElseThrow();
            }

            private boolean isOutcome(Option firstParticipantOption, Option secondParticipantOption) {
                return combinations.get(firstParticipantOption).equals(secondParticipantOption);
            }
        }
    }
}
