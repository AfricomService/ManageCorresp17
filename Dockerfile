FROM eclipse-temurin:17-jdk

EXPOSE 8056

ADD target/corresp-manage-0.0.1-SNAPSHOT.jar application.jar

ENTRYPOINT ["java", "-jar", "/application.jar"]
