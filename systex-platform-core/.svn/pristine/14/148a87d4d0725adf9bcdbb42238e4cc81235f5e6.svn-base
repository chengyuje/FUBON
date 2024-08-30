package com.systex.jbranch.platform.common.dataaccess.serialnumber;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import common.Logger;

@Component("SeriaNextSequence")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@SuppressWarnings("unchecked")
public class SeriaNextSequence extends BizLogic{
	public static Logger logger = Logger.getLogger(SeriaNextSequence.class);

	/**前面自訂 +左補0**/
	public String nextSeqLpad(String name , String before , int adNum , String adStr) throws JBranchException {
		if(StringUtils.isBlank(name)) throw new JBranchException("in SeriaNextSequence , arguments name can't empty");
		return exeQueryNextSeq(before + " || " + lrpAdQueryStr("LP" , name , adNum , adStr));
	}
	
	/**左補0**/
	public String nextSeqLpad(String name , int adNum , String adStr) throws JBranchException {
		if(StringUtils.isBlank(name)) throw new JBranchException("in SeriaNextSequence , arguments name can't empty");
		return exeQueryNextSeq(lrpAdQueryStr("LP" , name , adNum , adStr));
	}
	
	/**前面自訂 + 左補0**/
	public String nextSeqRpad(String name , String before , int adNum , String adStr) throws JBranchException {
		if(StringUtils.isBlank(name)) throw new JBranchException("in SeriaNextSequence , arguments name can't empty");
		return exeQueryNextSeq(before + " || " + lrpAdQueryStr("RP" , name , adNum , adStr));
	}
	
	/**右邊補0**/
	public String nextSeqRpad(String name , int adNum , String adStr) throws JBranchException {
		if(StringUtils.isBlank(name)) throw new JBranchException("in SeriaNextSequence , arguments name can't empty");
		return exeQueryNextSeq(lrpAdQueryStr("RP" , name , adNum , adStr));
	}
	
	public String lrpAdQueryStr(String type , String name , int adNum , String adStr){
		return type + "AD(" + name + ".nextval , " + String.valueOf(adNum) + ", '" + adStr + "')";
	}
	/**純取號**/
	public String nextSeq(String name) throws JBranchException {
		if(StringUtils.isBlank(name)) throw new JBranchException("in SeriaNextSequence , arguments name can't empty");
		return exeQueryNextSeq(name + ".nextval");
	}
	
	public String exeQueryNextSeq(String nextSql) throws JBranchException{
		String sql = "select " + nextSql + " SEQ from dual ";
		logger.info("取號：" + sql);
		List<Map<String , Object>> seqResultList = exeQueryForQcf(
			genDefaultQueryConditionIF().setQueryString("select " + nextSql + " SEQ from dual "));
		String seq = seqResultList.get(0).get("SEQ").toString();
		logger.info("取號：" + seq);
		return seq;
	}
}
