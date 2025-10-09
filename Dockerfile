# 베이스 이미지 선택
FROM amazoncorretto:17-alpine-jdk

# 작업자 정보
LABEL maintainer="tkdguq0654@naver.com"

# JAR 파일 변수 선언
ARG JAR_FILE=build/libs/*.jar

# JAR 파일을 Docker 이미지 안으로 복사
COPY ${JAR_FILE} app.jar

# 애플리케이션 실행 포트 설정
EXPOSE 8080

# Docker 컨테이너가 시작될 때 실행할 명령어
ENTRYPOINT ["java","-jar","/app.jar"]