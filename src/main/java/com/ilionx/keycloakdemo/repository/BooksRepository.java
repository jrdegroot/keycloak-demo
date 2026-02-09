package com.ilionx.keycloakdemo.repository;

import com.ilionx.keycloakdemo.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BooksRepository extends JpaRepository<Book, Long> {
}
