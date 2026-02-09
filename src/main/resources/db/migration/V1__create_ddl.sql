CREATE TABLE books (
                       id VARCHAR(36) PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       author VARCHAR(255) NOT NULL,
                       description VARCHAR(1000),
                       isbn VARCHAR(255) NOT NULL,
                       publication_year INT NOT NULL,
                       price DOUBLE NOT NULL,
                       pages INT NOT NULL,
                       language VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP
);

CREATE INDEX idx_books_author ON books(author);
CREATE INDEX idx_books_isbn ON books(isbn);

