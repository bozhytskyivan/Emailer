package com.covain.projects.emailer.utils;


import com.covain.projects.emailer.localization.UTF8Control;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class Localizer {

    private static Logger LOGGER = LoggerFactory.getLogger(Localizer.class);

    private static final String APP_LOCALE = "locale";
    private static final String LOCALE_PROPERTIES = "localization.properties";
    private static final String DEFAULT_LOCALE = "ru";

    private static ResourceBundle LOCALIZATION_BUNDLE;

    static {
        LOCALIZATION_BUNDLE = ResourceBundle.getBundle(APP_LOCALE, getLocale(), new UTF8Control());
    }

    public static String getString(String key) {
        return LOCALIZATION_BUNDLE.getString(key);
    }

    public static Locale getLocale() {
        Locale locale;
        try (InputStream propsInputStream = new FileInputStream(LOCALE_PROPERTIES)) {
            Properties properties = new Properties();
            properties.load(propsInputStream);
            String savedLocale = properties.getProperty(APP_LOCALE, DEFAULT_LOCALE);
            locale = new Locale(savedLocale);
        } catch (IOException e) {
            LOGGER.error("Failed to get localization. Default will be used.");
            locale = new Locale(DEFAULT_LOCALE);
        }
        return locale;
    }

    public static void setLocale(Locale locale) {
        try (OutputStream propsOutputStream = new FileOutputStream(LOCALE_PROPERTIES)) {
            Properties properties = new Properties();
            properties.setProperty(APP_LOCALE, locale.getLanguage());
            properties.store(propsOutputStream, null);
        } catch (IOException e) {
            LOGGER.error("Failed to save localization.", e);
        }
        LOCALIZATION_BUNDLE = ResourceBundle.getBundle(APP_LOCALE, locale);
    }
}
