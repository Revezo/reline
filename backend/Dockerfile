FROM gradle:8.11.1-jdk23 AS build

COPY ./ /app/
WORKDIR /app
RUN gradle clean build

FROM openjdk:23-jdk
ARG version
COPY --from=build /app/build/libs/reline-${version}.jar /usr/local/lib/app.jar
ENTRYPOINT ["java","-jar","/usr/local/lib/app.jar"]