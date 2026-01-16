# 🚀 Quick Start Guide

이 문서는 빠르게 프로젝트를 이해하고 실행하기 위한 가이드입니다.

## 📖 먼저 읽어야 할 문서

1. **[README.md](README.md)** ⭐ - 전체 프로젝트 개요 (가장 중요!)
2. **[backend/README.md](backend/README.md)** - Backend 상세 설명
3. **[LICENSE](LICENSE)** - CC BY 4.0 라이선스

---

## 🎯 프로젝트 핵심

### 목적
Cursor 해커톤 포트폴리오 - 멀티 스택 아키텍처 설계 역량 시연

### 주요 특징
- ✅ Backend (Java/Spring Boot) + Layered Architecture
- ✅ Web (React/TypeScript) + Feature-Sliced Design
- ✅ Mobile (Android/Kotlin) + MVVM Pattern
- ✅ **AI 상품 추천 시스템** (Mock AI Client)

### 핵심 기능
1. 주문 관리 (Order Management)
2. **AI 기반 상품 추천** ⭐
3. 전역 에러 처리 + Correlation ID
4. 테스트 코드 (단위 + 슬라이스)

---

## ⚡ 5분 안에 실행하기

### 1️⃣ Backend 실행

```bash
cd backend
./gradlew bootRun
```

**확인:**
- API: http://localhost:8080
- H2 콘솔: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:sampledb`
  - Username: `sa`
  - Password: (비어있음)

### 2️⃣ Web 실행

```bash
cd web
npm install
npm run dev
```

**확인:**
- http://localhost:3000
- "Get AI Recommendations" 버튼 클릭 → AI 추천 확인

### 3️⃣ Mobile (선택)

Android Studio에서 `mobile/` 폴더 열기 → Run

---

## 🧪 API 테스트

### 주문 생성
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 100,
    "customerName": "John Doe",
    "items": [{
      "productId": 1,
      "productName": "Spring Boot in Action",
      "quantity": 2,
      "unitPrice": 45.99
    }]
  }'
```

### AI 추천 조회
```bash
curl http://localhost:8080/api/recommendations?customerId=100
```

**응답 예시:**
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

---

## 📂 프로젝트 구조 한눈에 보기

```
multi-stack-sample-portfolio/
├── README.md          # 전체 개요 (필독!)
├── QUICK_START.md     # 이 파일
├── LICENSE            # CC BY 4.0
├── backend/           # Java/Spring Boot
│   ├── src/main/java/com/sample/system/
│   │   ├── domain/            # 비즈니스 로직
│   │   ├── application/       # Use Case
│   │   ├── infrastructure/    # DB, 외부 API
│   │   └── presentation/      # REST API
│   └── README.md
├── web/               # React/TypeScript
│   ├── src/
│   │   ├── features/          # order, recommendation
│   │   ├── shared/            # api, components
│   │   └── app/
│   └── package.json
└── mobile/            # Android/Kotlin
    ├── app/src/main/java/com/sample/mobile/
    │   ├── ui/                # MVVM ViewModel
    │   ├── data/              # Repository
    │   └── domain/            # Model
    └── build.gradle
```

---

## 🎨 UI 스크린샷 (예상)

**Web - AI 추천 화면:**
```
┌─────────────────────────────────────┐
│ 🤖 AI-Powered Product Recommendations │
│ Based on your order history         │
├─────────────────────────────────────┤
│ #1 Spring Boot in Action            │
│ 📝 Based on your Java interests     │
│ Confidence: ████████░░ 92%          │
│                    [Add to Cart]    │
├─────────────────────────────────────┤
│ #2 Clean Architecture               │
│ 📝 Similar customers also bought    │
│ Confidence: ███████░░░ 87%          │
│                    [Add to Cart]    │
└─────────────────────────────────────┘
```

---

## 🔑 핵심 코드 위치

### Backend
- **AI 추천**: `backend/src/.../domain/recommendation/`
- **주문 도메인**: `backend/src/.../domain/order/Order.java`
- **예외 처리**: `backend/src/.../common/exception/`
- **테스트**: `backend/src/test/.../domain/order/OrderServiceTest.java`

### Web
- **AI 추천 UI**: `web/src/features/recommendation/`
- **상태 관리**: `web/src/features/order/store/orderStore.ts`
- **API 클라이언트**: `web/src/shared/api/`

### Mobile
- **ViewModel**: `mobile/app/src/.../ui/order/OrderListViewModel.kt`
- **Repository**: `mobile/app/src/.../data/repository/`

---

## 🤔 자주 묻는 질문

### Q1. 실제로 동작하나요?
**A:** Backend는 H2 메모리 DB로 실행 가능합니다. Web은 Backend API를 호출합니다. Mobile은 구조 샘플이며, 빌드 설정이 추가로 필요합니다.

### Q2. AI 추천은 실제 AI인가요?
**A:** 현재는 Mock 응답입니다. Claude API 또는 OpenAI API로 전환 가능한 구조로 설계되어 있습니다.

### Q3. 이 프로젝트를 사용해도 되나요?
**A:** 네! CC BY 4.0 라이선스로, 출처만 표시하면 자유롭게 사용, 수정, 상업적 이용 가능합니다.

### Q4. Cursor 해커톤과 어떤 관련이 있나요?
**A:** 이 프로젝트는 Cursor AI를 활용하여 개발되었으며, AI 통합 (상품 추천) 기능을 포함하고 있습니다.

---

## 📚 더 알아보기

- **아키텍처 상세**: [README.md](README.md) → "핵심 설계 결정"
- **면접 대비**: [README.md](README.md) → "면접 대비 Q&A"
- **확장 시나리오**: [README.md](README.md) → "확장 시나리오"

---

## 💡 개선 아이디어

이 프로젝트를 기반으로 다음을 추가할 수 있습니다:

1. **실제 AI API 연동**: Anthropic Claude API 또는 OpenAI GPT
2. **프론트엔드 개선**: TailwindCSS, Storybook
3. **CI/CD**: GitHub Actions, Docker
4. **모니터링**: Prometheus + Grafana
5. **마이크로서비스**: 서비스 분리, API Gateway

---

**Happy Coding! 🚀**

문의사항이 있으면 [README.md](README.md)의 "면접 대비 Q&A"를 참고하세요.
