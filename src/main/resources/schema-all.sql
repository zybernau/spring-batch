DROP TABLE stocks IF EXISTS;

CREATE TABLE stocks  (
    "stockName" "char"[] NOT NULL,
    "ftHigh" double precision,
    "ftLow" double precision,
    "buyPrice" double precision,
    "sellPrice" double precision,
    margin double precision,
    profitPercent double precision,
    CONSTRAINT stocks_pkey PRIMARY KEY ("stockName")
);