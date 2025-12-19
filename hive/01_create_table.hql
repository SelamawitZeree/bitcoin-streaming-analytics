-- Create Hive database and external table for Spark Streaming output
-- This table reads from HDFS location where Spark writes CSV files

CREATE DATABASE IF NOT EXISTS cs599;

USE cs599;

-- Drop table if exists (for testing)
DROP TABLE IF EXISTS project_stream_ext;

-- Create external table pointing to HDFS output directory
-- Spark Streaming writes CSV files here
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

-- Verify table was created
SHOW TABLES;

-- Show table structure
DESCRIBE project_stream_ext;

