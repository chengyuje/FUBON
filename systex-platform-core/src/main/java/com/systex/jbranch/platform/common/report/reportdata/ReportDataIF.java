package com.systex.jbranch.platform.common.report.reportdata;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.errHandle.JBranchException;

//import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

public interface ReportDataIF {

//	public void setData(List<Map> list);
	// public void setDataBy(List<Object> list,String[] fields);
	
	public void addParameter(String id,Object value);
	public Map getParameters();
	public void addRecord(Map record);
	@Deprecated
	public void addRecordList(List<Map> list);
	public void setSQL(String sql) throws JBranchException;
	public void setMerge(boolean merge);
	public void setFileName(String fileName);
	public void setPath(String path);
	public void addRecordList(String dataSet, List list);
	public List getRecordList(String dataSet);
	public Map getRecordListAll();
	public void setRecords(Blob blob) throws Exception;
	public Blob getRecords()throws IOException;
	public byte[] getRecordsBytes() throws IOException;
	public void setRecordsBytes(byte[] bytes) throws Exception;
	public Object getOthers();
	public void setOthers(Object others); 
}
