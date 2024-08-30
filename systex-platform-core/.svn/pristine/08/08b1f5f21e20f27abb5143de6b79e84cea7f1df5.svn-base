package com.systex.jbranch.platform.common.report.reportdata;

import java.io.IOException;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanMap;
import org.hibernate.Hibernate;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.HibernateUtil;
import com.systex.jbranch.platform.common.util.ObjectUtil;

public class ReportData implements ReportDataIF {
// ------------------------------ FIELDS ------------------------------

    private static String PARAMS1 = "Params1";
    private static String PARAMS2 = "Params2";

    private List<Map> dataList = null;
    private Map parameters = null;
    private boolean merge;
    private String fileName = null;
    private String path = null;
    private Map<String, List> records = new HashMap<String, List>();
    private Object others = null;

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface ReportDataIF ---------------------


    /**
     * 設定外部傳入參數
     *
     * @param id    key
     * @param value value
     * @author Richard
     * @since 2009/06/03
     */
    public void addParameter(String id, Object value) {
        if (parameters == null) {
            parameters = new HashMap();
        }
        parameters.put(id, value);
    }
    
    public void addParameter(Object beanVo) {
		addParameter(new BeanMap(beanVo));
	}

	public void addParameter(Map map) {
		Iterator<String> it = map.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			addParameter(key, map.get(key));
		}
	}

    /**
     * 設定多筆資料
     *
     * @param Map Map
     * @author Richard
     * @since 2009/06/03
     */
    public void addRecord(Map record) {
        if (dataList != null) {
            dataList.add(record);
        }
        else {
            dataList = new ArrayList<Map>();
            dataList.add(record);
        }
    }

    /**
     * 設定多筆資料
     *
     * @param list list
     * @author Richard
     * @since 2009/06/03
     * @deprecated
     */
    public void addRecordList(List<Map> list) {
        if (dataList == null) {
            dataList = list;
        }
        else {
            dataList.addAll(list);
        }
    }

    /**
     * 直接設定資料庫查詢條件
     *
     * @param sql sql
     * @author Richard
     * @since 2009/06/03
     */
    public void setSQL(String sql) throws JBranchException {
        DataAccessManager dam = new DataAccessManager();
        QueryConditionIF cond = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
        cond.setQueryString(sql);
        List recordList = dam.executeQuery(cond);

        addRecordList(recordList);
    }

    /**
     * 自訂報表路徑
     *
     * @param path path
     * @author Richard
     * @since 2009/06/03
     */
    public void setPath(String path) {
        this.path = path;// + System.getProperties().getProperty("file.separator");
    }

    public void addRecordList(String dataSet, List list) {
        records.put(dataSet, list);
    }

    public List getRecordList(String dataSet) {
        return records.get(dataSet);
    }

    public Map getRecordListAll() {
        return records;
    }

    public void setRecords(Blob blob) throws Exception {
        byte[] data = blob.getBytes(1, (int) blob.length());
        Object o = ObjectUtil.getObject(data);
        if (o instanceof Map) {
            Map<String, Map> map = (Map<String, Map>) o;
            parameters = map.get(PARAMS1);
            records = map.get(PARAMS2);
        }
        else {
            throw new JBranchException("Blob內容型態錯誤(非Map)");
        }
    }
    
    public void setRecords(Map<String, List> records){
		this.records = records;
	}

    public Blob getRecords() throws IOException {
        Map<String, Map> map = new HashMap<String, Map>();
        map.put(PARAMS1, parameters);
        map.put(PARAMS2, records);
        byte[] data = ObjectUtil.getBytes(map);
        return Hibernate.getLobCreator(
				HibernateUtil.getSessionFactory().getCurrentSession())
				.createBlob(data);
    }

    public byte[] getRecordsBytes() throws IOException {
        Map<String, Map> map = new HashMap<String, Map>();
        map.put(PARAMS1, parameters);
        map.put(PARAMS2, records);
        byte[] data = ObjectUtil.getBytes(map);
        return data;
    }

    public void setRecordsBytes(byte[] bytes) throws Exception {
        Object o = ObjectUtil.getObject(bytes);
        if (o instanceof Map) {
            Map<String, Map> map = (Map<String, Map>) o;
            parameters = map.get(PARAMS1);
            records = map.get(PARAMS2);
        }
        else {
            throw new JBranchException("Blob內容型態錯誤(非Map)");
        }
    }

// -------------------------- OTHER METHODS --------------------------

    public List<Map> getRecord() throws JBranchException {
        if (dataList != null) {
            return dataList;
        }
        else {
            return new ArrayList<Map>();
        }
    }

    /**
     * 設定多筆資料
     *
     * @param list list
     * @author Richard
     * @since 2009/06/03
     */
    public void setData(List<Map> list) {
        dataList = list;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public String getFileName() {
        return fileName;
    }

    /**
     * 自訂報表檔名
     *
     * @param fileName fileName
     * @author Richard
     * @since 2009/06/03
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Map getParameters() {
        if (parameters == null) {
            parameters = new HashMap();
        }
        return parameters;
    }

    public String getPath() {
        return path;
    }

    /**
     * 是否有設定合併flag
     *
     * @return boolean
     * @author Richard
     * @since 2009/06/03
     */
    public boolean isMerge() {
        return merge;
    }

    /**
     * 設定合併flag
     *
     * @param merge merge
     * @author Richard
     * @since 2009/06/03
     */
    public void setMerge(boolean merge) {
        this.merge = merge;
    }

	public Object getOthers() {
		return others;
	}

	public void setOthers(Object others) {
		this.others = others;
	}
}
