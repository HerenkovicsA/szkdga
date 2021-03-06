GRANT ALL PRIVILEGES ON DATABASE szkdg_db TO szkdg;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA szkdg_db TO szkdg;
create table szkdg_db.deliveries (id  bigserial not null, delivery_date date, delivery_order varchar(255), distance float8, done boolean, employee_id int8, primary key (id));
create table szkdg_db.orders (id  bigserial not null, dead_line date not null, done boolean not null, value Decimal(15,2), deliver_id int8, user_id int8 not null, primary key (id));
create table szkdg_db.post_code_to_cities (id  serial not null, city_name varchar(255), post_code int4, primary key (id));
create table szkdg_db.products (id  bigserial not null, deleted boolean, height float8, length float8, name varchar(255) not null, onstock int4 not null, path_to_picture varchar(2000) not null, price float8 not null, width float8, primary key (id));
create table szkdg_db.productstoorders (id  bigserial not null, prod_act_value float8, quantity int4 not null, order_id int8 not null, product_id int8 not null, primary key (id));
create table szkdg_db.roles (id  bigserial not null, name varchar(255) not null, primary key (id));
create table szkdg_db.users (id  bigserial not null, address varchar(255) not null, birthday date not null, city varchar(255) not null, email varchar(255) not null, full_address varchar(255), house_number int4 not null, name varchar(255) not null, password varchar(255) not null, phone_number varchar(255) not null, post_code int4 not null check (post_code>=1000 AND post_code<=9999), sex int4 not null, role_id int8 not null, primary key (id));

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA szkdg_db TO szkdg;

alter table szkdg_db.deliveries add constraint FKliapawv8n3kvwhv2vutvuc51d foreign key (employee_id) references szkdg_db.users;
alter table szkdg_db.orders add constraint FKg5071dh8iv1ee8a1h9racnwf9 foreign key (deliver_id) references szkdg_db.deliveries;
alter table szkdg_db.orders add constraint FK32ql8ubntj5uh44ph9659tiih foreign key (user_id) references szkdg_db.users;
alter table szkdg_db.productstoorders add constraint FK7yjm90g3nq2crs0m5ww2w9esm foreign key (order_id) references szkdg_db.orders;
alter table szkdg_db.productstoorders add constraint FKagq4yiyog3g0c4y9wyx40xlwa foreign key (product_id) references szkdg_db.products;
alter table szkdg_db.users add constraint FKp56c1712k691lhsyewcssf40f foreign key (role_id) references szkdg_db.roles;

GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA szkdg_db TO szkdg;
COMMIT;