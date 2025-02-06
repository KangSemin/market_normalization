# 🛍️ 거래소 정상화

## 프로젝트 소개
Market normalization은 특정 가상의 게임을 대상으로 한 아이템 거래 플랫폼으로, 사용자들이 물품을 거래 및 경매 방식으로 거래할 수 있는 서비스입니다.

## 🛠️ 기술 스택

- Java 17
- Spring Boot 3.4.2
- Spring Data JPA
- QueryDSL
- MySQL 8.0
- Redis
- Github Action
- AWS EC2
- Docker

## 📋 주요 기능

### 1. 사용자 관리
- 회원가입/로그인
- 유저 프로필 조회
- 거래/경매 내역 조회

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

### 성능 최적화
- QueryDSL을 활용한 동적 쿼리 최적화
- Redis 캐싱을 통한 조회 성능 향상
- Cursor 페이지네이션을 통한 조회 성능 향상

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

## 📈 성능 테스트 결과

  - 동시 사용자 6,000,000명 기준
  - 응답 시간: 평균 ?ms
  - TPS: ?

## 👥 팀원
- 
- 
- 
