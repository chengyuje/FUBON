package com.systex.jbranch.platform.server;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.systex.jbranch.platform.common.dataManager.Branch;
import com.systex.jbranch.platform.common.dataManager.BranchFactoryIF;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.initiation.PlatformInitiator;
import com.systex.jbranch.platform.common.util.PlatformContext;
//import com.systex.jbranch.platform.server.HsmService;

/**
 * 平台在Web環境中的啟動class
 */
public class PlatformStartupService extends HttpServlet {
// ------------------------------ FIELDS ------------------------------

    private static int serviceStatus = 0;
    private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(PlatformStartupService.class);

// -------------------------- STATIC METHODS --------------------------

    public static int getServiceStatus() {
        return serviceStatus;
    }

    public static void setServiceStatus(int serviceStatus) {
        PlatformStartupService.serviceStatus = serviceStatus;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Servlet ---------------------

    /**
     * 啟動平台
     */
    public void init(ServletConfig config) throws ServletException {
        if (logger.isInfoEnabled()) {
            logger.info("*************Platform Start up****************");
        }
        try {
            //1.執行Spring初始化
            PlatformContext.initiate(WebApplicationContextUtils.getWebApplicationContext(config.getServletContext()));
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

            setDataManagerBranch();
            
            //3.執行平台模組初始化化<DataManager之後的模組>
            initiator.initiatePost();
            if (logger.isInfoEnabled()) {
                logger.info("3.Post-DM Platfrom Modules initiated");
                logger.info("==================================");
            }
            //4. 設定hsm基本資料
            //HSM hsm=HsmService.getHsm();
        }
        catch (Throwable e) {
            logger.error(e.getMessage(), e);
            serviceStatus = 1;
            throw new ServletException("initiate fail");
        }

        if (logger.isInfoEnabled()) {
            logger.info("*************Platform Started up****************");
        }
    }
    
    private static void setDataManagerBranch() throws JBranchException{
		
    	BranchFactoryIF branchFactory = (BranchFactoryIF) PlatformContext.getBean(BranchFactoryIF.DEFAULT_BRANCH_FACTORY);
    	List<Branch> branchList = branchFactory.getBranchList();
    	for (Branch branch : branchList) {
    		DataManager.setBranch(branch.getBrchID(), branch);
		}
    	
	}
}
