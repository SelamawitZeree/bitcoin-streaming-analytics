package cs523.FinalProject;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class ProjSparkSQLProcess {
	
	public static void main(String[] args) throws AnalysisException {	
		
		final SparkConf sparkConf = new SparkConf();

        sparkConf.setMaster("local[*]");
        sparkConf.set("hive.metastore.uris", "thrift://localhost:9083");

        
        final SparkSession sparkSession = SparkSession.builder()
        		.appName("Spark SQL-Hive Bitcoin Analytics")
        		.config(sparkConf)
                .enableHiveSupport()
                .getOrCreate();
        
        System.out.println("Connected to Hive");
        System.out.println("Running queries on Bitcoin data...");
        System.out.println("");
        
        Dataset<Row> totalCount = sparkSession.sql("SELECT COUNT(*) AS total_records FROM cs599.project_stream_ext");
        System.out.println("Query 1: Total records");
        totalCount.show();
        
        Dataset<Row> bySymbol = sparkSession.sql(
        		"SELECT symbol, COUNT(*) AS record_count, " +
        		"AVG(avg_price) AS overall_avg_price, " +
        		"SUM(trade_count) AS total_trades " +
        		"FROM cs599.project_stream_ext " +
        		"GROUP BY symbol " +
        		"ORDER BY total_trades DESC"
        );
        System.out.println("Query 2: Group by symbol");
        bySymbol.show();
        
        Dataset<Row> topByPrice = sparkSession.sql(
        		"SELECT symbol, MAX(max_price) AS highest_price, " +
        		"COUNT(*) AS window_count " +
        		"FROM cs599.project_stream_ext " +
        		"GROUP BY symbol " +
        		"ORDER BY highest_price DESC " +
        		"LIMIT 10"
        );
        System.out.println("Query 3: Top 10 by max price");
        topByPrice.show();
        
        Dataset<Row> recent = sparkSession.sql(
        		"SELECT * FROM cs599.project_stream_ext " +
        		"ORDER BY window_start DESC " +
        		"LIMIT 10"
        );
        System.out.println("Query 4: Recent windows");
        recent.show(false);
        
        System.out.println("Done");
	}
}
