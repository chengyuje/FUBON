package com.systex.jbranch.platform.common.test.advice;

import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.server.conversation.ToaHelperIF;
import com.systex.jbranch.platform.server.conversation.message.EnumTiaHeader;
import com.systex.jbranch.platform.server.conversation.message.Tia;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * @author Alex Lin
 * @version 2011/02/24 12:05 PM
 */
public class YamlDumper {
    private File resourceFolderFile;
    
	private Logger logger = LoggerFactory.getLogger(YamlDumper.class);

    public void dump(Tia tia, ToaHelperIF toaHelper, UUID uuid, Map workflowParams) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("dumping");
        }
        try {
            DumperOptions options = new DumperOptions();
            options.setPrettyFlow(true);
            Yaml yaml = new Yaml(options);
            Map headers = tia.getHeaders();
            String txnCode = (String) headers.get(EnumTiaHeader.TxnCode.name());
            String bizCode = (String) headers.get(EnumTiaHeader.BizCode.name());
            if (logger.isDebugEnabled()) {
                logger.debug("bizCode = " + bizCode);
                logger.debug("txnCode = " + txnCode);
            }
            if (StringUtils.isNotEmpty(txnCode) && StringUtils.isNotEmpty(bizCode)) {
                String fileName = txnCode + "_" + bizCode + ".yaml";
                yaml.dump(tia, new FileWriter(new File(this.resourceFolderFile, fileName)));
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("dumped");
        }
    }

    public void setResourceFolder(String resourceFolder) {
        this.resourceFolderFile = new File(resourceFolder);
    }
}

