package cs523.FinalProject;

import java.util.Properties;
import java.util.Random;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class ProjKafkaBitcoinProducer {
	
	public static void main(String[] args) throws Exception {
		
		String topicName = "project_stream";
		String bootstrapServers = "localhost:9092";
		
		String[] symbols = {"BTC", "ETH", "BNB", "ADA", "SOL"};
		double[] basePrices = {43210.50, 2450.75, 315.20, 0.52, 98.30};
		
		Properties props = new Properties();
		props.put("bootstrap.servers", bootstrapServers);
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		Producer<String, String> producer = new KafkaProducer<String, String>(props);
		
		Random random = new Random();
		int messageCount = 0;
		
		System.out.println("Bitcoin Producer Started");
		System.out.println("Topic: " + topicName);
		System.out.println("Broker: " + bootstrapServers);
		System.out.println("");
		
		while (true) {
			int symbolIndex = random.nextInt(symbols.length);
			String symbol = symbols[symbolIndex];
			double basePrice = basePrices[symbolIndex];
			
			double priceChange = (random.nextDouble() - 0.5) * 0.04;
			double price = basePrice * (1 + priceChange);
			basePrices[symbolIndex] = price;
			
			long[] supplyEstimates = {19500000L, 120000000L, 153000000L, 33000000000L, 400000000L};
			double marketCap = price * supplyEstimates[symbolIndex];
			double volume = 50000000 + random.nextDouble() * 450000000;
			
			String timestamp = java.time.Instant.now().toString();
			String msg = String.format("%s,%s,%.2f,%.0f,%.2f", 
					timestamp, symbol, price, marketCap, volume);
			
			producer.send(new ProducerRecord<String, String>(topicName, symbol, msg));
			System.out.println("Sent: " + msg);
			
			messageCount++;
			Thread.sleep(1000);
			
			if (messageCount % 10 == 0) {
				System.out.println("Total messages: " + messageCount);
			}
		}
	}
}

