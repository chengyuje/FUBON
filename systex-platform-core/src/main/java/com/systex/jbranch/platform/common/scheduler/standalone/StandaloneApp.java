package com.systex.jbranch.platform.common.scheduler.standalone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.initiation.InitiatorIF;
import com.systex.jbranch.platform.common.scheduler.ScheduleInitiator;
import com.systex.jbranch.platform.common.scheduler.ScheduleManagement;
import com.systex.jbranch.platform.common.util.PlatformContext;

public class StandaloneApp {
	
	private static Logger logger = LoggerFactory.getLogger(StandaloneApp.class);

	public static void main(String[] args) {
		try {
			String jbranchRoot = System.getenv("JBRANCH_ROOT");
			logger.info("JBranch root=" + jbranchRoot);
			
			PlatformContext.initiate();
//			DataManager.setRoot(jbranchRoot);
		
//			SecurityInitiator init = new SecurityInitiator();//PlatformContext.getBean("securityInitiator", InitiatorIF.class);
//			init.setDataAccessManager(new DataAccessManager());
//			init.execute();
	
			//3.執行DataManager初始化
			com.systex.jbranch.platform.common.dataManager.System systemx=null;
			systemx = PlatformContext.getBean(com.systex.jbranch.platform.common.dataManager.System.class);
			DataManager.setSystem(systemx);
		
			InitiatorIF scheduleInitiator = PlatformContext.getBean(ScheduleInitiator.class);
			scheduleInitiator.execute();
			
			ScheduleManagement.start();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}
	}
}
