CREATE TABLE IF NOT EXISTS participates (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    post_id INT NOT NULL,
    CONSTRAINT fk_participates_user FOREIGN KEY (user_id) REFERENCES auto_user(id),
    CONSTRAINT fk_participates_post FOREIGN KEY (post_id) REFERENCES auto_post(id),
    UNIQUE (user_id, post_id)
);
