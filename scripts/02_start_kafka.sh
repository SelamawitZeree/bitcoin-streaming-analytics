#!/bin/bash
# Start Kafka broker
# Check if already running, if not start it

source $(dirname $0)/00_env.sh

echo "Checking if Kafka broker is already running on port 9092..."

if netstat -tulpn 2>/dev/null | grep -q ":9092.*LISTEN"; then
    echo "Kafka broker is already running on port 9092"
    echo "You can skip this step or stop it first with: pkill -f kafka"
else
    echo "Starting Kafka broker..."
    cd $KAFKA_HOME
    nohup bin/kafka-server-start.sh config/server.properties > /tmp/kafka.log 2>&1 &
    sleep 5
    
    if netstat -tulpn 2>/dev/null | grep -q ":9092.*LISTEN"; then
        echo "Kafka broker started successfully on port 9092"
        echo "Logs are in /tmp/kafka.log"
    else
        echo "ERROR: Kafka broker failed to start. Check /tmp/kafka.log"
        exit 1
    fi
fi

