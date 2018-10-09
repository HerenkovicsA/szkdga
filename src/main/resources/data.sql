INSERT INTO szkdg_db.users(
			name, birthday, city, address, housenumber, latitude, longitude, email, male, phonenumber)
    VALUES ( 'Nagy Pál', '1989-03-25', 'Győr', 'Szent István utca', 5, '47.682242', '17.6276891', 'nagypali89@freemail.hu', true, '+36201234567');

INSERT INTO szkdg_db.employees(
			name, birthday, city, address, housenumber, email, male, phonenumber)
    VALUES ( 'Kis Péter', '1992-04-22', 'Győr', 'Szent Imre utca', 9, 'kisP92@gmail.com', true, '+36201235486');

INSERT INTO szkdg_db.deliveries(
			delivery_date, done, employee_id)
    VALUES ( '2018-10-15', false, 1);
	
INSERT INTO szkdg_db.orders(
			dead_line, done, delivery_id, user_id)
    VALUES ( '2018-10-16', false, 1, 1);
	
INSERT INTO szkdg_db.products(
			name, onstock, price, path_to_picture)
    VALUES ( 'knife', 100, 5999, 'C:\dev\szakdoga\szakdolgozat\src\main\resources\static\images\knife.jpg');

INSERT INTO szkdg_db.productstoorders(
			order_id, product_id, quantity)
    VALUES ( 1, 1, 2);
	
COMMIT		