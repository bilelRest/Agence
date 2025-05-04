FROM openjdk:17

WORKDIR /app


COPY ./agence-0.0.1-SNAPSHOT.jar /app/agence-0.0.1-SNAPSHOT.jar

EXPOSE 6060


CMD ["java", "-jar", "agence.jar"]
