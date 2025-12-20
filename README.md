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

## Project Flow

### Step 1: Start Services

**Start Zookeeper:**
```bash
cd /home/cloudera/kafka_2.11-0.10.2.2
nohup bin/zookeeper-server-start.sh config/zookeeper.properties > /tmp/zookeeper.log 2>&1 &
```

**Start Kafka:**
```bash
cd /home/cloudera/kafka_2.11-0.10.2.2
nohup bin/kafka-server-start.sh config/server.properties > /tmp/kafka.log 2>&1 &
```

**Create Topic:**
```bash
cd /home/cloudera/kafka_2.11-0.10.2.2
bin/kafka-topics.sh --create --topic project_stream --zookeeper localhost:2181 --partitions 1 --replication-factor 1
```

### Step 2: Build Project

```bash
mvn clean package
```

### Step 3: Prepare HDFS

```bash
hdfs dfs -mkdir -p /user/cloudera/project_stream_out
hdfs dfs -mkdir -p /user/cloudera/project_stream_ckpt
hdfs dfs -chmod 777 /user/cloudera/project_stream_out
hdfs dfs -chmod 777 /user/cloudera/project_stream_ckpt
```

### Step 4: Run Pipeline

**Terminal 1 - Producer:**
```bash
java -cp target/FinalProject-1.0.0-jar-with-dependencies.jar \
    cs523.FinalProject.ProjKafkaBitcoinProducer
```

**Terminal 2 - Spark Streaming:**
```bash
spark2-submit \
    --class cs523.FinalProject.ProjSparkSQLKafkaStream \
    --master local[*] \
    --packages org.apache.spark:spark-sql-kafka-0-10_2.11:2.4.0 \
    target/FinalProject-1.0.0-jar-with-dependencies.jar
```

**Note:** If `--packages` fails (no internet), use:
```bash
spark2-submit \
    --class cs523.FinalProject.ProjSparkSQLKafkaStream \
    --master local[*] \
    --jars /usr/lib/spark/jars/spark-sql-kafka-0-10_2.11-2.4.0.jar \
    target/FinalProject-1.0.0-jar-with-dependencies.jar
```

**Terminal 3 - Verify HDFS (after 2-3 minutes):**
```bash
hdfs dfs -ls /user/cloudera/project_stream_out
hdfs dfs -cat /user/cloudera/project_stream_out/part-* | head -10
```

**Terminal 4 - Hive Queries:**
```bash
hive -f hive/01_create_table.hql
hive -f hive/02_queries.hql
```

## Project Structure

```
.
├── src/main/java/cs523/FinalProject/
│   ├── ProjKafkaBitcoinProducer.java    # Kafka producer
│   ├── ProjSparkSQLKafkaStream.java     # Spark streaming consumer
│   └── ProjSparkSQLProcess.java         # Hive queries via Spark SQL
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

## Troubleshooting

**Kafka not starting:**
- Check Zookeeper: `netstat -tulpn | grep 2181` or `ss -tulpn | grep 2181`
- Check Kafka: `netstat -tulpn | grep 9092` or `ss -tulpn | grep 9092`
- Check logs: `/tmp/zookeeper.log`, `/tmp/kafka.log`

**Spark can't find Kafka connector:**
- If no internet, download jar manually and use `--jars` option
- Check Spark version: `spark2-submit --version`
- Common location: `/usr/lib/spark/jars/spark-sql-kafka-0-10_2.11-2.4.0.jar`

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
