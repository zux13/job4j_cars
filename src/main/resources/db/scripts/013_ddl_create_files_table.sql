CREATE TABLE files (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    path VARCHAR(512) NOT NULL UNIQUE,
    auto_post_id INT NOT NULL,
    CONSTRAINT fk_files_post FOREIGN KEY (auto_post_id) REFERENCES auto_post(id)
        ON DELETE CASCADE
);
