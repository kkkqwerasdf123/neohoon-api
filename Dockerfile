FROM openjdk:17
LABEL maintainer="kkkqwerasdf123 <kkkqwerasdf123@naver.com>"

VOLUME ["/var/log/api"]

ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]