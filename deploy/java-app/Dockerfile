FROM eclipse-temurin:21

WORKDIR /app
COPY source .

RUN ./mvnw --batch-mode --update-snapshots package

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/target/com.yubicolabs.passkey_rp.jar"]