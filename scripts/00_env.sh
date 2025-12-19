#!/bin/bash
# Environment variables for CS599 Final Project
# Source this file before running other scripts

export KAFKA_HOME=/home/cloudera/kafka_2.11-0.10.2.2
export BROKER=localhost:9092
export ZK=localhost:2181
export TOPIC=project_stream
export HDFS_OUT=/user/cloudera/project_stream_out
export HDFS_CKPT=/user/cloudera/project_stream_ckpt

echo "Environment variables set:"
echo "  KAFKA_HOME: $KAFKA_HOME"
echo "  BROKER: $BROKER"
echo "  ZK: $ZK"
echo "  TOPIC: $TOPIC"
echo "  HDFS_OUT: $HDFS_OUT"
echo "  HDFS_CKPT: $HDFS_CKPT"

