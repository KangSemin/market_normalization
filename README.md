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
<details>
  <summary>📌 클릭해서 ERD 펼치기</summary>

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
        int bidder_count "경매 참여자 수"
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

    USER ||--o{ AUCTION : "참여"
    USER ||--o{ BID : "입찰"
    USER ||--o{ INVENTORY : "보유"
    USER ||--o{ MARKET : "거래소 판매"
    USER ||--o{ TRADE : "거래"

    ITEM ||--o{ AUCTION : "경매 대상"
    ITEM ||--o{ INVENTORY : "보유"
    ITEM ||--o{ MARKET : "거래 가능"
    ITEM ||--o{ TRADE_COUNT : "거래 기록"

    AUCTION ||--o{ BID : "입찰 진행"
    MARKET ||--o{ TRADE : "거래 발생"
```
</details>

## 📋 주요 기능
### 1. 사용자 관리
- 로그인
- 유저 프로필 조회
- 거래 및 경매 내역 조회

### 2. 경매 시스템
- 실시간 경매 입찰
- 경매 종료 자동화

### 3. 상품 관리
- 상품 등록/수정/삭제
- 인기 상품 조회
- 상품 검색

## 🔍 핵심 기술 구현 사항

### 실시간 경매 시스템
- Redis를 활용한 동시성 제어
- 스케줄러를 통한 경매 자동 종료

## 성능 개선
<details><summary>성능 개선</summary>
  
## 문제: 조회 성능이 매우 매우 느리다.
<img src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fbgg7xA%2FbtsL9Qcy1Bu%2FQdxCnqo18fwdUiNyJJLmY1%2Fimg.png"/>

### 초기 거래소 조회 속도: 36.8초
### 1차 개선 후: 24.17초
- 개선내용:
1. tradeCount 집계테이블 생성
2. tradeCount테이블의 count에 인덱스 생성(asc)
3. 마켓status와 createdAt 복합인덱스 생성
4. trade에 createdAt 인덱스 생성(desc)
- 문제점: 인덱스가 너무 많고, trade에 인덱스가 걸려있어 삽입 시 오버헤드 우려됨. trade는 삽입이 활발히 일어나는 항목이므로 해당 부분에 대한 개선 필요했음
  
<br>

### 커서기반 페이지네이션 적용 후: 3.6초
- 개선내용:
1. 커서 기반 페이지네이션 적용
2. 정렬 전략 별로 다른 cursor 사용, tie-breaker로 itemId 사용
3. 조건에 따른 조회와 검색이 많이 일어나는 항목이므로, (status,createdAt, itemId, amount, price) 복합 인덱스 생성. 이전처럼 인덱스를 많이 사용하기보다 하나의 인덱스로 성능을 개선함.
- 문제점: market 삽입 시 오버헤드를 고려할 필요가 있으나, trade보단 빈도가 덜할 것이기에 상대적으로 괜찮다고 판단함.
  
<br>

### 풀텍스트 인덱스 적용 후: 39ms
- 개선내용: CustomFunctionContributor 이용하여 풀텍스트 인덱스 적용
  
<br>

## caching
### 튜닝을 마치지 못한 느린 쿼리(거래소 인기내역): 26.7초
- 문제점: cursor와 tie-breaker를 넣어주면 그나마 속도가 빠르지만, 인기 내역 첫 로드 시 조회속도가 매우 느림. 원인 찾지 못하였고, 이후 생각날 때 마다 개선 예정.
- 해결(임시방편): redis 캐싱을 이용하여 성능 향상. 어차피 인기 내역은 변동성이 적기 때문에 TTL을 꽤 길게 가져감. DB 부하 감소: 느린 쿼리로 인한 DB 트래픽 줄여줌
  
<img src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FuJNAC%2FbtsMawR4y3A%2F2NmxLIrasD8epKDLkO3YF1%2Fimg.png" width="750" height="450"/>
  
</details>

## 📝 API 

### 1.사용자 API

| 기능 |HTTP 메서드|END POINT  | 설명 |
|-|-|-|-|
로그인|POST|auth/login|게임 아이디를 통한 유저 로그인
로그아웃|	DELETE|	/auth/logout|로그아웃
프로필 조회|	GET|	/user/my-profile|유저 프로필 조회
유저 거래소 내역 조회|	GET	|/user/history/markets|로그인 한 유저의 거래소 내역 조회
유저 경매장 내역 조회|	GET	|/user/history/auctions|로그인 한 유저의 경매장 내역 조회

### 2. 거래소 API
| 기능 |HTTP 메서드|END POINT  | 설명 |
|-|-|-|-|
거래소 조회|	GET|	/markets/main| 거래소에 등록된 아이템 검색 및 조회
거래소 아이템 조회|	GET|	/markets/{itemId}|거래소에 등록된 특정 아이템의 매물 조회
아이템 등록(거래소)|	POST|	/markets| 거래소에 아이템 등록
아이템 구매(거래소)|	POST|	/markets/trades| 거래소에 등록된 아이템 구매
아이템 등록취소(거래소)|	DELETE|	/markets/{marketId}| 거래소에 등록한 아이템 취소|
인기아이템 조회(거래소)|	GET|	/markets/populars| 인기 아이템 조회|

### 3. 경매장 API
| 기능 |HTTP 메서드|END POINT  | 설명 |
|-|-|-|-|
경매장 조회|	GET	|/auctions/main|진행중인 경매 검색 및 조회
아이템 등록(경매장)|	POST	|/auctions|경매장에 아이템 등록
아이템 입찰(경매장)|	PATCH	|/auctions/bids|경매장에 등록된 아이템 입찰
아이템 등록취소(경매장)|	DELETE	|/auctions/{auctionId}|경매장에 등록한 아이템 취소
인기아이템 조회(경매장)|	GET	|/auctions/populars|인기 아이템 조회

## 팀원

| <img src="https://avatars.githubusercontent.com/u/185327147?v=4" width="130" height="130"> | <img src="https://avatars.githubusercontent.com/u/185164572?v=4" width="130" height="130"> | <img src="https://avatars.githubusercontent.com/u/77243795?v=4" width="130" height="130"> | <img src="https://avatars.githubusercontent.com/u/67899848?v=4" width="130" height="130"> |
| :---------------------------------------------------------------------------------------: | :---------------------------------------------------------------------------------------: | :---------------------------------------------------------------------------------------: | :---------------------------------------------------------------------------------------: |
|[강세민](https://github.com/KangSemin)|[신지현](https://github.com/backswan0)|[이우진](https://github.com/gkdl4239)|[이채영](https://github.com/roqkfchqh)|
