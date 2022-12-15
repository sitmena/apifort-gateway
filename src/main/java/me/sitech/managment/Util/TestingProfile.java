package me.sitech.managment.Util;

import io.quarkus.test.junit.QuarkusTestProfile;
import javax.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class TestingProfile {

    public static class NoTags implements QuarkusTestProfile {

    }

    public static class integration implements QuarkusTestProfile {
        @Override
        public Set<String> tags() {
            return Collections.singleton("integration");
        }
    }

    public static class unit implements QuarkusTestProfile {
        @Override
        public Set<String> tags() { return Collections.singleton("unit");
        }
    }

    public static class fullTest implements QuarkusTestProfile {
        @Override
        public Set<String> tags() {
            return new HashSet<>(Arrays.asList("unit", "integration"));
        }
    }
}
