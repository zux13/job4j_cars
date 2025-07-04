CREATE TABLE IF NOT EXISTS price_history (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    auto_post_id INT NOT NULL,
    before_price BIGINT,
    after_price BIGINT NOT NULL,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_price_post FOREIGN KEY (auto_post_id) REFERENCES auto_post(id)
        ON DELETE CASCADE
);
