# CS599 Final Project - Bitcoin Streaming Analytics

Real-time Bitcoin cryptocurrency price streaming pipeline using Kafka, Spark Structured Streaming, and Hive.

**Pipeline:** Bitcoin Data → Kafka → Spark Streaming → HDFS → Hive

## Architecture

1. **Kafka Producer** - Generates Bitcoin price data (BTC, ETH, BNB, ADA, SOL)
2. **Kafka Topic** - `project_stream` receives streaming messages
3. **Spark Streaming** - Processes data with 1-minute windowed aggregations
4. **HDFS** - Stores aggregated results as CSV files (NO header row)
5. **Hive** - External table queries the HDFS output

## Locked Contract

- **Kafka home:** `/home/cloudera/kafka_2.11-0.10.2.2`
- **Zookeeper:** `localhost:2181`
- **Broker:** `localhost:9092`
- **Topic:** `project_stream`
- **HDFS output:** `/user/cloudera/project_stream_out`
- **HDFS checkpoint:** `/user/cloudera/project_stream_ckpt`
- **Output format:** CSV, comma-separated, **NO header row**
- **Hive DB:** `cs599`
- **Hive table:** `project_stream_ext`
- **Output columns (7):** `window_start, window_end, symbol, avg_price, min_price, max_price, trade_count`

## Requirements

- Cloudera QuickStart VM
- Kafka 0.10.2.2 (at `/home/cloudera/kafka_2.11-0.10.2.2`)
- Spark 2.x
- Hive
- HDFS
- Maven (for building)
- Java 8

## Quick Start

### Step 0: Health Check
```bash
chmod +x scripts/*.sh
./scripts/99_healthcheck.sh
```

### Step 1: Start Services
```bash
source scripts/00_env.sh
./scripts/01_start_zookeeper.sh
./scripts/02_start_kafka.sh
./scripts/03_create_topic.sh
```

### Step 2: Run Pipeline

**Terminal 1 - Producer (continuous):**
```bash
./scripts/04_produce_bitcoin_messages.sh
```

**Terminal 2 - Spark Streaming:**
```bash
./scripts/05_run_spark_stream.sh
```

**Wait 2-3 minutes for Spark to process windows, then:**

**Terminal 3 - Verify HDFS:**
```bash
./scripts/06_verify_hdfs.sh
```

**Terminal 4 - Hive Queries:**
```bash
./scripts/07_run_hive.sh
```

## Project Structure

```
.
├── src/
│   ├── ProjKafkaBitcoinProducer.java    # Kafka producer
│   ├── ProjSparkSQLKafkaStream.java     # Spark streaming consumer
│   └── ProjSparkSQLProcess.java         # Hive queries via Spark SQL
├── scripts/
│   ├── 00_env.sh                        # Environment setup
│   ├── 01_start_zookeeper.sh
│   ├── 02_start_kafka.sh
│   ├── 03_create_topic.sh
│   ├── 04_produce_bitcoin_messages.sh
│   ├── 05_run_spark_stream.sh
│   ├── 06_verify_hdfs.sh
│   ├── 07_run_hive.sh
│   └── 99_healthcheck.sh                # Environment check
├── hive/
│   ├── 01_create_table.hql              # Create Hive table
│   └── 02_queries.hql                   # Analysis queries
├── sample_data/
│   └── bitcoin_sample.csv              # Sample input data
├── evidence/
│   └── final/                           # Screenshots folder
├── pom.xml                              # Maven build file
└── README.md
```

## Data Format

**Input (Kafka messages):**
```
2025-12-19T15:00:00Z,BTC,43210.12,850000000000,123456789.0
2025-12-19T15:00:05Z,ETH,2450.50,295000000000,45678901.2
```

**Output (HDFS/Hive) - NO header row:**
```
2025-12-19 15:00:00,2025-12-19 15:01:00,BTC,43215.50,43210.12,43220.00,5
2025-12-19 15:00:00,2025-12-19 15:01:00,ETH,2451.20,2450.50,2452.80,3
```

## Manual Run Commands

### Build
```bash
mvn clean package
```

### Producer
```bash
java -cp target/FinalProject-1.0.0-jar-with-dependencies.jar \
    cs523.FinalProject.ProjKafkaBitcoinProducer
```

### Spark Streaming
```bash
spark2-submit \
    --class cs523.FinalProject.ProjSparkSQLKafkaStream \
    --master local[*] \
    --packages org.apache.spark:spark-sql-kafka-0-10_2.11:2.4.0 \
    target/FinalProject-1.0.0-jar-with-dependencies.jar
```

**Note:** If `--packages` fails (no internet), manually add the Kafka connector jar:
- Download: `spark-sql-kafka-0-10_2.11-2.4.0.jar`
- Use: `--jars /path/to/spark-sql-kafka-0-10_2.11-2.4.0.jar`

### Spark SQL Hive
```bash
spark2-submit \
    --class cs523.FinalProject.ProjSparkSQLProcess \
    --master local[*] \
    target/FinalProject-1.0.0-jar-with-dependencies.jar
```

## Screenshots Checklist

Save screenshots in `evidence/final/`:

1. **kafka_topic_list.png** - Output from `kafka-topics.sh --list`
2. **kafka_topic_describe.png** - Output from `kafka-topics.sh --describe --topic project_stream`
3. **producer_running.png** - Terminal showing producer sending messages
4. **spark_streaming_running.png** - Spark job console output (showing batches)
5. **hdfs_file_listing.png** - Output from `hdfs dfs -ls /user/cloudera/project_stream_out`
6. **hdfs_sample_data.png** - Sample CSV rows from HDFS (`hdfs dfs -cat ... | head -5`)
7. **hive_table_created.png** - Hive table creation success message
8. **hive_query1_count.png** - Total count query result
9. **hive_query2_groupby.png** - Group by symbol query result
10. **hive_query3_top10.png** - Top 10 symbols query result

## Demo Video Flow (8-10 minutes)

1. **Introduction (30s)** - Project overview, architecture diagram
2. **Environment Setup (1m)** - Show healthcheck script, verify ports
3. **Start Services (1m)** - Zookeeper, Kafka, create topic (show topic list)
4. **Start Producer (30s)** - Show producer sending messages
5. **Start Spark Streaming (1m)** - Show Spark job starting, processing batches
6. **Verify HDFS (1m)** - Show HDFS files, sample data (NO header row)
7. **Hive Integration (2m)** - Create table, run queries, show results
8. **Summary (30s)** - Pipeline flow, key features

## Troubleshooting

**Kafka not starting:**
- Check Zookeeper: `netstat -tulpn | grep 2181` or `ss -tulpn | grep 2181`
- Check Kafka: `netstat -tulpn | grep 9092` or `ss -tulpn | grep 9092`
- Check logs: `/tmp/zookeeper.log`, `/tmp/kafka.log`

**Spark can't find Kafka connector:**
- Run healthcheck: `./scripts/99_healthcheck.sh`
- If no internet, download jar manually and use `--jars` option
- Check Spark version: `spark2-submit --version`

**Hive table empty:**
- Verify HDFS has files: `hdfs dfs -ls /user/cloudera/project_stream_out`
- Wait for Spark to write at least one batch (checkpoint directory)
- Check file format matches table schema (CSV, comma-separated, NO header)

**Build errors:**
- Check Java: `java -version` (should be 1.8)
- Check Maven: `mvn --version`
- Try: `mvn clean install -U`

## Authors

Selamawit Zeree
Yordanos [Last Name]

Course: CS599 Big Data Technology
Professor: Mrudula Mukadam
Maharishi International University

