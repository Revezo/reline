FROM openjdk:17-jdk

EXPOSE 8080
WORKDIR /app
ARG JAR=*.jar

COPY  /build/libs/$JAR /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]