package com.cjmware.utils;

import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Properties;

@Component
public class MessageHandler {

    static Properties messages;

    public MessageHandler() {

        Locale locale = Locale.getDefault();
        loadMessages(locale.getLanguage());
    }

    public MessageHandler(String language) {
        if(language == null)
            language = "en";
        loadMessages(language);
    }

    private void loadMessages(String language) {
        try {
            messages = new Properties();
            String resourceName = "/messages_" +language.toLowerCase() +".properties";
            messages.load(this.getClass().getResourceAsStream(resourceName));
        }
        catch(Exception e) {
            System.out.println("Error: Unable to access message translation");
        }
    }

    public String getMessage(String key) {
        String message = messages.getProperty(key);
        if( message == null ) {
            message = key.replaceAll(".", " ");
        }
        return message;
    }
    public String getMessage(String key, String ... values) {
        String message = messages.getProperty(key);
        if( message == null ) {
            message = key.replaceAll(".", " ");
        }
        message = String.format(message, values);
        return message;
    }
}
