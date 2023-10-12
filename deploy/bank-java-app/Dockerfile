FROM eclipse-temurin
COPY /source /source
RUN apt-get update; apt-get install -y maven; mvn -f /source/pom.xml clean package
#ENTRYPOINT ["cd", "source", "&&", "mvn", "clean", "package"]
ENTRYPOINT ["java", "-jar", "/source/target/com.yubicolabs.bank_app.jar"]