package cs523.FinalProject;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.streaming.Trigger;
import org.apache.spark.sql.types.DataTypes;

public class ProjSparkSQLKafkaStream {
	
	public static void main(String[] args) throws StreamingQueryException {

		Logger.getLogger("org").setLevel(Level.OFF);

		SparkSession spark = SparkSession.builder()
				.appName("Spark Kafka Bitcoin Streaming")
				.master("local[*]")
				.getOrCreate();
		
		Dataset<Row> ds = spark.readStream()
				.format("kafka")
				.option("failOnDataLoss", "false")
				.option("kafka.bootstrap.servers", "localhost:9092")
				.option("subscribe", "project_stream")
				.option("startingOffsets", "latest")
				.load();

		Dataset<Row> lines = ds.selectExpr("CAST(value AS STRING)");
		System.out.println("Reading from Kafka topic: project_stream");
		
		Dataset<Row> dataAsSchema = lines
                .selectExpr("value",
                        "split(value,',')[0] as timestamp",
                        "split(value,',')[1] as symbol",
                        "split(value,',')[2] as price",
                        "split(value,',')[3] as market_cap",
                        "split(value,',')[4] as volume")
                .drop("value");
		
		dataAsSchema = dataAsSchema
		                    .withColumn("timestamp", functions.regexp_replace(functions.col("timestamp"), " ", ""))
		                    .withColumn("symbol", functions.regexp_replace(functions.col("symbol"), " ", ""))
		                    .withColumn("price", functions.regexp_replace(functions.col("price"), " ", ""))
		                    .withColumn("market_cap", functions.regexp_replace(functions.col("market_cap"), " ", ""))
		                    .withColumn("volume", functions.regexp_replace(functions.col("volume"), " ", ""));

		dataAsSchema = dataAsSchema
		                    .withColumn("timestamp", functions.col("timestamp").cast(DataTypes.StringType))
		                    .withColumn("symbol", functions.col("symbol").cast(DataTypes.StringType))
		                    .withColumn("price", functions.col("price").cast(DataTypes.DoubleType))
		                    .withColumn("market_cap", functions.col("market_cap").cast(DataTypes.DoubleType))
		                    .withColumn("volume", functions.col("volume").cast(DataTypes.DoubleType));
		
		Dataset<Row> withTimestamp = dataAsSchema
		                    .withColumn("event_time", functions.to_timestamp(functions.col("timestamp"), "yyyy-MM-dd'T'HH:mm:ss'Z'"))
		                    .withColumn("event_time", functions.coalesce(
		                    		functions.to_timestamp(functions.col("timestamp"), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
		                    		functions.col("event_time")
		                    ));
		
		Dataset<Row> windowedData = withTimestamp
		                    .withWatermark("event_time", "1 minute")
		                    .groupBy(
		                    		functions.window(functions.col("event_time"), "1 minute"),
		                    		functions.col("symbol")
		                    )
		                    .agg(
		                    		functions.avg("price").alias("avg_price"),
		                    		functions.min("price").alias("min_price"),
		                    		functions.max("price").alias("max_price"),
		                    		functions.count("symbol").alias("trade_count")
		                    )
		                    .select(
		                    		functions.col("window.start").cast(DataTypes.StringType).alias("window_start"),
		                    		functions.col("window.end").cast(DataTypes.StringType).alias("window_end"),
		                    		functions.col("symbol"),
		                    		functions.col("avg_price"),
		                    		functions.col("min_price"),
		                    		functions.col("max_price"),
		                    		functions.col("trade_count").cast(DataTypes.IntegerType)
		                    );

		StreamingQuery consoleQuery = windowedData
				.writeStream()
				.outputMode("update")
				.format("console")
				.option("truncate", false)
				.start();

		StreamingQuery hdfsQuery = windowedData
				.coalesce(1)
				.writeStream()
				.format("csv")
				.outputMode("append")
				.trigger(Trigger.ProcessingTime("10 seconds"))
				.option("truncate", false)
				.option("header", "false")
				.option("maxRecordsPerFile", 10000)
				.option("path", "hdfs:///user/cloudera/project_stream_out")
				.option("checkpointLocation", "hdfs:///user/cloudera/project_stream_ckpt")
				.start();
		
		System.out.println("Spark Streaming started...");
		System.out.println("Writing to HDFS: /user/cloudera/project_stream_out");
		System.out.println("Checkpoint: /user/cloudera/project_stream_ckpt");
		
		hdfsQuery.awaitTermination();
		consoleQuery.awaitTermination();
	}
}
