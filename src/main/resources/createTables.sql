DO $$
BEGIN
    IF EXISTS
    (SELECT 
      schema_name
    FROM 
      information_schema.schemata 
    WHERE 
      schema_name = 'szkdg_db')
    THEN
      DROP SCHEMA szkdg_db CASCADE; 
    END IF; 
    
    CREATE SCHEMA IF NOT EXISTS SZKDG_DB;
	
    CREATE TABLE SZKDG_DB.USERS
	  ( 
	  id SERIAL NOT NULL, 
	  name VARCHAR(60) NOT NULL, 
	  birthday DATE NOT NULL, 
	  city VARCHAR(60) NOT NULL,
	  address VARCHAR(100) NOT NULL,
	  housenumber INTEGER NOT NULL,
	  longitude VARCHAR(20),
	  latitude VARCHAR(20),
	  email VARCHAR(30) NOT NULL,	  
	  male BOOLEAN NOT NULL,
	  phoneNumber VARCHAR(30),
	  CONSTRAINT USERS_PK PRIMARY KEY (id)
	  );
	
	CREATE TABLE SZKDG_DB.EMPLOYEES
	    ( 
	  id SERIAL NOT NULL, 
	  name VARCHAR(60) NOT NULL, 
	  birthday DATE NOT NULL, 
	  city VARCHAR(60) NOT NULL,
	  address VARCHAR(100) NOT NULL,
	  housenumber INTEGER NOT NULL,
	  email VARCHAR(30) NOT NULL,	  
	  male BOOLEAN NOT NULL,
	  phoneNumber VARCHAR(30),
	  CONSTRAINT EMPLOYEES_PK PRIMARY KEY (id)
	  ); 
	   
	CREATE TABLE SZKDG_DB.DELIVERIES
	   (	
	    id SERIAL NOT NULL, 
	    delivery_date DATE NOT NULL,
		done BOOLEAN NOT NULL,
		employee_id INTEGER NOT NULL,
		CONSTRAINT DELIVERIES_PK PRIMARY KEY (id),
		  CONSTRAINT DELIVERIES_EMPLOYEES_FK1 FOREIGN KEY (employee_id)
		  REFERENCES SZKDG_DB.EMPLOYEES(id)
	   );

	CREATE TABLE SZKDG_DB.ORDERS	
	   (
		id SERIAL NOT NULL,
		dead_line DATE NOT NULL,
		done BOOLEAN NOT NULL,
		delivery_id INTEGER NOT NULL,
		user_id INTEGER NOT NULL,
		CONSTRAINT ORDERS_PK PRIMARY KEY (id),
		  CONSTRAINT ORDERS_DELIVERIES_FK1 FOREIGN KEY (delivery_id)
		  REFERENCES SZKDG_DB.DELIVERIES(id)
	   );

	CREATE TABLE SZKDG_DB.PRODUCTS	
	   (
		id SERIAL NOT NULL,
		name VARCHAR(30) NOT NULL,
		onstock INTEGER NOT NULL,
		price INTEGER NOT NULL,
		path_to_picture VARCHAR(2000) NOT NULL,
		CONSTRAINT PRODUCTS_PK PRIMARY KEY (id)
	   );

	CREATE TABLE SZKDG_DB.PRODUCTSTOORDERS	
	   (
		id SERIAL NOT NULL,
		order_id INTEGER NOT NULL,
		product_id INTEGER NOT NULL,
		quantity INTEGER NOT NULL,
		CONSTRAINT PRODUCTSTOORDERS_PK PRIMARY KEY (id),
		  CONSTRAINT PRODUCTSTOORDERS_ORDERS_FK FOREIGN KEY (order_id)
		  REFERENCES SZKDG_DB.ORDERS(id),
		  CONSTRAINT PRODUCTSTOORDERS_PRODUCTS_FK FOREIGN KEY (product_id)
		  REFERENCES SZKDG_DB.PRODUCTS(id)
	   );
END$$;
   
   