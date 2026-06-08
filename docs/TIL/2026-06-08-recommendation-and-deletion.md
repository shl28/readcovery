# 2026-06-08 — AI 분석 활용 사이클: 추천 + 삭제

## 한 사이클의 전체 흐름

readcovery의 정체성: "인용구로 자기를 발견"
첫 사이클에선 **발견하고 끝**이었음. 닫힌 흐름.

이번 작업의 목표:
인용구 → 분석 → 추천 → 서재 담기 → (사용 후) 빼기
↑                                              ↓
└──────── 새 인용구 ←  새 책 만남  ───────────┘
분석을 활용하는 추천 + 자유롭게 흐를 수 있는 빼기. 두 가지로 **선순환 완성**.

## 추천 시스템 설계

### 검색 방식 결정 (A vs B vs C)

분석 결과의 keywords로 어떻게 카카오 검색할지:

- A: 각 키워드 별도 검색 후 합치기 → 채택
- B: 키워드들 하나의 질의로 결합
- C: 대표 키워드만

A를 선택한 이유:
1. 카카오는 단일 키워드일 때 정확도가 높음 (실제 사용 경험)
2. 키워드는 사용자의 다차원적 관심사 표현 → 각각 별도 검색해 다양성 확보
3. "왜 이렇게 했나" 명확히 답할 수 있음

### 외부 API 호출 최소화

- 키워드별 최대 권수 + 5권 모이면 조기 종료
- 이미 담은 책 ISBN 집합으로 중복 제외

```java
private static final int RECOMMENDATIONS_PER_KEYWORD = 10;
private static final int TOTAL_RECOMMENDATIONS = 5;

for (String keyword : keywords) {
    // ... 검색 후 누적
    if (recommendations.size() >= TOTAL_RECOMMENDATIONS) {
        return recommendations;  // 조기 종료
    }
}
```

### GET vs POST 결정

추천 엔드포인트는 **GET**:
- 분석 결과 읽기 + 외부 검색만, 본인 DB 상태 변경 없음
- RESTful 의미상 조회

## 사용해보다 발견한 한계 — 외부 API 결정론성

추천 5권 다 담은 후 "다시 불러오기" → 3권만 채워짐.

### 원인
같은 요청(page 1, size 3)은 카카오에서 항상 같은 결과:
변화 → 같은 책 3권 (이미 다 담음) → 추가 0
행복 → 같은 책 3권 (이미 다 담음) → 추가 0
감정 → 새 책 3권 → 종료

### 해결 — 후보 풀 확대
`RECOMMENDATIONS_PER_KEYWORD: 3 → 10`

검토한 대안들:
- 페이지 랜덤: 다양성↑ 일관성↓
- 커서 페이지네이션: 정교하지만 컬럼 + 로직 추가
- 권수 확대 → **채택** (한 줄 변경, 후보 풀 충분)

후보 풀 결정 기준:
- 카카오 API의 size 최대(50) 안전 범위
- 사용자가 50권 다 담기는 사실상 도달 불가

## 선순환 완성 — 삭제 기능

추천은 만들었는데 빼기가 없었음. 사용자 흐름 불완전.

### 두 곳에 삭제 위치

1. **내 서재 카드**: 우측 상단 작은 X (B 변형)
    - 평소 amber-300 (은은), hover 시 amber-700 (진해짐)
    - 모바일에서도 발견 가능, 데스크탑에선 깔끔
2. **책 상세 페이지**: "서재에서 빼기" 텍스트 (우측 상단)
    - 의도적 진입한 곳이라 텍스트가 더 명확

### Link 안의 button — 이벤트 전파 차단

내 서재 책 카드는 `<Link>`로 감싸져 있어서 X 누를 때도 Link 작동해버림.

```typescript
const handleDelete = async (e: React.MouseEvent, ...) => {
    e.preventDefault();      // Link의 navigate 막기
    e.stopPropagation();     // 부모로의 버블링 막기
    // ... 삭제 로직
};
```

둘 다 필요. 한쪽만으론 부족.

### cascade 동작을 confirm으로 안내

```typescript
confirm(`"${title}"을(를) 서재에서 빼시겠어요?\n\n등록된 인용구도 함께 삭제됩니다.`)
```

내가 백엔드에서 만든 cascade(MyBook 삭제 시 Quote도 함께 삭제)는 사용자에게 보이지 않는 동작.
confirm에 명시해서 사용자가 인지하고 동의한 후 진행하게 함.

## 면접 답변 재료

1. **선순환 흐름 설계**: 닫힌 분석 → 열린 사이클로
2. **외부 API의 결정론적 응답 함정과 해결**
3. **GET vs POST의 RESTful 의미 한 끗 차이**
4. **Link 안의 button — preventDefault + stopPropagation**
5. **백엔드 cascade를 프론트 UX로 일관되게 흘리기**
6. **후보 풀 크기 결정의 객관적 근거 (API max, 도달 가능성)**

## 새로 만난 개념

- React 이벤트 전파 차단 (preventDefault vs stopPropagation 차이)
- 외부 API의 결정론적 응답 (deterministic response)
- 후보 풀(candidate pool) 개념 — 추천 시스템 기본 패턴
- aria-label로 시각 의존 UI 접근성

## 다음에 할 것

- 마이페이지 (회원 조회/수정)
- 디자인 폴리시
- 배포 (AWS)
- (선택) 추천 결과 캐싱 — 분석 결과별로 추천도 캐시 가능
