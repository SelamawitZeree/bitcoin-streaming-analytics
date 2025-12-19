#!/bin/bash
# Script to create and push to new GitHub repository for CS599 Final Project

echo "=========================================="
echo "CS599 Final Project - GitHub Setup"
echo "=========================================="
echo ""

# Check if git is installed
if ! command -v git &> /dev/null; then
    echo "ERROR: git is not installed. Please install git first."
    exit 1
fi

# Get GitHub username
echo "Enter your GitHub username (e.g., SelamawitZeree):"
read GITHUB_USERNAME

if [ -z "$GITHUB_USERNAME" ]; then
    echo "ERROR: GitHub username is required"
    exit 1
fi

REPO_NAME="cs599-final-project"
REPO_URL="https://github.com/${GITHUB_USERNAME}/${REPO_NAME}.git"

echo ""
echo "Repository will be created at: ${REPO_URL}"
echo ""
echo "STEP 1: Create the repository on GitHub.com first!"
echo "  1. Go to https://github.com/new"
echo "  2. Repository name: ${REPO_NAME}"
echo "  3. Description: CS599 Big Data Final Project - Bitcoin Streaming Analytics"
echo "  4. Make it Public (or Private)"
echo "  5. DO NOT initialize with README, .gitignore, or license"
echo "  6. Click 'Create repository'"
echo ""
echo "Press Enter after you've created the repository on GitHub..."
read

# Remove existing remote if it exists
if git remote get-url origin &> /dev/null; then
    echo "Removing existing remote 'origin'..."
    git remote remove origin
fi

# Add new remote
echo "Adding remote repository..."
git remote add origin "${REPO_URL}"

# Set branch to main
git branch -M main

# Push to GitHub
echo ""
echo "Pushing to GitHub..."
echo "You may be prompted for your GitHub username and password/token"
echo ""

git push -u origin main

if [ $? -eq 0 ]; then
    echo ""
    echo "=========================================="
    echo "SUCCESS! Your repository is now on GitHub"
    echo "=========================================="
    echo ""
    echo "Repository URL: ${REPO_URL}"
    echo ""
    echo "Next steps:"
    echo "1. Visit your repository: ${REPO_URL}"
    echo "2. Verify all files are visible"
    echo "3. Add screenshots to evidence/ folder when ready"
    echo "4. Add demo video link to README.md when ready"
    echo "5. Submit this URL on Sakai: ${REPO_URL}"
    echo ""
else
    echo ""
    echo "ERROR: Failed to push to GitHub"
    echo ""
    echo "Common issues:"
    echo "1. Repository doesn't exist yet - make sure you created it on GitHub.com"
    echo "2. Authentication failed - you may need to use a Personal Access Token"
    echo "   Go to: GitHub → Settings → Developer settings → Personal access tokens"
    echo "3. Wrong username - check your GitHub username"
    echo ""
    echo "You can manually push using:"
    echo "  git push -u origin main"
    echo ""
fi

