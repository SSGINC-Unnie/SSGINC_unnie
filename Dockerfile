# --- builder ---
FROM gradle:8.7-jdk17-alpine AS builder
WORKDIR /workspace
COPY --chown=gradle:gradle . .

RUN /bin/sh -lc '\
  set -e; \
  CMD=./gradlew; \
  if [ -f gradlew ]; then chmod +x gradlew; else CMD=gradle; fi; \
  # 루트에서 시도
  if $CMD tasks --all | grep -q "[[:space:]]bootJar[[:space:]]"; then \
    echo ">> root: bootJar"; \
    $CMD bootJar --no-daemon; \
  elif $CMD tasks --all | grep -q "[[:space:]]assemble[[:space:]]"; then \
    echo ">> root: assemble"; \
    $CMD assemble --no-daemon; \
  else \
    echo ">> root에 표준 태스크 없음. 하위모듈 스캔"; \
    FOUND=""; \
    # 하위모듈 목록 추출 (예: :app, :server)
    SUBS=$($CMD -q projects | sed -n "s/.*Project '\\(:[^']*\\)'.*/\\1/p"); \
    # 1) bootJar 찾기
    for S in $SUBS; do \
      if $CMD ${S}:tasks --all | grep -q "[[:space:]]bootJar[[:space:]]"; then \
        echo ">> $S: bootJar"; \
        $CMD ${S}:bootJar --no-daemon; FOUND=1; break; \
      fi; \
    done; \
    # 2) assemble 찾기
    if [ -z "$FOUND" ]; then \
      for S in $SUBS; do \
        if $CMD ${S}:tasks --all | grep -q "[[:space:]]assemble[[:space:]]"; then \
          echo ">> $S: assemble"; \
          $CMD ${S}:assemble --no-daemon; FOUND=1; break; \
        fi; \
      done; \
    fi; \
    if [ -z "$FOUND" ]; then \
      echo ">> bootJar/assemble 태스크를 어디서도 찾지 못했습니다."; \
      $CMD -q projects || true; \
      $CMD tasks --all || true; \
      exit 1; \
    fi; \
  fi'

FROM amazoncorretto:17-alpine-jdk
WORKDIR /app
COPY --from=builder /workspace/**/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
