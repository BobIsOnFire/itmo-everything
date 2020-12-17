DROP FUNCTION find_order_responsible;
DROP FUNCTION find_delivery_responsible;
DROP FUNCTION get_user_data;
DROP FUNCTION get_order_history;
DROP FUNCTION get_order_content;
DROP FUNCTION get_item_content(integer);
DROP FUNCTION get_item_content(varchar(64));
DROP FUNCTION add_order;
DROP FUNCTION add_delivery_order;
DROP FUNCTION resolve_order;

DROP TABLE delivery_order;
DROP TABLE order_content;
DROP TABLE customer_order;
DROP TABLE item_part;
DROP TABLE item;
DROP TABLE store_navigation;
DROP TABLE store_room;
DROP TABLE user_account;
DROP TABLE family_card;
DROP TABLE employee;

DROP FUNCTION store_navigation_unique_pair;
DROP FUNCTION delivery_time_after_order_time;
DROP FUNCTION store_room_responsible_support;
DROP FUNCTION order_responsible_sales;
DROP FUNCTION delivery_order_responsible_delivery;

DROP TYPE department;
DROP TYPE order_data;
DROP TYPE user_data;
DROP TYPE order_item;
DROP TYPE order_item_data;
DROP TYPE item_content;
