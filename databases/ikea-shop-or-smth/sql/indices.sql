CREATE INDEX employee_department_btree ON employee USING BTREE(department);

CREATE INDEX order_responsible_id_hash ON customer_order USING HASH(responsible_id);
CREATE INDEX order_user_id_hash ON customer_order USING HASH(customer_id);

CREATE INDEX order_content_order_id_hash ON order_content USING HASH(order_id);

CREATE INDEX delivery_order_responsible_id_hash ON delivery_order USING HASH(responsible_id);

CREATE INDEX store_room_order_responsible_id_hash ON store_room USING HASH(responsible_id);

CREATE INDEX item_name_hash ON item USING HASH(name);

CREATE INDEX user_family_card_id_hash ON user_account USING HASH(family_card_id);
CREATE INDEX user_email_hash ON user_account USING HASH(email);
