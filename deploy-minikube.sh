#!/bin/bash

# Minikube deployment script optimized for free tier and Killercoda
set -e

echo "🚀 Deploying Hackathon App to Minikube (Free Tier Optimized)..."

# Check if minikube is running
if ! minikube status > /dev/null 2>&1; then
    echo "Starting Minikube with minimal resources..."
    # Reduced resources for free tier compatibility
    minikube start --driver=docker --memory=2048 --cpus=2 --disk-size=10g
fi

# Enable required addons
echo "Enabling essential Minikube addons..."
minikube addons enable metrics-server

# Build and load Docker image into Minikube
echo "Building Docker image with minimal resources..."
eval $(minikube docker-env)
# Build with reduced parallelism to save memory
docker build --memory=1g --cpus=1 -t hackathon-app:latest .

# Deploy dependencies first
echo "Deploying MongoDB (minimal resources)..."
kubectl apply -f k8s/mongodb.yaml

echo "Deploying Kafka and Zookeeper (minimal resources)..."
kubectl apply -f k8s/kafka.yaml

# Wait for dependencies to be ready with extended timeout for slower startup
echo "Waiting for dependencies to be ready (this may take a few minutes with minimal resources)..."
echo "⏳ Waiting for MongoDB..."
kubectl wait --for=condition=available --timeout=300s deployment/mongodb || echo "MongoDB deployment may still be starting"

echo "⏳ Waiting for Zookeeper..."
kubectl wait --for=condition=available --timeout=300s deployment/zookeeper || echo "Zookeeper deployment may still be starting"

echo "⏳ Waiting for Kafka..."
kubectl wait --for=condition=available --timeout=600s deployment/kafka || echo "Kafka deployment may still be starting"

# Give services time to stabilize
echo "Allowing services to stabilize..."
sleep 30

# Deploy the application
echo "Deploying Hackathon App..."
sed 's|ghcr.io/.*|hackathon-app:latest|g' k8s/deployment.yaml | kubectl apply -f -

# Wait for application to be ready with extended timeout
echo "⏳ Waiting for application to be ready (extended timeout for minimal resources)..."
kubectl wait --for=condition=available --timeout=600s deployment/hackathon-app || echo "Application may still be starting"

# Get service URLs
echo "📋 Deployment Summary:"
echo "====================="
kubectl get pods
echo ""
kubectl get services

# Get the application URL using NodePort
NODE_IP=$(minikube ip)
NODE_PORT=30085
APP_URL="http://$NODE_IP:$NODE_PORT"

echo ""
echo "🎉 Application deployed successfully!"
echo "📍 Application URL: $APP_URL"
echo "🏥 Health Check: $APP_URL/actuator/health"
echo "📖 API Docs: $APP_URL/swagger-ui.html"

# Test the application with extended wait time
echo ""
echo "🧪 Testing application health (waiting for startup)..."
sleep 60  # Extended wait for minimal resource environment

# Health check with retries
for i in {1..5}; do
    echo "Health check attempt $i/5..."
    if curl -f --connect-timeout 10 --max-time 30 "$APP_URL/actuator/health" > /dev/null 2>&1; then
        echo "✅ Application is healthy!"
        break
    else
        if [ $i -eq 5 ]; then
            echo "❌ Application health check failed after 5 attempts"
            echo "📝 Checking logs for troubleshooting..."
            kubectl logs -l app=hackathon-app --tail=20
            echo ""
            echo "💡 Tips for troubleshooting:"
            echo "   - Check if all pods are running: kubectl get pods"
            echo "   - View detailed logs: kubectl logs -l app=hackathon-app"
            echo "   - Check resource usage: kubectl top pods"
        else
            echo "⏳ Retrying in 30 seconds..."
            sleep 30
        fi
    fi
done

echo ""
echo "📊 Resource Usage:"
kubectl top pods 2>/dev/null || echo "Metrics not available yet"
