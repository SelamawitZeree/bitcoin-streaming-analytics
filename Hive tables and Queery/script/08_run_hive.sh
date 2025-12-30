#!/bin/bash
set -e
echo "Creating Hive tables..."
hive -f scripts/06_create_hive_table.hql

echo "Running Hive queries..."
hive -f scripts/07_hive_queries.hql
