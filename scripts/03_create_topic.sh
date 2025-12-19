#!/bin/bash
# Create Kafka topic project_stream
# This script is idempotent - safe to run multiple times

source $(dirname $0)/00_env.sh

echo "Creating Kafka topic: $TOPIC"

cd $KAFKA_HOME

# Check if topic already exists
if bin/kafka-topics.sh --list --zookeeper $ZK | grep -q "^${TOPIC}$"; then
    echo "Topic $TOPIC already exists"
else
    echo "Creating topic $TOPIC..."
    bin/kafka-topics.sh --create \
        --topic $TOPIC \
        --zookeeper $ZK \
        --partitions 1 \
        --replication-factor 1
    
    if [ $? -eq 0 ]; then
        echo "Topic $TOPIC created successfully"
    else
        echo "ERROR: Failed to create topic"
        exit 1
    fi
fi

echo ""
echo "Listing all topics:"
bin/kafka-topics.sh --list --zookeeper $ZK

echo ""
echo "Topic details:"
bin/kafka-topics.sh --describe --topic $TOPIC --zookeeper $ZK

