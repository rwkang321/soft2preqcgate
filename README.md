# soft2preqcgate

사전 품질검증(Pre-QC) 게이트 API 서버.

## 스택

| 영역 | 사용 기술 |
|---|---|
| 빌드 | Gradle 9.5.1 (wrapper), Java 21 |
| 프레임워크 | Spring Boot 4.0.7 (Spring MVC) |
| DB | Oracle (ojdbc11), HikariCP |
| 영속성 | MyBatis 4.0.1 (mapper-spring-boot-starter) |
| 스키마 | Flyway + flyway-database-oracle |
| 인증/인가 | Spring Security (STATELESS) |
| 캐시/세션 | Redis (Spring Data Redis, Lettuce) |
| API 문서 | springdoc-openapi 3.0.3 (Swagger UI) |

## 프로젝트 구조

```
src/main/java/com/soft2preqcgate/
├── Soft2preqcgateApplication.java
├── common/
│   ├── config/MyBatisConfig.java      # com.soft2preqcgate.**.mapper 스캔
│   └── security/SecurityConfig.java   # STATELESS, CORS, BCrypt
└── health/controller/HealthController.java

src/main/resources/
├── application.yaml                   # 공통 + 기본 프로파일(dev)
├── application-dev.yaml
├── application-prod.yaml
├── db/migration/                      # Flyway V___*.sql
└── mapper/                            # MyBatis XML (classpath:mapper/**/*.xml)
```

## 실행

환경변수로 접속 정보를 주입한다. 리포지토리에 자격증명을 커밋하지 않는다.

```bash
export SOFT2PREQCGATE_DB_URL="jdbc:oracle:thin:@//host:1521/service"
export SOFT2PREQCGATE_DB_USERNAME="..."
export SOFT2PREQCGATE_DB_PASSWORD="..."
export SOFT2PREQCGATE_REDIS_HOST="localhost"
export SOFT2PREQCGATE_REDIS_PASSWORD="..."

./gradlew bootRun
```

- 기본 프로파일: `dev` (운영은 `SPRING_PROFILES_ACTIVE=prod` 명시)
- 기본 포트: `8082` (`SOFT2PREQCGATE_API_PORT`로 변경)
- 헬스체크: `GET /api/health` (인증 불필요)
- Swagger UI: `/swagger-ui.html` (dev 기본 활성, prod 기본 비활성)

## 빌드 / 테스트

```bash
./gradlew build     # 컴파일 + 테스트
./gradlew test
```

## 주요 환경변수

| 변수 | 기본값 | 설명 |
|---|---|---|
| `SOFT2PREQCGATE_API_PORT` | 8082 (dev) | 서버 포트 |
| `SOFT2PREQCGATE_DB_URL` | — | Oracle JDBC URL (필수) |
| `SOFT2PREQCGATE_DB_USERNAME` / `_PASSWORD` | — | DB 계정 (필수) |
| `SOFT2PREQCGATE_DB_MAX_POOL_SIZE` | 10 (dev) / 20 (prod) | Hikari 풀 크기 |
| `SOFT2PREQCGATE_REDIS_HOST` | localhost (dev) | Redis 호스트 |
| `SOFT2PREQCGATE_REDIS_PORT` | 6379 | Redis 포트 |
| `SOFT2PREQCGATE_FLYWAY_ENABLED` | true | 기동 시 마이그레이션 |
| `SOFT2PREQCGATE_SWAGGER_ENABLED` | true (dev) / false (prod) | Swagger 노출 |
| `SOFT2PREQCGATE_ALLOWED_ORIGIN` | http://localhost:9091 (dev) | CORS 허용 origin |
| `SOFT2PREQCGATE_BCRYPT_STRENGTH` | 12 | BCrypt 강도 |
