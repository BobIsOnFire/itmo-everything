-- store_navigation: if (from, to) already exists, neither (from, to) nor (to, from) can be added
CREATE OR REPLACE FUNCTION store_navigation_unique_pair()
    RETURNS TRIGGER AS
    $$
    BEGIN
        IF EXISTS(
            SELECT from_id, to_id FROM store_navigation WHERE
                (from_id = NEW.from_id AND to_id = NEW.to_id) OR
                (from_id = NEW.to_id AND to_id = NEW.from_id)
        ) THEN
            RETURN NULL;
        END IF;
        RETURN NEW;
    END
    $$ LANGUAGE PLPGSQL;

CREATE TRIGGER store_navigation_unique_pair
    BEFORE INSERT ON store_navigation
    FOR EACH ROW
    EXECUTE PROCEDURE store_navigation_unique_pair();


-- delivery_time should be at least 2 hrs after order_time
CREATE OR REPLACE FUNCTION delivery_time_after_order_time()
    RETURNS TRIGGER AS
    $$
    DECLARE order_time timestamp := (
        SELECT order_time FROM customer_order WHERE id = NEW.order_id
    );

    BEGIN
        IF NEW.delivery_time - order_time >= interval '2 hour' THEN
            RETURN NEW;
        END IF;
        RETURN NULL;
    END;
    $$ LANGUAGE PLPGSQL;

CREATE TRIGGER delivery_time_after_order_time
    BEFORE INSERT ON delivery_order
    FOR EACH ROW
    EXECUTE PROCEDURE delivery_time_after_order_time();


-- responsible for activity employees should work in a specified department
CREATE OR REPLACE FUNCTION store_room_responsible_support()
    RETURNS TRIGGER AS
    $$
    DECLARE responsible_dpt department := (
        SELECT department FROM employee WHERE id = NEW.responsible_id
    );

    BEGIN
        IF responsible_dpt = 'support' THEN
            RETURN NEW;
        END IF;
        RETURN NULL;
    END;
    $$ LANGUAGE PLPGSQL;

CREATE TRIGGER store_room_responsible_support
    BEFORE INSERT ON store_room
    FOR EACH ROW
    EXECUTE PROCEDURE store_room_responsible_support();

CREATE OR REPLACE FUNCTION order_responsible_sales()
    RETURNS TRIGGER AS
    $$
    DECLARE responsible_dpt department := (
        SELECT department FROM employee WHERE id = NEW.responsible_id
    );

    BEGIN
        IF responsible_dpt = 'sales' THEN
            RETURN NEW;
        END IF;
        RETURN NULL;
    END;
    $$ LANGUAGE PLPGSQL;

CREATE TRIGGER order_responsible_sales
    BEFORE INSERT ON customer_order
    FOR EACH ROW
    EXECUTE PROCEDURE order_responsible_sales();

CREATE OR REPLACE FUNCTION delivery_order_responsible_delivery()
    RETURNS TRIGGER AS
    $$
    DECLARE responsible_dpt department := (
        SELECT department FROM employee WHERE id = NEW.responsible_id
    );

    BEGIN
        IF responsible_dpt = 'delivery' THEN
            RETURN NEW;
        END IF;
        RETURN NULL;
    END;
    $$ LANGUAGE PLPGSQL;

CREATE TRIGGER delivery_order_responsible_delivery
    BEFORE INSERT ON delivery_order
    FOR EACH ROW
    EXECUTE PROCEDURE delivery_order_responsible_delivery();
