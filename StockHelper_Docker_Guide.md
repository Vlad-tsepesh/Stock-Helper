# 🚀 Full Guide: Building and Running StockHelper in Docker

## 1. Prerequisites

- Install Docker Desktop (Windows/Mac) or Docker Engine (Linux).  
- Verify install:

```bash
docker --version
```

---

## 2. Package your Spring Boot app

In your project root, build the JAR:

```bash
mvn clean package -DskipTests
```

This will create:

```
target/Stock-Helper-0.0.1-SNAPSHOT.jar
```

---

## 3. Create a Dockerfile

Inside your project root, create a file named **Dockerfile**:

```dockerfile
# Use a lightweight OpenJDK 17 image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory inside container
WORKDIR /app

# Copy the jar into the container
COPY target/Stock-Helper-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your app runs on
EXPOSE 8080

# Command to run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 4. Build the Docker image

Run in project root:

```bash
docker build -t stockhelper:1.0 .
```

Check it was created:

```bash
docker images
```

You should see:

```
REPOSITORY     TAG    IMAGE ID      SIZE
stockhelper    1.0    <some-id>     ...
```

---

## 5. Run the container

**Option A – Pass API key directly**

```bash
docker run -e OPENAI_API_KEY -p 8080:8080 stockhelper:1.0
```

**Option B – Use host environment variable (better)**

PowerShell:

```powershell
$env:OPENAI_API_KEY="your-api-key"
docker run -e OPENAI_API_KEY -p 8080:8080 stockhelper:1.0
```

Linux/Mac:

```bash
export OPENAI_API_KEY="your-api-key"
docker run -e OPENAI_API_KEY -p 8080:8080 stockhelper:1.0
```

Now your app is running on:  
👉 [http://localhost:8080](http://localhost:8080)

---

## 6. Share the app

**Option A – Save & send file**

Save image:

```bash
docker save -o stockhelper.tar stockhelper:1.0
```

Send `stockhelper.tar` to your friend.  
They load it with:

```bash
docker load -i stockhelper.tar
docker run -e OPENAI_API_KEY -p 8080:8080 stockhelper:1.0
```

**Option B – Upload to Docker Hub**

Login:

```bash
docker login
```

Tag image:

```bash
docker tag stockhelper:1.0 your-dockerhub-username/stockhelper:1.0
```

Push:

```bash
docker push your-dockerhub-username/stockhelper:1.0
```

On any machine:

```bash
docker run -e OPENAI_API_KEY -p 8080:8080 your-dockerhub-username/stockhelper:1.0
```

---

## 7. Useful commands

List running containers:

```bash
docker ps
```

Stop container:

```bash
docker stop <container_id>
```

Remove container:

```bash
docker rm <container_id>
```

Remove image:

```bash
docker rmi stockhelper:1.0
```

---

✅ After following these steps, your **StockHelper** will run anywhere that has Docker installed — no need for JDK, Maven, or GraalVM on your friend’s machine.

---

Do you also want me to create a **PowerShell script (`deploy.ps1`)** that automates build → run so you can just double-click it?
