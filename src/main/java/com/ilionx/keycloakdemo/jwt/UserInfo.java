package com.ilionx.keycloakdemo.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Set;

public record UserInfo(
    @JsonProperty("sub")
    String sub,

    @JsonProperty("email_verified")
    boolean emailVerified,

    @JsonProperty("roles")
    Set<String> roles,

    @JsonProperty("name")
    String name,

    @JsonProperty("preferred_username")
    String preferredUsername,

    @JsonProperty("given_name")
    String givenName,

    @JsonProperty("family_name")
    String familyName
) {
}
