package com.stockhelper.application.exceptions;

public class XmpUpdateException extends RuntimeException  {
    public XmpUpdateException(String message) {
        super(message);
    }

    public XmpUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
