package com.covain.projects.emailer.ui.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public interface ComponentsConfigs {

    Logger LOGGER = LoggerFactory.getLogger("Emailer");

    interface Fonts {

        Font BOLD = new Font(Font.DIALOG, Font.BOLD, 15);
        Font PLAIN = new Font(Font.SANS_SERIF, Font.PLAIN, 15);
    }

    interface Inset {

        Insets INPUT = new Insets(0, 5, 0, 0);
    }

}
