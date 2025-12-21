CREATE DATABASE IF NOT EXISTS cs599;

USE cs599;

DROP TABLE IF EXISTS project_stream_ext;

CREATE EXTERNAL TABLE project_stream_ext (
  window_start   STRING,
  window_end     STRING,
  symbol         STRING,
  avg_price      DOUBLE,
  min_price      DOUBLE,
  max_price      DOUBLE,
  trade_count    INT
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE
LOCATION '/user/cloudera/project_stream_out';

SHOW TABLES;

DESCRIBE project_stream_ext;
