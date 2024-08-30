package com.systex.jbranch.app.server.fps.pms200;



import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.net.ntp.TimeStamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms345.PMS345;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSPARAMETERPK;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSPARAMETERVO;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.util.IPrimitiveMap;



/**
 * Copy Right Information :<br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :客戶等級聯絡頻次維護<br>
 * Comments Name : PMS200.java<br>
 * Author : 啊瑋<br>
 * Date :2017/04/20 <br>
 * Version : 1.0 <br>
 * Editor : 啊瑋<br>
 * Editor Date : 2017年04月20日<br>
 */

@Component("pms200")
@Scope("request")
public class PMS200 extends FubonWmsBizLogic{
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS200.class);
	
	/**
	 * 查詢
	 * 
	 * @param body
	 * @param header
	 * @throws  
	 * @throws JBranchException
	 */
	public void inquire(Object body, IPrimitiveMap header) throws  JBranchException{
		PMS200InputVO inputvo=(PMS200InputVO)body;
		PMS200OutputVO outputvo=new PMS200OutputVO();
		
		try {
			dam=this.getDataAccessManager();
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sql=new StringBuilder();
			
			sql.append(" select ");
			sql.append(" TO_CHAR(CREATETIME,'yyyymmdd') as creat, ");
			sql.append(" TO_CHAR(LASTUPDATE,'yyyymmdd') as last, ");
			sql.append(" PARAM_NAME_EDIT,PARAM_CODE ");
			sql.append(" from TBSYSPARAMETER ");
			sql.append(" where PARAM_TYPE='PMS.CONTACT_SEQ' ");
			sql.append(" ORDER BY PARAM_CODE,PARAM_NAME_EDIT ");
			
			condition.setQueryString(sql.toString());
            List<Map<String,Object>> list=dam.exeQuery(condition);
            Integer max=0,min=0;
            for (Map<String,Object> map:list) {
            	
            	Integer tempmin=Integer.valueOf(String.valueOf(map.get("LAST")));	 
            	Integer tempmax=Integer.valueOf(String.valueOf(map.get("LAST")));	
            	if(min==0 || tempmin<min){
            		min=tempmin;
            	}
            	
            	if(max==0 || tempmax>max){
            		max=tempmax;
            	}     		    
            }
            
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");  
            SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-mm-dd"); 
            String d1=String.valueOf(min);
            d1=d1.substring(0,4)+"-"+d1.substring(5, 6)+"-"+d1.substring(6,8)+" 00:00:00";
            Long date1=sdf.parse(d1).getTime();            
            String d2=String.valueOf(max);
            d2=d2.substring(0,4)+"-"+d2.substring(5, 6)+"-"+d2.substring(6,8)+" 00:00:00";
            Long date2=sdf.parse(d2).getTime();
            Long time=(date2-date1)/(1000 * 60 * 60 * 24);
            
            list.get(0).put("time",time);
            
            outputvo.setResultList(list);
            this.sendRtnObject(outputvo);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	  }
		
	/**
	 * 儲存
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void save(Object body, IPrimitiveMap header) throws JBranchException {
		PMS200InputVO input = (PMS200InputVO)body;
		PMS200OutputVO outputVO=new PMS200OutputVO();
		List<Map<String,Object>> list = input.getList();
		dam=this.getDataAccessManager();
		try {
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sql = new StringBuilder();
			sql.append("select SUBSTR(PARAM_CODE,0,1) as lev,PARAM_CODE,PARAM_NAME_EDIT,PARAM_TYPE,PARAM_NAME,PARAM_DESC,PARAM_STATUS,CREATETIME,CREATOR,MODIFIER,PARAM_ORDER,VERSION from TBSYSPARAMETER where PARAM_TYPE='PMS.CONTACT_SEQ'");
			condition.setQueryString(sql.toString());
			List<Map<String,Object>> listquery = dam.exeQuery(condition);
			List<Map<String,Object>> insertmap = new ArrayList<Map<String,Object>>();
			List<Integer> updateindex = new ArrayList<Integer>();
			Boolean checktemp = true, check = true; 
			int j = 0;
			

			for (Map<String,Object> map:listquery) {			
				for(int i = 0; i < list.size(); ++i) {
					String LEV = "" , DATA1 = "" , DATA2 = "", DATA3 = "", DATA4 = "";
					LEV = ObjectUtils.toString(list.get(i).get("head"));
					DATA1 = ObjectUtils.toString(list.get(i).get("DATA1"));
					DATA2 = ObjectUtils.toString(list.get(i).get("DATA2"));
					DATA3 = ObjectUtils.toString(list.get(i).get("DATA3"));
					DATA4 = ObjectUtils.toString(list.get(i).get("DATA4"));
					
					if (map.containsValue(LEV)) {
					    j = i;
					    
						/**資料原為欄位PARAM_NAME，修改後存在PARAM_NAME，但原本的資料不會更動
						 * 下列註解程式處理的事情為:資料PARAM_NAME_EDIT與原先PARAM_NAME相同時不做儲存動作
						 * 此邏輯不明有何意義且導致使用者不知道為何會出錯 */
//					    if (map.get("PARAM_NAME").toString().equals(DATA1) && (map.get("PARAM_CODE").toString().equals(LEV + "1"))) { 				
//					    	System.err.println(LEV + " : " + map.get("PARAM_NAME").toString() + " === " + map.get("PARAM_CODE").toString());
//					    	break; 
//					    } 
//					    else if(map.get("PARAM_NAME").toString().equals(DATA2) && (map.get("PARAM_CODE").toString().equals(LEV + "2"))) {
//					    	System.err.println(LEV + " : " + map.get("PARAM_NAME").toString() + " === " + map.get("PARAM_CODE").toString());
//					    	break;   
//					    }
//					    else if(map.get("PARAM_NAME").toString().equals(DATA3) && (map.get("PARAM_CODE").toString().equals(LEV + "3"))) {
//					    	System.err.println(LEV + " : " + map.get("PARAM_NAME").toString() + " === " + map.get("PARAM_CODE").toString());
//					    	break;  
//					    }		 
//					    else if(map.get("PARAM_NAME").toString().equals(DATA4) && (map.get("PARAM_CODE").toString().equals(LEV + "4"))) {
//					    	System.err.println(LEV + " : " + map.get("PARAM_NAME").toString() + " === " + map.get("PARAM_CODE").toString());
//					    	break;  
//					    }
					    checktemp = false;
				    }
					
				}
				
				if (checktemp == false) {
					check = false;	
					insertmap.add(map);			 
					updateindex.add(new Integer(j));
					checktemp = true;
			    }
			}
			
			if (check == false) {
				int k = 0;
				for (Map<String,Object> map:insertmap) {	
					TBSYSPARAMETERPK pk = new TBSYSPARAMETERPK();
					pk.setPARAM_CODE(map.get("PARAM_CODE").toString());
					pk.setPARAM_TYPE(map.get("PARAM_TYPE").toString());
					TBSYSPARAMETERVO vo = (TBSYSPARAMETERVO)dam.findByPKey(TBSYSPARAMETERVO.TABLE_UID, pk);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
					Timestamp time = new  Timestamp(sdf.parse(map.get("CREATETIME").toString()).getTime()); 
		 			vo.setCreatetime(time); 
		 			vo.setCreator(map.get("CREATOR").toString()); 			 
		 			Timestamp now = new Timestamp(System.currentTimeMillis());
		 			vo.setLastupdate(now); 
		 			vo.setModifier(map.get("MODIFIER").toString());
		 			vo.setPARAM_DESC(map.get("PARAM_DESC").toString()); 
			     
		 			vo.setPARAM_NAME(map.get("PARAM_NAME").toString()); 
		 			String num = "DATA" + map.get("PARAM_CODE").toString().substring(1,2);
		 			vo.setPARAM_NAME_EDIT(list.get(updateindex.get(k++)).get(num).toString());
		 			vo.setPARAM_ORDER(Integer.valueOf(map.get("PARAM_ORDER").toString()));
		 			vo.setPARAM_STATUS(map.get("PARAM_STATUS").toString());
		 			vo.setVersion(Long.parseLong(map.get("VERSION").toString()));
		 			vo.setcomp_id(pk);
		 			dam.update(vo);
				}
			}
			
			outputVO.setResultList(list);
			this.sendRtnObject(outputVO);
			
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
}
