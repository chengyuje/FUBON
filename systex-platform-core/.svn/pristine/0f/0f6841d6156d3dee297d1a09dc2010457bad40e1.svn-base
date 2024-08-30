package com.systex.jbranch.platform.common.log;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author Alex Lin
 * @version 2010/12/15 3:46 PM
 */
public class LogInit {
    public void init(File configPath) {
        // assume SLF4J is bound to logback in the current environment
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(lc);
            // the context was probably already configured by default configuration
            // rules
            lc.reset();
            configurator.doConfigure(configPath);
        }
        catch (JoranException je) {
            // StatusPrinter will handle this
            je.printStackTrace();
        }
    }
}
