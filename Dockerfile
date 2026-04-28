FROM gradle:8.7-jdk21 AS builder

WORKDIR /app
COPY . .
RUN gradle war --no-daemon


FROM tomcat:10.1-jdk21-temurin

RUN rm -rf /usr/local/tomcat/webapps/*

COPY --from=builder /app/build/libs/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080