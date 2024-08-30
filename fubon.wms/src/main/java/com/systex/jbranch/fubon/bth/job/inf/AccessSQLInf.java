package com.systex.jbranch.fubon.bth.job.inf;
import java.sql.Connection;
import java.util.List;
import java.util.Vector;

import com.systex.jbranch.platform.common.errHandle.APException;
/**
 * 
 * SQL抽象介面
 * @author Eli
 * @since V1.1版
 * @date 2017/10/12
 * @spec 
 * 		<p><b>實現此介面必須實作下列方法 :</b>
 * 		   <li>setExeInfo</li>
 * 		   <li>getSqlString</li>
 * 		   <li>exeQuery</li>
 * 		</p>
 */ 
@SuppressWarnings({ "rawtypes" })
public interface AccessSQLInf {
	public void setExeInfo(String str, Object[] arr) throws Exception;
	public String getSqlString();
	public List exeQuery(Connection c) throws Exception;
	public void exeUpdate(Connection c) throws Exception;
	public void setSpcVectorSqls(Vector sqls) throws APException;
	public void setDatalist(List datalist);
	public List exeQueryMeta(Connection c) throws Exception;
}
