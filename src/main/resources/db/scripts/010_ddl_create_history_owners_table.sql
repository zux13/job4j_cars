CREATE TABLE history_owners (
    car_id INT,
    owner_id INT,
    history_id INT,
    PRIMARY KEY (car_id, owner_id),
    FOREIGN KEY (car_id) REFERENCES car(id) ON DELETE CASCADE,
    FOREIGN KEY (owner_id) REFERENCES owners(id) ON DELETE CASCADE,
    FOREIGN KEY (history_id) REFERENCES history(id)
);
