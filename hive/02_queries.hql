USE cs599;

SELECT COUNT(*) AS total_records 
FROM project_stream_ext;

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

SELECT 
  symbol,
  MAX(max_price) AS highest_price_reached,
  COUNT(*) AS number_of_windows
FROM project_stream_ext
GROUP BY symbol
ORDER BY highest_price_reached DESC
LIMIT 10;
