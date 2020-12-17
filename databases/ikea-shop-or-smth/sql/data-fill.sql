INSERT INTO employee 
    (name,                  email,                         department,            wage) VALUES 
    ('Jesper Brodin',       'jbrodin@ikea.com',            'higher management', 350000),
    ('Torbjorn Loof',       'tloof@ikea.com',              'higher management', 300000),
    ('Gustav Johnson',      'gustav.johnson@ikea.com',     'sales',              50000),
    ('Mikhail Vasiliev',    'mikhail.vasiliev@ikea.com',   'sales',              45000),
    ('Vladimir Morinov',    'vladimir.morinov@ikea.com',   'sales',              60000),
    ('Michelle Trust',      'mtrust@ikea.com',             'sales',              80000),
    ('Anna Vasilieva',      'anna.vasilieva@ikea.com',     'sales',              70000),
    ('Alex Wong',           'wong_alex@techy.hk',          'engineering',       150000),
    ('Catey Ma',            'ma_catey@techy.hk',           'engineering',       170000),
    ('Yandhi Mahal',        'mahal@ft.inc.in',             'engineering',        75000),
    ('Valery Glotov',       'vglotov@ikea.com',            'delivery',           35000),
    ('Alexander Ivanov',    'alexander.ivanov@ikea.com',   'delivery',           30000),
    ('Maxim Iurov',         'maxim.iurov@ikea.com',        'delivery',           35000),
    ('Konstantin Yakushev', 'kyakushev@ikea.com',          'delivery',           30000),
    ('Natalie Dough',       'natalie.dough@ikea.com',      'support',            50000),
    ('Anastasia Smitko',    'asmitko@ikea.com',            'support',            60000),
    ('Valeria Afanasieva',  'valeria.afanasieva@ikea.com', 'support',            55000);

INSERT INTO family_card
    (level, points, release_time         ) VALUES
    (    5,   1503, '2015-01-30 14:03:46'),
    (    2,     71, '2020-04-08 20:11:35'),
    (    1,      0, '2020-12-06 19:46:30');

INSERT INTO user_account
    (name,              email,                  family_card_id) VALUES
    ('Nikita Akatyev',  'akatyevnl@gmail.com',               2),
    ('Mikhail Mirchuk', 'miker@goodgame.ru',              NULL),
    ('Artem Gorshkov',  'gorshik@gmail.com',                 3),
    ('Sandy Muse',      'muse134@bing.com',                  1),
    ('Maxim Iurov',     'maxim.iurov@ikea.com',           NULL);

INSERT INTO store_room
    (name,                        length, width, responsible_id) VALUES
    ('Entrance',                      10,    10,             15),
    ('Soft Furniture',                50,    30,             16),
    ('Living Room',                   50,    50,             16),
    ('Workspaces',                    50,    50,             16),
    ('Kitchen',                       40,    30,             16),
    ('Dining',                        50,    30,             16),
    ('Bedroom',                       60,    40,             16),
    ('Wardrobes & Storage',           30,    30,             16),
    ('Childrens IKEA',               60,    50,             16),
    ('IKEA Cafe',                     30,    20,             15),
    ('IKEA Restaurant',               40,    20,             15),
    ('IKEA Credit',                   10,    10,             15),
    ('Cookshop',                      30,    20,             17),
    ('Tableware',                     30,    20,             17),
    ('Home Textiles',                 30,    30,             17),
    ('Bedroom Textiles',              20,    20,             17),
    ('Rugs',                          20,    20,             17),
    ('Bathroom',                      30,    30,             17),
    ('Home Organisation',             40,    20,             17),
    ('Hallway',                       30,    20,             17),
    ('Lighting',                      20,    20,             17),
    ('Home Decoration',               30,    30,             17),
    ('Plants',                        20,    20,             17),
    ('Self-serve Furniture Area',    300,   200,             15),
    ('Discounts',                     50,    20,             16),
    ('Check-outs',                   100,    10,             15),
    ('IKEA Bistro',                   20,    20,             15),
    ('IKEA Swedish Food Market',      30,    20,             17),
    ('Exchanges & Returns',           10,    10,             15),
    ('Pick-up',                       15,    10,             15),
    ('Home Delivery Service',         10,    10,             15),
    ('Fabrics & Sewing',              20,    15,             17),
    ('IKEA Business',                 30,    30,             15),
    ('Exit',                          10,    10,             15);

INSERT INTO store_navigation
    (from_id, to_id) VALUES
    (      1,     2),
    (      2,     3),
    (      3,     4),
    (      4,     5),
    (      5,     6),
    (      6,     7),
    (      7,     8),
    (      8,     9),
    (      9,    10),
    (     10,    11),
    (     11,    12),
    (     12,    13),
    (     13,    14),
    (     14,    15),
    (     15,    16),
    (     16,    17),
    (     17,    18),
    (     18,    19),
    (     19,    20),
    (     20,    21),
    (     21,    22),
    (     22,    23),
    (     23,    24),
    (     25,    26),
    (     26,    27),
    (     27,    28),
    (     28,    29),
    (     29,    30),
    (     30,    31),
    (     31,    32),
    (     32,    33),
    (     33,    34),
    (      1,    11),
    (      3,     9),
    (      5,     8),
    (     13,    16),
    (     15,    22);

INSERT INTO item
    (name,          price, length, width, height, in_stock_storage, in_stock_shop, store_room_id) VALUES
    ('SVENSTA',      4699,    122,    73,     67, true,             true,                      2),
    ('BLÅHAJ',        999,    100,    30,     30, true,             true,                      9),
    ('FRIDLEV',      1999,     66,    29,    129, true,             false,                    19),
    ('BRIMNES',      8999,    117,    50,    190, true,             false,                     8),
    ('LILL',          299,    280,     1,    300, true,             true,                     15),
    ('SVALLET',       369,     16,    16,     35, true,             true,                     21),
    ('KLOKREN',       219,     25,    25,      5, false,            false,                    13),
    ('ISTAD',         159,     20,    15,      1, true,             true,                     13),
    ('UTRUSTNING',    899,     10,    10,     29, true,             true,                     13),
    ('SANELA',        499,     50,    50,      1, true,             false,                    16),
    ('ALSEDA',       2799,     60,    60,     18, true,             true,                     22),
    ('STEFAN',       1999,     49,    42,     90, true,             true,                     14),
    ('FULLSPÄCKAD',  1199,     28,    20,     27, false,            false,                    14),
    ('PILLEMARK',     799,     90,    50,      5, true,             false,                    17),
    ('BROGRUND',     1199,     19,    19,     58, true,             true,                     18),
    ('PINNIG',       6999,     90,    37,    193, false,            false,                    20),
    ('FEJKA',         279,      9,     9,     21, true,             true,                     22);

INSERT INTO item_part
    (name,            length, width, height, color,              material,   part_count, item_id) VALUES
    ('Armrest',           73,     2,     33, x'000000'::integer, 'wood',              2,       1),
    ('Mattress',         118,    70,     12, x'000000'::integer, 'textile',           1,       1),
    ('Pillow',           118,    38,     12, x'000000'::integer, 'textile',           1,       1),
    ('Plank 125x29',     125,    29,      2, x'FFFFFF'::integer, 'wood',              3,       3),
    ('Plank 66x29',       66,    29,      2, x'FFFFFF'::integer, 'wood',              5,       3),
    ('Plank 30x29',       30,    29,      2, x'FFFFFF'::integer, 'wood',              4,       3),
    ('Plank 188x113',    188,   113,      2, x'FFFFFF'::integer, 'wood',              1,       4),
    ('Plank 188x50',     188,    50,      2, x'FFFFFF'::integer, 'wood',              2,       4),
    ('Plank 176x48',     176,    48,      2, x'FFFFFF'::integer, 'wood',              1,       4),
    ('Plank 117x50',     117,    50,      2, x'FFFFFF'::integer, 'wood',              2,       4),
    ('Plank 74x48',       74,    48,      2, x'FFFFFF'::integer, 'wood',              1,       4),
    ('Plank 37x48',       48,    37,      2, x'FFFFFF'::integer, 'wood',              3,       4),
    ('Crossbar',          74,     5,      5, x'C0C0C0'::integer, 'metal',             1,       4),
    ('Door',             176,    39,      2, x'FFFFFF'::integer, 'wood',              3,       4),
    ('Crossbar',         280,     5,      5, x'C0C0C0'::integer, 'metal',             1,       5),
    ('Curtain',          140,     1,    300, x'FFFFFF'::integer, 'textile',           2,       5),
    ('Chair seat',        39,    36,      2, x'654321'::integer, 'wood',              1,      12),
    ('Bar 90cm',          90,     5,      5, x'654321'::integer, 'wood',              2,      12),
    ('Bar 45cm',          45,     5,      5, x'654321'::integer, 'wood',              2,      12),
    ('Bar 30cm',          30,     5,      5, x'654321'::integer, 'wood',              7,      12),
    ('Hinge',             40,     5,      1, x'333333'::integer, 'metal',             4,      13),
    ('Plank 28x20',       28,    20,      2, x'F8DFA1'::integer, 'wood',              2,      13),
    ('Bar 25cm',          25,     5,      5, x'F8DFA1'::integer, 'wood',              1,      13),
    ('Hinge',             58,     5,      1, x'C0C0C0'::integer, 'metal',             2,      15),
    ('Tray',              19,    19,      4, x'C0C0C0'::integer, 'metal',             3,      15),
    ('Pot',                9,     9,      9, x'333333'::integer, 'ceramics',          1,      17),
    ('Grass',              9,     9,     12, x'41980A'::integer, 'plastic',           1,      17);

INSERT INTO customer_order
    (order_time,            resolve_time,          customer_id, responsible_id) VALUES
    ('2020-11-05 14:38:01', '2020-11-08 09:20:00',           1,              4),
    ('2020-11-18 18:30:43', '2020-11-19 08:00:00',           3,              4),
    ('2020-12-10 07:15:02', NULL,                            3,              3),
    ('2020-12-09 12:33:08', NULL,                            4,              7);

INSERT INTO delivery_order
    (address,                                          delivery_time,         assembly_ordered, responsible_id, order_id) VALUES
    ('St. Petersburg, Nikolaya Rubtsova 11, apt. 389', '2020-11-07 19:00:00', false,                        13,        1),
    ('St. Petersburg, Novoizmaylovsky 16',             '2020-12-12 09:00:00', true,                         14,        3);

INSERT INTO order_content
    (order_id, item_id, item_count) VALUES
    (       1,       2,         50),
    (       2,       7,          3),
    (       2,       9,          1),
    (       2,      13,          1),
    (       3,       1,          1),
    (       3,       3,          1),
    (       4,      17,          3),
    (       4,       8,          2),
    (       4,       5,          2);
