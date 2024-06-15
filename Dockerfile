FROM eclipse-temurin:21-jdk-focal
WORKDIR /app
COPY target/CurrencyExchangeApplication-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar"]
