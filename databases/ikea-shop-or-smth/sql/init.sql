CREATE TYPE department AS enum ('sales', 'engineering', 'delivery', 'support', 'higher management');

CREATE TYPE order_item AS (
    item_id integer,
    item_count integer
);

CREATE TABLE employee
(
    id serial
        PRIMARY KEY,
    name varchar(64)
        NOT NULL,
    email varchar(64)
        NOT NULL UNIQUE,
    department department
        NOT NULL,
    wage real
        NOT NULL CHECK (wage > 0)
);

CREATE TABLE family_card
(
    id serial
        PRIMARY KEY,
    level integer
        DEFAULT 1
        CHECK(level >= 1 AND level <= 5),
    points integer
        DEFAULT 0
        CHECK(points >= 0),
    release_time timestamp
        DEFAULT current_timestamp
        NOT NULL
);

CREATE TABLE user_account
(
    id serial
        PRIMARY KEY,
    name varchar(64)
        NOT NULL,
    email varchar(64)
        NOT NULL UNIQUE,
    family_card_id integer
        DEFAULT NULL
        REFERENCES family_card(id) ON DELETE SET NULL,
    password_hash varchar(128)
        NOT NULL
);

CREATE TABLE store_room
(
    id serial
        PRIMARY KEY,
    name varchar(64)
        NOT NULL UNIQUE,
    length real
        NOT NULL CHECK (length > 0),
    width real
        NOT NULL CHECK (width > 0 AND width <= length),
    responsible_id integer
        NOT NULL
        REFERENCES employee(id) ON DELETE RESTRICT
);

CREATE TABLE store_navigation
(
    from_id integer
        NOT NULL
        REFERENCES store_room(id) ON DELETE CASCADE,
    to_id integer
        NOT NULL
        REFERENCES store_room(id) ON DELETE CASCADE,

    PRIMARY KEY (from_id, to_id),
    CHECK (from_id != to_id)
);

CREATE TABLE item
(
    id serial
        PRIMARY KEY,
    name varchar(64)
        NOT NULL UNIQUE,
    price real
        NOT NULL CHECK (price >= 0),
    length real
        NOT NULL CHECK (length > 0),
    width real
        NOT NULL CHECK (width > 0 and width <= length),
    height real
        NOT NULL CHECK (height > 0),
    in_stock_storage boolean
        DEFAULT false
        NOT NULL,
    in_stock_shop boolean
        DEFAULT false
        NOT NULL,
    store_room_id integer
        DEFAULT NULL
        REFERENCES store_room(id) ON DELETE SET NULL,

    CHECK (in_stock_storage OR (NOT in_stock_shop)),
    CHECK (store_room_id IS NOT NULL OR (NOT in_stock_shop))
);

CREATE TABLE item_part
(
    id serial
        PRIMARY KEY,
    name varchar(64)
        NOT NULL,
    length real
        NOT NULL CHECK (length > 0),
    width real
        NOT NULL CHECK (width > 0),
    height real
        NOT NULL CHECK (height > 0),
    color integer
        DEFAULT 0
        NOT NULL CHECK (color >= 0 AND color <= x'FFFFFF'::integer),
    material varchar(64)
        NOT NULL,
    part_count integer
        DEFAULT 1
        NOT NULL CHECK (part_count > 0),
    item_id integer
        NOT NULL
        REFERENCES item(id) ON DELETE CASCADE
);

CREATE TABLE customer_order
(
    id serial
        PRIMARY KEY,
    order_time timestamp
        DEFAULT current_timestamp
        NOT NULL,
    resolve_time timestamp
        DEFAULT NULL
        CHECK(resolve_time > order_time),
    customer_id integer
        NOT NULL
        REFERENCES user_account(id) ON DELETE CASCADE,
    responsible_id integer
        REFERENCES employee(id) ON DELETE SET NULL
);

CREATE TABLE order_content
(
    order_id integer
        NOT NULL
        REFERENCES customer_order(id) ON DELETE CASCADE,
    item_id integer
        NOT NULL
        REFERENCES item(id) ON DELETE CASCADE,
    item_count integer
        DEFAULT 1
        CHECK(item_count > 0),
    
    PRIMARY KEY (order_id, item_id)
);

CREATE TABLE delivery_order
(
    id serial
        PRIMARY KEY,
    address varchar(64)
        NOT NULL,
    delivery_time timestamp
        NOT NULL,
    assembly_ordered boolean
        DEFAULT false
        NOT NULL,
    responsible_id integer
        REFERENCES employee(id) ON DELETE SET NULL,
    order_id integer
        NOT NULL UNIQUE
        REFERENCES customer_order(id) ON DELETE CASCADE
);
