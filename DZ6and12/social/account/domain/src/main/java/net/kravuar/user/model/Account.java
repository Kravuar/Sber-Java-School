package net.kravuar.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Account {
    private final Long id;
    private String firstName;
    private String secondName;
    private String username;
    private String passwordEncoded;

    public static Account withoutId(String firstName, String secondName, String username, String passwordEncoded) {
        return new Account(null, firstName, secondName, username, passwordEncoded);
    }
}