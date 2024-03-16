#FROM openjdk:21-jdk
#
#EXPOSE 8080
#WORKDIR /app
#ARG JAR=*.jar
#
#COPY  /build/libs/$JAR /app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]
FROM gradle:8.6.0-jdk21 AS cahce
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME /home/gradle/cache_home
COPY build.gradle.kts /home/gradle/java-code/
WORKDIR /home/gradle/java-code
RUN gradle clean build -i --stacktrace

FROM gradle:8.6.0-jdk21 AS build

COPY ./ /app/
WORKDIR /app
RUN gradle build

FROM openjdk:21-jdk
COPY --from=build /app/build/libs/reline-1.0.0.jar /usr/local/lib/app.jar
ENTRYPOINT ["java","-jar","/usr/local/lib/app.jar"]