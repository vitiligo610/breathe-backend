ALTER TABLE sensor_nodes
    ALTER COLUMN active SET NOT NULL;

ALTER TABLE sensor_readings
    ALTER COLUMN humidity DROP NOT NULL;

ALTER TABLE sensor_readings
    ALTER COLUMN temperature DROP NOT NULL;

ALTER TABLE sensor_readings
    DROP COLUMN timestamp;

ALTER TABLE sensor_readings
    ADD timestamp BIGINT DEFAULT EXTRACT(EPOCH FROM NOW());