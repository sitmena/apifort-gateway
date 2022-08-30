package me.sitech.apifort.config;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@Slf4j
@ApplicationScoped
public class AppLifecycleBean {
    //TODO load Data to caching server
    void onStart(@Observes StartupEvent event) {}

    void onStop(@Observes ShutdownEvent event) {}
}