# 🛍️ Market Normalization 🛍️

## 💻 프로젝트 소개
- 특정 가상의 게임을 대상으로 한 게임 아이템 거래 플랫폼
- 사용자가 아이템을 자유롭게 거래하거나 경매하는 서비스 제공 
- 진행 기간: 2025/01/31 ~ 2025/02/07

## 🛠️ 기술 스택

### Back
<img src="https://img.shields.io/badge/Java-007396?style=flat-square&logo=OpenJDK&logoColor=white">&nbsp;
<img src="https://img.shields.io/badge/Spring-6DB33F?style=flat-square&logo=spring&logoColor=white">&nbsp;
<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white">&nbsp;
<img src="https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white">&nbsp;
<img src="https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=redis&logoColor=white">&nbsp;
<img src="https://img.shields.io/badge/GitHub Actions-2088FF?style=flat-square&logo=githubactions&logoColor=white">&nbsp;
<img src="https://img.shields.io/badge/Amazon AWS-232F3E?style=flat-square&logo=amazonaws&logoColor=white">&nbsp;
<img src="https://img.shields.io/badge/Amazon EC2-FF9900?style=flat-square&logo=amazonec2&logoColor=white">&nbsp;
<img src="https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=Docker&logoColor=white"/>&nbsp;

### Tool
<img src="https://img.shields.io/badge/jira-%230A0FFF.svg?style=for-the-badge&logo=jira&logoColor=white"/>&nbsp;
<img src="https://img.shields.io/badge/git-%23F05033.svg?style=for-the-badge&logo=git&logoColor=white"/>&nbsp;
<img src="https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white"/>&nbsp;

## 🔗 ERD

```mermaid
erDiagram
    USER {
        bigint id PK "사용자 식별자"
        varchar email "이메일"
        bigint gold "보유 골드"
        varchar job "게임 캐릭터 직업"
        smallint level "게임 캐릭터 레벨"
        varchar nickname "닉네임"
        varchar password "비밀번호"
        varchar server "게임 서버"
    }

    ITEM {
        bigint id PK "아이템 식별"
        varchar name "아이템 이름"
    }

    AUCTION {
        bigint id PK "경매장 식별자"
        datetime created_at "생성 시간"
        int bidder_count "입찰 횟수"
        datetime due_date "경매 마감 기한"
        bigint starting_price "경매 시작가"
        enum status "경매 진행 상태 (CANCELLED, COMPLETED, ON_SALE)"
        bigint item_id FK "아이템 외래키"
        bigint user_id FK "사용자 외래키"
    }

    BID {
        bigint id PK "입찰 식별자"
        datetime created_at "생성 시간"
        bigint bid_price "입찰 가격"
        datetime updated_at "마지막 입찰 성공 시간"
        bigint auction_id FK "경매 외래키"
        bigint user_id FK "사용자 외래키"
    }

    INVENTORY {
        bigint id PK "인벤토리 식별자"
        int amount "사용자 인벤토리 아이템 개수"
        bigint version "낙관적 락을 위한 엔티티 버전"
        bigint item_id FK "아이템 외래키"
        bigint user_id FK "사용자 외래키"
    }

    MARKET {
        bigint id PK "거래소 식별자"
        datetime created_at "생성 시간"
        int amount "아이템 수량"
        bigint price "아이템 가격"
        enum status "아이템 거래 상태 (CANCELLED, COMPLETED, ON_SALE)"
        bigint version "낙관적 락을 위한 엔티티 버전"
        bigint item_id FK "아이템 외래키"
        bigint user_id FK "사용자 외래키"
    }

    TRADE {
        bigint id PK "거래 식별자"
        datetime created_at "생성 시간"
        int amount "거래 아이템 수량"
        bigint total_price "총 거래 가격"
        bigint market_id FK "거래소 외래키"
        bigint user_id FK "사용자 외래키"
    }

    TRADE_COUNT {
        bigint item_id PK "아이템 식별자"
        bigint count "거래 횟수"
    }

    USER ||--o{ AUCTION : "경매 등록"
    USER ||--o{ BID : "입찰"
    USER }|--|| INVENTORY : "보유"
    USER ||--o{ MARKET : "거래소 판매"
    USER ||--o{ TRADE : "거래"

    ITEM ||--o{ AUCTION : "경매 대상"
    ITEM ||--o{ INVENTORY : "보유"
    ITEM ||--o{ MARKET : "거래 가능"

    AUCTION }|--|| BID : "입찰 진행"
    MARKET ||--o{ TRADE : "거래 발생"
```

## 📋 주요 기능
### 1. 사용자 관리
- 로그인
- 유저 프로필 조회
- 거래 및 경매 내역 조회

### 2. 거래 시스템
- 판매 등록/거래/판매 취소
- 인기 거래 아이템 조회

### 3. 경매 시스템
- 경매 등록/경매에 입찰/경매 취소
- 경매 종료 자동화
- 인기 경매 아이템 조회

## 🔍 핵심 서비스 소개
### 1. 실시간 거래소 시스템
- Redis를 활용한 동시성 제어

### 2. 실시간 경매장 시스템
- JPA 비관적 락을 활용하여 동시성 제어 
- 스케줄러를 활용하여 30초마다 마감일이 지난 경매 상태를 '마감(COMPLETED)'으로 자동 변경 

## 🎯 성능 개선
<details><summary>📌 1. 조회 성능 개선 </summary>

**문제점**

조회 성능이 매우 느림↓ ➜ 초기 거래소 조회 속도: **36.8초** 

<img src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fbgg7xA%2FbtsL9Qcy1Bu%2FQdxCnqo18fwdUiNyJJLmY1%2Fimg.png"/>

**개선 과정**

1. **1차 개선**
    - ⏳ 36.8초 ➜ 🚀 **24.17초**
    - ⚡ **12.63초** 단축

   **개선 내용**
    - **tradeCount** 집계 테이블 생성
    - **tradeCount** 테이블의 `count`에 인덱스 생성 (ASC)
    - 거래소 `status`와 `createdAt` 복합 인덱스 생성
    - **trade** 테이블의 `createdAt` 인덱스 생성 (DESC)

   **문제점**  
   - 인덱스가 너무 많고, **trade** 테이블에 인덱스가 있어 삽입 시 오버헤드 우려됨  
   - **trade**는 삽입이 활발히 일어나는 항목이므로 해당 부분에 대한 개선이 필요함

---
2. **2차 개선**
    - ⏳ 24.17초 ➜ 🚀 **3.6초**
    - ⚡ **20.57초** 단축

   **개선 내용**
    - 커서 기반 페이지네이션 적용
    - 정렬 전략 별로 다른 cursor 사용
    - tie-breaker로 `itemId` 사용
    - (status, createdAt, itemId, amount, price) 복합 인덱스 생성 
      - 이전처럼 인덱스를 많이 사용하기보다 하나의 인덱스로 성능을 개선함

   **문제점**
    - `market` 삽입 시 오버헤드를 고려할 필요가 있으나, **trade**보단 빈도가 덜할 테니 상대적으로 괜찮다고 판단됨

---
3. **3차 개선**

   **3-1. 풀텍스트 인덱스 적용**
   - ⏳ 3.6초 ➜ 🚀 **0.039초**
   - ⚡ **3.561초** 단축

   **개선 내용**
   - CustomFunctionContributor 이용하여 풀텍스트 인덱스(Full-Text Index) 적용

  **3-2. 느린 쿼리 최적화 (거래소 인기내역)**
   - ⏳ 26.7초 ➜ 🚀 **0.035초**
   - ⚡ **26.665초** 단축 

  **개선 내용**
   - Redis 캐싱을 이용하여 성능 향상
   - 인기 내역은 변동성이 적기 때문에 TTL을 꽤 길게 가져감
   - DB 부하 감소
     - 느린 쿼리로 인한 DB 트래픽 줄여줌
   
  **향후 개선 목표**
   - 현재 해결책은 임시 방편임 
   - `cursor`와 `tie-breaker`를 넣어주면 그나마 속도가 빠르지만, 인기 내역 첫 로드 시 조회속도가 매우 느림
   - 원인을 찾지 못하였기에 추후 생각날 때마다 개선 예정
  
<img src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FuJNAC%2FbtsMawR4y3A%2F2NmxLIrasD8epKDLkO3YF1%2Fimg.png" width="750" height="450"/>
  
</details>

<details><summary>📌 2. 동시성 제어 </summary>

**문제점**

한 경매에 여러 입찰이 동시에 이루어졌을 때 교착상태(deadlock) 발생↓

![이미지](https://github.com/KangSemin/market_normalization/blob/dev/%E1%84%83%E1%85%A1%E1%84%8B%E1%85%AE%E1%86%AB%E1%84%85%E1%85%A9%E1%84%83%E1%85%B3%20(6).png)

**개선 과정**

- **대안 비교**
   - **선택 사항**
     - 1안: 비관적 락 
     - 2안: 낙관적 락 
   - **테스트 조건**
      - 입찰 요청 총 개수: **100개**
      - 스레드: **20개**
    - **테스트 과정**
      - 1안 및 2안을 각각 비즈니스 로직에 적용 후 테스트 코드 실행
      - 이후 `속도`, `발생한 예외 개수` 등등 비교
   - **테스트 결과**
![스크린샷](https://github.com/KangSemin/market_normalization/blob/dev/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202025-02-07%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%201.09.45.png)

- **결정 및 근거**
  - **1안 `비관적 락` 적용**
  - **근거**
    - 경매 및 거래는 동시에 사용자가 몰리기 때문에 충돌이 자주 발생함 
    - 속도 면에서는 낙관적 락이 우세하나, CustomException 외에 예상치 못한 예외가 너무 발생함 
    - 경매와 거래는 속도보다 데이터 정확도가 정말 중요하기 때문에 비관적 락을 적용하기로 결정함
</details>

## 🏆 서비스 최종 성능 정리 
- queryDSL을 활용한 동적 조회 구현
- 조회 성능 개선 
  - 커서 기반 페이지네이션, 풀텍스트인덱스, redis caching 활용 
  - **평균 17배 속도 단축**

## 📝 API 명세서
### 1. 사용자 API
| Method | URI                    | Request Body       | Request Parameters | Path Variables | Response Code | Description  |
|--------|------------------------|--------------------|--------------------|----------------|---------------|--------------|
| POST   | /auth/login            | `email` `password` |                    |                | 200           | 게임 아이디로 로그인  |
| GET    | /users/my-profile      |                    |                    |                | 200           | 본인 프로필 조회    |
| GET    | /user/history/markets  |                    |                    |                | 200           | 본인의 거래 목록 조회 | 
| GET    | /user/history/trades   |                    |                    |                | 200           | 본인의 거래 단건 조회 | 
| GET    | /user/history/auctions |                    |                    |                | 200           | 본인의 경매 내역 조회 |
| DELETE | /auth/logout           |                    |                    |                | 204           | 로그아웃         |

### 2. 거래소 API
| Method | URI                 | Request Body              | Request Parameters | Path Variables | Response Code | Description      |
|--------|---------------------|---------------------------|--------------------|----------------|---------------|------------------|
| POST   | /markets            | `itemId` `price` `amount` |                    |                | 201           | 판매할 아이템 등록       |
| POST   | /markets/trades     | `itemId` `amount`         |                    |                | 200           | 거래소에 등록된 아이템 구매  |
| GET    | /markets/{itemId}   |                           | `                  | `itemId`       | 200           | 특정 아이템의 거래 목록 조회 | 
| GET    | /markets/populars   |                           |                    |                | 200           | 인기 아이템 조회        |
| DELETE | /markets/{marketId} |                           |                    | `marketId`     | 200           | 거래 취소            |

### 3. 경매장 API
| Method | URI                   | Request Body                           | Request Parameters                                                                              | Path Variables | Response Code | Description     |
|--------|-----------------------|----------------------------------------|-------------------------------------------------------------------------------------------------|----------------|---------------|-----------------|
| POST   | /auctions             | `itemId` `startingPrice` `auctionDays` |                                                                                                 |                | 201           | 경매 등록           |
| GET    | /auctions/main        |                                        | `lastAuctionId` `searchKeyword` `sortBy` `sortDirection` `lastStartPrice` `lastCurrentMaxPrice` |                | 200           | 마감 전 경매 검색 및 조회 |
| GET    | /auctions/{auctionId} |                                        | `                                                                                               | `auctionId`    | 200           | 경매 단건 조회        | 
| GET    | /auctions/populars    |                                        | `lastBidderCount` `lastAuctionId`                                                               |                | 200           | 인기 아이템 조회       |
| PATCH  | /auctions/bids        | `auctionId` `bidPrice`                 |                                                                                                 |                | 200           | 경매에 입찰          |
| DELETE | /auctions/{auctionId} |                                        |                                                                                                 | `auctionId`    | 200           | 경매 취소           |

## 팀원
| <img src="https://avatars.githubusercontent.com/u/185327147?v=4" width="130" height="130"> | <img src="https://avatars.githubusercontent.com/u/185164572?v=4" width="130" height="130"> | <img src="https://avatars.githubusercontent.com/u/77243795?v=4" width="130" height="130"> | <img src="https://avatars.githubusercontent.com/u/67899848?v=4" width="130" height="130"> |
| :---------------------------------------------------------------------------------------: | :---------------------------------------------------------------------------------------: | :---------------------------------------------------------------------------------------: | :---------------------------------------------------------------------------------------: |
|[강세민](https://github.com/KangSemin)|[신지현](https://github.com/backswan0)|[이우진](https://github.com/gkdl4239)|[이채영](https://github.com/roqkfchqh)|
