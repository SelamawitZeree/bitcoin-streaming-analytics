#!/bin/bash
# Verify HDFS output files
# Shows what Spark Streaming wrote to HDFS

source $(dirname $0)/00_env.sh

echo "Checking HDFS output directory: $HDFS_OUT"
echo ""

if hdfs dfs -test -d $HDFS_OUT; then
    echo "Output directory exists"
    echo ""
    echo "Listing files in output directory:"
    hdfs dfs -ls $HDFS_OUT
    echo ""
    
    FILE_COUNT=$(hdfs dfs -ls $HDFS_OUT | grep -v "^Found" | wc -l)
    echo "Number of files/directories: $FILE_COUNT"
    echo ""
    
    if [ $FILE_COUNT -gt 0 ]; then
        echo "Sample data from output files (first 20 lines):"
        echo "----------------------------------------"
        hdfs dfs -cat $HDFS_OUT/part-* 2>/dev/null | head -n 20
        echo "----------------------------------------"
    else
        echo "WARNING: Output directory is empty"
        echo "Make sure Spark Streaming job has processed at least one batch"
    fi
else
    echo "WARNING: Output directory does not exist: $HDFS_OUT"
    echo "Make sure Spark Streaming job has run and written output"
fi

echo ""
echo "Checking checkpoint directory: $HDFS_CKPT"
if hdfs dfs -test -d $HDFS_CKPT; then
    echo "Checkpoint directory exists"
    hdfs dfs -ls $HDFS_CKPT | head -n 10
else
    echo "WARNING: Checkpoint directory does not exist"
fi

