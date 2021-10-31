FROM alpine:3.13.6
LABEL maintainer=rnett
WORKDIR /app
COPY frontend-alpine-linux jre/

COPY app.jar .

ENTRYPOINT ["jre/bin/java", "-jar", "app.jar", "com.rnett.ApplicationKt"]
EXPOSE 8081

