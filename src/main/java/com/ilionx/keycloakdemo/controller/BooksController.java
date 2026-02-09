package com.ilionx.keycloakdemo.controller;

import com.ilionx.keycloakdemo.dto.BookDto;
import com.ilionx.keycloakdemo.service.BooksService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BooksController {

    private final BooksService booksService;

    @RolesAllowed("READ")
    @GetMapping("/books")
    public List<BookDto> getBooks() {
        return booksService.getAllBooks().stream().map(BookDto::fromBook).toList();
    }
}
