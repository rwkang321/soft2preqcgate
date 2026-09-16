# soft2preqcgate

사전 품질검증(Pre-QC) 게이트 API 서버.

## 스택

soft2cost2와 동일한 구성으로 맞춘다.

| 영역 | 사용 기술 |
|---|---|
| 빌드 | Gradle 9.7.1 (wrapper), Java 17 |
| 프레임워크 | Spring Boot 4.0.8 (Spring MVC) |
| DB | Oracle (ojdbc17), HikariCP |
| 영속성 | MyBatis 4.0.1 (mybatis-spring-boot-starter) |
| 스키마 | Flyway + flyway-database-oracle |
| 인증/인가 | Spring Security (STATELESS, `@EnableMethodSecurity`) |
| 캐시/세션 | Redis (Spring Data Redis, Lettuce) |
| 검증 | spring-boot-starter-validation |

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
├── application-test.yaml
├── db/migration/                      # Flyway V___*.sql
└── mapper/                            # MyBatis XML (classpath:mapper/**/*.xml)
```

## 실행

DB/Redis 접속 정보는 soft2cost1 / soft2cost2와 같은 `SOFT2COST_*` 환경변수를 공유한다.
애플리케이션 고유 설정만 `SOFT2PREQCGATE_*`를 쓴다.

```bash
export SOFT2COST_DB_URL="jdbc:oracle:thin:@//host:1521/service"
export SOFT2COST_DB_USERNAME="..."
export SOFT2COST_DB_PASSWORD="..."
export SOFT2COST_REDIS_HOST="localhost"
export SOFT2COST_REDIS_PASSWORD="..."

./gradlew bootRun
```

- 기본 프로파일: `dev` (운영은 `SPRING_PROFILES_ACTIVE=prod` 명시)
- 기본 포트: `8082` (soft2cost1 / soft2cost2가 쓰는 8081과 충돌 회피)
- 헬스체크: `GET /api/v1/health` (인증 불필요)
- Flyway는 기본 비활성(`SOFT2COST_FLYWAY_ENABLED=true`로 켠다)
- Swagger는 기본 비활성. 켜려면 `SOFT2PREQCGATE_SWAGGER_ENABLED=true`
  (springdoc 의존성은 soft2cost2와 동일하게 아직 넣지 않았다)

## 빌드 / 테스트

```bash
./gradlew build     # 컴파일 + 테스트
./gradlew test
```

## 주요 환경변수

| 변수 | 기본값 | 설명 |
|---|---|---|
| `SOFT2PREQCGATE_API_PORT` | 8082 | 서버 포트 |
| `SOFT2COST_DB_URL` | — | Oracle JDBC URL (필수) |
| `SOFT2COST_DB_USERNAME` / `_PASSWORD` | — | DB 계정 (필수) |
| `SOFT2COST_DB_MAX_POOL_SIZE` | 10 | Hikari 풀 크기 |
| `SOFT2COST_REDIS_HOST` | localhost | Redis 호스트 |
| `SOFT2COST_REDIS_PORT` | 6379 | Redis 포트 |
| `SOFT2COST_FLYWAY_ENABLED` | false | 기동 시 마이그레이션 |
| `SOFT2PREQCGATE_SWAGGER_ENABLED` | false | Swagger 노출 |
| `SOFT2PREQCGATE_NEXACRO_ORIGIN` | http://localhost:9091 | CORS 허용 origin |
| `SOFT2PREQCGATE_BCRYPT_STRENGTH` | 12 | BCrypt 강도 (8~14) |
