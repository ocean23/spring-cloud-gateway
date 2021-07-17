package com.fujfu.gateway.exception;

/**
 * @author ocean
 * @date 2021/7/16 15:23
 */
public class InvalidTokenException extends Exception {

    public InvalidTokenException() {
    }

    public InvalidTokenException(String message) {
        super(message);
    }    
}
