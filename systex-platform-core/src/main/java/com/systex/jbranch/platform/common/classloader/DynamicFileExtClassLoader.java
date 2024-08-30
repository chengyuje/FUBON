package com.systex.jbranch.platform.common.classloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;


public class DynamicFileExtClassLoader extends FileExtClassLoader {
// ------------------------------ FIELDS ------------------------------

    static FileUrlPath fileUrlpath;
    static LinkedList<URL> filrUrlpaths = null;


    String rootPath = "";
    private Logger logger = LoggerFactory.getLogger(DynamicFileExtClassLoader.class);

// --------------------------- CONSTRUCTORS ---------------------------

    public DynamicFileExtClassLoader(String rootPath) {
        super();
        this.rootPath = rootPath;
        if (FileUrlPath.getFileUrlPaths() == null) {
            fileUrlpath = new FileUrlPath(this.rootPath);
        }
        filrUrlpaths = fileUrlpath.getFileUrlPaths();
        addURLs(filrUrlpaths);
    }

// -------------------------- OTHER METHODS --------------------------

    public Class getClass(String nameWithPackage) throws ClassNotFoundException {
        String urlPath = "";
        Class classToGet;
        try {
            classToGet = findClass(nameWithPackage);
        }
        catch (ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
            try {
                fileUrlpath.search(this.rootPath);
                addURLs(fileUrlpath.getFileUrlPaths());
            }
            catch (IOException e1) {
                logger.error(e1.getMessage(), e);
            }
            classToGet = findClass(nameWithPackage);
        }
        return classToGet;
    }

    protected void addURLs(LinkedList<URL> urls) {
        //LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "addURL:"+url);
        for (int i = 0; i < urls.size(); i++) {
            //LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "add url="+urls.get(i));
            super.addURL((URL) urls.get(i));
        }
    }
}
