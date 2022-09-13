package me.sitech.apifort.config;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.cache.ApiFortCache;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ApplicationScoped
public class AppLifecycleBean {

    @ConfigProperty(name = "apifort.admin.public-context")
    public String publicContextConfigVal;

    @ConfigProperty(name = "apifort.admin.private-context")
    public String privateContextConfigVal;

    @ConfigProperty(name = "apifort.admin.methods.public")
    public List<String> publicMethods;

    @ConfigProperty(name = "apifort.admin.methods.private")
    public List<String> privateMethods;


    @Getter @Setter
    private static String publicContext;

    @Getter @Setter
    private static String privateContext;

    @Getter
    protected static final List<String> allowedPublicMethods = new ArrayList<>();

    @Getter
    protected static final List<String> allowedPrivateMethods = new ArrayList<>();

    void onStart(@Observes StartupEvent event) {
        setPublicContext(publicContextConfigVal);
        setPrivateContext(privateContextConfigVal);
        allowedPublicMethods.addAll(publicMethods);
        allowedPrivateMethods.addAll(privateMethods);
    }

    void onStop(@Observes ShutdownEvent event) {

    }


}