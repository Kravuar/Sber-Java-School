package net.kravuar.user.model;


public record AccountAuthenticationRequest(
    String username,
    String password
) {}