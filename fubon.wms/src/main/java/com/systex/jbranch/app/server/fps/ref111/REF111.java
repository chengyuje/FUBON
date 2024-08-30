package com.systex.jbranch.app.server.fps.ref111;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * REF110 新增轉介資料
 * 
 * @author Ocean
 * @date 20160622
 * @spec 
 */
@Component("ref111")
@Scope("request")
public class REF111 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(REF111.class);
	
	public void query(Object body, IPrimitiveMap header) throws JBranchException {
		
		//System.out.println("###########" + getUserVariable(FubonSystemVariableConsts.LOGIN_SOURCE_TOKEN));
		
		REF111InputVO inputVO = (REF111InputVO)body;
		REF111OutputVO outputVO = new REF111OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		if(StringUtils.isBlank(inputVO.getQueryType()) || !inputVO.getQueryType().matches("0|1")){
			this.sendRtnObject(outputVO);
			return;
		}
		
		String tbName = null;
		String diffColumn = null;
		StringBuffer whereStr = new StringBuffer();
		StringBuffer sbr = new StringBuffer();
		
		if("0".equals(inputVO.getQueryType())){
			diffColumn = "SALES_ROLE";
			tbName = "TBCAM_REF_TARG_ROL";
		}
		else if("1".equals(inputVO.getQueryType())){
			diffColumn = "BRANCH_NBR";
			tbName = "TBCAM_REF_TARG_BRH";
		}
		
		if(StringUtils.isNotBlank(inputVO.getDateYearMonth())){
			whereStr.append("and YYYYMM = :yyyyMM ");
			queryCondition.setObject("yyyyMM", new SimpleDateFormat("yyyyMM").format(new Date(Long.parseLong(inputVO.getDateYearMonth()))));
		}
		
		sbr.append("SELECT ROWNUM , tb.* FROM( ");
		sbr.append("	SELECT ");
		sbr.append("		YYYYMM, REF_PROD, MON_TARGET_CNT, MON_SUC_TARGET_CNT, YEAR_TARGET_CNT, ");
		sbr.append("		YEAR_SUC_TARGET_CNT, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE, #diffColumn ");
		sbr.append("	FROM #tableName ");
		sbr.append("	WHERE 1 = 1 #where ");
		sbr.append("	ORDER BY YYYYMM, #diffColumn, REF_PROD ");
		sbr.append(")tb ");
		
		replaceAll(sbr , "#diffColumn" , diffColumn);
		replaceAll(sbr , "#tableName" , tbName);
		replaceAll(sbr , "#where" , whereStr);
		queryCondition.setQueryString(sbr.toString());
		outputVO.setResultList(dam.exeQuery(queryCondition));
		this.sendRtnObject(outputVO);
	}
	

	public static StringBuffer replaceAll(StringBuffer sbr , String name , StringBuffer reVal){
		return replaceAll(sbr , name , reVal.toString());
	}
	
	public static StringBuffer replaceAll(StringBuffer sbr , String name , String reVal){
		while(sbr.indexOf(name) != -1){
			sbr.replace(sbr.indexOf(name), sbr.indexOf(name) + name.length() , reVal);
		}
		return sbr;
	}
	
	public void save(Object body, IPrimitiveMap header) throws JBranchException {
		REF111InputVO inputVO = (REF111InputVO)body;
		REF111OutputVO outputVO = new REF111OutputVO();	

		try {
			outputVO.setDeleteSize(delForYM(inputVO));
			outputVO.setInsertSize(saveForList(inputVO));
		} 
		catch (ParseException e) {
			e.printStackTrace();
			throw new JBranchException(e);
		}
		finally{
			this.sendRtnObject(outputVO);
		}
	}
	
	
	public int delForYM(REF111InputVO inputVO) throws DAOException, JBranchException, ParseException{
		String tbName = "0".equals(inputVO.getQueryType()) ? "TBCAM_REF_TARG_ROL" : "1".equals(inputVO.getQueryType()) ? "TBCAM_REF_TARG_BRH" : "";
		String del = "delete from " + tbName + " where YYYYMM = :YYYYMM";
		
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setQueryString(del);
		
		if(CollectionUtils.isEmpty(inputVO.getList()) && StringUtils.isNotBlank(inputVO.getDateYearMonth())){
			condition.setObject("YYYYMM", new SimpleDateFormat("yyyyMM").format(new Date(Long.parseLong(inputVO.getDateYearMonth()))));
			return dam.exeUpdate(condition);
		}
		else if(CollectionUtils.isNotEmpty(inputVO.getList())){
			condition.setObject("YYYYMM", inputVO.getList().get(0).get("YYYYMM"));
			return dam.exeUpdate(condition);
		}
		return 0;
	}
	
	public int saveForList(REF111InputVO inputVO) throws DAOException, JBranchException{
		String tbName = null;
		String columnAllPt = "YYYYMM|#column|REF_PROD|MON_TARGET_CNT|MON_SUC_TARGET_CNT|YEAR_TARGET_CNT|YEAR_SUC_TARGET_CNT|VERSION|CREATETIME|CREATOR|MODIFIER|LASTUPDATE";
		String columnDatePt = "CREATETIME|LASTUPDATE";
		String columnNumberPt = "MON_TARGET_CNT|MON_SUC_TARGET_CNT|YEAR_TARGET_CNT|YEAR_SUC_TARGET_CNT|VERSION";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		if("0".equals(inputVO.getQueryType())){
			tbName = "TBCAM_REF_TARG_ROL"; 
			columnAllPt = columnAllPt.replaceFirst("#column", "SALES_ROLE");
		}
		else if("1".equals(inputVO.getQueryType())){
			tbName = "TBCAM_REF_TARG_BRH";
			columnAllPt = columnAllPt.replaceFirst("#column", "BRANCH_NBR");
		}
		else{
			return 0;
		}
		
		int saveSize = 0;
		//insert
		for(Map<String, Object> param : inputVO.getList()){				
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sbrBrfore = new StringBuffer();
			StringBuffer sbrAfter = new StringBuffer();
			
			//insert sql
			sbrBrfore.append("INSERT INTO ").append(tbName).append(" ( ");
			sbrAfter.append(") VALUES( ");
			
			for(String key : param.keySet()){
				if(!key.matches(columnAllPt) || param.get(key) == null){
					continue;	
				}
				
				sbrBrfore.append(key + "," );
				sbrAfter.append(":" + key +",");
				try {
					queryCondition.setObject(key , 
						key.matches(columnDatePt) ? dateFormat.parse(param.get(key).toString()) :
						key.matches(columnNumberPt) ? Double.parseDouble(param.get(key).toString()) :
						param.get(key)
					);
				} catch (ParseException e) {
					e.printStackTrace();
					throw new JBranchException(e);
				}
			}
			
			sbrBrfore.replace(sbrBrfore.length() - 1 , sbrBrfore.length() , "");
			sbrAfter.replace(sbrAfter.length() - 1 , sbrAfter.length() , ")");
			queryCondition.setQueryString(sbrBrfore.append(sbrAfter).toString());
			saveSize += dam.exeUpdate(queryCondition);
		}
		
		return saveSize;
	}
	
}