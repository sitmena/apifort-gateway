package me.sitech.apifort.config;

import io.quarkus.qute.i18n.Localized;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class Translator {

    private final AlertMessages enMessage;

    @Inject
    public Translator(@Localized("en") AlertMessages enMessage) {
        this.enMessage = enMessage;
    }

    public AlertMessages getMessage() {
        return this.enMessage;
    }

}
