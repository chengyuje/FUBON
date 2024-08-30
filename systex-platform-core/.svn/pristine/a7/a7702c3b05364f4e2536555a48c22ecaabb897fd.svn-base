package com.systex.jbranch.platform.common.classloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FileExtClassLoader extends URLClassLoader {
// ------------------------------ FIELDS ------------------------------

    private Logger logger = LoggerFactory.getLogger(FileExtClassLoader.class);

// --------------------------- CONSTRUCTORS ---------------------------

    public FileExtClassLoader() {
        super(new URL[]{});
    }

// -------------------------- OTHER METHODS --------------------------

    public Class findClass(String nameWithPackage) throws ClassNotFoundException {
        Class theClass = null;
        theClass = super.findClass(nameWithPackage);
        return theClass;
    }

    public Properties getProperties(String fileUrlPath, String fileName) throws ClassNotFoundException {
        // InputStream is  = getClass().getResourceAsStream(fileName);
        InputStream is = findClass(fileUrlPath, fileName).getResourceAsStream(fileName);
        Properties property = new Properties();
        try {
            property.load(is);
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return property;
    }

    protected void addURLs(String[] urls) {
        //LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "addURL:"+url);
        for (int i = 0; i < urls.length; i++) {
            //LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "url="+(String)urls[i]);
            try {
                super.addURL(new URL("file:" + urls[i]));
            }
            catch (MalformedURLException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

// --------------------------- main() method ---------------------------

    //    public Properties  getProperties(String fileName)throws ClassNotFoundException{
//    	 InputStream is  = getClass().getResourceAsStream(fileName);
//    	// InputStream is  = findClass(fileName).getResourceAsStream(fileName);
//    	 Properties property = new Properties();
//      	 try {
//			property.load(is);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return property;
//    }
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        FileExtClassLoader ecl = new FileExtClassLoader();
        Object dm = null;
        try {
            dm = ecl.findClass("d:/JBranch/Services/lib/datamanager.jar", "com.systex.jbranch.platform.common.dataManager.DataManager");
            // dm=ecl.findClass("d:/JBranch/Services/lib/bizlogics.jar","com.systex.jbranch.platform.server.bizlogictest.Bizlogic1");
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // DataManager dmx=(DataManager)dm;
        //	 LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "system="+dmx.getSystem());
//		 FileExtClassLoader pcl=new FileExtClassLoader();
//		 Properties perp=null;
//			 try {
//				 perp=pcl.getProperties("D:/JBranch/Services/lib/","ml_msg_01_en_US.Properties");
//			 } catch (ClassNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			 LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "perp="+perp.getProperty("msg_01_combo_001_text_002"));
    }

    public Class findClass(String fileUrlPath, String nameWithPackage) throws ClassNotFoundException {
        try {
            addURL(new URL("file:" + fileUrlPath));
        }
        catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
        }
        Class theClass = null;
        theClass = super.findClass(nameWithPackage);


        return theClass;
    }

    protected void addURL(URL url) {
        //LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "addURL:"+url);
        super.addURL(url);
    }
    
    public void close() {
    	try {
    	   Class clazz = java.net.URLClassLoader.class;
    	   java.lang.reflect.Field ucp = clazz.getDeclaredField("ucp");
    	   ucp.setAccessible(true);
    	   Object sun_misc_URLClassPath = ucp.get(this);
    	   java.lang.reflect.Field loaders = 
    	      sun_misc_URLClassPath.getClass().getDeclaredField("loaders");
    	   loaders.setAccessible(true);
    	   Object java_util_Collection = loaders.get(sun_misc_URLClassPath);
    	   for (Object sun_misc_URLClassPath_JarLoader :
    	        ((java.util.Collection) java_util_Collection).toArray()) {
    	      try {
    	         java.lang.reflect.Field loader = 
    	            sun_misc_URLClassPath_JarLoader.getClass().getDeclaredField("jar");
    	         loader.setAccessible(true);
    	         Object java_util_jar_JarFile = 
    	            loader.get(sun_misc_URLClassPath_JarLoader);
    	         ((java.util.jar.JarFile) java_util_jar_JarFile).close();
    	      } catch (Throwable t) {
    	         // if we got this far, this is probably not a JAR loader so skip it
    	    	  logger.error(t.getMessage(), t);
    	      }
    	   }
    	} catch (Throwable t) {
    	   // probably not a SUN VM
    		logger.error(t.getMessage(), t);
    	}
    	return;
	}
}