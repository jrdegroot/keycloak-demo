package com.ilionx.keycloakdemo.jwt;

import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.*;
import java.util.function.Function;

/**
 * Custom {@link
 * org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter} to
 * extract roles using an Access Token.
 */
@SuppressWarnings("unused")
public class JwtAuthenticationTokenConverter implements Converter<Jwt, AbstractAuthenticationToken> {

  private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationTokenConverter.class);

  private static final String ROLE_PATTERN = "ROLE_%s";

  /**
   * Map roles form the user to ROLE_<permission>
   *
   * @param clientRole role from the user
   * @return ROLE_<permission> or clientRole otherwise.
   */
  private static String fromClientRole(String clientRole) {
    return ROLE_PATTERN.formatted(
            clientRole.replace("-", "_").toUpperCase());
  }

  // Receive User info.
  private final Function<String, UserInfo> userInfoSupplier;

  @Setter
  private Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter =
      new JwtGrantedAuthoritiesConverter();

  public JwtAuthenticationTokenConverter(Function<String, UserInfo> userInfoSupplier) {
    this.userInfoSupplier = userInfoSupplier;
  }

  @Override
  public AbstractAuthenticationToken convert(Jwt jwt) {
    UserInfo userInfo = getUserInfo(jwt).orElse(null);

    return new JwtAuthenticationToken(
        jwt, getGrantedAuthorities(jwt, userInfo), getUserName(jwt, userInfo));
  }

    private String getUserName(Jwt jwt, UserInfo userInfo) {
        return Optional.ofNullable(userInfo)
            .map(user -> "%s %s".formatted(user.givenName(), user.familyName()))
            .orElse(jwt.getClaimAsString(JwtClaimNames.SUB));
  }

  private Collection<GrantedAuthority> getGrantedAuthorities(Jwt jwt, UserInfo userInfo) {

    Collection<GrantedAuthority> authorities = new ArrayList<>();

    extractAuthoritiesFromJwt(jwt).ifPresent(authorities::addAll);
    extractAuthoritiesFromUserInfo(userInfo).ifPresent(authorities::addAll);

    return Collections.unmodifiableCollection(authorities);
  }

  private Optional<Collection<GrantedAuthority>> extractAuthoritiesFromJwt(Jwt jwt) {
    return Optional.ofNullable(jwtGrantedAuthoritiesConverter.convert(jwt));
  }

  private Optional<Collection<? extends GrantedAuthority>> extractAuthoritiesFromUserInfo(
      UserInfo userInfo) {
    return Optional.ofNullable(userInfo).map(user -> toAuthorities(user.roles()));
  }

  private Optional<UserInfo> getUserInfo(Jwt jwt) {
    try {
      return Optional.of(userInfoSupplier.apply(jwt.getTokenValue()));
    } catch (Exception e) {
      log.warn("Cannot obtain user info: {}", e.getMessage());
      return Optional.empty();
    }
  }

  private Collection<? extends GrantedAuthority> toAuthorities(Set<String> roles) {
    return roles.stream().map(role -> new SimpleGrantedAuthority(   fromClientRole(role))).toList();
  }
}
