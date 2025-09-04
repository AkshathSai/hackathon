# Killercoda Deployment Guide

## Step-by-Step Instructions for Deploying to Killercoda Minikube

### Prerequisites
- Killercoda environment with Docker and kubectl pre-installed
- Your code committed to GitHub develop branch

### Step 1: Clone Repository
```bash
git clone https://github.com/YOUR_USERNAME/YOUR_REPO_NAME.git
cd YOUR_REPO_NAME
git checkout develop
```

### Step 2: Make Deployment Script Executable
```bash
chmod +x deploy-killercoda.sh
```

### Step 3: Start Deployment
```bash
./deploy-killercoda.sh
```

### Step 4: Monitor Deployment Progress
The script will:
1. Start Minikube with 1-core optimized settings
2. Build your Docker image locally
3. Deploy MongoDB first
4. Deploy Kafka (Zookeeper + Kafka broker)
5. Deploy your Spring Boot application
6. Perform health checks

### Expected Deployment Timeline (1-core environment):
- Minikube startup: 2-3 minutes
- Docker build: 3-5 minutes
- MongoDB deployment: 2-3 minutes
- Kafka deployment: 5-8 minutes
- Application deployment: 5-10 minutes
- **Total time: 17-29 minutes**

### Step 5: Access Your Application
Once deployment completes, you'll see output like:
```
🎉 Deployment completed!
📍 Application URL: http://192.168.49.2:30085
📖 Swagger UI: http://192.168.49.2:30085/swagger-ui/index.html
🏥 Health Check: http://192.168.49.2:30085/actuator/health
```

### Step 6: Test the Application
```bash
# Check health
curl http://$(minikube ip):30085/actuator/health

# Access Swagger UI in browser or test endpoints
curl http://$(minikube ip):30085/swagger-ui/index.html
```

### Troubleshooting Commands
```bash
# Check pod status
kubectl get pods

# View application logs
kubectl logs -l app=hackathon-app -f

# Check resource usage
kubectl top pods

# Get detailed pod information
kubectl describe pods -l app=hackathon-app

# Restart deployment if needed
kubectl rollout restart deployment/hackathon-app
```

### Important Notes for Killercoda:
1. **Be Patient**: 1-core environment means slower startup times
2. **Memory Constraints**: All services are configured with minimal memory
3. **Timeout Considerations**: Health checks have extended timeouts
4. **Resource Monitoring**: Use `kubectl top pods` to monitor resource usage

### Port Forwarding Alternative (if NodePort doesn't work):
```bash
# If NodePort is not accessible, use port forwarding
kubectl port-forward service/hackathon-service 8085:8085

# Then access via localhost
curl http://localhost:8085/actuator/health
# Swagger: http://localhost:8085/swagger-ui/index.html
```
