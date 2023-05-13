FROM openjdk:8-jre-slim as build_springboot

# 使用适当的Java 8基础镜像


# 将工作目录设置为/app
WORKDIR /app

# 从本地target目录中将整个内容复制到Docker容器中的/app/target目录
COPY ./target /app/target

# 在容器中运行Java应用程序
CMD ["java", "-jar", "/app/target/document-sharing-site-1.0-SNAPSHOT.jar"]

# 暴露端口，例如8080（根据您的应用程序进行更改）
EXPOSE 8082

