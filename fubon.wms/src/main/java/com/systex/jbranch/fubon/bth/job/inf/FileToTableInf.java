package com.systex.jbranch.fubon.bth.job.inf;

import java.util.List;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
/**
 * 
 * 檔案寫入Table介面
 * @author Eli
 * @since V1.1版
 * @date 2017/10/12
 * @spec 
 * 		<p> <b>實現此介面必須實作下列方法 :</b>
 * 		    <li>getInsertSQL</li>
 * 		    <li>getData</li>
 * 		</p>
 */ 
@SuppressWarnings({"rawtypes"})
public interface FileToTableInf {
	public String getInsertSQL();
	public List getData() throws JBranchException;
}
