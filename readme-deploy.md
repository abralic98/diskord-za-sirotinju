### local
1. docker compose build --no-cache zza clean
2. docker tag demo-springboot-app abralic98/demo-springboot-app:latest
3. docker login
4. docker push abralic98/demo-springboot-app:latest

### hetzner

1. docker pull abralic98/demo-springboot-app:latest
2. docker run -d -p 8080:8080 --name demo-app abralic98/demo-springboot-app:latest
