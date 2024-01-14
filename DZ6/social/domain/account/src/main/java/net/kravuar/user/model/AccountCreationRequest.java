package net.kravuar.user.model;


public record AccountCreationRequest(
    String firstName,
    String secondName,
    String username,
    String password
) {}