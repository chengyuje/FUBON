package com.systex.jbranch.platform.server.info;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSPARAMETERPK;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSPARAMETERVO;
import com.systex.jbranch.platform.common.scheduler.ScheduleManagement;
import com.systex.jbranch.platform.common.util.PlatformContext;

public class XmlInfo {
// ------------------------------ FIELDS ------------------------------
    private static DataAccessManager dataAccessManager = null;
	private Logger logger = LoggerFactory.getLogger(XmlInfo.class);

// -------------------------- STATIC METHODS --------------------------

    /**
     * 以新transcation方式執行Xml匯出作業。
     *
     * @param param_type - 指定匯出的xml名稱， 若為null或空字串，則表示全部匯出。
     */
    public void refreshXML(String param_type) throws JBranchException {
        doRefreshXML(param_type);      
    }
    
    public Hashtable getVariable(String param_Type, String format) throws JBranchException {
        return doGetVariable(param_Type , format);
    }

    public String getVariable(String param_Type, String param_Code, String format) throws JBranchException {
        return doGetVariable(param_Type , param_Code , format);
    }

    public String getVariable(String param_Type, String param_Code, String format, List<String> interMsgList) throws JBranchException {
        String variable = getVariable(param_Type, param_Code, format);
        for (int i = 0; i < interMsgList.size(); i++) {
            variable = variable.replace("{" + i + "}", interMsgList.get(i));
        }
        return variable;
    }

    public void setVariable(String param_type, String param_code, String param_name) throws JBranchException {
        doSetVariable(param_type , param_code , param_name);
    }
    

// -------------------------- OTHER METHODS --------------------------
    public Hashtable doGetVariable(String param_Type, String format) throws JBranchException {
        DataAccessManager dam = getDataAccessManager();
        TBSYSPARAMETERVO vo;
        TBSYSPARAMETERPK pk;
        StringBuilder value;
        Hashtable<String, String> valueTable = new Hashtable<String, String>();

        Criteria criteria = dam.getHibernateCriteria(TBSYSPARAMETERVO.TABLE_UID);
        criteria.add(Expression.eq("comp_id.PARAM_TYPE", param_Type));
        criteria.add(Expression.ne("PARAM_STATUS", "3"));

        List<TBSYSPARAMETERVO> voList = null;
        try {
            voList = criteria.list();
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new JBranchException("pf_info_common_001");
        }

        if (!(voList.size() > 0)) {
            return null;
        }
        for (int i = 0; i < voList.size(); i++) {
            vo = voList.get(i);
            pk = vo.getcomp_id();

            if (format.endsWith(FormatHelper.FORMAT_1)) {
                value = new StringBuilder();
                value.append(pk.getPARAM_CODE()).append("-").append(vo.getPARAM_NAME());
                valueTable.put(pk.getPARAM_CODE(), value.toString());
            }
            else if (format.endsWith(FormatHelper.FORMAT_2)) {
                valueTable.put(pk.getPARAM_CODE(), pk.getPARAM_CODE());
            }
            else if (format.endsWith(FormatHelper.FORMAT_3)) {
                valueTable.put(pk.getPARAM_CODE(), vo.getPARAM_NAME());
            }
        }

        return valueTable;
    }

    public String doGetVariable(String param_Type, String param_Code, String format) throws JBranchException {
        DataAccessManager dam = getDataAccessManager();
        TBSYSPARAMETERPK pk = new TBSYSPARAMETERPK();
        TBSYSPARAMETERVO vo;
        StringBuilder value = new StringBuilder();

        List<TBSYSPARAMETERVO> voList = null;
        try {
            Criteria criteria = dam.getHibernateCriteria(TBSYSPARAMETERVO.TABLE_UID);
            criteria.add(Expression.eq("comp_id.PARAM_TYPE", param_Type));
            criteria.add(Expression.eq("comp_id.PARAM_CODE", param_Code));
            criteria.add(Expression.ne("PARAM_STATUS", "3"));

            voList = criteria.list();
        }
        catch (Exception e) {
            throw new JBranchException("pf_info_common_001");
        }
        if (voList.size() == 0) {
            return param_Code;
        }

        vo = voList.get(0);

        if (format.endsWith(FormatHelper.FORMAT_1)) {
            value.append(param_Code).append("-").append(vo.getPARAM_NAME());
        }
        else if (format.endsWith(FormatHelper.FORMAT_2)) {
            value.append(param_Code);
        }
        else if (format.endsWith(FormatHelper.FORMAT_3)) {
            value.append(vo.getPARAM_NAME());
        }

        return value.toString();
    }

    /**
     * @param param_type - 指定匯出的xml名稱， 若為null或空字串，則表示全部匯出。
     */
    public void doRefreshXML(String param_type) throws JBranchException {
        String clazz = "com.systex.jbranch.app.server.fps.CMBTH103";
        String job_param_type = "method=refreshXml";

        Session session = null;
        try {
            com.systex.jbranch.platform.common.dataaccess.datasource.DataSource ds = new
                    com.systex.jbranch.platform.common.dataaccess.datasource.DataSource();
            SessionFactory sf = (SessionFactory) PlatformContext.getBean(ds.getDataSource());
            session = sf.openSession();
            String hql = "from TBSYSPARAMETERVO where param_type='TBSYSSCHD.PROCESSOR.AP'";

            List<TBSYSPARAMETERVO> parameterList = session.createQuery(hql).list();
            if (parameterList != null) {
                String scheduleParameter = null;
                if (param_type == null || "".equals(param_type)) {
                    scheduleParameter = "";
                }
                else {
                    scheduleParameter = "param_type=" + param_type;
                }
                ScheduleManagement sm = new ScheduleManagement();
                for (int i = 0; i < parameterList.size(); i++) {
                    TBSYSPARAMETERVO parameter = parameterList.get(i);
                    String process = parameter.getcomp_id().getPARAM_CODE();
                    sm.addOneTime(getDataAccessManager() , clazz, scheduleParameter, job_param_type, process);
                }
            }
        }
        catch (Exception e) {
            throw new JBranchException(e.getMessage(), e);
        }
        finally {
            try {
                session.close();
            }
            catch (Exception e) {
            }
        }
    }

    public void doSetVariable(String param_type, String param_code, String param_name) throws DAOException, JBranchException {
        DataAccessManager dam = getDataAccessManager();
        TBSYSPARAMETERPK pk = new TBSYSPARAMETERPK();
        pk.setPARAM_TYPE(param_type);
        pk.setPARAM_CODE(param_code);

        TBSYSPARAMETERVO vo = (TBSYSPARAMETERVO) dam.findByPKey(TBSYSPARAMETERVO.TABLE_UID, pk);

        if (vo == null) {
            vo = new TBSYSPARAMETERVO();
            vo.setcomp_id(pk);
            vo.setPARAM_NAME(param_name);
            vo.setPARAM_ORDER(0);
            vo.setPARAM_STATUS("0");
            vo.setPARAM_NAME_EDIT("");
            dam.create(vo);
        }
        else {
            vo.setPARAM_NAME(param_name);
            dam.update(vo);
        }
    }
    
    public DataAccessManager getDataAccessManager() throws JBranchException {
    	return dataAccessManager = dataAccessManager == null ? 
    			(DataAccessManager)PlatformContext.getBean("dataAccessManager") : dataAccessManager;
    }
}