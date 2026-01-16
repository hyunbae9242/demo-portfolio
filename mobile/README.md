# Mobile Sample - Android/Kotlin MVVM Architecture

> **이 프로젝트는 실제 운영 앱이 아닌, 모바일 아키텍처 및 설계 역량을 보여주기 위한 샘플 코드베이스입니다.**

## 아키텍처: MVVM + Repository Pattern

**선택 이유:**
- Google 권장 아키텍처
- UI와 비즈니스 로직 명확한 분리
- LiveData/StateFlow로 반응형 UI
- 테스트 가능성

## 레이어 구조

```
src/main/java/com/sample/mobile/
├── ui/                    # Presentation Layer
│   ├── order/            # 주문 화면
│   │   ├── OrderListViewModel.kt
│   │   ├── OrderListFragment.kt
│   │   └── OrderUiState.kt
│   └── recommendation/   # AI 추천 화면
│       └── RecommendationFragment.kt
│
├── data/                 # Data Layer
│   ├── repository/       # Repository 인터페이스 및 구현
│   ├── datasource/       # Remote/Local DataSource
│   └── model/            # DTO (Data Transfer Object)
│
├── domain/               # Domain Layer
│   ├── model/            # Domain Model
│   └── usecase/          # Use Case (비즈니스 로직)
│
└── common/               # Common
    └── network/          # Network 설정 (Retrofit, OkHttp)
```

## 핵심 패턴

### 1. UiState Pattern
```kotlin
sealed class OrderUiState {
    object Loading : OrderUiState()
    data class Success(val orders: List<Order>) : OrderUiState()
    data class Error(val message: String) : OrderUiState()
}
```

### 2. Repository Pattern
- DataSource 추상화
- 단일 진실 공급원 (Single Source of Truth)

### 3. Dependency Injection
- 수동 DI (Hilt/Dagger 없음, 구조 샘플에 집중)

## 빌드 및 실행

Android Studio에서 프로젝트 열기 → Run

## 주요 의존성

- **Jetpack Compose**: 선언적 UI
- **ViewModel & LiveData**: 생명주기 인식 데이터
- **Retrofit & OkHttp**: REST API 클라이언트
- **Coroutines**: 비동기 처리

---

**Note:** 이 프로젝트는 구조 샘플이며, 실제 빌드를 위해서는 추가 설정이 필요합니다.
