--------------------------------------------------------------------------------
-- Задание: получить список сотрудников с самым большим "пробегом" из комнаты в
-- комнату при сборке заказов.
--
-- Можно разбить задачу на две подзадачи:
--
-- A. Найти перемещения каждого сотрудника, собирающего заказ
--   1. Получить пронумерованный список посещений комнат сотрудником
--   2. Объединить соседние строки, чтобы получить перемещения
--
-- B. Найти минимальное время, которое занимает движение между любыми комнатами
--   1. Рекурсивно найти все возможные перемещения между комнатами
--   2. Выбрать среди них минимальные для каждой пары комнат
--
-- Объединение этих двух подзадач даст нам необходимые данные для решения
-- основной задачи.
--------------------------------------------------------------------------------
-- Каждому пункту подзадачи соответствует WITH-выражение в запросе.

WITH RECURSIVE 

-- A. Найти перемещения каждого сотрудника, собирающего заказ
--   1. Получить пронумерованный список посещений комнат сотрудником
--
-- Посещения пронумерованы так, как они указаны в таблице order_content -
-- предполагается, что именно в таком порядке их добавлял пользователь, и именно
-- в таком порядке работник будет собирать заказ.
employee_visits (employee_id, room_id, visit_id) AS (
    SELECT ord.responsible_id, it.store_room_id, row_number() OVER
            (PARTITION BY ord.responsible_id ORDER BY (ord.id, it.id)) - 1
        FROM order_content con
        JOIN customer_order ord ON con.order_id = ord.id
        JOIN item it ON con.item_id = it.id
),

--   2. Объединить соседние строки, чтобы получить перемещения
employee_moves (employee_id, from_id, to_id) AS (
    SELECT v1.employee_id, v1.room_id, v2.room_id
        FROM employee_visits v1 JOIN employee_visits v2
        ON v1.employee_id = v2.employee_id AND v1.visit_id + 1 = v2.visit_id
),


-- B. Найти минимальное время, которое занимает движение между любыми комнатами
--   0. Заменить односторонний граф store_navigation таким же двусторонним
--
-- С двусторонним графом гораздо проще будет выполнить следующий пункт.
store_nav_both (from_id, to_id) AS (
    SELECT from_id, to_id FROM store_navigation

    UNION ALL

    SELECT to_id, from_id FROM store_navigation
),

--   1. Рекурсивно найти все возможные перемещения между комнатами
--
-- Заполним таблицу начальными значениями - перемещения из комнаты в нее же
-- занимают 0 единиц времени. Применим поиск в ширину: при проходе в соседнюю
-- комнату увеличиваем длину пути на 1 и сохраняем новую комнату в массив
-- посещенных. Если попали в цикл (в массиве уже есть данная комната), дальше
-- не спускаемся.
paths (from_id, to_id, length, visited) AS (
    SELECT id, id, 0, array[id] FROM store_room

    UNION ALL

    SELECT p.from_id, n.to_id, p.length + 1, array_append(p.visited, n.to_id)
        FROM paths p JOIN store_nav_both n ON p.to_id = n.from_id
        WHERE NOT n.to_id = ANY(p.visited)
),

--   2. Выбрать среди них минимальные для каждой пары комнат
min_paths (from_id, to_id, min_length) AS (
    SELECT from_id, to_id, min(length) FROM paths GROUP BY (from_id, to_id)
)


-- Остается объединить две получившиеся таблицы из обеих подзадач и вычислить
-- сумму перемещений.
SELECT m.employee_id, sum(p.min_length) AS total_moves_length
    FROM employee_moves m JOIN min_paths p
    ON m.from_id = p.from_id AND m.to_id = p.to_id
    GROUP BY m.employee_id;
