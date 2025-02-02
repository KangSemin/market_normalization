package no.gunbang.market.common.exception;

public class ForbiddenException extends BaseException {

    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode.getStatus());
    }

}