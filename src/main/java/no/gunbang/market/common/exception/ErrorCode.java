package no.gunbang.market.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),

    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 아이템입니다."),

    AUCTION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 경매입니다."),

    AVAILABLE_MARKET_NOT_FOUND(HttpStatus.NOT_FOUND, "구매 가능한 마켓이 존재하지 않습니다."),

    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 엔티티입니다."),

    AUCTION_DAYS_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "경매 희망 기간은 3일 이상, 7일 이하여야 합니다."),

    PAGING_ERROR(HttpStatus.BAD_REQUEST, "페이지 입력값이 잘못되었습니다."),

    WRONG_EMAIL_OR_PASSWORD(HttpStatus.UNAUTHORIZED, "이메일이나 비밀번호를 잘못 입력하였습니다."),

    NO_AUTHORITY(HttpStatus.UNAUTHORIZED, "권한이 존재하지 않습니다"),

    MARKET_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 거래입니다."),

    INVENTORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 인벤토리입니다."),

    BID_TOO_LOW (HttpStatus.BAD_REQUEST, "입찰 가격은 현재 입찰 가격보다 높아야 합니다."),

    LACK_OF_GOLD(HttpStatus.BAD_REQUEST, "골드가 부족합니다."),

    EXCESSIVE_BID(HttpStatus.BAD_REQUEST, "입찰 가격은 보유한 골드를 초과할 수 없습니다."),

    AUCTION_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "이미 완료되거나 취소된 경매입니다."),

    CANNOT_CANCEL_AUCTION(HttpStatus.BAD_REQUEST, "입찰되거나 완료된 경매는 취소할 수 없습니다."),

    USER_DIFFERENT(HttpStatus.BAD_REQUEST, "사용자가 일치하지 않습니다."),

    LACK_OF_SELLER_INVENTORY(HttpStatus.BAD_REQUEST, "판매자의 재고가 부족합니다."),

    AUCTION_EXPIRED(HttpStatus.BAD_REQUEST, "마감일이 지난 경매에는 입찰할 수 없습니다"),

    FORBIDDEN_OPERATION(HttpStatus.FORBIDDEN, "권한이 없습니다."),

    UNAUTHORIZED_OPERATION(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");

    private final HttpStatus status;
    private final String message;
}
