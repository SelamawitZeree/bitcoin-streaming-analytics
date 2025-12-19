-- Analysis queries for Bitcoin streaming data
-- Run these after Spark Streaming has written data to HDFS

USE cs599;

-- Query 1: Total count of records
-- Shows how many windowed aggregations we have
SELECT COUNT(*) AS total_records 
FROM project_stream_ext;

-- Query 2: Statistics per symbol
-- Shows count, average price, and total trades per cryptocurrency symbol
SELECT 
  symbol,
  COUNT(*) AS window_count,
  AVG(avg_price) AS overall_avg_price,
  MIN(min_price) AS lowest_price,
  MAX(max_price) AS highest_price,
  SUM(trade_count) AS total_trades
FROM project_stream_ext
GROUP BY symbol
ORDER BY total_trades DESC;

-- Query 3: Top 10 symbols by maximum price
-- Shows which cryptocurrencies reached the highest prices
SELECT 
  symbol,
  MAX(max_price) AS highest_price_reached,
  COUNT(*) AS number_of_windows
FROM project_stream_ext
GROUP BY symbol
ORDER BY highest_price_reached DESC
LIMIT 10;

-- Bonus query: Recent windows (if you want to see latest data)
-- Uncomment to see the most recent time windows
-- SELECT * FROM project_stream_ext ORDER BY window_start DESC LIMIT 20;

