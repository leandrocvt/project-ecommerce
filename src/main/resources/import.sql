INSERT INTO tb_user (first_name, last_name, email, phone, password, birth_date, cpf) VALUES ('Maria', 'Brown', 'maria@gmail.com', '988888888', '123456', '2001-07-25', '12345678911');
INSERT INTO tb_user (first_name, last_name, email, phone, password, birth_date, cpf) VALUES ('Joao', 'Brown', 'Joao@gmail.com', '988888888', '123456', '2001-07-25', '12345678911');

INSERT INTO tb_address (road, neighborhood, city, state, zip_code, number, complement, user_id) VALUES ('Av Fernandez', 'Parque Paulista', 'Franco da Rocha', 'SP', '123-4567', '176', 'Próximo ao mercado Don Juan', 1);

INSERT INTO tb_category(name, parent_id) VALUES ('Camisetas', NULL);
INSERT INTO tb_category(name, parent_id) VALUES ('Calças', NULL);
INSERT INTO tb_category(name, parent_id) VALUES ('Jaquetas', NULL);

INSERT INTO tb_category (id, name, parent_id) VALUES (4, 'Polo', 1);
INSERT INTO tb_category (id, name, parent_id) VALUES (5, 'Oversized', 1);
INSERT INTO tb_category (id, name, parent_id) VALUES (6, 'Regata', 1);
INSERT INTO tb_category (id, name, parent_id) VALUES (7, 'Casual', 1);

INSERT INTO tb_product (name, base_price, description, category_id) VALUES ('Camiseta Polo', 99.90, 'Lorem ipsum dolor sit amet, consectetur adipiscing elit...', 4);

INSERT INTO tb_product_variation (color, size, price_adjustment, stock_quantity, product_id) VALUES ('PRETO', 'M', 0.00, 20, 1);
INSERT INTO tb_product_variation (color, size, price_adjustment, stock_quantity, product_id) VALUES ('PRETO', 'G', 5.00, 15, 1);
INSERT INTO tb_product_variation (color, size, price_adjustment, stock_quantity, product_id) VALUES ('BRANCO', 'M', 0.00, 10, 1);

-- imagens da variação BLACK M (id=1, supondo autoincrement começando em 1)
INSERT INTO tb_product_variation_images (variation_id, img_url) VALUES (1, 'https://exemplo.com/polo-preta-m-frente.jpg');
INSERT INTO tb_product_variation_images (variation_id, img_url) VALUES (1, 'https://exemplo.com/polo-preta-m-costas.jpg');

-- imagens da variação BLACK G (id=2)
INSERT INTO tb_product_variation_images (variation_id, img_url) VALUES (2, 'https://exemplo.com/polo-preta-g-frente.jpg');
INSERT INTO tb_product_variation_images (variation_id, img_url) VALUES (2, 'https://exemplo.com/polo-preta-g-costas.jpg');

-- imagens da variação WHITE M (id=3)
INSERT INTO tb_product_variation_images (variation_id, img_url) VALUES (3, 'https://exemplo.com/polo-branca-m-frente.jpg');
INSERT INTO tb_product_variation_images (variation_id, img_url) VALUES (3, 'https://exemplo.com/polo-branca-m-costas.jpg');

INSERT INTO tb_order (moment, status, client_id) VALUES (TIMESTAMP WITH TIME ZONE '2022-07-25T13:00:00Z', 1, 1);

INSERT INTO tb_order_item (order_id, product_id, quantity, price) VALUES (1, 1, 2, 99.90);

INSERT INTO tb_payment (order_id, moment) VALUES (1, TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z');