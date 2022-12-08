--DROP TABLE stocks IF EXISTS;
--
--CREATE TABLE stocks  (
--    "stockName" "char"[] NOT NULL,
--    "ftHigh" double precision,
--    "ftLow" double precision,
--    "buyPrice" double precision,
--    "sellPrice" double precision,
--    margin double precision,
--    profitPercent double precision,
--    last_upd timestamp without time zone,
--    CONSTRAINT stocks_pkey PRIMARY KEY ("stockName")
--);

DROP TABLE people IF EXISTS;

CREATE TABLE people  (
    person_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    first_name VARCHAR(20),
    last_name VARCHAR(20),
    pipry VARCHAR(10) DEFAULT 'madness'
);
