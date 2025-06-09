ALTER TABLE auto_post
ADD CONSTRAINT fk_auto_post_car FOREIGN KEY (car_id) REFERENCES car(id)
    ON DELETE SET NULL;
