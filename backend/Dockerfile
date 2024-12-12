FROM gradle:8.6.0-jdk21 AS build

COPY ./ /app/
WORKDIR /app
RUN gradle clean build

FROM openjdk:21-jdk
ARG version
COPY --from=build /app/build/libs/reline-${version}.jar /usr/local/lib/app.jar
ENTRYPOINT ["java","-jar","/usr/local/lib/app.jar"]