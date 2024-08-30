package com.systex.jbranch.platform.common.util;


import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.errHandle.EnumErrInputType;
import com.systex.jbranch.platform.common.errHandle.JBranchException;


/**
 * 平台Context，提供平台bean資源的存取
 *
 * @author Hong-jie
 * @version 1.0
 * @since 2008/12/25
 */
public class PlatformContext {
// ------------------------------ FIELDS ------------------------------

    public static final String JBRANCH_ROOT_NOT_FOUND = "pf_platformutil_error_001";
    public static final String PLATFROM_CONTEXT_NOT_INITED = "pf_platformutil_error_002";


    private static ApplicationContext context;
    private static Logger logger = LoggerFactory.getLogger(PlatformContext.class);
    private static final String BEAN_FILE_PATTERN = "*.bean.xml";
    private static final String BEAN_FOLDER = "bean";
    private static final String JBRANCH_ROOT = "JBRANCH_ROOT";
    private static final String NOT_INITIAL_ERR = "PlatformContext is not initiated";
    private static final String PATH_REPLACE_SEPARATOR = "\\\\";
    private static final String SEPARATOR = "/";
    private static final String SERVER_PATH = "SERVER_PATH";

// -------------------------- STATIC METHODS --------------------------

    /**
	 * 初始化AP平台Context物件
	 * @return
	 * @throws JBranchException
	 */
	public static void initiate() throws Exception{
		
		if(context != null) 
			return;	
		
		String jbranchRoot = System.getenv("JBRANCH_ROOT");
		//存放bean檔案的目錄位置
	    String beanFilePath = null;
	    //判斷目錄是否存在

	    if(jbranchRoot == null || jbranchRoot.trim().length()==0) {
	    	throw new JBranchException(JBRANCH_ROOT_NOT_FOUND);
	    }else{
	    	beanFilePath = (jbranchRoot+SEPARATOR+BEAN_FOLDER).replaceAll(PATH_REPLACE_SEPARATOR, SEPARATOR);
	    }

	    //建立ApplicationContext物件
	    context = new FileSystemXmlApplicationContext("file:"+beanFilePath+SEPARATOR+BEAN_FILE_PATTERN);
	    DataManager.setRoot(jbranchRoot);
	}
    
    /**
     * 初始化WEB平台Context物件
     *
     * @return
     * @throws JBranchException
     */
    public static void initiate(ServletContext sc) throws JBranchException {
        if (context != null) {
            return;
        }
        try {
            context = WebApplicationContextUtils.getWebApplicationContext(sc);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            JBranchException jbe = new JBranchException(
                    EnumErrInputType.MSG, e.getMessage());
            jbe.setException(e);
            throw jbe;
        }
    }

    /**
     * 初始化AP平台Context物件
     *
     * @param root jbranch root path
     * @throws JBranchException jbranch exception
     */
    public static void initiate(ApplicationContext applicationContext) throws Exception {
        if (context != null) {
            return;
        }
        try {
            context = applicationContext;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            JBranchException jbe = new JBranchException(EnumErrInputType.MSG, e.getMessage());
            jbe.setException(e);
            throw jbe;
        }
    }

    /**
     * 取得Bean物件
     *
     * @param clazz bean class
     * @return bean
     * @throws JBranchException jbranchexception
     */
    public static <T> T getBean(Class<T> clazz) throws JBranchException {
        try {
            if (context == null) {
                throw new JBranchException(PLATFROM_CONTEXT_NOT_INITED);
            }
            return context.getBean(clazz);
        }
        catch (org.springframework.beans.factory.NoSuchBeanDefinitionException e) {
            logger.error(e.getMessage(), e);
            JBranchException jbe = new JBranchException(
                    EnumErrInputType.MSG, e.getMessage());
            jbe.setException(e);
            throw jbe;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            JBranchException jbe = new JBranchException(
                    EnumErrInputType.MSG, e.getMessage());
            jbe.setException(e);
            throw jbe;
        }
    }

    /**
     * 取得Bean物件
     *
     * @param beanId bean id
     * @param clazz bean class
     * @return bean
     * @throws JBranchException jbranchexception
     */
    public static <T> T getBean(String beanId, Class<T> clazz) throws JBranchException {
        try {
            if (context == null) {
                throw new JBranchException(PLATFROM_CONTEXT_NOT_INITED);
            }
            return context.getBean(beanId, clazz);
        }
        catch (org.springframework.beans.factory.NoSuchBeanDefinitionException e) {
            logger.error(e.getMessage(), e);
            JBranchException jbe = new JBranchException(
                    EnumErrInputType.MSG, e.getMessage());
            jbe.setException(e);
            throw jbe;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            JBranchException jbe = new JBranchException(
                    EnumErrInputType.MSG, e.getMessage());
            jbe.setException(e);
            throw jbe;
        }
    }


    public static boolean beanExists(String beanId) {
        try {
            Object o = getBean(beanId);
            if (o == null) {
                return false;
            }
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 取得Bean 物件
     *
     * @param beanId bean id
     * @return bean
     * @throws JBranchException jbranchexception
     */
    public static Object getBean(String beanId) throws JBranchException {
        try {
            if (context == null) {
                throw new JBranchException(PLATFROM_CONTEXT_NOT_INITED);
            }
            return context.getBean(beanId);
        }
        catch (org.springframework.beans.factory.NoSuchBeanDefinitionException e) {
            logger.error(e.getMessage(), e);
            JBranchException jbe = new JBranchException(
                    EnumErrInputType.MSG, e.getMessage());
            jbe.setException(e);
            throw jbe;
        }
        catch (Exception e) {
            JBranchException jbe = new JBranchException(
                    EnumErrInputType.MSG, e.getMessage());
            jbe.setException(e);
            throw jbe;
        }
    }
}
