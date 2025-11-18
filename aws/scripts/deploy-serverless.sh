#!/bin/bash

set -e

ENVIRONMENT=${1:-dev}
REGION=${2:-us-east-2}

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

echo "=== Deploying Serverless Microservices ==="
echo "Environment: ${ENVIRONMENT}"
echo "Region: ${REGION}"
echo ""

cd "$PROJECT_ROOT"

AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
LAMBDA_BUCKET="${ENVIRONMENT}-lambda-code-${AWS_ACCOUNT_ID}"

echo "Step 1: Creating S3 bucket for Lambda code..."
if ! aws s3 ls "s3://${LAMBDA_BUCKET}" 2>/dev/null; then
    aws s3 mb s3://${LAMBDA_BUCKET} --region ${REGION}
    echo "Created bucket: ${LAMBDA_BUCKET}"
else
    echo "Bucket ${LAMBDA_BUCKET} already exists"
fi

echo ""
echo "Step 2: Building Lambda packages..."

for service in customer order product; do
    echo "Building ${service}..."
    cd "${service}"
    mvn clean package -DskipTests
    cd "$PROJECT_ROOT"
    
    JAR_FILE="${service}/target/${service}-0.0.1-SNAPSHOT.jar"
    if [ -f "$JAR_FILE" ]; then
        echo "Uploading ${service} Lambda..."
        aws s3 cp "$JAR_FILE" s3://${LAMBDA_BUCKET}/${service}-service.jar --region ${REGION}
        echo "✓ ${service} uploaded"
    else
        echo "❌ JAR file not found: $JAR_FILE"
        exit 1
    fi
done

echo ""
echo "Step 3: Deploying CloudFormation stack..."
aws cloudformation deploy \
    --template-file aws/infrastructure/serverless/serverless-stack.yaml \
    --stack-name microservices-${ENVIRONMENT}-serverless \
    --parameter-overrides Environment=${ENVIRONMENT} \
    --region ${REGION} \
    --capabilities CAPABILITY_NAMED_IAM

echo ""
echo "Step 4: Retrieving outputs..."
COGNITO_POOL_ID=$(aws cloudformation describe-stacks \
    --stack-name microservices-${ENVIRONMENT}-serverless \
    --query "Stacks[0].Outputs[?OutputKey=='CognitoUserPoolId'].OutputValue" \
    --output text \
    --region ${REGION})

COGNITO_CLIENT_ID=$(aws cloudformation describe-stacks \
    --stack-name microservices-${ENVIRONMENT}-serverless \
    --query "Stacks[0].Outputs[?OutputKey=='CognitoClientId'].OutputValue" \
    --output text \
    --region ${REGION})

CUSTOMER_API=$(aws cloudformation describe-stacks \
    --stack-name microservices-${ENVIRONMENT}-serverless \
    --query "Stacks[0].Outputs[?OutputKey=='CustomerApiUrl'].OutputValue" \
    --output text \
    --region ${REGION})

ORDER_API=$(aws cloudformation describe-stacks \
    --stack-name microservices-${ENVIRONMENT}-serverless \
    --query "Stacks[0].Outputs[?OutputKey=='OrderApiUrl'].OutputValue" \
    --output text \
    --region ${REGION})

PRODUCT_API=$(aws cloudformation describe-stacks \
    --stack-name microservices-${ENVIRONMENT}-serverless \
    --query "Stacks[0].Outputs[?OutputKey=='ProductApiUrl'].OutputValue" \
    --output text \
    --region ${REGION})

COGNITO_UI=$(aws cloudformation describe-stacks \
    --stack-name microservices-${ENVIRONMENT}-serverless \
    --query "Stacks[0].Outputs[?OutputKey=='CognitoHostedUIUrl'].OutputValue" \
    --output text \
    --region ${REGION})

echo ""
echo "=== ✅ DEPLOYMENT COMPLETE ==="
echo ""
echo "📍 API Endpoints:"
echo "  Customer API: ${CUSTOMER_API} (Requires Cognito auth)"
echo "  Order API: ${ORDER_API}"
echo "  Product API: ${PRODUCT_API}"
echo ""
echo "🔐 Cognito:"
echo "  User Pool ID: ${COGNITO_POOL_ID}"
echo "  Client ID: ${COGNITO_CLIENT_ID}"
echo "  Login URL: ${COGNITO_UI}"
echo ""
echo "🧪 Testing:"
echo "  1. Create a user in Cognito User Pool"
echo "  2. Login via: ${COGNITO_UI}"
echo "  3. Get JWT token and test Customer API:"
echo "     curl -H 'Authorization: Bearer YOUR_JWT_TOKEN' ${CUSTOMER_API}"
echo "  4. Test Order API: curl ${ORDER_API}"
echo "  5. Test Product API: curl ${PRODUCT_API}"
