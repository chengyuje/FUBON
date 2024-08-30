package com.systex.jbranch.workflow;

import com.opensymphony.workflow.FactoryException;
import com.opensymphony.workflow.InvalidWorkflowDescriptorException;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import com.opensymphony.workflow.loader.WorkflowLoader;
import com.opensymphony.workflow.loader.XMLUtil;
import com.opensymphony.workflow.spi.hibernate.SpringWorkflowFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @author Alex Lin
 * @version 2010/02/08 5:01:19 PM
 */
@SuppressWarnings("unchecked")
public class JBranchWorkflowFactory extends SpringWorkflowFactory {
// ------------------------------ FIELDS ------------------------------

    
	private Logger logger = LoggerFactory.getLogger(JBranchWorkflowFactory.class);
    private List<Repository> repositories;

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface WorkflowFactory ---------------------

    @Override
    public WorkflowDescriptor getWorkflow(String name, boolean validate) throws FactoryException {
        WorkflowConfig c = (WorkflowConfig) workflows.get(name);

        if (c == null) {
            throw new FactoryException("Unknown workflow name \"" + name + '\"');
        }

        if (c.descriptor != null) {
            if (reload) {
                File file = new File(c.url.getFile());

                if (file.exists() && (file.lastModified() > c.lastModified)) {
                    c.lastModified = file.lastModified();
                    loadWorkflow(c, validate);
                }
            }
        }
        else {
            loadWorkflow(c, validate);
        }

        c.descriptor.setName(name);

        return c.descriptor;
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    public void init() {
        try {
            workflows = new HashMap();
            properties.setProperty("reload", String.valueOf(reload));

            for (Repository repository : repositories) {
                String baseDir = repository.getDir();
                if (logger.isInfoEnabled()) {
                    logger.info("baseDir = " + baseDir);
                }
                OrFileFilter ioFilter = new OrFileFilter();

                if (repository.getRegexPatterns() != null) {
                    for (String pattern : repository.getRegexPatterns()) {
                        ioFilter.addFileFilter(new RegexFileFilter(pattern));
                    }
                }

                if (repository.getFileNames() != null) {
                    ioFilter.addFileFilter(new NameFileFilter(repository.getFileNames()));
                }

                Iterator<File> iterator = FileUtils.iterateFiles(new File(baseDir), ioFilter, TrueFileFilter.TRUE);
                while (iterator.hasNext()) {
                    File wf = iterator.next();
                    if (logger.isInfoEnabled()) {
                        logger.info("loading " + wf.getAbsolutePath());
                    }
                    properties.setProperty("groupName", getFileMainName(wf.getName()));
                    properties.setProperty("resource", wf.toURL().toString());
                    initDone();
                }
            }
        }
        catch (Exception e) {
        	logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private String getFileMainName(String fileName) {
        int index = fileName.lastIndexOf(".");
        return index != -1 ? fileName.substring(0, index) : fileName;
    }

    public void initDone() throws FactoryException {
        reload = getProperties().getProperty("reload", "false").equals("true");

        String name = getProperties().getProperty("resource", "workflows.xml");
        InputStream is = getInputStream(name);

        if (is == null) {
            throw new FactoryException("Unable to find workflows file '" + name + "' in classpath");
        }
        String groupName = getProperties().getProperty("groupName");

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);

            DocumentBuilder db;

            try {
                db = dbf.newDocumentBuilder();
            }
            catch (ParserConfigurationException e) {
                throw new FactoryException("Error creating document builder", e);
            }

            Document doc = db.parse(is);

            Element root = (Element) doc.getElementsByTagName("workflows").item(0);
            if(root == null){
            	throw new FactoryException("Unable to find workflows tag in [" + name + "]");
            }
            String basedir = getBaseDir(root);

            List list = XMLUtil.getChildElements(root, "workflow");

            for (int i = 0; i < list.size(); i++) {
                Element e = (Element) list.get(i);
                String configType = e.getAttribute("type");
                String configFile = FileUtils.toFile(new URL(name)).getParent() + "/" + e.getAttribute("location");
                WorkflowConfig config = new WorkflowConfig(basedir, configType, configFile);
                String workflowName = groupName + "." + e.getAttribute("name");
                if (logger.isInfoEnabled()) {
                    logger.info("workflowName = " + workflowName + ", config = " + config);
                }
                workflows.put(workflowName, config);
            }
        }
        catch (Exception e) {
            throw new InvalidWorkflowDescriptorException("Error in workflow config", e);
        }
    }

    private void loadWorkflow(WorkflowConfig c, boolean validate) throws FactoryException {
        try {
            c.descriptor = WorkflowLoader.load(c.url, validate);
        }
        catch (Exception e) {
            throw new FactoryException("Error in workflow descriptor: " + c.url, e);
        }
    }

// -------------------------- INNER CLASSES --------------------------

    static class WorkflowConfig implements Serializable {
        private static final long serialVersionUID = 4939957922893602958L;
        String location;
        String type; // file, URL, service
        URL url;
        WorkflowDescriptor descriptor;
        long lastModified;

        public WorkflowConfig(String basedir, String type, String location) {
            if ("URL".equals(type)) {
                try {
                    url = new URL(location);

                    File file = new File(url.getFile());

                    if (file.exists()) {
                        lastModified = file.lastModified();
                    }
                }
                catch (Exception ex) {
                }
            }
            else if ("file".equals(type)) {
                try {
                    File file = new File(basedir, location);
                    url = file.toURL();
                    lastModified = file.lastModified();
                }
                catch (Exception ex) {
                }
            }
            else {
                url = Thread.currentThread().getContextClassLoader().getResource(location);
            }

            this.type = type;
            this.location = location;
        }

        @Override
        public String toString() {
            return "com.systex.jbranch.workflow.JBranchWorkflowFactory.WorkflowConfig{" +
                    "location='" + location + '\'' +
                    ", type='" + type + '\'' +
                    ", url=" + url +
                    ", descriptor=" + descriptor +
                    ", lastModified=" + lastModified +
                    '}';
        }
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public List<Repository> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<Repository> repositories) {
        this.repositories = repositories;
    }
}
