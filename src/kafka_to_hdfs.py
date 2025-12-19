"""
Spark Structured Streaming Job
Reads Bitcoin data from Kafka topic and writes aggregated results to HDFS

This script:
1. Reads from Kafka topic 'project_stream'
2. Parses CSV messages (timestamp, symbol, price, market_cap, volume)
3. Performs windowed aggregations (1-minute windows)
4. Writes results to HDFS as CSV files
"""

from pyspark.sql import SparkSession
from pyspark.sql.functions import col, split, avg, min as spark_min, max as spark_max, count, window, to_timestamp
from pyspark.sql.types import StructType, StructField, StringType, DoubleType

# Configuration
KAFKA_BOOTSTRAP = "localhost:9092"
KAFKA_TOPIC = "project_stream"
HDFS_OUTPUT = "hdfs:///user/cloudera/project_stream_out"
HDFS_CHECKPOINT = "hdfs:///user/cloudera/project_stream_ckpt"

def main():
    print("Starting Spark Structured Streaming job...")
    print(f"Kafka broker: {KAFKA_BOOTSTRAP}")
    print(f"Kafka topic: {KAFKA_TOPIC}")
    print(f"HDFS output: {HDFS_OUTPUT}")
    print(f"HDFS checkpoint: {HDFS_CHECKPOINT}")
    
    # Create Spark session
    spark = SparkSession.builder \
        .appName("CS599_Bitcoin_Streaming") \
        .config("spark.sql.streaming.checkpointLocation", HDFS_CHECKPOINT) \
        .getOrCreate()
    
    spark.sparkContext.setLogLevel("WARN")
    
    print("\nReading from Kafka...")
    
    # Read from Kafka
    kafka_df = spark.readStream \
        .format("kafka") \
        .option("kafka.bootstrap.servers", KAFKA_BOOTSTRAP) \
        .option("subscribe", KAFKA_TOPIC) \
        .option("startingOffsets", "latest") \
        .load()
    
    # Extract value as string
    value_df = kafka_df.selectExpr("CAST(value AS STRING) AS value")
    
    # Parse CSV: timestamp,symbol,price,market_cap,volume
    split_col = split(col("value"), ",")
    
    parsed_df = value_df.select(
        split_col.getItem(0).alias("timestamp"),
        split_col.getItem(1).alias("symbol"),
        split_col.getItem(2).cast("double").alias("price"),
        split_col.getItem(3).cast("double").alias("market_cap"),
        split_col.getItem(4).cast("double").alias("volume")
    ).filter(
        col("symbol").isNotNull() & 
        col("price").isNotNull()
    )
    
    print("Parsing messages and creating windows...")
    
    # Try to parse timestamp, if fails use processing time
    try:
        # Try parsing ISO timestamp format
        windowed_df = parsed_df.withColumn(
            "event_time",
            to_timestamp(col("timestamp"), "yyyy-MM-dd'T'HH:mm:ss'Z'")
        )
        
        # Create 1-minute windows
        windowed = windowed_df \
            .withWatermark("event_time", "1 minute") \
            .groupBy(
                window(col("event_time"), "1 minute"),
                col("symbol")
            ) \
            .agg(
                avg("price").alias("avg_price"),
                spark_min("price").alias("min_price"),
                spark_max("price").alias("max_price"),
                count("*").alias("trade_count")
            ) \
            .select(
                col("window.start").cast("string").alias("window_start"),
                col("window.end").cast("string").alias("window_end"),
                col("symbol"),
                col("avg_price"),
                col("min_price"),
                col("max_price"),
                col("trade_count")
            )
    except Exception as e:
        print(f"Warning: Timestamp parsing failed, using processing time: {e}")
        # Fallback: use processing time
        from pyspark.sql.functions import current_timestamp
        windowed_df = parsed_df.withColumn("event_time", current_timestamp())
        
        windowed = windowed_df \
            .withWatermark("event_time", "1 minute") \
            .groupBy(
                window(col("event_time"), "1 minute"),
                col("symbol")
            ) \
            .agg(
                avg("price").alias("avg_price"),
                spark_min("price").alias("min_price"),
                spark_max("price").alias("max_price"),
                count("*").alias("trade_count")
            ) \
            .select(
                col("window.start").cast("string").alias("window_start"),
                col("window.end").cast("string").alias("window_end"),
                col("symbol"),
                col("avg_price"),
                col("min_price"),
                col("max_price"),
                col("trade_count")
            )
    
    print("Writing to HDFS...")
    print("Output columns: window_start, window_end, symbol, avg_price, min_price, max_price, trade_count")
    
    # Write to HDFS as CSV
    query = windowed.writeStream \
        .format("csv") \
        .option("path", HDFS_OUTPUT) \
        .option("checkpointLocation", HDFS_CHECKPOINT) \
        .option("header", "false") \
        .outputMode("append") \
        .trigger(processingTime='10 seconds') \
        .start()
    
    print("\nSpark Streaming job is running...")
    print("Waiting for data from Kafka topic...")
    print("Press Ctrl+C to stop\n")
    
    # Show progress in console (optional)
    query.awaitTermination()

if __name__ == "__main__":
    main()

