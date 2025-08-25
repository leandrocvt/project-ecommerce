INSERT INTO tb_user (first_name, last_name, email, phone, password, birth_date, cpf) VALUES ('Maria', 'Brown', 'maria@gmail.com', '988888888', '$2a$10$swcqxYe2OybmFZ/3dv8f9ujnE8B5QycDXHZkWlzdWOZp0llaRWNYK', '2001-07-25', '12345678911');
INSERT INTO tb_user (first_name, last_name, email, phone, password, birth_date, cpf) VALUES ('Joao', 'Brown', 'joao@gmail.com', '988888888', '$2a$10$swcqxYe2OybmFZ/3dv8f9ujnE8B5QycDXHZkWlzdWOZp0llaRWNYK', '2001-07-25', '12345678911');
INSERT INTO tb_user (first_name, last_name, email, phone, password, birth_date, cpf) VALUES ('Leandro', 'Amaral', 'leandrocavalcanti499@gmail.com', '988888888', '$2a$10$swcqxYe2OybmFZ/3dv8f9ujnE8B5QycDXHZkWlzdWOZp0llaRWNYK', '2001-07-25', '12345678911');

INSERT INTO tb_role (authority) VALUES ('ROLE_CLIENT');
INSERT INTO tb_role (authority) VALUES ('ROLE_ADMIN');

INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 2);
INSERT INTO tb_user_role (user_id, role_id) VALUES (3, 1);

INSERT INTO tb_address (road, neighborhood, city, state, zip_code, number, complement, user_id) VALUES ('Av Fernandez', 'Parque Paulista', 'Franco da Rocha', 'SP', '123-4567', '176', 'Próximo ao mercado Don Juan', 1);

INSERT INTO tb_category(name, parent_id) VALUES ('Camisetas', NULL);
INSERT INTO tb_category(name, parent_id) VALUES ('Calças', NULL);
INSERT INTO tb_category(name, parent_id) VALUES ('Jaquetas', NULL);

INSERT INTO tb_category (id, name, parent_id) VALUES (4, 'Polo', 1);
INSERT INTO tb_category (id, name, parent_id) VALUES (5, 'Oversized', 1);
INSERT INTO tb_category (id, name, parent_id) VALUES (6, 'Regata', 1);
INSERT INTO tb_category (id, name, parent_id) VALUES (7, 'Casual', 1);

INSERT INTO tb_product (name, base_price, description, category_id, active) VALUES ('Camiseta Polo', 99.90, 'Lorem ipsum dolor sit amet, consectetur adipiscing elit...', 4, TRUE);

INSERT INTO tb_product_variation (color, size, price_adjustment, discount_amount, stock_quantity, product_id) VALUES ('BRANCO', 'M', 0.00, 10.00, 10, 1);
INSERT INTO tb_product_variation (color, size, price_adjustment, discount_amount, stock_quantity, product_id) VALUES ('PRETO', 'M', 0.00, 0.00, 20, 1);
INSERT INTO tb_product_variation (color, size, price_adjustment, discount_amount, stock_quantity, product_id) VALUES ('PRETO', 'G', 5.00, 0.00, 15, 1);

INSERT INTO tb_product_variation_images (variation_id, img_url, is_primary) VALUES (1, 'https://exemplo.com/polo-branca-m-frente.jpg', TRUE);
INSERT INTO tb_product_variation_images (variation_id, img_url, is_primary) VALUES (1, 'https://exemplo.com/polo-branca-m-costas.jpg', FALSE);

INSERT INTO tb_product_variation_images (variation_id, img_url, is_primary) VALUES (2, 'https://exemplo.com/polo-preta-m-frente.jpg', TRUE);
INSERT INTO tb_product_variation_images (variation_id, img_url, is_primary) VALUES (2, 'https://exemplo.com/polo-preta-m-costas.jpg', FALSE);

INSERT INTO tb_product_variation_images (variation_id, img_url, is_primary) VALUES (3, 'https://exemplo.com/polo-preta-g-frente.jpg', TRUE);
INSERT INTO tb_product_variation_images (variation_id, img_url, is_primary) VALUES (3, 'https://exemplo.com/polo-preta-g-costas.jpg', FALSE);

INSERT INTO tb_order (moment, status, client_id) VALUES (TIMESTAMP WITH TIME ZONE '2022-07-25T13:00:00Z', 1, 1);

INSERT INTO tb_order_item (order_id, product_id, quantity, price) VALUES (1, 1, 2, 99.90);

INSERT INTO tb_payment (order_id, moment) VALUES (1, TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z');

INSERT INTO tb_assessment (score, product_id, user_id, comment, photo_url, created_at) VALUES (4.0, 1, 1, 'Produto ótimo, confortável', 'https://exemplo.com', '2022-07-25T13:00:00Z')
INSERT INTO tb_assessment (score, product_id, user_id, comment, photo_url, created_at) VALUES (5.0, 1, 2, 'Produto ótimo, confortável', 'https://exemplo.com', '2025-07-25T13:00:00Z')