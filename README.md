# Sellanding Guestbook API (Spring Boot Migration)

본 프로젝트는 기존 Go(Gin) 기반의 백엔드 서버를 **Java 21 + Spring Boot** 환경으로 마이그레이션한 결과물입니다. 

## 기술 스택
- **Language**: Java 17
- **Framework**: Spring Boot 4.0.3
- **Database**: MySQL (Operational), H2 (Local/Test)
- **Migration**: Flyway
- **Security**: Spring Security + JWT (Local Auth)
- **Documentation**: Swagger/OpenAPI 3.0
- **Build Tool**: Gradle (Kotlin DSL)

---

## 마이그레이션 과정 및 주요 개선 사항

### 1. 에코시스템의 강점 활용 (Standardization)
- **Go (Custom)**: Go 서버에서는 수동으로 에러 처리를 하던 방식을 Spring의 `@RestControllerAdvice`를 활용한 **Global Exception Handling**으로 전환했습니다. 이를 통해 모든 API 응답 구조를 표준화하고 유지보수성을 극대화했습니다.
- **DTO 리팩토링**: Java 17의 `Record` 타입을 도입하여 불변성을 보장하고, 반복적인 Getter/Setter 코드를 제거하여 비즈니스 로직에만 집중할 수 있는 환경을 만들었습니다.

### 2. 데이터베이스 형상 관리 (Flyway 도입)
- 기존에는 SQL 파일을 수동으로 관리했으나, **Flyway**를 도입하여 DB 스키마의 버전을 코드로 관리합니다. 협업 시 DB 정합성 문제를 원천 차단하고, 배포 파이프라인의 자동화를 고려했습니다.

### 3. 유연한 비속어 필터링 시스템 (DB-based & Caching)
- 기존 JSON 파일 기반의 비속어 관리를 **데이터베이스 관리 방식**으로 고도화했습니다.
- **성능 최적화**: 매번 DB를 조회하지 않도록 `@PostConstruct`와 `refreshTerms()` 메서드를 통해 메모리에 캐싱하여 텍스트 필터링 성능을 확보했습니다. 서버 중단 없이 비속어 목록을 동적으로 업데이트할 수 있는 구조입니다.

### 4. 외부 의존성 제거 및 로컬 인증 완결성
- Supabase에 의존하던 인증 방식을 **로컬 JWT 발급 및 검증 시스템**으로 전환했습니다. 외부 서비스 장애에 영향을 받지 않으며, 로컬 환경에서 즉시 실행 및 테스트가 가능한 완결성을 갖췄습니다.

---

## 기술적 깊이: Deep-Dive 문제 케이스 및 해결 전략

마이그레이션 과정에서 고려했거나, 향후 확장 시 직면할 수 있는 기술적 도전 과제입니다.

### Case 1: 대규모 비속어 필터링의 성능 확장성 (Scalability)
- **문제**: 현재는 `String.contains` 방식을 사용하지만, 비속어 단어가 만 단위로 늘어나거나 본문 텍스트가 매우 길어질 경우 $O(N \times M)$의 시간 복잡도로 인해 성능 저하가 발생할 수 있습니다.
- **해결 전략**: 추후 비속어 데이터가 방대해질 경우, **Aho-Corasick(아호-코라식)** 알고리즘이나 **Trie(트라이)** 자료구조를 도입하여 텍스트 길이에 비례하는 $O(N)$ 시간 복잡도로 성능을 최적화할 계획입니다.

### Case 2: 대량 데이터 조회 시 Offset 기반 페이징의 성능 저하 (Database I/O)
- **문제**: 방명록 게시글이 수십만 건 이상 쌓일 경우, 기존의 `Offset` 기반 페이징(`LIMIT 100000, 10`)은 앞선 10만 건의 데이터를 모두 읽어야 하므로 뒤로 갈수록 쿼리 속도가 급격히 느려집니다.
- **해결 전략**: '더보기' 방식의 UI에 적합한 **No-Offset(Cursor-based) 페이징**으로 전환하여, 마지막으로 읽은 `ID`를 기준으로 인덱스를 타게 함으로써 데이터 양에 관계없이 일정한 조회 성능($O(1)$에 근접)을 유지하도록 개선할 수 있습니다.

### Case 3: 동시성 상황에서의 통계 데이터 정합성 (Concurrency Control)
- **문제**: 여러 사용자가 동시에 게시글을 작성할 때, `usage_counters` 테이블의 `ink_count`를 '조회 후 수정(Read-Modify-Write)'하는 방식은 **Lost Update** 문제를 발생시켜 실제 작성 수보다 카운트가 적게 기록될 수 있습니다.
- **해결 전략**: 애플리케이션 레벨의 잠금보다는 DB 레벨의 **원자적 연산(Atomic Increment, `UPDATE SET count = count + 1`)**을 사용하거나, 충돌이 잦을 경우 **Optimistic Lock(@Version)**을 도입하여 데이터 정합성을 보장하는 전략이 유효합니다.

---
