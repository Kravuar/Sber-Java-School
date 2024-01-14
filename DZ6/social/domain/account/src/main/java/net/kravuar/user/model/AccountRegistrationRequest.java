package net.kravuar.user.model;


public record AccountRegistrationRequest(
    String firstName,
    String secondName,
    String username,
    String password
) {}