#!/bin/bash
# Start Zookeeper for Kafka
# Check if already running, if not start it

source $(dirname $0)/00_env.sh

echo "Checking if Zookeeper is already running on port 2181..."

if netstat -tulpn 2>/dev/null | grep -q ":2181.*LISTEN"; then
    echo "Zookeeper is already running on port 2181"
    echo "You can skip this step or stop it first with: pkill -f zookeeper"
else
    echo "Starting Zookeeper..."
    cd $KAFKA_HOME
    nohup bin/zookeeper-server-start.sh config/zookeeper.properties > /tmp/zookeeper.log 2>&1 &
    sleep 3
    
    if netstat -tulpn 2>/dev/null | grep -q ":2181.*LISTEN"; then
        echo "Zookeeper started successfully on port 2181"
        echo "Logs are in /tmp/zookeeper.log"
    else
        echo "ERROR: Zookeeper failed to start. Check /tmp/zookeeper.log"
        exit 1
    fi
fi

