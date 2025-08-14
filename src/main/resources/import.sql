INSERT INTO tb_user (first_name, last_name, email, phone, password, birth_date, cpf) VALUES ('Maria', 'Brown', 'maria@gmail.com', '988888888', '123456', '2001-07-25', '12345678911');
INSERT INTO tb_user (first_name, last_name, email, phone, password, birth_date, cpf) VALUES ('Joao', 'Brown', 'Joao@gmail.com', '988888888', '123456', '2001-07-25', '12345678911');

INSERT INTO tb_address (road, neighborhood, city, state, zip_code, number, complement, user_id) VALUES ('Av Fernandez', 'Parque Paulista', 'Franco da Rocha', 'SP', '123-4567', '176', 'Próximo ao mercado Don Juan', 1);

INSERT INTO tb_category(name, parent_id) VALUES ('Camisetas', NULL);
INSERT INTO tb_category(name, parent_id) VALUES ('Calças', NULL);
INSERT INTO tb_category(name, parent_id) VALUES ('Jaquetas', NULL);

INSERT INTO tb_product (name, price, description, img_urls, stock_quantity, category_id) VALUES ('Camiseta Polo', 99.90, 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', '["https://exemplo.com/imagem1.jpg", "https://exemplo.com/imagem2.jpg"]', 30, 1);

INSERT INTO tb_order (moment, status, client_id) VALUES (TIMESTAMP WITH TIME ZONE '2022-07-25T13:00:00Z', 1, 1);

INSERT INTO tb_order_item (order_id, product_id, quantity, price) VALUES (1, 1, 2, 99.90);

INSERT INTO tb_payment (order_id, moment) VALUES (1, TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z');