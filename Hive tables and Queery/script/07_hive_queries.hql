SHOW TABLES;

SELECT * FROM bitcoin_parsed LIMIT 10;

SELECT symbol, COUNT(*) AS total_records
FROM bitcoin_parsed
GROUP BY symbol;

SELECT symbol, AVG(price_usd) AS avg_price_overall
FROM bitcoin_parsed
GROUP BY symbol;

SELECT *
FROM bitcoin_agg
ORDER BY window_start DESC
LIMIT 20;
