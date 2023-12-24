package net.kravuar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

// В задании список 0-0
class PhoneBook {
    private final List<PhoneBookEntry> entries = new ArrayList<>();

    public boolean add(String familyName, String phoneNumber) {
        return entries.add(new PhoneBookEntry(familyName, phoneNumber));
    }

    public Collection<String> get(String familyName) {
        return entries.stream()
                .filter(entry -> entry.getFamilyName().equals(familyName))
                .map(PhoneBookEntry::getPhoneNumber)
                .collect(Collectors.toSet());
    }

    @AllArgsConstructor
    @Getter
    @Setter
    private static class PhoneBookEntry {
        private String familyName;
        private String phoneNumber;
    }
}
