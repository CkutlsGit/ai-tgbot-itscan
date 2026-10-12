FROM eclipse-temurin:21-alpine

WORKDIR /app

RUN addgroup -S itscan && adduser -S itscan -G itscan

COPY target/itscantgbot-0.0.1-SNAPSHOT.jar /app/itscan.jar

RUN chown -R itscan:itscan /app

USER itscan

ENTRYPOINT ["java", "-jar", "itscan.jar"]