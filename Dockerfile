# --- builder ---
FROM gradle:8.7-jdk17-alpine AS builder
WORKDIR /workspace
COPY --chown=gradle:gradle . .
RUN apk add --no-cache bash \
RUN bash -lc 'if gradle tasks --all | grep -q bootJar; then gradle bootJar --no-daemon; else gradle jar --no-daemon; fi'

# --- runtime ---
FROM amazoncorretto:17-alpine-jdk
WORKDIR /app
# 빌더에서 생성된 JAR을 복사 (하위모듈까지 포괄)
COPY --from=builder /workspace/**/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
