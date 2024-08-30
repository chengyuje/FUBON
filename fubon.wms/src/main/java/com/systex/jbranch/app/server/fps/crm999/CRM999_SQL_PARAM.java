package com.systex.jbranch.app.server.fps.crm999;

import java.util.ArrayList;
import java.util.List;

import java.util.Collections;

/**
 * DataObject for SQL and it's params
 * @author Eli
 * @date 2017/11/23
 * @spec 
 */
@SuppressWarnings({"rawtypes"})
public class CRM999_SQL_PARAM {
	String sql1 = "", sql2 = "", sql3 = "", sql4 = "";
	List param1 = new ArrayList();
	List param2 = new ArrayList();
	List param3 = new ArrayList();
	List param4 = new ArrayList();
	
	public String getSql1() {
		return sql1;
	}
	public void setSql1(String sql1) {
		this.sql1 = sql1;
	}
	public String getSql2() {
		return sql2;
	}
	public void setSql2(String sql2) {
		this.sql2 = sql2;
	}
	public String getSql3() {
		return sql3;
	}
	public void setSql3(String sql3) {
		this.sql3 = sql3;
	}
	public String getSql4() {
		return sql4;
	}
	public void setSql4(String sql4) {
		this.sql4 = sql4;
	}
	public List getParam1() {
		return Collections.unmodifiableList(param1);
	}
	public void setParam1(List param1) {
		this.param1 = param1;
	}
	public List getParam2() {
		return Collections.unmodifiableList(param2);
	}
	public void setParam2(List param2) {
		this.param2 = param2;
	}
	public List getParam3() {
		return Collections.unmodifiableList(param3);
	}
	public void setParam3(List param3) {
		this.param3 = param3;
	}
	public List getParam4() {
		return Collections.unmodifiableList(param4);
	}
	public void setParam4(List param4) {
		this.param4 = param4;
	}
	
	public String[] getSqlArray() {
		return new String[]{sql1, sql2, sql3, sql4};
	}
	
	public Object[] getParamArray() {
		return new Object[]{param1, param2, param3, param4};
	}
}
