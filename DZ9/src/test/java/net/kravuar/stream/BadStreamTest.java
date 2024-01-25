package net.kravuar.stream;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BadStreamTest {
    @Test
    void givenList_whenToList_ThenEqualsToSource() {
        // given
        List<Integer> source = List.of(1, 2, 3, 4, 5);

        // when
        List<Integer> result = BadStream.stream(source).toList();

        // then
        assertEquals(source, result);
    }

    @Test
    void givenListAndListFactory_whenToList_ThenEqualsToSource_AndTypeMatchesFactory() {
        // given
        List<Integer> source = List.of(1, 2, 3, 4, 5);
        Supplier<List<Integer>> listFactory = LinkedList::new;

        // when
        List<Integer> result = BadStream.stream(source).toList(listFactory);

        // then
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), result);
        assertEquals(LinkedList.class, result.getClass());
    }

    @Test
    void givenListAndEvenNumberPredicate_WhenFilterAndToList_ThenResultFiltered() {
        // given
        List<Integer> source = List.of(1, 2, 3, 4, 5);
        Predicate<Integer> predicate = num -> num % 2 == 0;

        // when
        List<Integer> result = BadStream.stream(source)
                .filter(predicate)
                .toList();

        // then
        assertEquals(Arrays.asList(2, 4), result);
    }

    @Test
    void testMap() {
        // given
        List<Integer> source = List.of(1, 2, 3, 4, 5);
        Function<Integer, String> mapper = Object::toString;

        // when
        List<String> result = BadStream.stream(source)
                .map(mapper)
                .toList();

        // then
        assertEquals(Arrays.asList("1", "2", "3", "4", "5"), result);
    }

    @Test
    void testToMap() {
        // Given
        List<Integer> source = List.of(1, 2, 3, 4, 5);
        Function<Integer, String> keyMapper = num -> "Key-" + num;
        Function<Integer, String> valueMapper = Object::toString;
        Supplier<Map<String, String>> mapFactory = HashMap::new;

        // when
        Map<String, String> resultMap = BadStream.stream(source)
                .toMap(keyMapper, valueMapper, mapFactory);

        // then
        Map<String, String> expectedMap = Map.of(
                "Key-1", "1",
                "Key-2", "2",
                "Key-3", "3",
                "Key-4", "4",
                "Key-5", "5"
        );

        assertEquals(expectedMap, resultMap);
    }

    @Test
    void givenListAndComplexPipeline_WhenEvaluateAndToList_ThenMatchesExpected() {
        // given
        List<String> source = List.of("5bebebeb3ebe", "bebe5", "be42be6bbebee", "beb1e7bebe");
        Predicate<String> lengthPredicate = str -> str.length() > "bebe5".length();
        Function<String, String> nonDigitRemover = str -> str.replaceAll("[^\\d.]", "");
        Predicate<Integer> oddNumberPredicate = i -> i % 2 == 1;

        // when
        List<Integer> result = BadStream.stream(source)
                .filter(lengthPredicate) // all except "bebe5"
                .map(nonDigitRemover) // ["53", "426", "17"]
                .map(Integer::parseInt) // [53, 426, 17]
                .filter(oddNumberPredicate) // [53, 17]
                .toList();

        // then
        List<Integer> expected = List.of(53, 17);
        assertEquals(expected, result);
    }
}
