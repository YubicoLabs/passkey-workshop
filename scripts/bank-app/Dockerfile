FROM eclipse-temurin
#COPY com.yubicolabs.passkey_rp.jar com.yubicolabs.passkey_rp.jar
COPY /source /source
RUN apt-get update; apt-get install -y maven; mvn -f /source/pom.xml clean package
#ENTRYPOINT ["cd", "source", "&&", "mvn", "clean", "package"]
ENTRYPOINT ["java", "-jar", "/source/target/com.yubicolabs.bank_app.jar"]