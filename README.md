# Readcovery

> **read + discovery** — 인용구로 나를 발견하는 독서 기록 서비스

## 소개

좋은 문장을 모으는 사람은 많지만, 그 문장들이 자신에 대해 무엇을 말해주는지 돌아볼 기회는 적습니다.
Readcovery는 사용자가 모은 인용구를 AI로 분석해 독자 자신의 관심사와 가치관을 비춰주는 거울이 되고자 합니다.

## 주요 기능 (MVP)

1.  책 검색 (카카오 책 검색 API)
2.  내 서재 관리 (읽는중 / 완독 / 읽고싶음)
3.  책별 인용구 등록 및 조회
4.  AI 기반 독서 분석 (한 줄 요약 / 키워드 / 독서 성향)

##  추가 예정 기능

- 인용구 전문 검색
- 독서 통계 (월별 / 장르별)
- 이메일 알림
- 인용구 공개 피드

## 기술 스택

### Backend
- Java 17
- Spring Boot 4.0.6
- Spring Data JPA
- Spring Security + JWT
- MySQL 8.0

### Frontend
- React 18
- Tailwind CSS

### 외부 API
- 카카오 책 검색 API
- OpenAI API

### 인프라 (예정)
- AWS EC2 / RDS / S3
- Redis (캐싱)
- Docker
- GitHub Actions (CI/CD)

## ERD

(작성 예정)

## 트러블슈팅

### Git 머지 충돌 해결 - .gitignore 파일
- 상황: GitHub에서 레포 생성 시 Java 템플릿으로 `.gitignore`가 자동 생성됨.
  동시에 Spring Initializr에서 다운로드한 프로젝트에도 Gradle 전용 `.gitignore`가 포함되어
  `git pull` 시 충돌 발생.
- 원인: 동일 파일이 양쪽 저장소에 독립적으로 생성되어 git이 자동 병합 불가.
- 해결: 두 파일을 비교한 결과, Spring Initializr 버전이 IntelliJ/Gradle 환경에 더 적합해
  로컬 버전을 채택. 추가로 비밀 정보 파일(`application-secret.yml`, `.env`) 무시 규칙을 보강.
- 배운 점: `.gitignore`처럼 두 환경에서 독립적으로 생성될 수 있는 파일은 한쪽에서만
  관리하거나, 초기 셋업 시 한 곳을 비워두는 게 안전함.

### WebClient의 baseUrl이 무시되는 케이스
- 상황: WebClient에 baseUrl을 설정했지만 카카오 API 호출 시 Connection refused 발생
- 원인: URI 객체로 uri() 호출하면 baseUrl이 무시됨. UriComponentsBuilder로 만든
  URI에는 호스트 정보가 없어서 localhost로 요청이 나감
- 해결: uri(Function<UriBuilder, URI>) 람다 방식으로 변경. WebClient 내부 빌더는
  baseUrl을 자동 적용함
- 배운 점: WebClient의 uri() 메서드는 인자 타입에 따라 baseUrl 적용 여부가 달라짐.
  String 또는 람다 사용이 안전

### Jackson SNAKE_CASE 전략과 요청 필드 매핑 불일치
- 상황: MyBook 등록 시 userId, bookId가 계속 null로 들어와 검증 실패(400).
  회원가입, 책 등록은 정상이었음.
- 원인: 카카오 API 연동 시 추가한 jackson.property-naming-strategy: SNAKE_CASE 설정 때문.
  Jackson이 자바 camelCase 필드(userId)를 JSON snake_case(user_id)로 기대하는데,
  요청은 camelCase로 보내서 매핑 실패. email, isbn처럼 단어 하나인 필드는
  두 표기가 같아 문제가 드러나지 않다가, userId처럼 합성어에서 처음 발생.
- 해결: API 요청/응답을 snake_case로 통일 (REST 관례에도 부합).
- 배운 점: 전역 네이밍 전략은 모든 DTO에 영향을 미치므로, 외부 API 응답 매핑을 위해
  도입할 때 자체 API의 요청/응답 규약도 함께 통일해야 함.

## 실행 방법

### 사전 요구사항
- Java 17 이상
- MySQL 8.0
- Spring Boot 4.0.6

### 1. 데이터베이스 준비
```sql
CREATE DATABASE readcovery DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 비밀 정보 설정
`src/main/resources/application-secret.yml` 파일을 생성하고 다음 내용을 입력:

```yaml
spring:
  datasource:
    password: 본인_MySQL_비밀번호

kakao:
  api-key: 본인_카카오_REST_API_키

jwt:
  secret: 무작위_영숫자_최소_32자_이상
  access-token-validity: 3600000
```

> ⚠️ `application-secret.yml`은 `.gitignore`에 등록되어 있어 Git에 포함되지 않습니다.
> 비밀 정보는 절대 코드에 직접 작성하지 않도록 주의합니다.

### 3. 애플리케이션 실행
```bash
./gradlew bootRun
```
