package com.systex.jbranch.platform.common.report.factory;

import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.generator.AbstractReportGenerator;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;

/**
 * @author Alex Lin
 * @version 2010/04/06 6:18:18 PM
 */
public class ReportEngine {
// ------------------------------ FIELDS ------------------------------

    private Map generators;
    private ReportFormat defaultFormat;
	private Logger logger = LoggerFactory.getLogger(ReportEngine.class);

// -------------------------- OTHER METHODS --------------------------

    public AbstractReportGenerator getGenerator() throws JBranchException {
        return getGenerator(defaultFormat);
    }

    public AbstractReportGenerator getGenerator(ReportFormat outputFormat) throws JBranchException {
        try {
            ObjectFactory objectFactory = (ObjectFactory) generators.get(outputFormat);
            if (objectFactory == null) {
                throw new JBranchException("pf_report_common_007", Arrays.asList(outputFormat.getType()));
            }
            return (AbstractReportGenerator) objectFactory.getObject();
        }
        catch (JBranchException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new JBranchException(e);
        }
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setDefaultFormat(ReportFormat defaultFormat) {
        this.defaultFormat = defaultFormat;
    }

    public void setGenerators(Map generators) {
        this.generators = generators;
    }
}
