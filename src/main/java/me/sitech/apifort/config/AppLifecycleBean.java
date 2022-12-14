package me.sitech.apifort.config;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ApplicationScoped
public class AppLifecycleBean {

    private final ApiFortProps apiFortProps;
    @Inject
    public AppLifecycleBean(ApiFortProps apiFortProps){
        this.apiFortProps = apiFortProps;
    }

    @Getter @Setter
    private static String publicContext;

    @Getter @Setter
    private static String privateContext;

    @Getter
    protected static final List<String> allowedPublicMethods = new ArrayList<>();

    @Getter
    protected static final List<String> allowedPrivateMethods = new ArrayList<>();

    void onStart(@Observes StartupEvent event) {
        setPublicContext(apiFortProps.admin().publicContext());
        setPrivateContext(apiFortProps.admin().privateContext());
        allowedPublicMethods.addAll(apiFortProps.admin().methods().publicAccess());
        allowedPrivateMethods.addAll(apiFortProps.admin().methods().privateAccess());
    }

    void onStop(@Observes ShutdownEvent event) {
        log.info("Shutdown event called");
    }


}