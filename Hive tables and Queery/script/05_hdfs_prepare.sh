#!/bin/bash
set -e
hdfs dfs -mkdir -p /user/cloudera/finalproject/bitcoin_parsed
hdfs dfs -mkdir -p /user/cloudera/finalproject/bitcoin_agg
hdfs dfs -mkdir -p /user/cloudera/finalproject/checkpoints/bitcoin_parsed
hdfs dfs -mkdir -p /user/cloudera/finalproject/checkpoints/bitcoin_agg
echo "HDFS folders prepared."
hdfs dfs -ls /user/cloudera/finalproject
