ALTER TABLE car ADD COLUMN auto_category_id INT NOT NULL;
ALTER TABLE car ADD COLUMN auto_brand_id INT NOT NULL;
ALTER TABLE car ADD COLUMN auto_type_id INT NOT NULL;
ALTER TABLE car ADD COLUMN owner_id INT NOT NULL;
ALTER TABLE car ADD CONSTRAINT fk_car_category FOREIGN KEY (auto_category_id) REFERENCES auto_category(id);
ALTER TABLE car ADD CONSTRAINT fk_car_brand FOREIGN KEY (auto_brand_id) REFERENCES auto_brand(id);
ALTER TABLE car ADD CONSTRAINT fk_car_type FOREIGN KEY (auto_type_id) REFERENCES auto_type(id);
ALTER TABLE car ADD CONSTRAINT fk_car_owner FOREIGN KEY (owner_id) REFERENCES owners(id);
