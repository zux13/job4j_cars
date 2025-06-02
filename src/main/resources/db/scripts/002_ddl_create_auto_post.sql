CREATE TABLE IF NOT EXISTS auto_post (
    id INT AUTO_INCREMENT PRIMARY KEY,
    description TEXT NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    auto_user_id INT NOT NULL,
    CONSTRAINT fk_post_user FOREIGN KEY (auto_user_id) REFERENCES auto_user(id)
        ON DELETE CASCADE
);
