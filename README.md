# SSGINC_unnie
위치 및 영수증 기반 뷰티 업체 리뷰 커뮤니티 웹 플랫폼 "언니어때" 입니다.

## 프로젝트 정보
- **프로젝트 기간:** 2025년 2월 11일 ~ 2025년 3월 27일


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
  - 커뮤니티

- **민소원**
  - 리뷰  

- **이가영**
  - 회원  

- **이상우**
  - 업체  


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

### 주요 기능
1. **업체 조회 기능**

2. **커뮤니티 기능**

3. **리뷰 기능**

4. **회원 기능**

### 배포(재배포 예정)
1. **URL**
   - https://www.unnieuttae.store
   - 테스트용 계정
      - id: test@example.com
      - pwd: 1234

2. **GitHub Actions 기반의 CI/CD Pipeline 구축**
- Git, GitHub Actions, AWS Elastic Beanstalk(EC2: Nginx-Tomcat, 오토스케일링/로드밸런싱 지원), RDS(MySQL), Docker/Redis, S3를 연계하여 자동 빌드/무중단 배포를 구축하였습니다.


  
### 개발 환경

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

- **Collaborates:**
  [![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white)](https://github.com/)


### 사용한 외부 API

 
- **API**
   - 다음카카오 주소 API
   - Google/Kakao/Naver 로그인 API
   - Coolsms 핸드폰 문자인증 API
   - NCP OCR API
   - chat gpt API
   - NCP GeoCoding API
   - 공공데이터 포털 사업자 진위여부확인 API


---

## 3. 프로젝트 구조

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

## 5.UI 구성 및 기능

### 기능

**회원가입**

![회원가입](https://github.com/user-attachments/assets/c72cffb2-fc35-4c4d-b582-2ca77d3d9407)

**소셜로그인**

![소셜로그인](https://github.com/user-attachments/assets/ac018eeb-fae8-4f2a-8b31-1947792938e9)

**ID/PW 찾기**

![0403](https://github.com/user-attachments/assets/e414be75-1c5c-4f7e-8950-85445bac47c0)

**내주변 뷰티샵 조회**

![내주변 뷰티샵 조회](https://github.com/user-attachments/assets/b49392b9-597e-40c2-be5e-5f30e25d4dbe)

**리뷰 작성**

![리뷰작성](https://github.com/user-attachments/assets/dce3c816-f7ae-4893-ab5f-024edf35bc47)

### 마이페이지

**업체 등록**

![업체등록](https://github.com/user-attachments/assets/f24ec2d3-0b7b-4cf0-8a1c-bb1207376755)

**디자이너 등록**

![0403 (1)](https://github.com/user-attachments/assets/7b8989e7-47b6-4d0d-a8a1-1186d977915a)

**시술 등록**

![시술등록](https://github.com/user-attachments/assets/fd0221f0-c54b-437a-9ae2-f5df17a8b83b)

**회원 정보 수정**

![회원수정](https://github.com/user-attachments/assets/48e72e69-b9c1-4db1-9e7b-557f032a862b)

**리뷰 수정/삭제**

![리뷰 수정삭제](https://github.com/user-attachments/assets/ab791ce0-c815-49da-be98-307a9f4d76d3)

**업체/디자이너/시술 수정/삭제**

![업체수정](https://github.com/user-attachments/assets/a1c6142f-c9b8-47e9-8853-05c84b544cd7)



### 관리자페이지

**모든 회원 조회**

![회원조회](https://github.com/user-attachments/assets/fb942fd4-34c1-4f84-97bf-593d1221fff8)


**승인 요청 업체 조회/승인/거절**

![승인요청업체조회](https://github.com/user-attachments/assets/ef569a60-ddca-4c61-8685-04d862b29b07)

**모든 업체 조회**

![모든업체조회](https://github.com/user-attachments/assets/51d27621-586b-44b3-b87c-7f2910eebf58)

## 6. 시연 영상
https://youtu.be/EprBTZnKQQE


 
