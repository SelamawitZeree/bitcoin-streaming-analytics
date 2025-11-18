#!/bin/bash

# Script to create GitHub repository and push project
# Repository name: aws-serverless-microservices-platform

REPO_NAME="aws-serverless-microservices-platform"
GITHUB_USER="SelamawitZeree"
REPO_URL="https://github.com/${GITHUB_USER}/${REPO_NAME}.git"

echo "🚀 Setting up GitHub repository..."
echo ""
echo "Repository name: ${REPO_NAME}"
echo "GitHub user: ${GITHUB_USER}"
echo ""

# Check if remote already exists
if git remote get-url origin &>/dev/null; then
    echo "⚠️  Remote 'origin' already exists. Removing..."
    git remote remove origin
fi

# Add remote
echo "📡 Adding remote repository..."
git remote add origin "${REPO_URL}"

# Set branch to main
git branch -M main

echo ""
echo "✅ Git is configured!"
echo ""
echo "📋 NEXT STEPS:"
echo ""
echo "1. Go to: https://github.com/new"
echo "2. Repository name: ${REPO_NAME}"
echo "3. Description: Enterprise-grade AWS serverless microservices platform"
echo "4. Choose Public or Private"
echo "5. DO NOT initialize with README (we already have one)"
echo "6. Click 'Create repository'"
echo ""
echo "7. After creating, run this command to push:"
echo "   git push -u origin main"
echo ""
echo "Or run this script again after creating the repo:"
echo "   ./create-and-push.sh push"
echo ""

# If "push" argument is provided, push immediately
if [ "$1" == "push" ]; then
    echo "⬆️  Pushing to GitHub..."
    git push -u origin main
    
    if [ $? -eq 0 ]; then
        echo ""
        echo "✅ Success! Your project is now on GitHub!"
        echo "🌐 Visit: https://github.com/${GITHUB_USER}/${REPO_NAME}"
    else
        echo ""
        echo "❌ Push failed. Make sure you've created the repository on GitHub first."
        echo "   Go to: https://github.com/new"
    fi
fi

