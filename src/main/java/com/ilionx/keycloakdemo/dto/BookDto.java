package com.ilionx.keycloakdemo.dto;

import com.ilionx.keycloakdemo.domain.Book;

import java.time.LocalDateTime;

public record BookDto(
        String title,
        String author,
        String description,
        String isbn,
        Integer publicationYear,
        Double price,
        Integer pages,
        String language,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static BookDto fromBook(Book book) {
        return new BookDto(
                book.getTitle(),
                book.getAuthor(),
                book.getDescription(),
                book.getIsbn(),
                book.getPublicationYear(),
                book.getPrice(),
                book.getPages(),
                book.getLanguage(),
                book.getCreatedAt(),
                book.getUpdatedAt()
        );
    }
}
