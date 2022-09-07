package me.sitech.apifort.exceptions;

public class APIFortNoDataFound extends RuntimeException {
    public APIFortNoDataFound() {
        super();
    }

    public APIFortNoDataFound(String s) {
        super(s);
    }
}
