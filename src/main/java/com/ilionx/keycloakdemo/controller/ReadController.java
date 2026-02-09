package com.ilionx.keycloakdemo.controller;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReadController {

    @RolesAllowed("READ")
    @GetMapping("/read")
    public String read() {
        return "Hello World!";
    }
}
