package managment.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Util {

    private Util(){}

    public static String getRealmName() {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HHmmss");
        LocalDateTime now = LocalDateTime.now();

        return String.format("Realm%s", dtf.format(now));
    }
}
