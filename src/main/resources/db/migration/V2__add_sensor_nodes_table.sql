CREATE TABLE sensor_nodes
(
    id       VARCHAR(255) NOT NULL,
    name     VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    CONSTRAINT pk_sensor_nodes PRIMARY KEY (id)
);