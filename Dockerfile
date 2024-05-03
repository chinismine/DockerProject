# 使用官方的 OpenJDK 映像作為基礎
FROM openjdk:17-jdk-slim


WORKDIR /app
# 拷貝打包好的 Spring Boot JAR 檔案到容器中
COPY target/*.jar /app/demo.jar

# 暴露應用程式運行的端口（根據你的應用程式端口配置）
EXPOSE 8082
ENV DATABASE_URL=jdbc:sqlserver://sql1:1433;databaseName=jojo;encrypt=true;trustServerCertificate=true;
ENV DATABASE_HOST=sql1

# 定義容器啟動時運行的命令
CMD ["java", "-jar", "/app/demo.jar"]