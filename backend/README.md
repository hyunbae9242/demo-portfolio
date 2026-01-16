# Backend Sample - Spring Boot Layered Architecture

> **이 프로젝트는 실제 운영 서비스가 아닌, 아키텍처 및 설계 역량을 보여주기 위한 샘플 코드베이스입니다.**

## 목차
- [아키텍처 개요](#아키텍처-개요)
- [핵심 설계 결정](#핵심-설계-결정)
- [레이어 구조](#레이어-구조)
- [주요 기술 패턴](#주요-기술-패턴)
- [확장 시나리오](#확장-시나리오)
- [코드 네비게이션 가이드](#코드-네비게이션-가이드)
- [빌드 및 실행](#빌드-및-실행)

---

## 아키텍처 개요

### 선택한 아키텍처: **Layered Architecture**

**선택 이유:**
1. **팀 이해도**: Spring 생태계에서 가장 일반적인 패턴으로, 신규 팀원이 빠르게 온보딩 가능
2. **Spring Boot 통합**: Spring Data JPA, Spring MVC와 자연스럽게 통합
3. **충분한 유연성**: 대부분의 비즈니스 요구사항을 만족하며, 필요시 Hexagonal로 진화 가능
4. **실용성**: 과도한 추상화 없이 명확한 레이어 분리로 유지보수성 확보

**Hexagonal 대신 Layered를 선택한 이유:**
- Hexagonal은 도메인 순수성을 극대화하지만, 포트/어댑터 구조가 소규모 팀에겐 오버엔지니어링이 될 수 있음
- Layered는 수직적 레이어 분리로 충분한 테스트 가능성과 의존성 역전을 제공
- 본 샘플은 실무 적용 가능한 실용적 구조를 우선시

---

## 핵심 설계 결정

### 1. **DTO/Command/Query 분리**

**결정:**
- **Command**: 쓰기 작업 (CreateOrderCommand)
- **Query**: 읽기 조건 (OrderQuery.SearchCriteria)
- **DTO**: 응답 데이터 (OrderDto)

**트레이드오프:**
- ✅ 장점: CQRS 패턴 기반, 읽기/쓰기 책임 명확, 확장성(이벤트소싱 전환 용이)
- ❌ 단점: 보일러플레이트 코드 증가, 작은 프로젝트엔 과도할 수 있음

**예시 코드:**
```java
// src/main/java/com/sample/system/application/order/CreateOrderCommand.java
public record CreateOrderCommand(
    @NotNull Long customerId,
    @NotBlank String customerName,
    @Valid List<OrderItemCommand> orderItems
) {}
```

---

### 2. **전역 예외 처리 + 에러 응답 규격**

**결정:**
- `ErrorCode` enum으로 모든 에러 코드 중앙 관리
- `BusinessException`으로 도메인 예외 표준화
- `GlobalExceptionHandler`로 일관된 에러 응답

**트레이드오프:**
- ✅ 장점: API 클라이언트가 에러 처리 로직 일관되게 구현 가능, 로깅 중앙화
- ❌ 단점: 초기 설정 복잡도, 예외 타입이 많아질수록 enum 관리 부담

**예시 코드:**
```java
// src/main/java/com/sample/system/common/exception/ErrorCode.java
public enum ErrorCode {
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORD001", "Order not found"),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "ORD002", "Invalid order status transition");
}

// src/main/java/com/sample/system/common/exception/GlobalExceptionHandler.java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(...) {
        // 통일된 에러 응답 생성
    }
}
```

**에러 응답 예시:**
```json
{
  "timestamp": "2025-01-16T12:34:56",
  "status": 404,
  "error": "Not Found",
  "code": "ORD001",
  "message": "Order not found",
  "path": "/api/orders/999",
  "correlationId": "a3f2e8d1-4b9c-..."
}
```

---

### 3. **트랜잭션 경계 및 격리수준**

**결정:**
- **Application Layer에 트랜잭션 경계 설정** (`@Transactional`)
- **읽기 전용 최적화** (`readOnly = true`)
- **상태 변경은 REPEATABLE_READ** (동시성 제어)

**트레이드오프:**
- ✅ 장점: 도메인 로직은 순수하게 유지, Use Case 단위로 트랜잭션 관리 명확
- ❌ 단점: Application Service가 두터워질 수 있음

**예시 코드:**
```java
// src/main/java/com/sample/system/application/order/OrderApplicationService.java
@Service
@RequiredArgsConstructor
public class OrderApplicationService {

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public OrderDto createOrder(CreateOrderCommand command) {
        // 주문 생성 트랜잭션
    }

    @Transactional(readOnly = true)
    public OrderDto getOrder(Long orderId) {
        // 읽기 전용 최적화
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public OrderDto confirmOrder(Long orderId) {
        // 상태 변경 시 동시성 제어
    }
}
```

**격리 수준 선택 기준:**
- `READ_COMMITTED`: 일반적인 조회/생성 (기본값, 성능 우선)
- `REPEATABLE_READ`: 상태 변경 (Phantom Read 방지, 일관성 우선)

---

### 4. **Correlation ID를 통한 요청 추적**

**결정:**
- 모든 API 요청에 `X-Correlation-ID` 헤더 추가
- MDC(Mapped Diagnostic Context)로 로그에 자동 포함
- 요청 생명주기 동안 모든 로그에 동일 ID 기록

**트레이드오프:**
- ✅ 장점: 분산 시스템에서 요청 추적 용이, 장애 디버깅 시간 단축
- ❌ 단점: 필터 설정 필요, 마이크로서비스 환경에선 전파 로직 추가 필요

**예시 코드:**
```java
// src/main/java/com/sample/system/common/logging/CorrelationIdFilter.java
@Component
public class CorrelationIdFilter implements Filter {
    @Override
    public void doFilter(...) {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        httpResponse.setHeader("X-Correlation-ID", correlationId);
        // ...
    }
}
```

**로그 출력 예시:**
```
2025-01-16 12:34:56 [a3f2e8d1-4b9c-...] INFO  OrderApplicationService - Order created: id=1, orderNumber=ORD-001
2025-01-16 12:34:57 [a3f2e8d1-4b9c-...] DEBUG OrderService - Confirming order: orderNumber=ORD-001
```

---

### 5. **도메인 모델의 상태 전환 검증**

**결정:**
- 도메인 엔티티 내부에 상태 전환 로직 캡슐화
- `OrderStatus.canTransitionTo()` 메서드로 유효성 검증
- 불가능한 전환 시 `BusinessException` 발생

**트레이드오프:**
- ✅ 장점: 도메인 규칙이 코드에 명시적으로 드러남, 중복 검증 방지
- ❌ 단점: enum에 비즈니스 로직 포함, 복잡한 상태 머신은 별도 패턴(State Pattern) 필요

**예시 코드:**
```java
// src/main/java/com/sample/system/domain/order/OrderStatus.java
public enum OrderStatus {
    PENDING, CONFIRMED, SHIPPING, DELIVERED, CANCELLED;

    public boolean canTransitionTo(OrderStatus newStatus) {
        return switch (this) {
            case PENDING -> newStatus == CONFIRMED || newStatus == CANCELLED;
            case CONFIRMED -> newStatus == SHIPPING || newStatus == CANCELLED;
            case SHIPPING -> newStatus == DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
    }
}

// src/main/java/com/sample/system/domain/order/Order.java
public class Order {
    private void changeStatus(OrderStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new BusinessException(ErrorCode.INVALID_ORDER_STATUS);
        }
        this.status = newStatus;
    }
}
```

---

## 레이어 구조

```
src/main/java/com/sample/system/
├── presentation/          # REST API 엔드포인트
│   ├── order/
│   │   ├── OrderController.java        # HTTP 요청 처리
│   │   └── CreateOrderRequest.java     # 요청 DTO
│   └── product/
│
├── application/           # 유스케이스 조율 (트랜잭션 경계)
│   ├── order/
│   │   ├── OrderApplicationService.java  # 트랜잭션 관리
│   │   ├── CreateOrderCommand.java       # 쓰기 명령
│   │   ├── OrderQuery.java               # 읽기 쿼리
│   │   └── OrderDto.java                 # 응답 DTO
│   └── product/
│
├── domain/                # 핵심 비즈니스 로직
│   ├── order/
│   │   ├── Order.java                   # 집합 루트(Aggregate Root)
│   │   ├── OrderItem.java               # 엔티티
│   │   ├── OrderStatus.java             # 값 객체(Value Object)
│   │   └── OrderService.java            # 도메인 서비스
│   └── product/
│
├── infrastructure/        # 외부 시스템 연동
│   ├── persistence/       # 데이터베이스 접근
│   │   ├── order/
│   │   │   ├── OrderRepository.java      # 인터페이스
│   │   │   └── OrderJpaRepository.java   # JPA 구현
│   │   └── product/
│   └── external/          # 외부 API 클라이언트
│       └── PaymentClient.java (placeholder)
│
├── common/                # 공통 유틸리티
│   ├── exception/         # 예외 처리
│   │   ├── GlobalExceptionHandler.java
│   │   ├── ErrorResponse.java
│   │   ├── ErrorCode.java
│   │   └── BusinessException.java
│   ├── validation/        # 검증 그룹
│   └── logging/           # 로깅/추적
│       ├── CorrelationIdFilter.java
│       └── LoggingAspect.java
│
└── config/                # 설정
    ├── WebConfig.java
    ├── SecurityConfig.java (placeholder)
    └── CorrelationIdConfig.java
```

### 레이어별 책임

| 레이어 | 책임 | 의존 방향 |
|--------|------|-----------|
| **Presentation** | HTTP 요청/응답 처리, 유효성 검증 | → Application |
| **Application** | Use Case 조율, 트랜잭션 경계, DTO 변환 | → Domain, Infrastructure |
| **Domain** | 비즈니스 규칙, 상태 관리, 도메인 이벤트 | ← 다른 레이어 |
| **Infrastructure** | DB 접근, 외부 API 호출, 메시징 | → Domain (인터페이스) |

**핵심 원칙:**
- Domain 레이어는 다른 레이어에 의존하지 않음 (의존성 역전)
- Infrastructure는 Domain 인터페이스를 구현 (DIP)
- Application은 Domain과 Infrastructure를 조율

---

## 주요 기술 패턴

### 1. **Validation 전략**

**3단계 검증:**
1. **Presentation Layer**: `@Valid` + Bean Validation
2. **Domain Layer**: 엔티티 생성자 내부 검증
3. **Business Logic**: 도메인 서비스에서 비즈니스 규칙 검증

**예시:**
```java
// Presentation - 요청 형식 검증
public record CreateOrderRequest(
    @NotNull @Positive Long customerId,
    @Size(min = 1) List<OrderItemRequest> items
) {}

// Domain - 엔티티 불변성 보장
public class OrderItem {
    public OrderItem(...) {
        if (quantity <= 0) throw new IllegalArgumentException(...);
    }
}

// Domain Service - 비즈니스 규칙
public class OrderService {
    public Order createOrder(...) {
        if (itemDataList.isEmpty()) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR);
        }
    }
}
```

---

### 2. **AOP 기반 로깅**

**Application/Domain 레이어의 모든 메서드 실행을 자동 로깅:**

```java
// src/main/java/com/sample/system/common/logging/LoggingAspect.java
@Aspect
@Component
public class LoggingAspect {
    @Around("applicationLayer() || domainLayer()")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) {
        log.debug("Executing {}.{}", className, methodName);
        // 실행 시간 측정 및 예외 로깅
    }
}
```

**출력 예시:**
```
2025-01-16 12:34:56 [a3f2e8d1] DEBUG OrderApplicationService - Executing createOrder() with args: [CreateOrderCommand(...)]
2025-01-16 12:34:56 [a3f2e8d1] DEBUG OrderApplicationService - Completed createOrder() in 45ms
```

---

### 3. **N+1 문제 해결**

**fetch join을 사용한 즉시 로딩:**

```java
// src/main/java/com/sample/system/infrastructure/persistence/order/OrderJpaRepository.java
@Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :id")
Optional<Order> findById(@Param("id") Long id);
```

**default_batch_fetch_size 설정:**
```yaml
# application.yml
spring:
  jpa:
    properties:
      hibernate:
        default_batch_fetch_size: 100
```

---

### 4. **테스트 전략**

**3가지 테스트 타입:**

1. **단위 테스트**: Domain/Application 서비스 로직 검증
   - `OrderServiceTest.java`: 도메인 로직 순수 테스트
   - Mockito로 의존성 격리

2. **슬라이스 테스트**: Controller/Repository 격리 테스트
   - `OrderControllerTest.java`: `@WebMvcTest`로 컨트롤러만 테스트
   - MockMvc로 HTTP 요청 시뮬레이션

3. **통합 테스트**: 전체 레이어 통합 (본 샘플에선 생략)
   - `@SpringBootTest`로 실제 DB와 연동

**예시:**
```java
// 단위 테스트 - src/test/.../domain/order/OrderServiceTest.java
@Test
void createOrder_Success() {
    Order order = orderService.createOrder("ORD-001", 100L, "John Doe", itemDataList);
    assertThat(order.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(40.00));
}

// 슬라이스 테스트 - src/test/.../presentation/order/OrderControllerTest.java
@WebMvcTest(OrderController.class)
class OrderControllerTest {
    @Test
    void createOrder_Success() throws Exception {
        mockMvc.perform(post("/api/orders")...)
            .andExpect(status().isCreated());
    }
}
```

---

## 확장 시나리오

### 1. **인증/인가 고도화**

**현재:**
- Spring Security 설정 없음 (placeholder)

**확장 방향:**
```java
// SecurityConfig.java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .oauth2ResourceServer(oauth2 -> oauth2.jwt())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/orders/**").hasRole("USER")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
            );
        return http.build();
    }
}
```

**JWT 토큰 기반 인증:**
- OAuth2 Resource Server 설정
- Correlation ID와 함께 사용자 컨텍스트 MDC 추가
- `@PreAuthorize`로 메서드 레벨 권한 검증

---

### 2. **캐싱 전략**

**Redis 기반 2차 캐시:**
```java
// application.yml
spring:
  cache:
    type: redis
    redis:
      time-to-live: 600000  # 10분

// OrderApplicationService.java
@Cacheable(value = "orders", key = "#orderId")
public OrderDto getOrder(Long orderId) {
    // 조회 로직
}

@CacheEvict(value = "orders", key = "#orderId")
public OrderDto confirmOrder(Long orderId) {
    // 상태 변경 시 캐시 무효화
}
```

**트레이드오프:**
- ✅ 장점: 읽기 성능 대폭 향상, DB 부하 감소
- ❌ 단점: 캐시 무효화 전략 복잡도, 분산 환경에서 일관성 이슈

---

### 3. **비동기 처리 (이벤트 기반)**

**Domain Event 도입:**
```java
// Domain Event
public record OrderConfirmedEvent(Long orderId, LocalDateTime confirmedAt) {}

// Order.java (도메인 엔티티)
public void confirm() {
    changeStatus(OrderStatus.CONFIRMED);
    registerEvent(new OrderConfirmedEvent(this.id, LocalDateTime.now()));
}

// Event Handler (Application Layer)
@EventListener
@Async
public void handleOrderConfirmed(OrderConfirmedEvent event) {
    // 재고 차감, 결제 처리, 알림 발송 등
}
```

**확장:**
- Spring Event → Kafka/RabbitMQ로 진화
- Event Sourcing 도입 (Command/Event 영속화)

---

### 4. **모듈 분리 (멀티 모듈)**

**Gradle 멀티 프로젝트 구조:**
```
sample-backend/
├── domain/             # 순수 도메인 로직 (JPA 의존 제거)
├── application/        # Use Case 조율
├── infrastructure/     # DB, 외부 API 구현
├── presentation/       # REST API
└── batch/              # 배치 작업 (별도 모듈)
```

**이점:**
- 도메인 로직 재사용 (배치, 이벤트 핸들러에서 공유)
- 빌드 시간 단축 (변경된 모듈만 재빌드)
- 의존성 강제 (domain은 infrastructure에 의존 불가)

---

### 5. **관측성(Observability) 강화**

**Micrometer + Prometheus + Grafana:**
```java
// build.gradle
implementation 'io.micrometer:micrometer-registry-prometheus'

// CustomMetrics.java
@Component
public class CustomMetrics {
    private final Counter orderCreatedCounter;

    public CustomMetrics(MeterRegistry registry) {
        this.orderCreatedCounter = Counter.builder("orders.created")
                .description("Total orders created")
                .register(registry);
    }
}
```

**분산 추적:**
- Spring Cloud Sleuth + Zipkin
- Correlation ID를 Trace ID로 대체

---

## 코드 네비게이션 가이드

### 처음 보는 사람을 위한 읽기 순서

1. **예외 처리 시스템** (전역 에러 처리 이해)
   - `common/exception/ErrorCode.java`
   - `common/exception/GlobalExceptionHandler.java`

2. **도메인 모델** (핵심 비즈니스 로직)
   - `domain/order/OrderStatus.java` (상태 전환 규칙)
   - `domain/order/Order.java` (집합 루트)
   - `domain/order/OrderService.java` (도메인 서비스)

3. **애플리케이션 서비스** (트랜잭션 경계)
   - `application/order/OrderApplicationService.java`
   - `application/order/CreateOrderCommand.java`

4. **API 엔드포인트** (외부 인터페이스)
   - `presentation/order/OrderController.java`

5. **인프라 구현** (기술적 세부사항)
   - `infrastructure/persistence/order/OrderJpaRepository.java`

6. **로깅/추적** (공통 관심사)
   - `common/logging/CorrelationIdFilter.java`
   - `common/logging/LoggingAspect.java`

7. **테스트** (품질 보증)
   - `test/.../domain/order/OrderServiceTest.java` (단위 테스트)
   - `test/.../presentation/order/OrderControllerTest.java` (슬라이스 테스트)

---

## 빌드 및 실행

### 요구사항
- Java 17 이상
- Gradle 8.0 이상

### 실행 방법

```bash
# 1. 프로젝트 클론
cd backend

# 2. 빌드
./gradlew clean build

# 3. 애플리케이션 실행
./gradlew bootRun

# 4. H2 콘솔 접속
# http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:sampledb
# Username: sa
# Password: (비어있음)
```

### 테스트 실행

```bash
# 전체 테스트
./gradlew test

# 특정 테스트만 실행
./gradlew test --tests OrderServiceTest

# 테스트 커버리지 확인
./gradlew jacocoTestReport
```

### API 테스트

**주문 생성:**
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 100,
    "customerName": "John Doe",
    "items": [
      {
        "productId": 1,
        "productName": "Book A",
        "quantity": 2,
        "unitPrice": 10.00
      }
    ]
  }'
```

**주문 조회:**
```bash
curl -X GET http://localhost:8080/api/orders/1
```

**주문 확정:**
```bash
curl -X POST http://localhost:8080/api/orders/1/confirm
```

---

## 면접 대비 Q&A

<details>
<summary><strong>Q1. Layered Architecture와 Hexagonal Architecture의 차이점은?</strong></summary>

**Layered:**
- 수평적 레이어 분리 (Presentation → Application → Domain → Infrastructure)
- Spring Boot와 자연스럽게 통합
- 충분한 테스트 가능성과 유지보수성 제공

**Hexagonal:**
- 도메인 중심 (Domain이 다른 모든 레이어와 독립)
- 포트/어댑터 패턴으로 외부 의존성 격리
- 도메인 순수성 극대화, 프레임워크 변경 용이

**선택 기준:**
- 대부분의 프로젝트는 Layered로 충분
- 도메인 로직이 복잡하고 외부 시스템 의존이 많으면 Hexagonal 고려

**본 프로젝트:**
- Layered 선택 (실용성과 팀 이해도 우선)
- 필요시 Hexagonal로 진화 가능한 구조 유지 (Repository 인터페이스 분리)
</details>

<details>
<summary><strong>Q2. 트랜잭션 격리 수준을 어떻게 선택했나?</strong></summary>

**기본 원칙:**
- 읽기 전용: `READ_COMMITTED` (성능 우선, Dirty Read 방지)
- 상태 변경: `REPEATABLE_READ` (일관성 우선, Non-Repeatable Read/Phantom Read 방지)

**예시:**
```java
@Transactional(readOnly = true)  // READ_COMMITTED
public OrderDto getOrder(Long orderId) {}

@Transactional(isolation = Isolation.REPEATABLE_READ)  // 동시성 제어
public OrderDto confirmOrder(Long orderId) {}
```

**트레이드오프:**
- `REPEATABLE_READ`는 성능 오버헤드가 있지만, 재고 차감 등 동시성 이슈에 필수
- 대부분의 조회는 `READ_COMMITTED`로 충분
</details>

<details>
<summary><strong>Q3. 도메인 서비스와 애플리케이션 서비스의 차이는?</strong></summary>

**도메인 서비스 (OrderService):**
- 순수 비즈니스 로직 (프레임워크 무관)
- 트랜잭션 없음
- 여러 엔티티를 조율하는 도메인 규칙

**애플리케이션 서비스 (OrderApplicationService):**
- Use Case 조율
- 트랜잭션 경계 설정
- DTO 변환, 인프라 레이어 호출

**예시:**
```java
// Domain Service - 순수 비즈니스 로직
public class OrderService {
    public void confirmOrder(Order order) {
        if (!order.isPending()) {
            throw new BusinessException(ErrorCode.INVALID_ORDER_STATUS);
        }
        order.confirm();
    }
}

// Application Service - 트랜잭션 + 조율
@Transactional
public class OrderApplicationService {
    public OrderDto confirmOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(...);
        orderService.confirmOrder(order);  // 도메인 서비스 호출
        return OrderDto.from(orderRepository.save(order));
    }
}
```
</details>

<details>
<summary><strong>Q4. Correlation ID를 어떻게 활용하는가?</strong></summary>

**목적:**
- 분산 시스템에서 단일 요청의 전체 흐름 추적
- 장애 발생 시 해당 요청의 모든 로그를 빠르게 검색

**구현:**
1. `CorrelationIdFilter`에서 요청 시작 시 UUID 생성
2. MDC(Mapped Diagnostic Context)에 저장
3. 모든 로그에 자동 포함
4. HTTP 응답 헤더로 클라이언트에 반환

**활용 예시:**
```bash
# 로그 검색
grep "a3f2e8d1-4b9c-..." application.log

# 출력:
# 2025-01-16 12:34:56 [a3f2e8d1-4b9c-...] INFO  OrderController - Received create order request
# 2025-01-16 12:34:56 [a3f2e8d1-4b9c-...] DEBUG OrderApplicationService - Creating order
# 2025-01-16 12:34:56 [a3f2e8d1-4b9c-...] ERROR GlobalExceptionHandler - Exception occurred
```
</details>

<details>
<summary><strong>Q5. N+1 문제를 어떻게 해결했나?</strong></summary>

**문제:**
```java
Order order = orderRepository.findById(1L);  // 1번 쿼리
order.getOrderItems().forEach(item -> {
    item.getProductName();  // N번 쿼리 (Lazy Loading)
});
```

**해결 방법:**

1. **Fetch Join:**
```java
@Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :id")
Optional<Order> findById(@Param("id") Long id);
```

2. **Batch Fetch Size:**
```yaml
spring:
  jpa:
    properties:
      hibernate:
        default_batch_fetch_size: 100
```

3. **DTO 직접 조회 (JPQL):**
```java
@Query("SELECT new OrderDto(o.id, o.orderNumber, ...) FROM Order o WHERE o.id = :id")
OrderDto findOrderDtoById(@Param("id") Long id);
```

**선택 기준:**
- 단건 조회: Fetch Join
- 목록 조회: Batch Fetch Size
- 복잡한 집계: DTO 직접 조회
</details>

<details>
<summary><strong>Q6. 테스트 전략을 어떻게 수립했나?</strong></summary>

**3단계 피라미드:**

1. **단위 테스트** (가장 많음)
   - Domain/Application Service 로직 검증
   - Mockito로 의존성 격리
   - 빠른 피드백, 높은 커버리지

2. **슬라이스 테스트** (중간)
   - `@WebMvcTest`: Controller만 테스트
   - `@DataJpaTest`: Repository만 테스트
   - 컨텍스트 부분 로딩으로 속도 향상

3. **통합 테스트** (최소)
   - `@SpringBootTest`: 전체 애플리케이션 테스트
   - 실제 DB 연동 (Testcontainers 활용)
   - CI/CD 파이프라인에서만 실행

**본 샘플:**
- 단위 테스트: `OrderServiceTest.java`
- 슬라이스 테스트: `OrderControllerTest.java`
- 통합 테스트: 생략 (인프라 의존성 최소화)
</details>

<details>
<summary><strong>Q7. 비즈니스 예외와 시스템 예외를 어떻게 구분하는가?</strong></summary>

**비즈니스 예외 (BusinessException):**
- 예상 가능한 도메인 규칙 위반
- 클라이언트에게 명확한 에러 메시지 제공
- 복구 가능 (예: 재고 부족 → 재시도 또는 대체 상품 추천)

**시스템 예외 (Exception):**
- 예상 불가능한 기술적 오류
- 로그 남기고 500 에러 반환
- 복구 불가능 (예: DB 연결 끊김, OutOfMemoryError)

**예시:**
```java
// 비즈니스 예외
throw new BusinessException(ErrorCode.INVALID_ORDER_STATUS);  // 400

// 시스템 예외
throw new SQLException("Connection timeout");  // 500
```

**처리:**
- `GlobalExceptionHandler`에서 타입별로 분기
- 비즈니스 예외는 에러 코드와 메시지 노출
- 시스템 예외는 일반적인 메시지만 노출 (보안)
</details>

<details>
<summary><strong>Q8. CQRS 패턴을 완전히 적용하지 않은 이유는?</strong></summary>

**CQRS (Command Query Responsibility Segregation):**
- 쓰기 모델(Command)과 읽기 모델(Query)을 완전히 분리
- 읽기 전용 DB 복제본 사용
- Event Sourcing과 함께 사용

**본 프로젝트:**
- Command/Query/DTO 분리는 적용 (CQRS의 일부)
- 하지만 읽기/쓰기 DB는 분리하지 않음

**이유:**
1. **복잡도**: 소규모 프로젝트에는 오버엔지니어링
2. **인프라 비용**: 읽기 전용 DB 복제본 불필요
3. **일관성**: 단일 DB로 강한 일관성 보장

**확장 가능:**
- 트래픽 증가 시 읽기 전용 Replica 추가
- QueryService 분리하여 CQRS 완성
</details>

<details>
<summary><strong>Q9. 보안 측면에서 추가로 구현할 사항은?</strong></summary>

**현재 상태:**
- CORS 설정 완료
- Validation 적용

**추가 필요:**

1. **인증/인가:**
   - Spring Security + JWT
   - OAuth2 Resource Server

2. **SQL Injection 방지:**
   - JPA 사용 (Prepared Statement 자동 적용)
   - Native Query 사용 시 파라미터 바인딩 필수

3. **XSS 방지:**
   - 입력 검증 (Bean Validation)
   - 출력 이스케이프 (Jackson 자동 처리)

4. **CSRF 방지:**
   - REST API는 Stateless이므로 CSRF 토큰 불필요
   - `http.csrf().disable()`

5. **민감 정보 관리:**
   - 환경 변수 (.env.example 제공)
   - AWS Secrets Manager / HashiCorp Vault

6. **Rate Limiting:**
   - Bucket4j + Redis로 API 호출 제한
</details>

<details>
<summary><strong>Q10. 성능 최적화를 위해 적용한 기법은?</strong></summary>

**1. JPA 최적화:**
- Fetch Join으로 N+1 방지
- `default_batch_fetch_size` 설정
- `open-in-view: false`로 트랜잭션 범위 명확화

**2. 읽기 전용 트랜잭션:**
```java
@Transactional(readOnly = true)  // Flush 생략, 스냅샷 미생성
```

**3. 로깅 수준 조절:**
- Production: INFO 레벨
- Development: DEBUG 레벨

**4. 인덱스 설정:**
```java
@Table(name = "orders", indexes = {
    @Index(name = "idx_customer_id", columnList = "customer_id"),
    @Index(name = "idx_order_number", columnList = "order_number")
})
```

**5. 추가 고려사항:**
- Redis 캐싱 (자주 조회되는 데이터)
- Connection Pool 튜닝 (HikariCP)
- 비동기 처리 (`@Async`, 메시징)
</details>

---

## 참고 자료

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Domain-Driven Design](https://martinfowler.com/bliki/DomainDrivenDesign.html)
- [Layered Architecture](https://herbertograca.com/2017/08/03/layered-architecture/)
- [Transaction Isolation Levels](https://www.postgresql.org/docs/current/transaction-iso.html)

---

**작성자 노트:**
이 프로젝트는 실제 운영 서비스가 아닌, 아키텍처 설계 역량을 보여주기 위한 샘플입니다. 모든 코드는 교육 목적으로 작성되었으며, 실제 프로덕션 환경에서는 추가적인 보안, 모니터링, 테스트가 필요합니다.
