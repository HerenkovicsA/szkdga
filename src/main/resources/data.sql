INSERT INTO szkdg_db.roles(
			name)
    VALUES ('ADMIN'),('USER'),('EMPLOYEE');

INSERT INTO szkdg_db.users(
			name, role_id, password, birthday, city, address, house_number, post_code, email, sex, phone_number)
    VALUES ( 'Nagy Pál', 1, 'nagyP', '1989-03-25', 'Győr', 'Szent István utca', 5, 9022, 'nagypali89@freemail.hu', 0, '+36201234567'),
    ( 'Kiss József', 2, 'KisJ', '1989-03-25', 'Győr', 'Szent István utca', 6, 9022, 'KisJ@gmail.com', 0, '+36202234567'),
    ( 'Lakatos Ramóna', 3, 'LakatosR', '1999-03-25', 'Győr', 'Szent István utca', 7, 9022, 'lakatosR@freemail.hu', 1, '+36301234567');

INSERT INTO szkdg_db.deliveries(
			delivery_date, done, employee_id)
    VALUES ( '2018-10-15', false, 3);
	
INSERT INTO szkdg_db.orders(
			dead_line, done, deliver_id, user_id)
    VALUES ( '2018-10-16', false, 1, 2);
	
INSERT INTO szkdg_db.products(
			name, onstock, price, path_to_picture)
    VALUES ( 'knife', 100, 5999, 'C:\dev\szakdoga\szakdolgozat\src\main\resources\static\images\knife.jpg');

INSERT INTO szkdg_db.productstoorders(
			order_id, product_id, quantity)
    VALUES ( 1, 1, 2);
	
COMMIT		