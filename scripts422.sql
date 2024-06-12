CREATE TABLE human
(
    id             INTEGER PRIMARY KEY NOT NULL,
    name           VARCHAR(50)         NOT NULL,
    age            INTEGER             NOT NULL,
    driver_license BOOLEAN DEFAULT FALSE,
    car_id         INTEGER REFERENCES car (id)
);

CREATE TABLE car
(
    id    INTEGER PRIMARY KEY NOT NULL,
    brand VARCHAR(100)        NOT NULL,
    model VARCHAR(10)         NOT NULL,
    price INTEGER             NOT NULL
);