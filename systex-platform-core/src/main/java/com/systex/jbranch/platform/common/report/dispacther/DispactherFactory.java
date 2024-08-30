package com.systex.jbranch.platform.common.report.dispacther;

import java.util.Map;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;


public class DispactherFactory {
// ------------------------------ FIELDS ------------------------------

    private Map<DispactherType, Dispacther> dispacthers;
    private DispactherType defaultDispacther;

// -------------------------- OTHER METHODS --------------------------

    public Dispacther getDispatcher() throws JBranchException {
        return dispacthers.get(defaultDispacther);
    }

    public Dispacther getDispatcher(DispactherType type) throws JBranchException {
    	
    	return dispacthers.get(type);
    }
    
    public ReportGeneratorIF getGenerator() throws JBranchException {
        return getGenerator(defaultDispacther);
    }

    public ReportGeneratorIF getGenerator(DispactherType type) throws JBranchException {
    	
    	return dispacthers.get(type).getGenerator();
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setDefaultDispacther(DispactherType type) {
        this.defaultDispacther = type;
    }

    public void setDispacthers(Map dispacthers) {
        this.dispacthers = dispacthers;
    }
}
