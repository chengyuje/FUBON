package com.systex.jbranch.platform.common.test;

import com.systex.jbranch.platform.common.initiation.PlatformInitiator;
import com.systex.jbranch.platform.common.log.listener.LogHandler;
import com.systex.jbranch.platform.common.report.engine.listener.BirtShutdownHandler;
import com.systex.jbranch.platform.common.scheduler.listener.SchedulerShutdownHandler;
import com.systex.jbranch.platform.common.util.PlatformContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * @author Alex Lin
 * @version 2010/01/04 5:54:41 PM
 */
@Ignore
public class JBranchTest {
// ------------------------------ FIELDS ------------------------------

    private static Logger logger = LoggerFactory.getLogger(JBranchTest.class);

// -------------------------- STATIC METHODS --------------------------

    @BeforeClass
    public static void setUp() throws Exception {
        if (logger.isInfoEnabled()) {
            logger.info("jbranch.root = " + System.getProperty("jbranch.root"));
            logger.info("jbranch.server = " + System.getProperty("jbranch.server"));
        }
        try {
            if (logger.isInfoEnabled()) {
                logger.info("*************Platform Start up****************");
            }

            //1.執行Spring初始化
            PlatformContext.initiate(new FileSystemXmlApplicationContext(new String[]{"classpath:/bean/*.bean.xml"}));
            if (logger.isInfoEnabled()) {
                logger.info("1.Platfrom Beans Initiated");
                logger.info("=================================");
            }

            PlatformInitiator initiator = (PlatformInitiator) PlatformContext.getBean("platformInitiator");
            //2.執行平台模組初始化化<DataManager之前的模組>
            initiator.initiate();
            if (logger.isInfoEnabled()) {
                logger.info("2.Pre-DM Platfrom Modules initiated");
                logger.info("=================================");
            }

            //4.執行平台模組初始化之後的模組>
            initiator.initiatePost();
            if (logger.isInfoEnabled()) {
                logger.info("3.Post-DM Platfrom Modules initiated");
                logger.info("=================================");
                logger.info("*************Platform Started up****************");
                logger.info("Platform Started up");
            }
        }
        catch (Exception e) {
            throw e;
        }
    }

    @AfterClass
    public static void tearDown() {
        new SchedulerShutdownHandler().terminate();
        new BirtShutdownHandler().terminate();
        new LogHandler().terminate();
    }
}
