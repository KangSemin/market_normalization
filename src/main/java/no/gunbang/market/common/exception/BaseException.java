package no.gunbang.market.common.exception;

import org.springframework.http.HttpStatus;

public abstract class BaseException extends RuntimeException {

    private final String message;
    private final HttpStatus httpStatus;

    protected BaseException(String message, HttpStatus httpStatus) {
        super(message);
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public HttpStatus getStatus(){
        return httpStatus;
    }

    @Override
    public String getMessage(){
        return message;
    }
}
