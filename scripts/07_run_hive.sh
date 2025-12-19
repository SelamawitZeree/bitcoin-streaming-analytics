#!/bin/bash
# Run Hive table creation and queries
# Creates external table and runs analysis queries

source $(dirname $0)/00_env.sh

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CREATE_TABLE="$SCRIPT_DIR/../hive/01_create_table.hql"
QUERIES="$SCRIPT_DIR/../hive/02_queries.hql"

if [ ! -f "$CREATE_TABLE" ]; then
    echo "ERROR: Hive table creation script not found: $CREATE_TABLE"
    exit 1
fi

if [ ! -f "$QUERIES" ]; then
    echo "ERROR: Hive queries script not found: $QUERIES"
    exit 1
fi

echo "Step 1: Creating Hive database and external table..."
echo "----------------------------------------"
hive -f "$CREATE_TABLE"
echo ""

if [ $? -eq 0 ]; then
    echo "Table created successfully"
    echo ""
    echo "Step 2: Running analysis queries..."
    echo "----------------------------------------"
    hive -f "$QUERIES"
    echo ""
    echo "Queries completed"
    echo ""
    echo "Step 3: Verifying table location..."
    hive -e "USE cs599; DESCRIBE EXTENDED project_stream_ext;" | grep -i location
else
    echo "ERROR: Failed to create table"
    exit 1
fi

