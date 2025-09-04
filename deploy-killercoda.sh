#!/bin/bash

# Killercoda Minikube deployment script - Optimized for 1 CPU core
set -e

echo "🚀 Deploying Hackathon App to Killercoda Minikube (1-core optimized)..."

# Check if minikube is running
if ! minikube status > /dev/null 2>&1; then
    echo "Starting Minikube with ultra-minimal resources for Killercoda..."
    # Ultra-minimal resources for 1-core Killercoda environment
    minikube start --driver=docker --memory=1500 --cpus=1 --disk-size=8g --container-runtime=containerd

    # Wait for minikube to be ready
    echo "Waiting for Minikube to be ready..."
    kubectl wait --for=condition=Ready nodes --all --timeout=300s
fi

# Enable only essential addons
echo "Enabling essential Minikube addons..."
minikube addons enable storage-provisioner

# Set up Docker environment
echo "Setting up Docker environment..."
eval $(minikube docker-env)

# Apply resource limits first
echo "Applying resource limits for 1-core environment..."
kubectl apply -f k8s/resource-limits.yaml

# Build Docker image with minimal resources
echo "Building Docker image with ultra-minimal resources..."
docker build --memory=800m --cpus=1 -t hackathon-app:latest . || {
    echo "❌ Docker build failed - trying with even lower memory..."
    docker build --memory=600m --cpus=1 -t hackathon-app:latest .
}

# Deploy dependencies with staggered approach
echo "📦 Deploying MongoDB first..."
kubectl apply -f k8s/mongodb.yaml

echo "⏳ Waiting for MongoDB to be ready..."
kubectl wait --for=condition=available --timeout=400s deployment/mongodb

echo "📦 Deploying Zookeeper..."
kubectl apply -f k8s/kafka.yaml

echo "⏳ Waiting for Zookeeper to be ready..."
kubectl wait --for=condition=available --timeout=400s deployment/zookeeper

# Small delay before Kafka
sleep 20

echo "⏳ Waiting for Kafka to be ready..."
kubectl wait --for=condition=available --timeout=600s deployment/kafka

# Give Kafka extra time to stabilize in low-resource environment
echo "Allowing Kafka to fully stabilize..."
sleep 45

# Deploy the application with image replacement
echo "📦 Deploying Hackathon App..."
sed 's|ghcr.io/\${{ github.repository }}:\${IMAGE_TAG:-latest}|hackathon-app:latest|g' k8s/deployment.yaml | kubectl apply -f -

# Wait for application with extended timeout for 1-core environment
echo "⏳ Waiting for application to be ready (this may take 5-10 minutes on 1-core)..."
kubectl wait --for=condition=available --timeout=800s deployment/hackathon-app || {
    echo "⚠️ Application taking longer than expected. Checking status..."
    kubectl get pods -l app=hackathon-app
    kubectl describe pods -l app=hackathon-app
}

# Get service information
echo ""
echo "📋 Deployment Status:"
echo "===================="
kubectl get pods
echo ""
kubectl get services

# Get the application URL
NODE_IP=$(minikube ip)
NODE_PORT=30085
APP_URL="http://$NODE_IP:$NODE_PORT"

echo ""
echo "🎉 Deployment completed!"
echo "📍 Application URL: $APP_URL"
echo "📖 Swagger UI: $APP_URL/swagger-ui/index.html"
echo "🏥 Health Check: $APP_URL/actuator/health"

# Extended health check for 1-core environment
echo ""
echo "🧪 Testing application (allowing extra startup time for 1-core)..."
sleep 90

# Health check with more retries for slow startup
for i in {1..8}; do
    echo "Health check attempt $i/8..."
    if curl -f --connect-timeout 15 --max-time 45 "$APP_URL/actuator/health" > /dev/null 2>&1; then
        echo "✅ Application is healthy!"
        echo ""
        echo "🎯 Access your application:"
        echo "   Main App: $APP_URL"
        echo "   Swagger UI: $APP_URL/swagger-ui/index.html"
        echo "   Health: $APP_URL/actuator/health"
        break
    else
        if [ $i -eq 8 ]; then
            echo "❌ Application health check failed after 8 attempts"
            echo ""
            echo "🔍 Troubleshooting information:"
            echo "Pod Status:"
            kubectl get pods -l app=hackathon-app -o wide
            echo ""
            echo "Recent Logs:"
            kubectl logs -l app=hackathon-app --tail=30
            echo ""
            echo "💡 Try accessing manually: $APP_URL"
        else
            echo "⏳ Retrying in 45 seconds (1-core startup is slow)..."
            sleep 45
        fi
    fi
done

echo ""
echo "📝 Useful commands for monitoring:"
echo "  kubectl get pods -w                    # Watch pods status"
echo "  kubectl logs -l app=hackathon-app -f   # Follow app logs"
echo "  kubectl top pods                       # Resource usage"
echo "  minikube dashboard                     # Kubernetes dashboard"
