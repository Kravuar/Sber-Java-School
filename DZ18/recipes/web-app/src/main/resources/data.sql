INSERT INTO ingredient (name, description)
VALUES ('Flour', 'White flour for baking'),
       ('Sugar', 'Granulated white sugar'),
       ('Eggs', 'Large eggs'),
       ('Milk', 'Whole milk'),
       ('Chocolate', 'Dark chocolate'),
       ('Butter', 'Unsalted butter');

INSERT INTO recipe (name, description, cooking_time)
VALUES ('Chocolate Cake', 'Delicious chocolate cake recipe', 3600),
       ('Pancakes', 'Classic pancake recipe', 1800),
       ('Scrambled Eggs', 'Simple scrambled eggs recipe', 600),
       ('Chocolate Chip Cookies', 'Homemade chocolate chip cookies', 2400),
       ('French Toast', 'Traditional french toast recipe', 1200);

INSERT INTO recipe_item (recipe_id, ingredient_id, count, count_unit)
VALUES (1, 1, 2, 'PIECE'),
       (1, 2, 200, 'MILLIGRAM'),
       (1, 5, 100, 'GRAM'),
       (1, 6, 100, 'GRAM'),
       (2, 1, 1.5, 'PIECE'),
       (2, 2, 150, 'MILLIGRAM'),
       (2, 4, 200, 'MILLILITRES'),
       (3, 3, 3, 'PIECE'),
       (3, 4, 100, 'MILLILITRES'),
       (3, 6, 20, 'GRAM'),
       (4, 1, 2, 'PIECE'),
       (4, 2, 250, 'GRAM'),
       (4, 5, 150, 'GRAM'),
       (4, 6, 150, 'GRAM'),
       (5, 1, 4, 'PIECE'),
       (5, 3, 4, 'PIECE'),
       (5, 4, 200, 'MILLILITRES'),
       (5, 6, 50, 'GRAM');
