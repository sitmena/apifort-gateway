package me.sitech.apifort.config;

import io.quarkus.qute.i18n.Message;
import io.quarkus.qute.i18n.MessageBundle;

@MessageBundle(value = "alert")
public interface AlertMessages {

    @Message("Default values is {str}")
    String hello_name(String str);
}
