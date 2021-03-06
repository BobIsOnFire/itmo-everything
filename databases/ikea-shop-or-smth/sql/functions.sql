CREATE OR REPLACE FUNCTION find_order_responsible()
    RETURNS INTEGER AS
    $$
    BEGIN
        RETURN (
            SELECT emp.id FROM employee AS emp
            LEFT JOIN customer_order AS ord ON ord.responsible_id = emp.id
            WHERE emp.department = 'sales'
            GROUP BY emp.id
            ORDER BY count(ord.id)
            LIMIT 1
        );
    END;
    $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION find_delivery_responsible()
    RETURNS INTEGER AS
    $$
    BEGIN
        RETURN (
            SELECT emp.id FROM employee AS emp
            LEFT JOIN delivery_order AS ord ON ord.responsible_id = emp.id
            WHERE emp.department = 'delivery'
            GROUP BY emp.id
            ORDER BY count(ord.id)
            LIMIT 1
        );
    END;
    $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION get_user_data(requester_id integer)
    RETURNS TABLE (
        user_id integer,
        name varchar(64),
        email varchar(64),
        card_level integer,
        card_points integer,
        card_release_time timestamp
    ) AS
    $$
    BEGIN
        RETURN QUERY
            SELECT u.id AS user_id, u.name, u.email, card.level, card.points, card.release_time
            FROM user_account u
            LEFT JOIN family_card card
            ON card.id = u.family_card_id
            WHERE u.id = requester_id;
    END;
    $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION get_order_history(requester_id integer)
    RETURNS TABLE (
        order_id integer,
        order_time timestamp,
        delivery_requested boolean,
        address varchar(64),
        delivery_time timestamp,
        assembly_ordered boolean,
        resolved boolean,
        resolve_time timestamp
    ) AS
    $$
    BEGIN
        RETURN QUERY
            SELECT ord.id AS order_id, ord.order_time, del.id IS NOT NULL AS delivery_requested,
                   del.address, del.delivery_time, del.assembly_ordered,
                   ord.resolve_time IS NOT NULL AS resolved, ord.resolve_time
            FROM customer_order ord
            LEFT JOIN delivery_order del ON del.order_id = ord.id
            WHERE ord.customer_id = requester_id
            ORDER BY ord.order_time;
    END;
    $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION get_order_data(main_order_id integer)
    RETURNS TABLE (
        customer_id integer,
        order_id integer,
        order_time timestamp,
        delivery_requested boolean,
        address varchar(64),
        delivery_time timestamp,
        assembly_ordered boolean,
        resolved boolean,
        resolve_time timestamp
    ) AS
    $$
    BEGIN
        RETURN QUERY
            SELECT ord.customer_id, ord.id AS order_id, ord.order_time,
                   del.id IS NOT NULL AS delivery_requested, del.address,
                   del.delivery_time, del.assembly_ordered,
                   ord.resolve_time IS NOT NULL AS resolved, ord.resolve_time
            FROM customer_order ord
            LEFT JOIN delivery_order del ON del.order_id = ord.id
            WHERE ord.id = main_order_id
            ORDER BY ord.order_time;
    END;
    $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION get_order_content(main_order_id integer)
    RETURNS TABLE (
        item_id integer,
        name varchar(64),
        price real,
        length real,
        width real,
        height real,
        in_stock_storage boolean,
        in_stock_shop boolean,
        store_room varchar(64),
        item_count integer
    ) AS
    $$
    BEGIN
        RETURN QUERY
            SELECT it.id AS item_id, it.name, it.price, it.length, it.width, it.height,
                   it.in_stock_storage, it.in_stock_shop, room.name, ord.item_count
            FROM order_content ord
            INNER JOIN item it ON it.id = ord.item_id
            INNER JOIN store_room room ON it.store_room_id = room.id
            WHERE ord.order_id = main_order_id;
    END;
    $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION get_item_data(main_item_id integer)
    RETURNS TABLE (
        item_id integer,
        name varchar(64),
        price real,
        length real,
        width real,
        height real,
        in_stock_storage boolean,
        in_stock_shop boolean,
        store_room varchar(64)
    ) AS
    $$
    BEGIN
        RETURN QUERY
            SELECT it.id AS item_id, it.name, it.price, it.length, it.width, it.height,
                   it.in_stock_storage, it.in_stock_shop, room.name
            FROM item it
            INNER JOIN store_room room ON it.store_room_id = room.id
            WHERE it.id = main_item_id;
    END;
    $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION get_item_content(main_item_id integer)
    RETURNS TABLE (
        name varchar(64),
        length real,
        width real,
        height real,
        color integer,
        material varchar(64),
        part_count integer
    ) AS
    $$
    BEGIN
        RETURN QUERY
            SELECT p.name, p.length, p.width, p.height, p.color, p.material, p.part_count
            FROM item_part p
            WHERE p.item_id = main_item_id;
    END;
    $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION add_order(
        requester_id integer,
        VARIADIC item_list order_item[]
    ) RETURNS integer AS
    $$
    DECLARE record order_item;
    DECLARE ord_id integer := nextval('customer_order_id_seq');

    BEGIN
        INSERT INTO customer_order(id, customer_id, responsible_id) VALUES
            (ord_id, requester_id, find_order_responsible());
        FOREACH record IN ARRAY item_list LOOP
            INSERT INTO order_content(order_id, item_id, item_count) VALUES
                (ord_id, record.item_id, record.item_count);
        END LOOP;
        RETURN ord_id;
    END;
    $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION add_delivery_order(
        requester_address varchar(64),
        requester_assembly boolean,
        requester_delivery_time timestamp,
        main_order_id integer
    ) RETURNS integer AS
    $$
    DECLARE ord_id integer := nextval('delivery_order_id_seq');

    BEGIN
        INSERT INTO delivery_order
            (id, address, assembly_ordered, delivery_time, responsible_id, order_id) VALUES
            (
                ord_id,
                requester_address,
                requester_assembly,
                requester_delivery_time,
                find_delivery_responsible(),
                main_order_id
            );
        RETURN ord_id;
    END;
    $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION resolve_order(
        main_order_id integer
    ) RETURNS void AS
    $$
    BEGIN
        UPDATE customer_order
        SET resolve_time = current_timestamp
        WHERE id = main_order_id;
    END;
    $$ LANGUAGE PLPGSQL;
