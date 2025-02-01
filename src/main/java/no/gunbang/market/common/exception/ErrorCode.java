package no.gunbang.market.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 사용자입니다."),

    PAGING_ERROR(HttpStatus.BAD_REQUEST, "페이지 입력값이 잘못되었습니다."),

    WRONG_EMAIL_OR_PASSWORD(HttpStatus.UNAUTHORIZED, "이메일이나 비밀번호를 잘못 입력하였습니다."),

    ITEM_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 아이템입니다."),

    MARKET_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 거래입니다."),

    INVENTORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 인벤토리입니다."),

    LACK_OF_GOLD(HttpStatus.BAD_REQUEST, "골드가 부족합니다."),

    LACK_OF_SELLER_INVENTORY(HttpStatus.BAD_REQUEST, "판매자의 재고가 부족합니다.");



    private final HttpStatus status;
    private final String message;
}
