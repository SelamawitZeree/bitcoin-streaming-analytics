CS599 Final Project - Screenshot Checklist
==========================================

Required Screenshots (save in this folder):

1. kafka_topic_list.png
   Command: /home/cloudera/kafka_2.11-0.10.2.2/bin/kafka-topics.sh --list --zookeeper localhost:2181
   Should show: project_stream

2. kafka_topic_describe.png
   Command: /home/cloudera/kafka_2.11-0.10.2.2/bin/kafka-topics.sh --describe --topic project_stream --zookeeper localhost:2181
   Should show: topic details, partitions, replication

3. producer_running.png
   Terminal showing: ./scripts/04_produce_bitcoin_messages.sh
   Should show: "Sent: ..." messages with Bitcoin data

4. spark_streaming_running.png
   Terminal showing: ./scripts/05_run_spark_stream.sh
   Should show: Spark batches processing, console output with windowed data

5. hdfs_file_listing.png
   Command: hdfs dfs -ls /user/cloudera/project_stream_out
   Should show: part-*.csv files created by Spark

6. hdfs_sample_data.png
   Command: hdfs dfs -cat /user/cloudera/project_stream_out/part-* | head -5
   Should show: Sample CSV rows (NO header row)
   Format: window_start,window_end,symbol,avg_price,min_price,max_price,trade_count

7. hive_table_created.png
   Terminal showing: ./scripts/07_run_hive.sh (first part)
   Should show: "Table created successfully" or SHOW TABLES output

8. hive_query1_count.png
   Terminal showing: Query 1 result (total_records)
   Should show: COUNT(*) result

9. hive_query2_groupby.png
   Terminal showing: Query 2 result (group by symbol)
   Should show: symbol, window_count, overall_avg_price, total_trades

10. hive_query3_top10.png
    Terminal showing: Query 3 result (top 10 by max price)
    Should show: symbol, highest_price_reached, number_of_windows

Tips:
- Take screenshots while running the pipeline
- Ensure terminal output is clearly visible
- Include command prompts in screenshots
- Verify data format matches expected schema

