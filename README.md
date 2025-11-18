# AWS Serverless Microservices Platform

Enterprise-grade serverless microservices architecture built on AWS, demonstrating modern cloud-native patterns, event-driven design, and production-ready DevOps practices.

## 🏗️ Architecture

- **Customer Service** - Lambda with Cognito OAuth2 authentication
- **Order Service** - Lambda with PostgreSQL and Redis (shopping cart)
- **Product Service** - Lambda with DynamoDB and SQS integration

## 🚀 Technology Stack

- **Java 17** with Spring Boot 3
- **AWS Lambda** with Serverless Java Container
- **AWS API Gateway** (REST APIs)
- **AWS DynamoDB** (Product data storage)
- **AWS SQS** (Asynchronous messaging)
- **AWS Cognito** (User authentication & authorization)
- **PostgreSQL** (Order data persistence)
- **Redis** (Shopping cart caching)
- **AWS X-Ray** (Distributed tracing)
- **CloudWatch** (Logging & monitoring)
- **CloudFormation** (Infrastructure as Code)

## 📁 Project Structure

```
.
├── customer/          # Customer microservice (Cognito auth)
├── order/            # Order microservice (PostgreSQL + Redis)
├── product/          # Product microservice (DynamoDB + SQS)
└── aws/              # AWS infrastructure
    ├── infrastructure/
    │   └── serverless/
    │       └── serverless-stack.yaml  # CloudFormation template
    └── scripts/
        └── deploy-serverless.sh       # Deployment automation
```

## 🔧 Features

- ✅ Serverless architecture (Lambda + API Gateway)
- ✅ OAuth2 authentication with AWS Cognito
- ✅ Event-driven messaging with SQS
- ✅ NoSQL data storage with DynamoDB
- ✅ Relational data with PostgreSQL
- ✅ Caching with Redis
- ✅ Distributed tracing with X-Ray
- ✅ Structured logging with CloudWatch
- ✅ Infrastructure as Code with CloudFormation
- ✅ Circuit breaker pattern (Resilience4j)
- ✅ Automated deployment scripts

## 🚀 Quick Start

### Prerequisites
- AWS CLI configured
- Java 17+
- Maven 3.6+

### Deployment

```bash
./aws/scripts/deploy-serverless.sh dev us-east-2
```

## 📡 API Endpoints

### Customer Service
- **Base URL**: `https://cb3twtftog.execute-api.us-east-2.amazonaws.com/dev/customer`
- **Public**: `GET /api/public`
- **Protected**: `GET /api/profile` (requires Cognito token)

### Order Service
- **Base URL**: `https://h4wzwrf2j6.execute-api.us-east-2.amazonaws.com/dev/orders`
- **Cart**: `POST /orders/cart/{userId}/items`
- **Checkout**: `POST /orders/cart/{userId}/checkout`

### Product Service
- **Base URL**: `https://5wlpxq2hjk.execute-api.us-east-2.amazonaws.com/dev/products`
- **CRUD**: `GET/POST/PUT/DELETE /products`

## 🔐 Security

- AWS Cognito User Pool for authentication
- OAuth2 Resource Server for API protection
- IAM roles for Lambda execution
- Secure parameter storage with SSM

## 📊 Monitoring & Observability

- CloudWatch Logs for centralized logging
- X-Ray for distributed tracing
- CloudWatch Metrics for performance monitoring
- Structured JSON logging

## 🏢 Enterprise Features

- Infrastructure as Code (CloudFormation)
- Automated CI/CD ready
- Scalable serverless architecture
- Cost-optimized (pay-per-use)
- High availability (multi-AZ)
- Event-driven architecture

## 📝 License

This project is part of a portfolio demonstration.
