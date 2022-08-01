package me.sitech.apifort.exceptions;

public class APIFortGeneralException extends RuntimeException{

    private transient int extendedMessageState;
    private transient String extendedMessage;

    public APIFortGeneralException() {
        super();
    }

    public APIFortGeneralException(String s) {
        super(s);
    }



    public synchronized Throwable fillInStackTrace() {
        if (extendedMessageState == 0) {
            extendedMessageState = 1;
        } else if (extendedMessageState == 1) {
            extendedMessage = getExtendedNPEMessage();
            extendedMessageState = 2;
        }
        return super.fillInStackTrace();
    }

    public String getMessage() {
        String message = super.getMessage();
        if (message == null) {
            synchronized(this) {
                if (extendedMessageState == 1) {
                    extendedMessage = getExtendedNPEMessage();
                    extendedMessageState = 2;
                }
                return extendedMessage;
            }
        }
        return message;
    }
    private native String getExtendedNPEMessage();
}