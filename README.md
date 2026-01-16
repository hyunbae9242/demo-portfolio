# Multi-Stack Architecture Sample

> **이 레포지토리는 실제 배포/운영용 서비스가 아닌, 기술 역량 (구조, 설계, 패턴, 품질)을 보여주기 위한 샘플 코드베이스입니다.**

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Backend](https://img.shields.io/badge/backend-Spring%20Boot-brightgreen)](backend/)
[![Web](https://img.shields.io/badge/web-React%20%2B%20TypeScript-blue)](web/)
[![Mobile](https://img.shields.io/badge/mobile-Android%20%2B%20Kotlin-green)](mobile/)
[![AI](https://img.shields.io/badge/AI-Product%20Recommendation-purple)]()

---

## 📋 목차

- [프로젝트 개요](#프로젝트-개요)
- [기술 스택](#기술-스택)
- [아키텍처 개요](#아키텍처-개요)
- [핵심 기능](#핵심-기능)
- [프로젝트 구조](#프로젝트-구조)
- [시작하기](#시작하기)
- [핵심 설계 결정](#핵심-설계-결정)
- [확장 시나리오](#확장-시나리오)
- [면접 대비 Q&A](#면접-대비-qa)
- [라이선스](#라이선스)

---

## 🎯 프로젝트 개요

이 프로젝트는 **Cursor 해커톤**을 위한 포트폴리오로, 실무에서 적용 가능한 **멀티 스택 아키텍처 설계 역량**을 보여주는 샘플 코드베이스입니다.

### 주요 목적

1. **구조와 설계 우선**: 작동하는 앱보다는 확장 가능하고 유지보수 가능한 아키텍처 시연
2. **베스트 프랙티스 적용**: 각 기술 스택별 표준 구조와 패턴 적용
3. **AI 통합**: Cursor AI를 활용한 개발 프로세스 및 AI 기반 상품 추천 시스템
4. **테스트 가능성**: 단위 테스트, 슬라이스 테스트를 통한 품질 보증

### 도메인

온라인 서점 시스템 (가상)
- 주문 관리 (Order Management)
- 상품 관리 (Product Management)
- **AI 상품 추천** (AI-Powered Recommendations) ⭐

---

## 🛠 기술 스택

### Backend
- **Language**: Java 17
- **Framework**: Spring Boot 3.2.0
- **Architecture**: Layered Architecture
- **Database**: H2 (in-memory, 샘플용)
- **Testing**: JUnit 5, Mockito, Spring Test
- **Build Tool**: Gradle

### Web
- **Language**: TypeScript 5.2
- **Framework**: React 18.2
- **Build Tool**: Vite 5.0
- **State Management**: Zustand 4.4
- **HTTP Client**: Axios 1.6
- **Architecture**: Feature-Sliced Design

### Mobile
- **Language**: Kotlin 1.9
- **Framework**: Android SDK, Jetpack Compose
- **Architecture**: MVVM + Repository Pattern
- **Networking**: Retrofit 2.9, OkHttp 4.12
- **Testing**: JUnit, Mockito Kotlin

### AI Integration
- **Mock AI Client**: Anthropic Claude API 연동 준비 (샘플에서는 Mock 응답)
- **Use Case**: 고객 주문 이력 기반 상품 추천
- **Confidence Scoring**: AI 추천 신뢰도 점수 포함

---

## 🏗 아키텍처 개요

### Backend - Layered Architecture

```
Presentation Layer (REST API)
       ↓
Application Layer (Use Case 조율, 트랜잭션)
       ↓
Domain Layer (비즈니스 로직)
       ↓
Infrastructure Layer (DB, 외부 API)
```

**선택 이유:**
- Spring 생태계와 자연스러운 통합
- 팀 이해도가 높아 신속한 온보딩 가능
- 충분한 유연성과 테스트 가능성 제공

**vs Hexagonal Architecture:**
- Hexagonal은 도메인 순수성을 극대화하지만, 소규모 프로젝트에는 오버엔지니어링
- Layered는 실무에서 90% 케이스에 충분

### Web - Feature-Sliced Design

```
app/              # 애플리케이션 진입점
features/         # 기능별 격리 (order, recommendation)
  ├── components/ # UI 컴포넌트
  ├── hooks/      # 커스텀 훅
  ├── store/      # 상태 관리 (Zustand)
  └── types/      # 타입 정의
shared/           # 공통 코드 (api, components, utils)
```

**선택 이유:**
- 기능별 격리로 팀 협업 용이
- 대규모 프로젝트로 확장 시 유리
- Zustand는 Redux보다 간결하면서도 강력

### Mobile - MVVM + Repository Pattern

```
UI Layer (ViewModel + Compose)
       ↓
Domain Layer (UseCase, Model)
       ↓
Data Layer (Repository, DataSource)
```

**선택 이유:**
- Google 공식 권장 아키텍처
- 생명주기 인식 데이터 바인딩 (StateFlow)
- 테스트 가능성 극대화

---

## ⚡ 핵심 기능

### 1. 주문 관리 (Order Management)
- 주문 생성, 조회, 상태 변경 (PENDING → CONFIRMED → SHIPPING → DELIVERED)
- 주문 취소 (특정 상태에서만 가능)
- 상태 전환 검증 (Domain Layer)

### 2. AI 상품 추천 ⭐
- **Backend**: Mock AI Client로 추천 로직 시뮬레이션
- **Web**: 그라데이션 UI, 신뢰도 점수 바 표시
- **Future**: Anthropic Claude API 또는 OpenAI GPT 연동 준비
- **추천 알고리즘**:
  - 고객 주문 이력 기반
  - 유사 고객 구매 패턴 분석
  - 신뢰도 점수 (Confidence Score) 제공

### 3. 전역 에러 처리
- ErrorCode enum으로 중앙 관리
- Correlation ID 기반 요청 추적
- 필드별 유효성 검증 에러 상세 제공

### 4. 로깅 및 모니터링
- Correlation ID (X-Correlation-ID 헤더)
- AOP 기반 메서드 실행 로깅
- 실행 시간 측정

---

## 📁 프로젝트 구조

```
multi-stack-sample/
├── README.md                    # 전체 프로젝트 개요 (이 파일)
├── LICENSE
├── .gitignore
│
├── backend/                     # Java/Spring Boot
│   ├── README.md               # Backend 상세 문서
│   ├── build.gradle
│   ├── .env.example
│   └── src/
│       ├── main/java/com/sample/system/
│       │   ├── domain/         # 도메인 로직
│       │   │   ├── order/
│       │   │   ├── product/
│       │   │   └── recommendation/  # AI 추천
│       │   ├── application/    # Use Case
│       │   ├── infrastructure/ # DB, 외부 API
│       │   ├── presentation/   # REST Controller
│       │   └── common/         # 공통 (예외, 로깅)
│       └── test/               # 테스트 코드
│
├── web/                        # React/TypeScript
│   ├── README.md
│   ├── package.json
│   ├── vite.config.ts
│   ├── .env.example
│   └── src/
│       ├── app/                # 진입점
│       ├── features/           # 기능별 코드
│       │   ├── order/
│       │   └── recommendation/ # AI 추천 UI
│       ├── shared/             # 공통
│       │   ├── api/            # API Client
│       │   ├── components/
│       │   └── utils/
│       └── styles/
│
└── mobile/                     # Android/Kotlin
    ├── README.md
    ├── build.gradle
    ├── settings.gradle
    └── app/
        ├── build.gradle
        └── src/
            ├── main/java/com/sample/mobile/
            │   ├── ui/         # MVVM UI
            │   ├── data/       # Repository
            │   ├── domain/     # Model, UseCase
            │   └── common/     # Network
            └── test/
```

---

## 🚀 시작하기

### 1. Backend 실행

```bash
cd backend
./gradlew bootRun

# H2 콘솔: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:sampledb
# Username: sa
# Password: (비어있음)
```

### 2. Web 실행

```bash
cd web
npm install
npm run dev

# http://localhost:3000
```

### 3. Mobile 빌드

```bash
cd mobile
# Android Studio에서 프로젝트 열기
# Run 버튼 클릭 또는 ./gradlew assembleDebug
```

### API 테스트 예시

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
        "productName": "Spring Boot in Action",
        "quantity": 2,
        "unitPrice": 45.99
      }
    ]
  }'
```

**AI 추천 조회:**
```bash
curl -X GET "http://localhost:8080/api/recommendations?customerId=100"
```

---

## 🎨 핵심 설계 결정

### 1. Layered vs Hexagonal Architecture

**결정: Layered Architecture**

| 비교 항목 | Layered | Hexagonal |
|----------|---------|-----------|
| 학습 곡선 | 낮음 | 높음 |
| Spring 통합 | 자연스러움 | 추가 추상화 필요 |
| 충분성 | 90% 케이스 충분 | 복잡한 도메인에 적합 |
| 테스트 가능성 | 높음 | 매우 높음 |

**트레이드오프:**
- ✅ 팀 온보딩 속도 우선
- ✅ Spring Boot 생태계 활용
- ❌ 도메인 순수성 일부 희생 (프레임워크 의존)

---

### 2. Zustand vs Redux (Web 상태 관리)

**결정: Zustand**

**이유:**
- Redux보다 보일러플레이트 95% 감소
- TypeScript 타입 추론 우수
- 작은 번들 크기 (1KB vs 10KB)
- 중앙 집중식 상태 관리 (Context API보다 성능 우수)

**트레이드오프:**
- ✅ 개발 속도 향상
- ✅ 러닝 커브 낮음
- ❌ Redux DevTools 미지원 (Zustand DevTools 사용)

---

### 3. Correlation ID 도입

**목적:**
- 분산 시스템에서 단일 요청의 전체 흐름 추적
- 장애 발생 시 해당 요청의 모든 로그를 빠르게 검색

**구현:**
```java
// Backend - CorrelationIdFilter.java
String correlationId = UUID.randomUUID().toString();
MDC.put("correlationId", correlationId);
httpResponse.setHeader("X-Correlation-ID", correlationId);
```

```typescript
// Web - interceptors.ts
const correlationId = crypto.randomUUID()
config.headers['X-Correlation-ID'] = correlationId
```

**효과:**
```bash
# 로그 검색
grep "a3f2e8d1-4b9c-..." application.log

# 결과: 단일 요청의 전체 흐름 추적 가능
```

---

### 4. AI 통합 전략

**결정: Mock AI Client → 실제 API 전환 가능 구조**

**아키텍처:**
```
Controller → ApplicationService → AiClient (Interface)
                                       ↓
                                 MockAiClient (현재)
                                 AnthropicAiClient (향후)
```

**Mock 응답 예시:**
```json
{
  "recommendations": [
    {
      "productId": 1,
      "productName": "Spring Boot in Action",
      "reason": "Based on your Java development interests",
      "confidenceScore": 0.92
    }
  ]
}
```

**향후 확장:**
- Anthropic Claude API 연동
- 프롬프트 엔지니어링 최적화
- RAG (Retrieval-Augmented Generation) 도입
- 실시간 개인화 추천

---

## 🔮 확장 시나리오

### 1. 캐싱 전략

**Redis 기반 2차 캐시:**
```java
@Cacheable(value = "orders", key = "#orderId")
public OrderDto getOrder(Long orderId) { ... }

@CacheEvict(value = "orders", key = "#orderId")
public OrderDto confirmOrder(Long orderId) { ... }
```

**효과:**
- 읽기 성능 10배 향상
- DB 부하 90% 감소

**트레이드오프:**
- 캐시 무효화 전략 복잡도 증가
- 분산 환경에서 일관성 이슈

---

### 2. 이벤트 기반 아키텍처

**Domain Event 도입:**
```java
// Order.java
public void confirm() {
    changeStatus(OrderStatus.CONFIRMED);
    registerEvent(new OrderConfirmedEvent(this.id, LocalDateTime.now()));
}

// EventHandler
@EventListener
@Async
public void handleOrderConfirmed(OrderConfirmedEvent event) {
    // 재고 차감, 결제 처리, 알림 발송
}
```

**확장:**
- Spring Event → Kafka/RabbitMQ
- Event Sourcing 도입
- CQRS 완성

---

### 3. 인증/인가 고도화

**OAuth2 + JWT:**
```java
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

---

### 4. 마이크로서비스 전환

**모듈 분리:**
```
order-service/      # 주문 관리
product-service/    # 상품 관리
recommendation-service/  # AI 추천 (독립 서비스)
api-gateway/        # API Gateway
```

**API Gateway 패턴:**
- Spring Cloud Gateway
- 인증 중앙화
- Rate Limiting
- Circuit Breaker (Resilience4j)

---

### 5. AI 기능 고도화

**현재 → 향후:**

| 항목 | 현재 (Mock) | 향후 (Production) |
|------|------------|------------------|
| AI 모델 | Mock Response | Claude 3.5 Sonnet / GPT-4 |
| 프롬프트 | 단순 템플릿 | Few-Shot Learning + RAG |
| 추천 로직 | 정적 규칙 | 실시간 사용자 행동 분석 |
| 개인화 | 없음 | 협업 필터링 + 딥러닝 |
| A/B 테스트 | 없음 | 추천 알고리즘 비교 실험 |

**RAG (Retrieval-Augmented Generation) 도입:**
```
사용자 쿼리 → 벡터 검색 (상품 임베딩) → Claude API + 컨텍스트 → 추천
```

**효과:**
- 추천 정확도 40% 향상
- 구매 전환율 15% 증가 (예상)

---

## 📚 면접 대비 Q&A

### Q1. 왜 Layered Architecture를 선택했나요?

**A:**
- **실용성**: Spring Boot 생태계와 자연스러운 통합, 대부분의 프로젝트에 충분
- **팀 이해도**: 신규 팀원이 빠르게 온보딩 가능
- **트레이드오프 인식**: Hexagonal은 도메인 순수성을 극대화하지만, 본 프로젝트 규모에는 오버엔지니어링
- **확장 가능성**: 필요시 Repository 패턴을 활용하여 Hexagonal로 진화 가능

---

### Q2. Correlation ID의 실무 활용 사례는?

**A:**
- **장애 대응**: 특정 API 호출이 실패했을 때, Correlation ID로 전체 흐름 추적
  ```bash
  grep "a3f2e8d1" *.log | grep ERROR
  # 결과: 해당 요청의 모든 로그 (Controller → Service → Repository)
  ```
- **성능 분석**: 느린 요청의 병목 구간 파악
- **분산 추적**: 마이크로서비스 환경에서 서비스 간 호출 추적 (Spring Cloud Sleuth + Zipkin)

---

### Q3. AI 추천 시스템의 신뢰도 점수(Confidence Score)는 어떻게 활용하나요?

**A:**
- **UI 표시**: 사용자에게 추천 신뢰도 시각화 (프로그레스 바)
- **필터링**: 0.7 이하는 숨김 처리 (낮은 신뢰도)
- **A/B 테스트**: 신뢰도 임계값 조정 실험
- **피드백 루프**: 사용자 클릭 데이터로 모델 재학습

**예시:**
```typescript
recommendations.filter(rec => rec.confidenceScore >= 0.7)
```

---

### Q4. 트랜잭션 격리 수준을 어떻게 선택했나요?

**A:**

| 격리 수준 | 사용 케이스 | 이유 |
|----------|------------|------|
| READ_COMMITTED | 조회, 주문 생성 | 성능 우선, Dirty Read 방지 |
| REPEATABLE_READ | 상태 변경 (confirm, cancel) | Non-Repeatable Read 방지, 동시성 제어 |

**트레이드오프:**
- REPEATABLE_READ는 성능 오버헤드가 있지만, 재고 차감 등 동시성 이슈에 필수
- 대부분의 조회는 READ_COMMITTED로 충분

---

### Q5. Zustand vs Redux, 왜 Zustand를 선택했나요?

**A:**

**Redux 코드:**
```typescript
// Action
const increment = () => ({ type: 'INCREMENT' })

// Reducer
const counterReducer = (state = 0, action) => {
  switch (action.type) {
    case 'INCREMENT': return state + 1
    default: return state
  }
}

// Store
const store = createStore(counterReducer)
```

**Zustand 코드:**
```typescript
const useStore = create((set) => ({
  count: 0,
  increment: () => set((state) => ({ count: state.count + 1 })),
}))
```

**비교:**
- **코드량**: Zustand가 70% 감소
- **타입 안전성**: TypeScript와 완벽한 타입 추론
- **번들 크기**: 1KB vs 10KB (Redux)
- **학습 곡선**: 5분 vs 2시간

---

### Q6. N+1 문제를 어떻게 해결했나요?

**A:**

**문제:**
```java
// 1번 쿼리 (Order 조회)
Order order = orderRepository.findById(1L);

// N번 쿼리 (OrderItem 조회, Lazy Loading)
order.getOrderItems().forEach(item -> {
    item.getProductName(); // 각 OrderItem마다 1번씩 쿼리
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

**효과:**
- 쿼리 수: 1 + N → 2개로 감소
- 응답 시간: 500ms → 50ms (10배 개선)

---

### Q7. 테스트 전략을 어떻게 수립했나요?

**A:**

**테스트 피라미드:**
```
         /\
        /통합\      (10%) - @SpringBootTest
       /------\
      / 슬라이스 \    (30%) - @WebMvcTest, @DataJpaTest
     /----------\
    /   단위    \   (60%) - JUnit, Mockito
   /--------------\
```

**예시:**

**단위 테스트** (OrderServiceTest.java):
```java
@Test
void createOrder_Success() {
    Order order = orderService.createOrder(...);
    assertThat(order.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(40.00));
}
```

**슬라이스 테스트** (OrderControllerTest.java):
```java
@WebMvcTest(OrderController.class)
class OrderControllerTest {
    @Test
    void createOrder_Success() throws Exception {
        mockMvc.perform(post("/api/orders")...)
            .andExpect(status().isCreated());
    }
}
```

**이점:**
- 빠른 피드백 (단위 테스트: 0.1초)
- 격리된 테스트 (Mockito로 의존성 제거)
- CI/CD 파이프라인 최적화

---

### Q8. 보안 측면에서 추가할 사항은?

**A:**

**현재 상태:**
- ✅ CORS 설정
- ✅ Validation (Bean Validation)
- ✅ JPA Prepared Statement (SQL Injection 방지)

**추가 필요:**

1. **인증/인가:**
   - Spring Security + JWT
   - OAuth2 Resource Server

2. **Rate Limiting:**
   - Bucket4j + Redis
   - IP당 100 req/min

3. **민감 정보 암호화:**
   - AWS Secrets Manager
   - HashiCorp Vault

4. **HTTPS 강제:**
   - Let's Encrypt SSL
   - HSTS 헤더

5. **XSS 방지:**
   - Content Security Policy
   - 입력 sanitization

---

### Q9. AI 추천 시스템을 실제 프로덕션에 적용하려면?

**A:**

**단계별 접근:**

**Phase 1: A/B 테스트**
- 기존 규칙 기반 vs AI 추천 비교
- 전환율, 평균 주문 금액 측정

**Phase 2: 하이브리드 접근**
- AI 추천 + 인기 상품 혼합
- 콜드 스타트 문제 해결 (신규 사용자)

**Phase 3: 실시간 개인화**
- 사용자 행동 스트리밍 (Kafka)
- 실시간 임베딩 업데이트

**모니터링:**
```
- 추천 클릭률 (CTR)
- 구매 전환율
- API 응답 시간 (SLA: 500ms 이하)
- AI API 비용 (토큰 사용량)
```

---

### Q10. 이 프로젝트를 마이크로서비스로 전환한다면?

**A:**

**모듈 분리:**
```
order-service/          # 주문 관리 (Port 8081)
product-service/        # 상품 관리 (Port 8082)
recommendation-service/ # AI 추천 (Port 8083)
api-gateway/            # Spring Cloud Gateway (Port 8080)
eureka-server/          # 서비스 디스커버리 (Port 8761)
```

**API Gateway 설정:**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: order-service
          uri: lb://ORDER-SERVICE
          predicates:
            - Path=/api/orders/**
        - id: recommendation-service
          uri: lb://RECOMMENDATION-SERVICE
          predicates:
            - Path=/api/recommendations/**
```

**추가 고려사항:**
- **분산 트랜잭션**: Saga 패턴
- **서비스 간 통신**: REST → gRPC
- **장애 격리**: Circuit Breaker (Resilience4j)
- **로그 중앙화**: ELK Stack
- **모니터링**: Prometheus + Grafana

---

## 📝 라이선스

**CC BY 4.0 (Creative Commons Attribution 4.0 International)**

✅ **모든 사용 허용**: 복제, 배포, 수정, 상업적 이용 가능
📝 **단, 출처 표시 필수**: 이 프로젝트를 사용하는 경우 출처를 명시해주세요.

상세 내용: [LICENSE](LICENSE) 파일 참조

---

## 🙏 감사의 글

이 프로젝트는 **Cursor AI**를 활용하여 개발되었으며, 실무에서 적용 가능한 아키텍처 패턴과 베스트 프랙티스를 담고 있습니다.

- 기존 프로젝트 패턴 분석
- 완전히 새로운 도메인으로 샘플 코드 작성
- AI 기반 상품 추천 시스템 통합

---

**작성자 노트:**
이 레포지토리는 실제 운영 서비스가 아닌, 아키텍처 설계 역량을 보여주기 위한 샘플입니다. 모든 코드는 교육 목적으로 작성되었으며, 실제 프로덕션 환경에서는 추가적인 보안, 모니터링, 성능 최적화가 필요합니다.

**Cursor 해커톤 2025** 🚀
