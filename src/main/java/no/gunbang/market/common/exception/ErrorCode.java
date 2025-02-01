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

    AUCTION_DAYS_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "경매 희망 기간은 3일 이상, 7일 이하여야 합니다."),

    PAGING_ERROR(HttpStatus.BAD_REQUEST, "페이지 입력값이 잘못되었습니다."),

    WRONG_EMAIL_OR_PASSWORD(HttpStatus.UNAUTHORIZED, "이메일이나 비밀번호를 잘못 입력하였습니다."),

    NO_AUTHORITY(HttpStatus.UNAUTHORIZED, "권한이 존재하지 않습니다"),

    MARKET_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 거래입니다."),

    INVENTORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 인벤토리입니다."),

    LACK_OF_GOLD(HttpStatus.BAD_REQUEST, "골드가 부족합니다."),

    CANNOT_CANCEL_AUCTION(HttpStatus.BAD_REQUEST, "입찰되거나 완료된 경매는 취소할 수 없습니다."),

    USER_DIFFERENT(HttpStatus.BAD_REQUEST, "사용자가 일치하지 않습니다."),

    LACK_OF_SELLER_INVENTORY(HttpStatus.BAD_REQUEST, "판매자의 재고가 부족합니다.");

    private final HttpStatus status;
    private final String message;
}
