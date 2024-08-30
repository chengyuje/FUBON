package com.systex.jbranch.platform.common.report.engine;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.core.internal.registry.RegistryProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.reportdata.ConfigAdapterIF;

public class BirtReportService implements ReportServiceIF {
// ------------------------------ FIELDS ------------------------------

    public ConfigAdapterIF path;
    private IReportEngine engine;
    private String systemRoot;
	private Logger logger = LoggerFactory.getLogger(BirtReportService.class);
	private String fontConfig;
	

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface ReportServiceIF ---------------------

    /**
     * 初始化生成ReportEngine
     *
     * @param
     * @return
     * @throws JBranchException
     * @author Richard
     * @since 2009/05/05
     */
    public void initial() throws JBranchException {
        try {
        	RegistryProviderFactory.releaseDefault();
            String runtimeEngineRoot = new File(systemRoot, path.getReportEngine()).getAbsolutePath();
            String resourcePath = new File(systemRoot, path.getTransactionpath()).getAbsolutePath();
            if (logger.isInfoEnabled()) {
                logger.info("runtimeEngineRoot = " + runtimeEngineRoot);
                logger.info("resourcePath = " + resourcePath);
            }

            //初始化Birt Engine
            EngineConfig config = new EngineConfig();
            if (fontConfig != null) {
            	config.setFontConfig(new URL(fontConfig));
			}
            config.setResourcePath(resourcePath);
            Platform.startup(config);
            IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
            engine = factory.createReportEngine(config);
            checkFolder();//檢查是war裡是否有存放PDF的folder
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new JBranchException("pf_report_common_001");
        }
    }

    /**
     * release ReportEngine 資源
     */
    public void release() {
        engine.destroy();
        Platform.shutdown();
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * release 取得報表engine
     *
     * @return IReportEngine
     * @throws com.systex.jbranch.platform.common.errHandle.JBranchException
     *          exception
     */
    public IReportEngine getEngine() throws JBranchException {
        if (engine != null) {
            return engine;
        }
        else {
            throw new JBranchException("pf_report_common_002");
        }
    }

    /**
     * 檢查產出報表folder是否存在
     *
     * @throws java.io.IOException ioexception
     */
    private void checkFolder() throws IOException {
        String serverRoot = path.getServerHome();
        String file = serverRoot + path.getReportTemp();

        File newFile = new File(file);
        FileUtils.forceMkdir(newFile);
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setPath(ConfigAdapterIF path) {
        this.path = path;
    }

    public void setSystemRoot(String systemRoot) {
        logger.info("systemRoot = " + systemRoot);
        this.systemRoot = systemRoot;
    }

	public String getFontConfig() {
		return fontConfig;
	}

	public void setFontConfig(String fontConfig) {
		this.fontConfig = fontConfig;
	}
    
}
