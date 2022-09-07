package me.sitech.apifort.exceptions;

public class ApiFortInvalidEndpoint extends RuntimeException {
    public ApiFortInvalidEndpoint() {
        super();
    }

    public ApiFortInvalidEndpoint(String s) {
        super(s);
    }
}
