package com.systex.jbranch.platform.common.scheduler;

import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSchedulerMonitor extends Thread {
	private Logger logger = LoggerFactory.getLogger(DefaultSchedulerMonitor.class);
	private boolean isRun = true;
	private int sleep = 10 * 60  * 1000;
//	private static DefaultSchedulerMonitor self;
//	
//	private DefaultSchedulerMonitor() {}
//	
//	public static DefaultSchedulerMonitor getInstance() {
//		if (self == null) {
//			self = new DefaultSchedulerMonitor();
//		}
//		return self;
//	}
	
	@Override
	public void run() {
		try {
			while (this.isRun) {
				Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
				if (!scheduler.isStarted() || scheduler.isInStandbyMode() || scheduler.isShutdown()) {
					scheduler.start(); 
					logger.debug("Restart Default Scheduler. isStarted={}, isInStandbyMode={}, isShutdown={}",
							scheduler.isStarted(), scheduler.isInStandbyMode(), scheduler.isShutdown());
				} else {
					logger.debug("Default Scheduler is alive");
				}
				Thread.sleep(this.sleep); // monitor every 10 mins
			}
		} catch (Exception e) {
			logger.warn("run", e);
		}
	}
	
	public void setIsRun(boolean isRun) {
		this.isRun = isRun;
	}
}
