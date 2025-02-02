package no.gunbang.market.common.exception;

public class UnauthorizedException extends BaseException {

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode.getStatus());
    }

}