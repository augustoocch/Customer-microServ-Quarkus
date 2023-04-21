package org.augustoocc.exceptions;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class NotWritableEx extends RuntimeException{

    public NotWritableEx() {
    }

    public NotWritableEx(String message) {
        super(message);
    }

    public NotWritableEx(String message, Throwable cause) {
        super(message, cause);
    }


}
