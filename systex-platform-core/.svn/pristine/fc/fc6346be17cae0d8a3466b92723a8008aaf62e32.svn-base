package com.systex.jbranch.platform.server.integration.message.config;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Alex Lin
 * @version 2010/12/20 4:44 PM
 */
@SuppressWarnings("unchecked")
public class DOMRepository {
// ------------------------------ FIELDS ------------------------------

    private Map<String, Configuration> configsByTrxCode = new HashMap<String, Configuration>();
    private Map<String, Configuration> configsByFileName = new HashMap<String, Configuration>();
    private List<Repository> repositories;
    
	private Logger logger = LoggerFactory.getLogger(DOMRepository.class);

// -------------------------- OTHER METHODS --------------------------

    public Configuration getConfigByFileName(String fileName) {
        return configsByFileName.get(fileName);
    }

    public Configuration getConfigByTrxCode(String trxCode) {
        return configsByTrxCode.get(trxCode);
    }

    public void init() throws FileNotFoundException, UnsupportedEncodingException, DocumentException {
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
                File config = iterator.next();
                if (logger.isInfoEnabled()) {
                    logger.info("loading " + config);
                }
                Reader reader = new InputStreamReader(new FileInputStream(config), "UTF-8");
                org.dom4j.Document doc = new SAXReader().read(reader);
                String trxCode = doc.selectSingleNode("/Trx/@TransactionCode").getText();
                Configuration configuration = new Configuration(trxCode, config, doc);
                configsByTrxCode.put(trxCode, configuration);
                configsByFileName.put(config.getAbsolutePath(), configuration);
            }
        }
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setRepositories(List<Repository> repositories) {
        this.repositories = repositories;
    }
}
