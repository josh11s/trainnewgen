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

-- 2. Planned trips initialization (20 daily trips per corridor for the next 30 days)
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
        -- Paris Montparnasse -> Rennes (10 trains / day)
        ('NGT-8101', 'FRPAR', 'FRRNS', TIME '06:15:00', 88, 29.00, 180, 50),
        ('NGT-8103', 'FRPAR', 'FRRNS', TIME '07:30:00', 90, 39.00, 160, 45),
        ('NGT-8105', 'FRPAR', 'FRRNS', TIME '08:45:00', 89, 45.00, 200, 60),
        ('NGT-8107', 'FRPAR', 'FRRNS', TIME '10:00:00', 92, 35.00, 190, 55),
        ('NGT-8109', 'FRPAR', 'FRRNS', TIME '11:30:00', 91, 32.00, 175, 48),
        ('NGT-8111', 'FRPAR', 'FRRNS', TIME '13:15:00', 89, 36.00, 185, 52),
        ('NGT-8113', 'FRPAR', 'FRRNS', TIME '14:45:00', 90, 42.00, 170, 46),
        ('NGT-8115', 'FRPAR', 'FRRNS', TIME '16:30:00', 88, 49.00, 150, 40),
        ('NGT-8117', 'FRPAR', 'FRRNS', TIME '18:15:00', 93, 59.00, 120, 30),
        ('NGT-8119', 'FRPAR', 'FRRNS', TIME '20:00:00', 90, 39.00, 160, 45),

        -- Rennes -> Paris Montparnasse (10 trains / day)
        ('NGT-8102', 'FRRNS', 'FRPAR', TIME '06:00:00', 90, 29.00, 180, 50),
        ('NGT-8104', 'FRRNS', 'FRPAR', TIME '07:15:00', 89, 45.00, 170, 48),
        ('NGT-8106', 'FRRNS', 'FRPAR', TIME '08:30:00', 91, 49.00, 150, 40),
        ('NGT-8108', 'FRRNS', 'FRPAR', TIME '10:15:00', 90, 35.00, 190, 55),
        ('NGT-8110', 'FRRNS', 'FRPAR', TIME '12:00:00', 88, 32.00, 200, 60),
        ('NGT-8112', 'FRRNS', 'FRPAR', TIME '13:45:00', 92, 36.00, 175, 50),
        ('NGT-8114', 'FRRNS', 'FRPAR', TIME '15:30:00', 89, 42.00, 165, 45),
        ('NGT-8116', 'FRRNS', 'FRPAR', TIME '17:15:00', 93, 55.00, 130, 35),
        ('NGT-8118', 'FRRNS', 'FRPAR', TIME '19:00:00', 90, 45.00, 155, 42),
        ('NGT-8120', 'FRRNS', 'FRPAR', TIME '20:45:00', 91, 35.00, 180, 50),

        -- Paris Montparnasse -> Nantes (10 trains / day)
        ('NGT-8201', 'FRPAR', 'FRNTE', TIME '06:30:00', 120, 35.00, 190, 50),
        ('NGT-8203', 'FRPAR', 'FRNTE', TIME '08:00:00', 118, 49.00, 170, 45),
        ('NGT-8205', 'FRPAR', 'FRNTE', TIME '09:30:00', 122, 42.00, 180, 48),
        ('NGT-8207', 'FRPAR', 'FRNTE', TIME '11:00:00', 119, 38.00, 200, 55),
        ('NGT-8209', 'FRPAR', 'FRNTE', TIME '12:45:00', 121, 35.00, 210, 60),
        ('NGT-8211', 'FRPAR', 'FRNTE', TIME '14:30:00', 118, 39.00, 195, 52),
        ('NGT-8213', 'FRPAR', 'FRNTE', TIME '16:15:00', 123, 49.00, 160, 40),
        ('NGT-8215', 'FRPAR', 'FRNTE', TIME '17:45:00', 125, 59.00, 110, 25),
        ('NGT-8217', 'FRPAR', 'FRNTE', TIME '19:15:00', 120, 45.00, 175, 45),
        ('NGT-8219', 'FRPAR', 'FRNTE', TIME '21:00:00', 119, 35.00, 190, 50),

        -- Nantes -> Paris Montparnasse (10 trains / day)
        ('NGT-8202', 'FRNTE', 'FRPAR', TIME '06:15:00', 120, 35.00, 185, 48),
        ('NGT-8204', 'FRNTE', 'FRPAR', TIME '07:45:00', 119, 49.00, 165, 42),
        ('NGT-8206', 'FRNTE', 'FRPAR', TIME '09:15:00', 122, 42.00, 180, 50),
        ('NGT-8208', 'FRNTE', 'FRPAR', TIME '11:00:00', 120, 38.00, 200, 55),
        ('NGT-8210', 'FRNTE', 'FRPAR', TIME '12:30:00', 118, 35.00, 210, 58),
        ('NGT-8212', 'FRNTE', 'FRPAR', TIME '14:15:00', 121, 39.00, 190, 50),
        ('NGT-8214', 'FRNTE', 'FRPAR', TIME '16:00:00', 124, 48.00, 150, 38),
        ('NGT-8216', 'FRNTE', 'FRPAR', TIME '17:30:00', 122, 59.00, 120, 30),
        ('NGT-8218', 'FRNTE', 'FRPAR', TIME '19:15:00', 120, 45.00, 160, 45),
        ('NGT-8220', 'FRNTE', 'FRPAR', TIME '20:45:00', 119, 35.00, 185, 50)
) AS t(train_number, dep_code, arr_code, dep_time, duration, price, standard_seats, first_seats)
CROSS JOIN generate_series(0, 30) AS d(day_offset)
JOIN stations dep ON dep.code = t.dep_code
JOIN stations arr ON arr.code = t.arr_code
ON CONFLICT (train_number, departure_time) DO NOTHING;
