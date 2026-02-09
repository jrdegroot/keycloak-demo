package com.ilionx.keycloakdemo.client;

import com.ilionx.keycloakdemo.jwt.UserInfo;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(contentType = MediaType.APPLICATION_JSON_VALUE)
public interface KeycloakClient {

    @GetExchange(value = "/protocol/openid-connect/userinfo")
    UserInfo getUserInfo(@RequestHeader("Authorization") String accessToken);

}
