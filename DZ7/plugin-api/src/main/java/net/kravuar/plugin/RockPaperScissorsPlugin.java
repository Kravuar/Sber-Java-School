package net.kravuar.plugin;

import java.util.Map;
import java.util.stream.Stream;

public interface RockPaperScissorsPlugin {
    enum Option {
        ROCK,
        PAPER,
        SCISSORS;

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

            public static Outcome getOutcome(Option firstOpponentOption, Option secondOpponentOption) {
                return Stream.of(values())
                        .filter(outcome -> outcome.isOutcome(firstOpponentOption, secondOpponentOption))
                        .findFirst().orElseThrow();
            }

            private boolean isOutcome(Option firstOpponentOption, Option secondOpponentOption) {
                return combinations.get(firstOpponentOption).equals(secondOpponentOption);
            }
        }
    }

    Option act();
}
