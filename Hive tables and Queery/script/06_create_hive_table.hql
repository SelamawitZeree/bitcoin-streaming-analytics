DROP TABLE IF EXISTS bitcoin_parsed;
CREATE EXTERNAL TABLE bitcoin_parsed (
  event_time STRING,
  symbol STRING,
  price_usd DOUBLE,
  marketcap_usd DOUBLE,
  volume24h DOUBLE
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS TEXTFILE
LOCATION '/user/cloudera/finalproject/bitcoin_parsed';

DROP TABLE IF EXISTS bitcoin_agg;
CREATE EXTERNAL TABLE bitcoin_agg (
  window_start STRING,
  window_end STRING,
  symbol STRING,
  avg_price DOUBLE,
  min_price DOUBLE,
  max_price DOUBLE,
  records BIGINT
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS TEXTFILE
LOCATION '/user/cloudera/finalproject/bitcoin_agg';
