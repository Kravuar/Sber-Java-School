CREATE TABLE ingredient
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE recipe
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    description  VARCHAR(255),
    cooking_time BIGINT
);

CREATE TABLE recipe_item
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipe_id     BIGINT,
    ingredient_id BIGINT,
    count         DOUBLE,
    count_unit    VARCHAR(50),
    FOREIGN KEY (recipe_id) REFERENCES recipe (id),
    FOREIGN KEY (ingredient_id) REFERENCES ingredient (id)
);
