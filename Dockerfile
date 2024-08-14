FROM amazoncorretto:17 AS builder
WORKDIR /app

COPY ./gradlew .
COPY ./gradle ./gradle
COPY ./build.gradle .
COPY ./settings.gradle .
COPY ./src ./src
COPY .env ./

# gradlew 실행권한 부여
RUN chmod +x ./gradlew

# jar 파일 생성
RUN ./gradlew clean bootJar

FROM amazoncorretto:17
WORKDIR /app
COPY --from=builder /app/build/libs/nbbang-0.0.1-SNAPSHOT.jar app.jar
COPY .env ./
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]