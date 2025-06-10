CREATE TABLE IF NOT EXISTS auto_type (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    auto_category_id INT NOT NULL,
    CONSTRAINT fk_type_category FOREIGN KEY (auto_category_id) REFERENCES auto_category(id)
        ON DELETE CASCADE
);
