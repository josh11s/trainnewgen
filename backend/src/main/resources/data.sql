-- ==========================================================
-- NewGen TGV - Database Initialization Script (data.sql)
-- Executed automatically by Spring Boot at startup
-- ==========================================================

-- 1. Stations initialization
INSERT INTO stations (code, name, city)
VALUES 
    ('FRPAR', 'Paris Montparnasse', 'Paris'),
    ('FRRNS', 'Rennes', 'Rennes'),
    ('FRNTE', 'Nantes', 'Nantes')
ON CONFLICT (code) DO NOTHING;

-- 2. Planned trips initialization (Daily schedules for the next 30 days)
INSERT INTO trips (
    train_number, 
    departure_station_id, 
    arrival_station_id, 
    departure_time, 
    arrival_time, 
    duration_minutes, 
    base_price, 
    standard_seats_available, 
    first_seats_available
)
SELECT 
    t.train_number,
    dep.id AS departure_station_id,
    arr.id AS arrival_station_id,
    (CURRENT_DATE + (d.day_offset * INTERVAL '1 day') + t.dep_time)::timestamp AS departure_time,
    (CURRENT_DATE + (d.day_offset * INTERVAL '1 day') + t.dep_time + (t.duration * INTERVAL '1 minute'))::timestamp AS arrival_time,
    t.duration AS duration_minutes,
    t.price AS base_price,
    t.standard_seats AS standard_seats_available,
    t.first_seats AS first_seats_available
FROM (
    VALUES 
        -- Paris Montparnasse <-> Rennes
        ('NGT-8101', 'FRPAR', 'FRRNS', TIME '07:15:00', 90, 35.00, 180, 50),
        ('NGT-8103', 'FRPAR', 'FRRNS', TIME '09:45:00', 92, 42.00, 160, 45),
        ('NGT-8105', 'FRPAR', 'FRRNS', TIME '14:00:00', 88, 29.00, 200, 60),
        ('NGT-8107', 'FRPAR', 'FRRNS', TIME '18:30:00', 95, 55.00, 120, 30),

        ('NGT-8102', 'FRRNS', 'FRPAR', TIME '06:45:00', 90, 35.00, 170, 48),
        ('NGT-8104', 'FRRNS', 'FRPAR', TIME '11:15:00', 89, 39.00, 190, 55),
        ('NGT-8106', 'FRRNS', 'FRPAR', TIME '16:20:00', 93, 49.00, 140, 40),
        ('NGT-8108', 'FRRNS', 'FRPAR', TIME '19:50:00', 91, 45.00, 150, 45),

        -- Paris Montparnasse <-> Nantes
        ('NGT-8201', 'FRPAR', 'FRNTE', TIME '07:30:00', 120, 39.00, 190, 50),
        ('NGT-8203', 'FRPAR', 'FRNTE', TIME '12:10:00', 118, 32.00, 210, 65),
        ('NGT-8205', 'FRPAR', 'FRNTE', TIME '17:45:00', 125, 59.00, 110, 25),

        ('NGT-8202', 'FRNTE', 'FRPAR', TIME '07:00:00', 120, 39.00, 175, 45),
        ('NGT-8204', 'FRNTE', 'FRPAR', TIME '13:30:00', 119, 35.00, 200, 58),
        ('NGT-8206', 'FRNTE', 'FRPAR', TIME '18:15:00', 122, 54.00, 130, 32)
) AS t(train_number, dep_code, arr_code, dep_time, duration, price, standard_seats, first_seats)
CROSS JOIN generate_series(0, 30) AS d(day_offset)
JOIN stations dep ON dep.code = t.dep_code
JOIN stations arr ON arr.code = t.arr_code
ON CONFLICT (train_number, departure_time) DO NOTHING;
