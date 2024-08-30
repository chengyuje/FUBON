package com.systex.jbranch.fubon.bth.job.inf;

/**
 * 
 * 抽象存放介面
 * @author Eli
 * @since V1.1版
 * @date 2017/10/12
 * @spec 
 * 		<p> <b>實現此介面必須實作下列方法 :</b>
 * 		    <li>getSQL</li>
 * 		    <li>getParams</li>
 * 		</p>
 */ 
public interface IPrepareStatementInfo {
	public String getSQL();
	public Object[] getParams();
}
