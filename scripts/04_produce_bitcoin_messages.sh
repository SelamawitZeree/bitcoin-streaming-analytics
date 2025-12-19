#!/bin/bash
# Send Bitcoin sample data to Kafka topic
# Reads from sample_data/bitcoin_sample.csv and sends to Kafka

source $(dirname $0)/00_env.sh

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SAMPLE_FILE="$SCRIPT_DIR/../sample_data/bitcoin_sample.csv"

if [ ! -f "$SAMPLE_FILE" ]; then
    echo "ERROR: Sample file not found: $SAMPLE_FILE"
    echo "Please make sure sample_data/bitcoin_sample.csv exists"
    exit 1
fi

echo "Reading Bitcoin data from: $SAMPLE_FILE"
echo "Sending messages to Kafka topic: $TOPIC"
echo "Press Ctrl+C to stop"
echo ""

cd $KAFKA_HOME

# Send messages one by one with 1 second delay to simulate streaming
while IFS= read -r line; do
    if [ -n "$line" ]; then
        echo "$line" | bin/kafka-console-producer.sh --broker-list $BROKER --topic $TOPIC
        echo "Sent: $line"
        sleep 1
    fi
done < "$SAMPLE_FILE"

echo ""
echo "Finished sending all messages from sample file"
echo "To send continuously, run this script in a loop or modify it"

