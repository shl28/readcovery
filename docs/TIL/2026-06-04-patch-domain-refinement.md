# 2026-06-04 — PATCH 도메인 정교화

## 오늘 한 것

- 독서 상태 관리 UI 완성 (MyBookDetailPage)
- 별점 토글 동작 구현 (해제 가능하게)
- 상태 다운그레이드 시 시간/별점 자동 클리어
- PATCH의 본질적 한계 발견 + 해결

## 핵심 의문 — PATCH는 정말 잘 동작하고 있었나?

MVP 끝낸 후 사용해보다 발견한 한계:

1. **별점 5 매겼다가 해제하려고 다시 누르면?** → null 보내도 무시됨
2. **완독 후 "읽고 싶은"으로 다시 바꾸면?** → 시작/완독 시간이 그대로 남음
3. **사용자 입장에서 자연스러운가?** → 아니다


## PATCH의 본질적 한계 — '안 보냄' vs 'null 보냄'

자바 객체로는 두 의도가 모두 null로 보임:

```json
// 케이스 A: 별점 해제 의도
{ "rating": null }

// 케이스 B: 별점 안 건드림 (상태만 변경)
{ "status": "READING" }
```

백엔드에서 둘 다 `request.rating == null`로 보인다. 구분 불가.

이게 JSON Merge Patch (RFC 7396)의 본질적 한계. 표준이 이미 인지하는 문제.

## 해결 방법 비교

| 방법 | 특징 | 채택 여부 |
|------|------|----------|
| A. JSON Patch (RFC 6902) | 표준이지만 복잡 | X |
| B. Optional 래퍼 | 강력하지만 Spring 4 + Jackson 3 환경 변수 많음 | X |
| C. 별도 엔드포인트 (DELETE /rating) | 깔끔하지만 통합 PATCH 의도와 어긋남 | X |
| D. clearRating 플래그 | 명시적 의도, 통합 PATCH 유지 | ✓ |

내 readcovery는 통합 PATCH로 설계했으니 D가 일관됨.

## 코드 변경 — 세 파일

### MyBook 도메인 — changeStatus를 의미 단위로

각 상태가 가져야 할 데이터 의미를 메서드에 박음:

- WANT: "아직 시작 안 함" → startedAt, finishedAt, rating 모두 null
- READING: "진행 중" → startedAt 기록(첫 진입 시), finishedAt/rating null
- DONE: "다 읽음" → startedAt 없으면 기록, finishedAt 기록, rating은 유지

별점은 "완독한 책에 대한 평가"의 의미를 가진다.
완독이 아닌 상태로 다운그레이드되면 별점도 자동 null.

```java
public void changeStatus(ReadingStatus newStatus) {
    this.status = newStatus;
    switch (newStatus) {
        case WANT -> {
            this.startedAt = null;
            this.finishedAt = null;
            this.rating = null;
        }
        case READING -> {
            if (this.startedAt == null) {
                this.startedAt = LocalDateTime.now();
            }
            this.finishedAt = null;
            this.rating = null;
        }
        case DONE -> {
            if (this.startedAt == null) {
                this.startedAt = LocalDateTime.now();
            }
            this.finishedAt = LocalDateTime.now();
        }
    }
}

public void clearRating() {
    this.rating = null;
}
```

### MyBookUpdateRequest — clearRating 플래그 추가

```java
public class MyBookUpdateRequest {
    private ReadingStatus status;
    @Min(1) @Max(5)
    private Integer rating;
    private boolean clearRating;   // 명시적 해제 의도
}
```

### MyBookService — clearRating 우선 분기

```java
if (request.isClearRating()) {
    myBook.clearRating();
} else if (request.getRating() != null) {
    myBook.rate(request.getRating());
}
```

명시적 의도(clearRating)가 우선. rating 값이 함께 와도 clearRating이 이김.

## 별점 null이 되는 두 경로 — 의도가 다름

같은 결과(rating=null)지만 의도와 코드 경로가 다르다:

1. **자동 클리어** — 상태 다운그레이드 시. 도메인 메서드 안.
2. **명시적 해제** — 완독 유지하며 별점만 토글. clearRating 플래그.

두 경로를 코드에서 분리한 게 의미 있다. 미래의 내가 봐도 어떤 의도인지 명확.

## 프론트 — isToggleOff 변수로 의도 표현

```typescript
const handleChangeRating = async (newRating: number) => {
    if (!myBook) return;
    const isToggleOff = myBook.rating === newRating;
    
    const updated = await myBookApi.update(myBook.my_book_id, 
        isToggleOff 
            ? { clear_rating: true }
            : { rating: newRating }
    );
    setMyBook(updated);
};
```

`myBook.rating === newRating`만 쓰면 기술적 비교.
`isToggleOff` 변수에 담으면 비즈니스 의도가 드러남.

## 면접 답변 재료

1. **PATCH의 본질적 한계와 해결**
    - JSON Merge Patch의 null 구분 불가 문제
    - 명시적 플래그(clearRating)로 우회
    - 명시적 의도가 우선이라는 원칙

2. **도메인 메서드에 상태 의미 박기**
    - changeStatus가 각 상태의 의미를 시간/별점에 반영
    - 데이터 정합성을 도메인 레이어에서 보장

3. **MVP → 사용 → 한계 발견 → 정교화 사이클**
    - 작게 시작한 결정이 옳았던 이유
    - 한 사이클 돌아본 사람만의 시야로 다음 사이클 보강

4. **별점 자동/명시 두 경로 분리**
    - 같은 결과지만 다른 의도
    - 코드에서 두 경로를 분리해 가독성 확보

## 새로 만난 개념

- **JSON Merge Patch (RFC 7396)** — PATCH의 표준 중 하나
- **Java 14+ switch with arrow (`->`)** — 향상된 switch 표현
- **명시적 플래그 패턴** — 의도 전달이 어려운 곳에 boolean 필드 추가

## 다음에 할 것

- 사용자가 시작/완독 날짜를 직접 선택할 수 있게 (이번엔 안 함, 산으로 안 가는 결정)
- 마이페이지 (회원 조회/수정)
- AI 분석 결과 활용 (추천 책 등)
