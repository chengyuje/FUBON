package com.systex.jbranch.platform.common.classloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;

public class FileUrlPath {
// ------------------------------ FIELDS ------------------------------

    static LinkedList<URL> fileUrlPaths = null;
    String rootPath = "";
    String type = "file:";
    StringBuffer jarPaths = new StringBuffer();
    private Logger logger = LoggerFactory.getLogger(FileUrlPath.class);

// -------------------------- STATIC METHODS --------------------------

    public static LinkedList<URL> getFileUrlPaths() {
        return fileUrlPaths;
    }

    public static void setFileUrlPaths(LinkedList<URL> fileUrlPaths) {
        FileUrlPath.fileUrlPaths = fileUrlPaths;
    }

// --------------------------- CONSTRUCTORS ---------------------------

    public FileUrlPath() {

    }

    public FileUrlPath(String rootPath) {
        this.rootPath = rootPath;
        try {
            search(rootPath);
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public LinkedList search(String rootPath) throws IOException {
        String path = "";
        String urlpath = "";
        fileUrlPaths = new LinkedList();
        File rootFile = new File(rootPath);
        if (!rootFile.exists()) {
            throw new IOException("Dir not exist:" + rootFile.getName());
        }

        File subs[] = rootFile.listFiles();
        for (int i = 0; i <= subs.length - 1; i++) {
            if (!subs[i].isDirectory()) {
                path = subs[i].getAbsolutePath();
                if (path.endsWith(".jar")) {
                    urlpath = path.replace('\\', '/');
                    jarPaths.append(path).append(";");
                    fileUrlPaths.add(new URL(type + urlpath));
                }
            }
        }
        // rslt = f.delete();
        return fileUrlPaths;
    }

// -------------------------- OTHER METHODS --------------------------

    public String getClasspaths() throws IOException {
        return jarPaths.toString();
    }
}
