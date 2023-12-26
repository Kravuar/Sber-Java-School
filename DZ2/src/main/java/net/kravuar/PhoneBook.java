package net.kravuar;

import java.util.*;

class PhoneBook {
    private final Map<String, Set<String>> entries = new HashMap<>();

    public void add(String familyName, String phoneNumber) {
        entries.merge(
                familyName,
                new HashSet<>(Collections.singleton(phoneNumber)),
                (oldSet, singleton) -> {
                    oldSet.addAll(singleton);
                    return oldSet;
                }
        );
    }

    public Collection<String> get(String familyName) {
        return Collections.unmodifiableSet(
                entries.getOrDefault(
                    familyName,
                    Collections.emptySet()
                )
        );
    }
}
