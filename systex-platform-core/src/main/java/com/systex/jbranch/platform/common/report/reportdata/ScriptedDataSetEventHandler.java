package com.systex.jbranch.platform.common.report.reportdata;

import org.eclipse.birt.report.engine.api.script.IReportContext;
import org.eclipse.birt.report.engine.api.script.IUpdatableDataSetRow;
import org.eclipse.birt.report.engine.api.script.ScriptException;
import org.eclipse.birt.report.engine.api.script.eventadapter.ScriptedDataSetEventAdapter;
import org.eclipse.birt.report.engine.api.script.instance.IDataSetInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ScriptedDataSetEventHandler extends ScriptedDataSetEventAdapter {
// ------------------------------ FIELDS ------------------------------

    private List dataList = null;
    private int num;
    private int total;
	private Logger logger = LoggerFactory.getLogger(ScriptedDataSetEventHandler.class);

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface IDataSetEventHandler ---------------------

    @Override
    public void beforeOpen(IDataSetInstance dataSet, IReportContext reportContext) throws ScriptException {
        super.beforeOpen(dataSet, reportContext);
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("beforeOpen = " + dataSet.getName());
            }
            final Map appContext = reportContext.getAppContext();

            Object o = appContext.get(dataSet.getName());

            if (o != null) {
                if (o instanceof List) {
                    this.dataList = (List) o;
                    if (logger.isDebugEnabled()) {
                        logger.debug("dataList.size() = " + dataList.size());
                    }
                }
                else {
                    if (logger.isDebugEnabled()) {
                        logger.debug(dataSet.getName() + " is not list");
                    }
                }
            }
            else {
                if (logger.isDebugEnabled()) {
                    logger.debug("can't find " + dataSet.getName() + " in map");
                }
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

// --------------------- Interface IScriptedDataSetEventHandler ---------------------

    @Override
    public void open(IDataSetInstance dataSet) throws ScriptException {
        // TODO Auto-generated method stub
        super.open(dataSet);
        if (logger.isDebugEnabled()) {
            logger.debug("open={}", dataSet.getName());
        }
        num = 0;
        total = 0;
        if (this.dataList != null) {
            this.total = dataList.size();
        }
    }

    @Override
    public boolean fetch(IDataSetInstance dataSet, IUpdatableDataSetRow row) {
        try {
            if (this.dataList == null || num >= total) {
                return false;
            }
//			System.out.println("num=" + num + ", fetch="+dataSet.getName());
            Object dataRowMap = this.dataList.get(num);

            if (dataRowMap != null && dataRowMap instanceof Map) {
                Map<String, Object> data = (Map<String, Object>) dataRowMap;
                Iterator<String> it = data.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    Object value = data.get(key);
                    try {
                        row.setColumnValue(key, value);
                    }
                    catch (Exception e) {
//                        logger.error(e.getMessage(), e);
//                    	ignore
                    }
                }
            }
            else {
                logger.error("datRowMap is not Map");
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        finally {
            num++;
        }
        return true;
    }
}
