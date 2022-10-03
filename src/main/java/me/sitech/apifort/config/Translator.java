package me.sitech.apifort.config;

import io.quarkus.qute.i18n.Localized;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class Translator {

    private final AlertMessages enMessage;

    @Inject
    public Translator(@Localized("en-US") AlertMessages enMessage) {
        this.enMessage = enMessage;
    }

    public AlertMessages getMessage() {
        return this.enMessage;
    }

    /*public TranslatorMessage getMessage(String lang) {
        return switch (lang) {
            case "ar"->arMessage;
            case "de"->deMessage;
            case "fr"->frMessage;
            default -> enMessage;
        };
    }*/

}
