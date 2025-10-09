# --- builder ---
FROM gradle:8.7-jdk17-alpine AS builder
WORKDIR /workspace
COPY --chown=gradle:gradle . .

RUN /bin/sh -lc 'if gradle tasks --all | grep -q "[[:space:]]bootJar[[:space:]]"; then gradle bootJar --no-daemon; else gradle assemble --no-daemon; fi'

FROM amazoncorretto:17-alpine-jdk
WORKDIR /app
COPY --from=builder /workspace/**/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
