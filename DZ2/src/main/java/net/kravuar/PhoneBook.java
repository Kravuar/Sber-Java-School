package net.kravuar;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class PhoneBook {
    private final Map<String, Set<String>> entries = new HashMap<>();

    public void add(String familyName, String phoneNumber) {
        entries.merge(
                familyName,
                Set.of(phoneNumber),
                (oldSet, singleton) -> Stream.of(oldSet, singleton)
                        .flatMap(Set::stream)
                        .collect(Collectors.toSet())
        );
    }

    public Collection<String> get(String familyName) {
        return entries.getOrDefault(
                familyName,
                Collections.emptySet()
        );
    }
}
