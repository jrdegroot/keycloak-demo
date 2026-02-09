package com.ilionx.keycloakdemo.service;

import com.ilionx.keycloakdemo.domain.Book;
import com.ilionx.keycloakdemo.repository.BooksRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BooksService {

    private final BooksRepository booksRepository;

    public List<Book> getAllBooks() {
        return booksRepository.findAll();
    }

}
