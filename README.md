# SSGINC_unnie
![image](https://github.com/user-attachments/assets/4c4f5183-9d08-433f-8639-abf8835aea47)

위치 및 영수증 기반 뷰티 업체 리뷰 커뮤니티 웹 플랫폼 "언니어때" 입니다.

## 프로젝트 정보
- **프로젝트 기간:** 2025년 2월 11일 ~ 2025년 3월 27일

- [프로젝트 정보](#프로젝트-정보)
- [1. 프로젝트 소개](#1-프로젝트-소개)
  - [팀원 구성](#팀원-구성)
  - [팀원별 개발 내용](#팀원별-개발-내용)
- [2. 아키텍쳐와 브랜치 전략](#2-아키텍쳐와-브랜치-전략)
  - [아키텍쳐](#아키텍쳐)
  - [브랜치 전략](#브랜치-전략)
  - [배포(재배포 예정)](#배포재배포-예정)
- [3. 개발 환경](#3-개발-환경)
  - [사용한 외부 API](#사용한-외부-api)
- [4. 프로젝트 구조](#4-프로젝트-구조)
- [5. UI 구성 및 기능](#5-ui-구성-및-기능)
    - [기능](#기능)
        - [회원가입](#회원가입)
        - [소셜로그인](#소셜로그인)
        - [ID/PW 찾기](#idpw-찾기)
        - [내주변 뷰티샵 조회](#내주변-뷰티샵-조회)
        - [리뷰 작성](#리뷰-작성)
    - [마이페이지](#마이페이지)
        - [업체 등록](#업체-등록)
        - [디자이너 등록](#디자이너-등록)
        - [시술 등록](#시술-등록)
        - [회원 정보 수정](#회원-정보-수정)
        - [회원 탈퇴](#회원-탈퇴)
        - [리뷰 수정/삭제](#리뷰-수정삭제)
        - [업체/디자이너/시술 수정/삭제](#업체디자이너시술-수정삭제)
    - [관리자페이지](#관리자페이지)
        - [모든 회원 조회](#모든-회원-조회)
        - [승인 요청 업체 조회/승인/거절](#승인-요청-업체-조회승인거절)
        - [모든 업체 조회](#모든-업체-조회)
- [6. 시연 영상](#6-시연-영상)




<br>

## 1. 프로젝트 소개

- 이 프로젝트는 **영수증 기반 리뷰 시스템**을 통해 거짓 리뷰를 최소화하고, 실제 이용자만 리뷰를 작성할 수 있도록 보장함으로써 신뢰성 높은 정보를 제공합니다.
- 사용자의 현재 위치 또는 특정 지역을 기반으로 원하는 뷰티샵을 쉽게 검색할 수 있으며, **필터링 기능**과 **지도 연동**을 통해 직관적으로 샵 위치를 파악하고 맞춤형 검색을 지원합니다.

<br>


## 팀원 구성

<div align="center">

| **김동현** | **민소원** | **이가영** | **이상우** |
| :------: | :------: | :------: | :------: |
| [@DHKim96](https://github.com/DHKim96) | [@wishs2](https://github.com/wishs2) | [@GaYoung28](https://github.com/GaYoung28) | [@sangwooLee1231](https://github.com/sangwooLee1231) |

</div>

### 팀원별 개발 내용

- **김동현**  
  <span style="color:red">**중도하차멤버**</span>
  - 프로젝트 설계
  - 스켈레톤 코드 작성

- **민소원**
  - 영수증 리뷰 작성(OCR)
  - 리뷰 조회/수정/삭제
  - 리뷰 요약

- **이가영**
  - DB설계
  - 회원가입 및 로그인(JWT)
  - oauth2.0 로그인
  - ID/PW 찾기
  - 회원 정보 수정
  - 회원 탈퇴
  - 모든 회원 조회
  - 관리자 페이지
  - 메인 페이지

- **이상우**
  - 내주변 뷰티샵 조회
  - 업체/디자이너/시술 등록
  - 내 업체 조회
  - 업체/디자이너/시술 수정/삭제
  - 모든 업체 조회
  - 승인 요청 업체 조회/승인/거절
  - HTTPS 웹 서버 구축
  - ci/cd




<br>

## 2. 아키텍쳐와 브랜치 전략

### 아키텍쳐
<img width="9416" alt="최종 아키텍처" src="https://github.com/user-attachments/assets/2ba8c618-b078-4769-99a2-06c255a2b0c3" />


### 브랜치 전략

- Git-flow 전략을 기반으로 main, develop 브랜치와 feature 보조 브랜치를 운용했습니다.
- main, develop, Feat 브랜치로 나누어 개발을 하였습니다.
    - **main** 브랜치는 배포 단계에서만 사용하는 브랜치입니다.
    - **develop** 브랜치는 개발 단계에서 git-flow의 master 역할을 하는 브랜치입니다.
    - **Feat** 브랜치는 기능 단위로 독립적인 개발 환경을 위하여 사용하고 merge 후 각 브랜치를 삭제해주었습니다.

<br>


### 배포(재배포 예정)
1. **URL**
   - https://www.unnieuttae.store
   - 테스트용 계정
      - id: test@example.com
      - pwd: 1234

2. **GitHub Actions 기반의 CI/CD Pipeline 구축**
- Git, GitHub Actions, AWS Elastic Beanstalk(EC2: Nginx-Tomcat, 오토스케일링/로드밸런싱 지원), RDS(MySQL), Docker/Redis, S3를 연계하여 자동 빌드/무중단 배포를 구축하였습니다.


  
### 3. 개발 환경

- **IDE:**
  [![Visual Studio Code](https://img.shields.io/badge/Visual%20Studio%20Code-0078d7?style=flat-square&logo=visual%20studio%20code&logoColor=white)](https://code.visualstudio.com/)
  [![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ%20IDEA-000000?style=flat-square&logo=intellij-idea&logoColor=white)](https://www.jetbrains.com/idea/)


- **Frontends:**
  <img src="https://img.shields.io/badge/html5-E34F26?style=flat-square&logo=html5&logoColor=white"/>
  <img src="https://img.shields.io/badge/css3-1572B6?style=flat-square&logo=css3&logoColor=white"/>
  <img src="https://img.shields.io/badge/javascript-F7DF1E?style=flat-square&logo=javascript&logoColor=white"/>

- **Backends:**
  [![Java](https://img.shields.io/badge/Java-007396?style=flat-square&logo=java&logoColor=white)](https://www.oracle.com/java/)
  <img src="https://img.shields.io/badge/Spring-6DB33F?style=flat-square&logo=Spring&logoColor=white"/>
  <img src="https://img.shields.io/badge/Mybatis-000000?style=flat-square&logo=Mybatis&logoColor=white"/>

- **Database:**
[![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=redis&logoColor=white)](https://redis.io/)

- **Collaborates:**
  [![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white)](https://github.com/)
  [![Slack](https://img.shields.io/badge/Slack-4A154B?style=flat-square&logo=slack&logoColor=white)](https://slack.com/)
  [![Notion](https://img.shields.io/badge/Notion-000000?style=flat-square&logo=notion&logoColor=white)](https://www.notion.so/)

- **DevOps/ Infra:**
[![AWS](https://img.shields.io/badge/AWS-232F3E?style=flat-square&logo=amazon-aws&logoColor=white)](https://aws.amazon.com/)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)](https://www.docker.com/)



### 사용한 외부 API

 
- **API**
   - 다음카카오 주소 API
   - Google/Kakao/Naver 로그인 API
   - CoolSMS 핸드폰 문자인증 API
   - NCP OCR API
   - chat gpt API
   - NCP GeoCoding API
   - 공공데이터 포털 사업자 진위여부확인 API


---

## 4. 프로젝트 구조

```
📦src
 ┣ 📂main
 ┃ ┣ 📂generated
 ┃ ┣ 📂java
 ┃ ┃ ┗ 📂com
 ┃ ┃ ┃ ┗ 📂ssginc
 ┃ ┃ ┃ ┃ ┗ 📂unnie
 ┃ ┃ ┃ ┃ ┃ ┣ 📂admin
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂member
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂report
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂shop
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂mapper
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂serviceImpl
 ┃ ┃ ┃ ┃ ┃ ┣ 📂common
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂config
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂converter
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂exception
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂handler
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂interceptor
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂listener
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂redis
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂util
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂generator
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂parser
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂validation
 ┃ ┃ ┃ ┃ ┃ ┣ 📂community
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂board
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂comment
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂member
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂mapper
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂serviceImpl
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂vo
 ┃ ┃ ┃ ┃ ┃ ┣ 📂like
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂mapper
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂serviceImpl
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂vo
 ┃ ┃ ┃ ┃ ┃ ┣ 📂media
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂mapper
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂serviceImpl
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂vo
 ┃ ┃ ┃ ┃ ┃ ┣ 📂member
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂mapper
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂serviceImpl
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂vo
 ┃ ┃ ┃ ┃ ┃ ┣ 📂mypage
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂community
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂member
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂review
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂shop
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂mapper
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂serviceImpl
 ┃ ┃ ┃ ┃ ┃ ┣ 📂notification
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂mapper
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂repository
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂serviceImpl
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂vo
 ┃ ┃ ┃ ┃ ┃ ┣ 📂report
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂mapper
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂serviceImpl
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂vo
 ┃ ┃ ┃ ┃ ┃ ┣ 📂review
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂debounce
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂serviceImpl
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂mapper
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂ReviewOCR
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂serviceImpl
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂vo
 ┃ ┃ ┃ ┃ ┃ ┣ 📂shop
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂mapper
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂ServiceImpl
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂vo
 ┃ ┗ 📂resources
 ┃ ┃ ┣ 📂attach
 ┃ ┃ ┣ 📂mapper
 ┃ ┃ ┣ 📂static
 ┃ ┃ ┃ ┣ 📂assets
 ┃ ┃ ┃ ┣ 📂css
 ┃ ┃ ┃ ┃ ┣ 📂admin
 ┃ ┃ ┃ ┃ ┃ ┣ 📂member
 ┃ ┃ ┃ ┃ ┃ ┗ 📂shop
 ┃ ┃ ┃ ┃ ┣ 📂board
 ┃ ┃ ┃ ┃ ┣ 📂member
 ┃ ┃ ┃ ┃ ┣ 📂mypage
 ┃ ┃ ┃ ┃ ┃ ┣ 📂member
 ┃ ┃ ┃ ┃ ┃ ┣ 📂review
 ┃ ┃ ┃ ┃ ┃ ┣ 📂shop
 ┃ ┃ ┃ ┃ ┣ 📂review
 ┃ ┃ ┃ ┃ ┣ 📂shop
 ┃ ┃ ┃ ┣ 📂img
 ┃ ┃ ┃ ┃ ┣ 📂common
 ┃ ┃ ┃ ┃ ┣ 📂member
 ┃ ┃ ┃ ┃ ┣ 📂shop
 ┃ ┃ ┃ ┣ 📂js
 ┃ ┃ ┃ ┃ ┣ 📂admin
 ┃ ┃ ┃ ┃ ┃ ┣ 📂member
 ┃ ┃ ┃ ┃ ┃ ┗ 📂shop
 ┃ ┃ ┃ ┃ ┣ 📂member
 ┃ ┃ ┃ ┃ ┣ 📂mypage
 ┃ ┃ ┃ ┃ ┃ ┣ 📂member
 ┃ ┃ ┃ ┃ ┃ ┣ 📂review
 ┃ ┃ ┃ ┃ ┃ ┗ 📂shop
 ┃ ┃ ┃ ┃ ┣ 📂review
 ┃ ┃ ┃ ┃ ┣ 📂shop
 ┃ ┃ ┃ ┗ 📂upload
 ┃ ┃ ┣ 📂templates
 ┃ ┃ ┃ ┣ 📂admin
 ┃ ┃ ┃ ┃ ┣ 📂member
 ┃ ┃ ┃ ┃ ┗ 📂shop
 ┃ ┃ ┃ ┣ 📂community
 ┃ ┃ ┃ ┣ 📂member
 ┃ ┃ ┃ ┣ 📂mypage
 ┃ ┃ ┃ ┃ ┣ 📂member
 ┃ ┃ ┃ ┃ ┣ 📂review
 ┃ ┃ ┃ ┃ ┣ 📂shop
 ┃ ┃ ┃ ┣ 📂review
 ┃ ┃ ┃ ┣ 📂shop
 ┗ 📂test
 ┃ ┣ 📂generated_tests
 ┃ ┣ 📂java
 ┃ ┃ ┗ 📂com
 ┃ ┃ ┃ ┗ 📂ssginc
 ┃ ┗ 📜test.iml
```

<br>

---


## 5. UI 구성 및 기능

### 기능

#### 회원가입
![회원가입](https://github.com/user-attachments/assets/c72cffb2-fc35-4c4d-b582-2ca77d3d9407)

#### 소셜로그인
![소셜로그인](https://github.com/user-attachments/assets/ac018eeb-fae8-4f2a-8b31-1947792938e9)

#### ID/PW 찾기
![ID/PW 찾기](https://github.com/user-attachments/assets/e414be75-1c5c-4f7e-8950-85445bac47c0)

#### 내주변 뷰티샵 조회
![내주변 뷰티샵 조회](https://github.com/user-attachments/assets/b49392b9-597e-40c2-be5e-5f30e25d4dbe)
- 사용자의 현재 위치를 가져오기 위해 브라우저의 Geolocation API를 사용하고, 해당 위치를 중심으로 네이버 지도를 초기화합니다.
- 각 상점의 위치 데이터를 바탕으로 Naver Maps의 마커를 생성하고, 카테고리에 따라 서로 다른 아이콘으로 표시합니다.
- 사용자의 좌표를 기반으로 실제 주소 정보를 받아와 페이지 상에 표시합니다.
- 상점 목록은 정렬(오름차순, 내림차순) 및 필터링된 결과를 렌더링하며, 각 상점에 대한 미디어 이미지를 캐러셀 형태로 보여줍니다.
- Daum 우편번호 서비스를 사용해 사용자가 주소를 검색하면, 해당 주소를 네이버 지도 Geocoder를 통해 좌표로 변환한 후 지도를 재설정합니다.
- 바텀시트(하단 패널)의 드래그 기능을 통해 높이를 조절할 수 있으며, 카테고리 버튼을 클릭하면 해당 카테고리의 상점 목록이 다시 로드됩니다.
- 상세 페이지에서는 탭 버튼을 통해 홈, 디자이너, 시술, 정보 탭 간 전환이 가능합니다.


#### 리뷰 작성
![리뷰 작성](https://github.com/user-attachments/assets/dce3c816-f7ae-4893-ab5f-024edf35bc47)

### 마이페이지

#### 업체 등록
![업체 등록](https://github.com/user-attachments/assets/f24ec2d3-0b7b-4cf0-8a1c-bb1207376755)
- 입력 받은 업체 정보에서 날짜 형식을 정리하고, 사업자 등록번호의 유효성을 검증합니다.
- Daum 우편번호 API를 활용하여 사용자가 주소를 검색하면, 선택한 주소가 입력 필드에 자동으로 채워집니다.
- 업체 등록 성공 후 반환된 업체 ID를 활용하여, 미디어 파일을 S3에 저장합니다.
- 업체 등록 및 미디어 파일 업로드가 완료되면, 자동으로 디자이너 등록 페이지로 리다이렉션됩니다.

#### 디자이너 등록
![디자이너 등록](https://github.com/user-attachments/assets/7b8989e7-47b6-4d0d-a8a1-1186d977915a)
  - 사용자가 모달 창에서 디자이너 이름, 소개, 그리고 프로필 이미지를 입력 및 업로드합니다.
  - 입력된 정보와 파일은 Designers 배열에 임시 저장되고, UI에 미리보기와 함께 목록으로 표시됩니다.
  - 수정 및 삭제 기능을 통해 사용자가 입력한 항목을 관리할 수 있습니다.
  - 등록 버튼을 누르면, Designers 배열의 데이터와 파일들이 FormData로 묶여 성공 시 시술 등록 페이지로 이동합니다.


#### 시술 등록
![시술 등록](https://github.com/user-attachments/assets/fd0221f0-c54b-437a-9ae2-f5df17a8b83b)
  - 사용자가 모달 창에서 시술 이름, 소개, 그리고 프로필 이미지를 입력 및 업로드합니다.
  - 입력된 정보와 파일은 Procedures 배열에 임시 저장되고, UI에 미리보기와 함께 목록으로 표시됩니다.
  - 수정 및 삭제 기능을 통해 사용자가 입력한 항목을 관리할 수 있습니다.
  - 등록 버튼을 누르면, Procedures 배열의 데이터와 파일들이 FormData로 묶여 성공 시 메인페이지로 이동합니다.

#### 회원 정보 수정
![회원 정보 수정](https://github.com/user-attachments/assets/48e72e69-b9c1-4db1-9e7b-557f032a862b)

#### 회원 탈퇴
![회원 탈퇴](https://github.com/user-attachments/assets/77a10514-7d03-4639-b2b1-878960306298)

#### 리뷰 수정/삭제
![리뷰 수정/삭제](https://github.com/user-attachments/assets/ab791ce0-c815-49da-be98-307a9f4d76d3)

#### 업체/디자이너/시술 수정/삭제
![업체/디자이너/시술 수정/삭제](https://github.com/user-attachments/assets/a1c6142f-c9b8-47e9-8853-05c84b544cd7)
  - 수정시 업체/디자이너/시술의 상세 정보를 가져와 폼 필드에 자동으로 채웁니다.
  - 업체 삭제 시, 스토어드 프로시저를 사용하여 관련된 여러 테이블에서 순차적으로 삭제 작업을 수행하도록 구현합니다.
  - 이를 통해 개별 DELETE 쿼리를 여러 번 호출하는 대신, 단일 트랜잭션 내에서 모든 작업을 처리할 수 있어 데이터베이스의 네트워크 왕복 시간을 줄이고, 데이터 일관성을 유지하며, 전체 삭제 성능이 향상되는 효과를 얻었습니다.


### 관리자페이지

#### 모든 회원 조회
![모든 회원 조회](https://github.com/user-attachments/assets/fb942fd4-34c1-4f84-97bf-593d1221fff8)

#### 승인 요청 업체 조회/승인/거절
![승인 요청 업체 조회/승인/거절](https://github.com/user-attachments/assets/ef569a60-ddca-4c61-8685-04d862b29b07)
  - 업체 목록은 페이지네이션을 적용하여, 이전/다음 버튼으로 데이터를 동적으로 업데이트합니다.
  - 각 업체 행에 "상세보기" 버튼을 배치하여 드롭다운 방식으로 상세 정보를 표시하고, 승인/거절 버튼으로 상태 변경이 가능합니다.
  - 승인 시, 업체 상태를 업데이트하고, 업체 주소를 기반으로 Naver Geocoding API를 호출하여 위도/경도 좌표를 업데이트합니다.
  - 업체 소유자의 회원 역할을 "ROLE_MANAGER"로 변경하여 업체 담당자로 권한을 부여합니다.
  - 스토어드 프로시저를 사용해 관련 테이블에서 순차적으로 데이터를 삭제함으로써, 개별 DELETE 쿼리를 여러 번 호출하는 것보다 네트워크 왕복 횟수를 줄이고 처리 속도를 개선합니다.

#### 모든 업체 조회
![모든 업체 조회](https://github.com/user-attachments/assets/ad3f853d-2d4a-47a3-8497-9cf8a133dca0)
  - 업체 목록은 페이지네이션을 적용하여, 이전/다음 버튼으로 데이터를 동적으로 업데이트합니다.
  - 각 업체 행에 "상세보기" 버튼을 배치하여 드롭다운 방식으로 상세 정보를 표시하고, 승인/거절 버튼으로 상태 변경이 가능합니다.

<br>




## 6. 시연 영상
https://youtu.be/EprBTZnKQQE



 
