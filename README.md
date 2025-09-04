# Hackathon E-commerce Application (Free Tier Optimized)

A Spring Boot microservice with Kafka messaging and MongoDB for e-commerce order processing, optimized for GitHub Actions free tier and Minikube resource constraints.

## Architecture

- **Spring Boot 3.5.5** with Java 21 (optimized JVM settings)
- **Apache Kafka** for asynchronous messaging (minimal memory allocation)
- **MongoDB** for data persistence (resource-constrained configuration)
- **Spring Boot Actuator** for health checks and monitoring
- **Docker** for containerization (multi-stage build optimization)
- **Kubernetes** for orchestration (free tier resource limits)

## 🆓 Free Tier Optimizations

### GitHub Actions (2,000 minutes/month free)
- **Build timeouts**: 15-20 minutes max per workflow
- **Single platform builds**: AMD64 only to reduce build time
- **Conditional security scans**: Only on main branch
- **Optimized test execution**: Reduced parallelism and timeouts
- **Efficient caching**: Maven dependencies cached between runs

### Minikube (Resource Constrained)
- **Memory allocation**: 2GB total (MongoDB: 256Mi, Kafka: 384Mi, App: 512Mi)
- **CPU allocation**: 2 cores total with minimal requests
- **Storage limits**: 10GB disk with 1GB MongoDB storage
- **Single replicas**: All deployments use 1 replica
- **NodePort services**: No LoadBalancer overhead

## Quick Start

### Local Development with Docker Compose (Recommended for Development)

```bash
# Start all services with resource limits
docker-compose up -d

# Monitor resource usage
docker stats

# View logs
docker-compose logs -f hackathon-app

# Stop services
docker-compose down
```

### Deployment to Minikube (Killercoda - Free Tier)

```bash
# Deploy with automatic resource optimization
./deploy-minikube.sh

# Monitor deployment progress
kubectl get pods -w

# Clean up when done
./cleanup-minikube.sh
```

### Manual Kubernetes Deployment (Advanced)

```bash
# Start Minikube with free tier resources
minikube start --driver=docker --memory=2048 --cpus=2 --disk-size=10g

# Apply resource limits first
kubectl apply -f k8s/resource-limits.yaml

# Build and load image
eval $(minikube docker-env)
docker build --memory=1g --cpus=1 -t hackathon-app:latest .

# Deploy dependencies with minimal resources
kubectl apply -f k8s/mongodb.yaml
kubectl apply -f k8s/kafka.yaml

# Wait for dependencies (extended timeout for minimal resources)
kubectl wait --for=condition=available --timeout=600s deployment/mongodb
kubectl wait --for=condition=available --timeout=600s deployment/kafka

# Deploy application
sed 's|ghcr.io/.*|hackathon-app:latest|g' k8s/deployment.yaml | kubectl apply -f -

# Get service URL (NodePort)
NODE_IP=$(minikube ip)
echo "Application URL: http://$NODE_IP:30085"
```

## API Endpoints

- **POST** `/orders/v1/kafka` - Submit order to Kafka
- **GET** `/actuator/health` - Health check
- **GET** `/actuator/metrics` - Application metrics
- **GET** `/swagger-ui.html` - API documentation

## Configuration

### Environment Variables

- `SPRING_DATA_MONGODB_URI` - MongoDB connection string
- `SPRING_KAFKA_BOOTSTRAP_SERVERS` - Kafka bootstrap servers
- `SERVER_PORT` - Application port (default: 8085)
- `JAVA_OPTS` - JVM optimization flags (auto-configured)

### Resource Profiles

- `default` - Local development (full resources)
- `k8s` - Kubernetes deployment (resource-optimized)

## CI/CD Pipeline (Free Tier Optimized)

### GitHub Actions Workflows
- **Build & Test** (≤15 min): Automated testing with resource-limited services
- **Docker Build** (≤20 min): Single-platform builds with caching
- **Security Scan** (≤10 min): Trivy scanning on main branch only
- **Kubernetes Deploy** (≤15 min): Automated deployment with timeouts

### Monthly Usage Estimates
- **Typical usage**: ~300-500 minutes/month (well within 2,000 free minutes)
- **Per workflow run**: 15-25 minutes total
- **Builds per day**: Recommended ≤10 to stay within limits

## Monitoring & Resource Management

### Health Checks
- Application: `http://NODE_IP:30085/actuator/health`
- Readiness: `/actuator/health/readiness`
- Liveness: `/actuator/health/liveness`
- Metrics: `/actuator/metrics`

### Resource Monitoring
```bash
# Check resource usage
kubectl top pods
kubectl top nodes

# View resource quotas
kubectl describe quota free-tier-quota

# Monitor container resources
docker stats  # For Docker Compose
```

## Development

```bash
# Run tests with minimal resources
mvn test -DforkCount=1 -DreuseForks=true

# Build application with memory limits
mvn clean package -Dmaven.compiler.maxmem=256m

# Run locally with optimized JVM
java -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -jar target/*.jar
```

## Troubleshooting (Free Tier Specific)

### Common Issues

1. **GitHub Actions timeout**: Workflows limited to 20 minutes
   - **Solution**: Optimize build steps, use caching effectively
   
2. **Minikube memory issues**: Pods stuck in Pending state
   - **Solution**: Reduce resource requests, check `kubectl describe pod`
   
3. **Slow startup times**: Extended delays with minimal resources
   - **Solution**: Increase probe timeouts, be patient during startup

4. **Docker build failures**: Out of memory during build
   - **Solution**: Use `docker build --memory=1g --cpus=1`

### Debugging Commands

```bash
# Check GitHub Actions usage
# Go to: Settings > Billing > Plans and usage

# Check pod resource usage
kubectl describe pod <pod-name>
kubectl top pods --containers

# View detailed resource allocation
kubectl describe nodes

# Check service status with extended timeout
timeout 60 curl -f http://NODE_IP:30085/actuator/health

# Monitor logs for memory issues
kubectl logs -l app=hackathon-app --tail=50 | grep -i "memory\|oom"
```

### Resource Optimization Tips

1. **Use resource quotas**: Apply `k8s/resource-limits.yaml`
2. **Monitor build minutes**: Check GitHub usage regularly
3. **Optimize Docker layers**: Use `.dockerignore` effectively
4. **Cache dependencies**: Maven/Docker layer caching enabled
5. **Single environment**: Use development environment only for free tier

## 📊 Resource Allocation Summary

| Component | CPU Request | CPU Limit | Memory Request | Memory Limit |
|-----------|-------------|-----------|----------------|--------------|
| App       | 100m        | 300m      | 256Mi          | 512Mi        |
| MongoDB   | 100m        | 200m      | 128Mi          | 256Mi        |
| Kafka     | 100m        | 200m      | 256Mi          | 384Mi        |
| Zookeeper | 50m         | 150m      | 96Mi           | 192Mi        |
| **Total** | **350m**    | **850m**  | **736Mi**      | **1.3Gi**    |

*Designed to fit comfortably within 2GB RAM and 2 CPU cores.*
