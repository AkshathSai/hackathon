#!/bin/bash

# Cleanup script for Minikube deployment
set -e

echo "🧹 Cleaning up Hackathon App deployment..."

# Delete application resources
echo "Removing application deployment..."
kubectl delete -f k8s/deployment.yaml --ignore-not-found=true

echo "Removing Kafka and Zookeeper..."
kubectl delete -f k8s/kafka.yaml --ignore-not-found=true

echo "Removing MongoDB..."
kubectl delete -f k8s/mongodb.yaml --ignore-not-found=true

# Wait for cleanup to complete
echo "Waiting for cleanup to complete..."
kubectl wait --for=delete pod -l app=hackathon-app --timeout=60s || true
kubectl wait --for=delete pod -l app=kafka --timeout=60s || true
kubectl wait --for=delete pod -l app=mongodb --timeout=60s || true

echo "✅ Cleanup completed!"

# Optional: Stop minikube
read -p "Do you want to stop Minikube? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "Stopping Minikube..."
    minikube stop
    echo "✅ Minikube stopped!"
fi
