CS599 Final Project - Bitcoin Streaming Analytics

Project Overview

This project implements a real-time streaming analytics pipeline for Bitcoin cryptocurrency data using Apache Kafka, Spark Structured Streaming, and Apache Hive. The pipeline ingests Bitcoin price data through Kafka, processes it using Spark Streaming with windowed aggregations, stores results in HDFS, and makes them queryable through Hive.

Architecture

The data flows through the following components:

Bitcoin Data → Kafka Producer → Kafka Topic (project_stream) → Spark Structured Streaming → HDFS → Hive External Table

Part 1: Spark Streaming Project (6 points)
- Data ingestion using Kafka (real-time streaming source)
- Spark Structured Streaming processes Bitcoin messages
- Windowed aggregations (1-minute windows) grouped by cryptocurrency symbol
- Results written to HDFS as CSV files

Part 2: Hive Integration (2 points)
- External Hive table created on HDFS output location
- Table automatically reads CSV files written by Spark Streaming
- Analysis queries for insights on Bitcoin trading data

Part 3: Demo Video (2 points)
- Video link: [TO BE ADDED - will upload to Microsoft Streams]
- Shows complete pipeline integration
- Duration: Up to 15 minutes

Environment Setup

This project runs on Cloudera QuickStart VM with the following components:

- Apache Kafka 0.10.2.2 (installed at /home/cloudera/kafka_2.11-0.10.2.2)
- Apache Spark 2.x (spark2-submit or spark-submit available)
- Apache Hive (for querying results)
- HDFS (for storing streaming output)

Prerequisites

Before running, ensure:
1. Kafka is installed and KAFKA_HOME is set to /home/cloudera/kafka_2.11-0.10.2.2
2. Zookeeper and Kafka broker can be started
3. Spark is available (check with: spark-submit --version or spark2-submit --version)
4. Hive is installed and accessible
5. HDFS is running and accessible

How to Run (Step by Step)

Follow these steps in order. Open multiple terminal windows as needed.

Step 1: Set Environment Variables

In your first terminal, navigate to the project directory and source the environment script:

cd /path/to/project
source scripts/00_env.sh

This sets all necessary environment variables (KAFKA_HOME, broker addresses, topic name, HDFS paths).

Step 2: Start Zookeeper

Run the Zookeeper startup script:

./scripts/01_start_zookeeper.sh

This script checks if Zookeeper is already running on port 2181. If not, it starts it in the background. You should see a message confirming Zookeeper is running.

If you see "Address already in use", Zookeeper is already running from a previous session - that's fine, you can continue.

Step 3: Start Kafka Broker

In the same or a new terminal:

./scripts/02_start_kafka.sh

This starts the Kafka broker on port 9092. Wait a few seconds for it to fully start. The script will verify it's listening on port 9092.

Step 4: Create Kafka Topic

Create the topic that will receive Bitcoin messages:

./scripts/03_create_topic.sh

This creates the topic "project_stream" if it doesn't exist. The script is idempotent - safe to run multiple times. You should see the topic listed when it completes.

Step 5: Send Bitcoin Data to Kafka

Open a new terminal and run the producer script. This reads from sample_data/bitcoin_sample.csv and sends messages to Kafka:

./scripts/04_produce_bitcoin_messages.sh

This script sends messages one by one with a 1-second delay to simulate real-time streaming. You can let it run, or press Ctrl+C after a few messages if you just want to test.

To verify messages are in Kafka, you can open another terminal and run:

cd $KAFKA_HOME
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic project_stream --from-beginning --max-messages 5

Step 6: Run Spark Streaming Job

This is the main processing step. Open a NEW terminal (keep the producer running if you want continuous data):

./scripts/05_run_spark_stream.sh

This script:
- Prepares HDFS directories for output and checkpointing
- Starts the Spark Structured Streaming job
- The job reads from Kafka topic, processes messages, and writes to HDFS

The job runs continuously. You should see Spark logs showing it's processing batches. Let it run for at least 30-60 seconds to process some data.

IMPORTANT: Keep this terminal open. The Spark job must keep running to process streaming data.

If you get an error about missing Kafka connector packages, the script tries to download them automatically. If that fails, you may need to manually download the spark-sql-kafka jar and modify the script to use --jars instead of --packages.

Step 7: Verify HDFS Output

Open another terminal and check what Spark wrote to HDFS:

./scripts/06_verify_hdfs.sh

This shows:
- Files in the HDFS output directory
- Sample data from the output files
- Checkpoint directory status

You should see CSV files with 7 columns: window_start, window_end, symbol, avg_price, min_price, max_price, trade_count

Step 8: Create Hive Table and Run Queries

Now create the Hive external table and run analysis queries:

./scripts/07_run_hive.sh

This script:
- Creates the cs599 database (if not exists)
- Creates external table pointing to HDFS output
- Runs three analysis queries:
  1. Total count of records
  2. Statistics per symbol (group by)
  3. Top 10 symbols by maximum price

You should see query results printed to the console. Take screenshots of these results for your submission.

Manual Verification Commands

If you want to check things manually:

Check Kafka topic exists:
cd $KAFKA_HOME
bin/kafka-topics.sh --list --zookeeper localhost:2181

Check HDFS output:
hdfs dfs -ls /user/cloudera/project_stream_out
hdfs dfs -cat /user/cloudera/project_stream_out/part-* | head -n 10

Check Hive table:
hive -e "USE cs599; SELECT COUNT(*) FROM project_stream_ext;"

Project Structure

.
├── scripts/              Shell scripts to run the pipeline
│   ├── 00_env.sh        Environment variables
│   ├── 01_start_zookeeper.sh
│   ├── 02_start_kafka.sh
│   ├── 03_create_topic.sh
│   ├── 04_produce_bitcoin_messages.sh
│   ├── 05_run_spark_stream.sh
│   ├── 06_verify_hdfs.sh
│   └── 07_run_hive.sh
├── src/                  Source code
│   └── kafka_to_hdfs.py  Spark Structured Streaming job
├── hive/                 Hive SQL files
│   ├── 01_create_table.hql
│   └── 02_queries.hql
├── sample_data/          Sample input data
│   └── bitcoin_sample.csv
├── evidence/             Screenshots folder (create this)
└── README.md            This file

Output Locations

- HDFS Output: /user/cloudera/project_stream_out
- HDFS Checkpoint: /user/cloudera/project_stream_ckpt
- Hive Database: cs599
- Hive Table: project_stream_ext

Screenshots Required for Submission

Take screenshots of:

1. Kafka topic list (kafka-topics.sh --list output)
2. Producer sending messages (terminal showing messages being sent)
3. Spark streaming job running (Spark console output showing batches processed)
4. HDFS output files (hdfs dfs -ls output showing part files)
5. Sample HDFS data (hdfs dfs -cat showing actual CSV rows)
6. Hive table creation success
7. Hive Query 1 result (total count)
8. Hive Query 2 result (group by symbol)
9. Hive Query 3 result (top 10 by max price)

Store screenshots in the evidence/ folder.

Troubleshooting

Problem: Zookeeper/Kafka ports already in use
Solution: Services are already running from a previous session. You can either:
- Use the existing services (skip start scripts)
- Stop them first: pkill -f zookeeper; pkill -f kafka
- Then restart with the scripts

Problem: Spark Kafka connector not found
Solution: The script tries to download packages automatically. If that fails:
1. Check internet connection in VM
2. Manually download spark-sql-kafka jar for your Spark version
3. Modify 05_run_spark_stream.sh to use --jars /path/to/jar instead of --packages

Problem: Hive table is empty
Solution:
1. Verify Spark Streaming job has written files: hdfs dfs -ls /user/cloudera/project_stream_out
2. Check file format: hdfs dfs -cat /user/cloudera/project_stream_out/part-* | head
3. Verify table location: hive -e "DESCRIBE EXTENDED cs599.project_stream_ext;" | grep location
4. Make sure table location matches HDFS output path exactly

Problem: No data in Kafka topic
Solution:
1. Make sure producer script is running (04_produce_bitcoin_messages.sh)
2. Check topic exists: kafka-topics.sh --list --zookeeper localhost:2181
3. Test consumer: kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic project_stream --from-beginning

Problem: Spark job not processing data
Solution:
1. Verify Kafka is running: netstat -tulpn | grep 9092
2. Check Spark logs for errors
3. Make sure producer is sending messages
4. Wait at least 30-60 seconds for first batch to process

Demo Video

Video will be uploaded to Microsoft Streams and link added here.

The video demonstrates:
- Complete pipeline flow (Kafka → Spark → HDFS → Hive)
- Running each script and showing outputs
- Hive query results
- Integration of all components

Submission Checklist

Before submitting, ensure GitHub repo contains:

✓ All source files (src/kafka_to_hdfs.py)
✓ Shell script files (all scripts in scripts/ folder)
✓ Sample input data (sample_data/bitcoin_sample.csv)
✓ Screenshots of outputs (evidence/ folder)
✓ README file (this file)
✓ Link to demo video (add when ready)

Submit only the GitHub link on Sakai.

Authors

Selamawit Zeree
Yordanos [Last Name]

Course: CS599 Big Data Technology
Professor: Mrudula Mukadam
Maharishi International University

