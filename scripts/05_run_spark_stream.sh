#!/bin/bash
# Run Spark Structured Streaming job
# Reads from Kafka topic and writes to HDFS

source $(dirname $0)/00_env.sh

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SPARK_SCRIPT="$SCRIPT_DIR/../src/kafka_to_hdfs.py"

if [ ! -f "$SPARK_SCRIPT" ]; then
    echo "ERROR: Spark script not found: $SPARK_SCRIPT"
    exit 1
fi

echo "Preparing HDFS directories..."

# Clean old output if it exists (optional - comment out if you want to keep old data)
# hdfs dfs -rm -r -f $HDFS_OUT $HDFS_CKPT

# Create directories
hdfs dfs -mkdir -p $HDFS_OUT
hdfs dfs -mkdir -p $HDFS_CKPT

echo "HDFS output directory: $HDFS_OUT"
echo "HDFS checkpoint directory: $HDFS_CKPT"
echo ""

# Check which spark-submit command exists
if command -v spark2-submit &> /dev/null; then
    SPARK_CMD=spark2-submit
    echo "Using spark2-submit"
elif command -v spark-submit &> /dev/null; then
    SPARK_CMD=spark-submit
    echo "Using spark-submit"
else
    echo "ERROR: Neither spark2-submit nor spark-submit found in PATH"
    exit 1
fi

echo ""
echo "Starting Spark Structured Streaming job..."
echo "This will run continuously. Press Ctrl+C to stop."
echo ""

# Try to run with Kafka packages
# If this fails, you may need to download the Kafka connector jar manually
$SPARK_CMD \
    --master local[2] \
    --packages org.apache.spark:spark-sql-kafka-0-10_2.11:2.4.0 \
    --conf spark.sql.streaming.checkpointLocation=$HDFS_CKPT \
    $SPARK_SCRIPT

# If the above fails due to package download issues, try this alternative:
# First download the jar manually, then use:
# $SPARK_CMD --master local[2] --jars /path/to/spark-sql-kafka-0-10_2.11-2.4.0.jar $SPARK_SCRIPT

