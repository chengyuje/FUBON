package com.systex.jbranch.app.server.fps.simgr009;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSMARQUEEVO;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSPARAMETERPK;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSPARAMETERVO;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSPARAMTYPEVO;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 參數內容設定
 * 
 * @author 胡海龍
 * @date 2009-8-28
 * @spec 修改註記 V0.1	2009/07/29	初版
 * 				V0.5	2009/8/10	增加 PARAM_NAME_EDIT, PARAM_STATUS欄位
 * 				V0.6	2009/8/25	1.	開放cmbPtypeBuss下拉選單﹐當為系統管理者登入使用時
						2.	按下儲存鍵時﹐判斷錯誤條件調整(if varOnChange <> true,顯示錯誤訊息 simgr009_002)
				V0.7	2009/8/27	1.	調整 2.1.3的項次3, 項次8放入畫面變數的說明
				2.	調整匯入CSV資料當進行相關係數匯入的處理方式
 * 
 */
@Component("simgr009")
@Scope("request")
public class SIMGR009 extends FubonWmsBizLogic {
	
	DataAccessManager dam = null;

	/**
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * 查詢表TBSYSPARAMTYPE取得參數種類清單，條件為PTYPE_BUSS=cmbPtypeBuss
	 */
	public void inquireParamType(Object body, IPrimitiveMap<?> header) throws JBranchException {
		SIMGR009InputVO inputVO=(SIMGR009InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		SIMGR009OutputVO outputVO=new SIMGR009OutputVO();

		//EleanorChang update
		//condition.setQueryString("select * from TBSYSPARAMTYPE where PTYPE_BUSS='"+inputVO.getCmbPtypeBuss()+"' order by PARAM_TYPE");
		condition.setQueryString("select * from TBSYSPARAMTYPE where PTYPE_BUSS = ? order by PARAM_TYPE");
		condition.setString(1, inputVO.getCmbPtypeBuss());
		List<Map<String, Object>> result = dam.exeQuery(condition);
		if(result.size()==0){
			throw new APException("ehl_01_simgr009_012");
		}
		outputVO.setLstParameter(result);
	

		sendRtnObject(outputVO);

	}
	/**
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * 查詢特定參數種類的參數清單，條件為客戶選中的參數種類
	 */
	public void inquireParamDetail(Object body, IPrimitiveMap<?> header) throws JBranchException {
		SIMGR009InputVO inputVO=(SIMGR009InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		SIMGR009OutputVO outputVO=new SIMGR009OutputVO();

		//EleanorChang update
//		condition.setQueryString("select * from  TBSYSPARAMTYPE A "+
//								"left join TBSYSPARAMETER B on B.PARAM_TYPE = A.PARAM_TYPE "+
//								"where A.PARAM_TYPE= '"+inputVO.getSelectedParameter()+"' "+
//								"order by B.PARAM_ORDER");
		condition.setQueryString("select * from  TBSYSPARAMTYPE A "+
				"left join TBSYSPARAMETER B on B.PARAM_TYPE = A.PARAM_TYPE "+
				"where A.PARAM_TYPE= ? "+
				"order by B.PARAM_CODE, B.PARAM_ORDER");
		condition.setString(1, inputVO.getSelectedParameter());
		System.out.println("aaa:"+ inputVO.getSelectedParameter());
		List<Map<String, Object>> result = dam.exeQuery(condition);
		if(result.size()==0){
			throw new APException("ehl_01_simgr009_013");
		}
		outputVO.setAdgParameter(result);
		

		sendRtnObject(outputVO);

	}
	/*
	public void test(Object body, IPrimitiveMap header) throws JBranchException, IOException {
		dam = this.getDataAccessManager();
		TBSYSPARAMTYPEVO typeVO=(TBSYSPARAMTYPEVO) dam.findByPKey(TBSYSPARAMTYPEVO.TABLE_UID,"TBFPS.SYS.ASSETS_CONFIG");

		
		typeVO.setPTYPE_DESC("abcdef");
		
		dam.update(typeVO);

	}
	*/
	/**
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws IOException
	 * 保存用戶對參數清單的修改
	 */
	public void save(Object body, IPrimitiveMap<?> header) throws JBranchException, IOException {
		dam = this.getDataAccessManager();
		List<Object> args = new ArrayList<Object>();
		args.add(body);
		SIMGR009InputVO inputVO = (SIMGR009InputVO) dam.newTransactionExeMethod(this, "doSave", args);
		if("0".equals(inputVO.getVarWorksType())&&!"2".equals(inputVO.getVarPtypeRange())){
			//2011-12-13 by Jacky 不立即執行XML更新,因平台jar平配合更新beanname
			//createXML(inputVO.getSelectedParameter());
		}
		sendRtnObject(null);
	}
	
	public SIMGR009InputVO doSave(SIMGR009InputVO inputVO) throws JBranchException{
		//try{
			
			//DataAccessManager dam = this.getDataAccessManager();
			DataAccessManager dam = this.getDataAccessManager();
			QueryConditionIF condition = dam.getQueryCondition();
			if("1".equals(inputVO.getVarWorksType())){
				String pk=inputVO.getSelectedParameter();
				Object o=dam.findByPKey(TBSYSPARAMTYPEVO.TABLE_UID,pk);
				if(o==null){
					throw new APException("ehl_01_simgr009_014");
				}
				TBSYSPARAMTYPEVO typeVO=(TBSYSPARAMTYPEVO)o;
				typeVO.setWORKS_DATE(new Timestamp(inputVO.getTipWorksDate().getTime()));
				dam.update(typeVO);
			}
			int i_temp=0;
			Iterator<Map<String,String>> iter=inputVO.getAdgParameter().iterator();
			while(iter.hasNext()){
				Map<String,String> data=iter.next();
				if("1".equals(inputVO.getVarWorksType())){
					//EleanorChang update
//					String delSql="delete from TBSYSPARAMETER where PARAM_TYPE='"+data.get("PARAM_TYPE")
//									+"' and PARAM_CODE='"+data.get("PARAM_CODE")+"'"
//									+" and PARAM_STATUS='3'";
					String delSql="delete from TBSYSPARAMETER where PARAM_TYPE = ? "
					+" and PARAM_CODE = ? "
					+" and PARAM_STATUS='3'";
					condition.setQueryString(delSql);
					condition.setString(0, data.get("PARAM_TYPE"));
					condition.setString(1, data.get("PARAM_CODE"));
					dam.exeUpdate(condition);
					
					TBSYSPARAMETERPK pameterPK=new TBSYSPARAMETERPK();
					pameterPK.setPARAM_TYPE(data.get("PARAM_TYPE"));
					pameterPK.setPARAM_CODE(data.get("PARAM_CODE"));
					TBSYSPARAMETERVO pameterVO=(TBSYSPARAMETERVO)dam.findByPKey(TBSYSPARAMETERVO.TABLE_UID, pameterPK);
					if(pameterVO!=null){
						pameterVO.setPARAM_ORDER(i_temp);
						pameterVO.setPARAM_NAME_EDIT(data.get("PARAM_NAME_EDIT"));
						pameterVO.setPARAM_STATUS(data.get("PARAM_STATUS"));
						pameterVO.setPARAM_DESC(data.get("PARAM_DESC"));
						dam.update(pameterVO);
					}
					else{
						TBSYSPARAMETERPK pk=new TBSYSPARAMETERPK();
						pk.setPARAM_TYPE(data.get("PARAM_TYPE"));
						pk.setPARAM_CODE(data.get("PARAM_CODE"));
						TBSYSPARAMETERVO insertVO=new TBSYSPARAMETERVO();
						insertVO.setcomp_id(pk);
						insertVO.setPARAM_ORDER(i_temp);
						insertVO.setPARAM_NAME_EDIT(data.get("PARAM_NAME_EDIT"));
						insertVO.setPARAM_NAME("");
						insertVO.setPARAM_STATUS(data.get("PARAM_STATUS"));
						insertVO.setPARAM_DESC(data.get("PARAM_DESC"));
						dam.create(insertVO);
					}
					//20100113新增 升 批次生效 所以有生效日
					String LOG_TYPE_ID="PARA_LOG";
					SimpleDateFormat sDateS = new SimpleDateFormat("yyyyMMdd");
					String WorksDate= sDateS.format(inputVO.getTipWorksDate());
//					LogUtil.writeLog(LOG_TYPE_ID,LogUtil.LEVEL_INFO, data.get("PARAM_TYPE"), inputVO.getSelectedParameterlabel(), data.get("PARAM_CODE"),data.get("PARAM_NAME_EDIT"),WorksDate);
				}
				else{
					TBSYSPARAMETERPK pameterPK=new TBSYSPARAMETERPK();
			
					pameterPK.setPARAM_TYPE(data.get("PARAM_TYPE"));
					pameterPK.setPARAM_CODE(data.get("PARAM_CODE"));
					TBSYSPARAMETERVO pameterVO=(TBSYSPARAMETERVO)dam.findByPKey(TBSYSPARAMETERVO.TABLE_UID, pameterPK);
				
						if("1".equals(data.get("PARAM_STATUS"))){
							
							if(pameterVO!=null){
								//EleanorChang update
//								String delSql="delete from TBSYSPARAMETER where PARAM_TYPE='"+data.get("PARAM_TYPE")+"' and PARAM_CODE='"+data.get("PARAM_CODE")+"'";
								
//								String delSql="delete from TBSYSPARAMETER where PARAM_TYPE = ? and PARAM_CODE = ? ";
//								condition.setQueryString(delSql);
//								condition.setString(0, data.get("PARAM_TYPE"));
//								condition.setString(1, data.get("PARAM_CODE"));
//								dam.executeUpdate(condition);
								
								
								//System.out.println("[PARAM_TYPE]" +"=[" + data.get("PARAM_TYPE") + "]");
								//System.out.println("[PARAM_CODE]" +"=[" + data.get("PARAM_CODE") + "]");
								
								dam.delete(pameterVO);
							}
							
							
						}
						else{
//							TBSYSPARAMETERVO pameterVO=(TBSYSPARAMETERVO)dam.findByPKey(TBSYSPARAMETERVO.TABLE_UID, pameterPK);
							if(pameterVO!=null){
								pameterVO.setPARAM_ORDER(i_temp);
								pameterVO.setPARAM_NAME_EDIT(data.get("PARAM_NAME_EDIT"));
								
								pameterVO.setPARAM_NAME(data.get("PARAM_NAME_EDIT"));
								pameterVO.setPARAM_STATUS("0");
								pameterVO.setPARAM_DESC(data.get("PARAM_DESC"));
								dam.update(pameterVO);
							}
							else{
								TBSYSPARAMETERPK pk=new TBSYSPARAMETERPK();
								pk.setPARAM_TYPE(data.get("PARAM_TYPE"));
								pk.setPARAM_CODE(data.get("PARAM_CODE"));
								TBSYSPARAMETERVO insertVO=new TBSYSPARAMETERVO();
								insertVO.setcomp_id(pk);
								insertVO.setPARAM_ORDER(i_temp);
								insertVO.setPARAM_NAME_EDIT(data.get("PARAM_NAME_EDIT"));
								insertVO.setPARAM_NAME(data.get("PARAM_NAME_EDIT"));
								insertVO.setPARAM_STATUS("0");
								insertVO.setPARAM_DESC(data.get("PARAM_DESC"));
								dam.create(insertVO);
							}
							
							//20100113新增 升 即時生效 所以無生效日
							String LOG_TYPE_ID="PARA_LOG";
//							LogUtil.writeLog(LOG_TYPE_ID,LogUtil.LEVEL_INFO, data.get("PARAM_TYPE"), inputVO.getSelectedParameterlabel(), data.get("PARAM_CODE"),data.get("PARAM_NAME_EDIT"));
						}
					
					
				}
				
				i_temp++;
				
			}
			
			//EleanorChang add 2009/12/28
			if("0".equals(inputVO.getVarWorksType())){
				TBSYSMARQUEEVO marqueeVO=new TBSYSMARQUEEVO();
				marqueeVO.setSUBJECT("業務參數異動更新");
				String hostdate = (String) SysInfo.getInfoValue(SystemVariableConsts.HOSTDATE);
				String hostdate_2 = (String.valueOf((Integer.valueOf(hostdate.substring(0,4))-1911))+ "/" + hostdate.substring(4,6)+ "/" + hostdate.substring(6,8));
				String hosttime = (String) SysInfo.getInfoValue(SystemVariableConsts.HOSTTIME);
				String hosttime_2 = hosttime.substring(0, 2) + ":" + hosttime.substring(2, 4);
				marqueeVO.setCONTENT("FPS系統於(" + hostdate_2 + ")(" +
						hosttime_2 + ")更新參數﹐請重新登入系統﹐啟動新參數內容");
				Timestamp ts = new Timestamp(new Date().getTime());
				marqueeVO.setSTART_DT(ts);
				marqueeVO.setEND_DT(ts);
				marqueeVO.setTELLERID((String) SysInfo.getInfoValue(SystemVariableConsts.LOGINID));
				marqueeVO.setSUSPEND("N");
				marqueeVO.setNEW_MSG("Y");
				marqueeVO.setMSG_TYPE("S");

				dam.create(marqueeVO);
			}
			
			
		//}catch(Exception e){
		//	e.printStackTrace();
		//}
		return inputVO;
	}
	
	/**
	 * 匯出參數清單
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void export(Object body, IPrimitiveMap<?> header) throws JBranchException {
		SIMGR009InputVO inputVO=(SIMGR009InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		CSVUtil util=new CSVUtil();
		
		if("0".equals(inputVO.getVarCsvType())||"9".equals(inputVO.getVarCsvType())){
			//EleanorChang update
//			condition.setQueryString("select * from  TBSYSPARAMETER B "+
//					"where B.PARAM_TYPE ='"+inputVO.getSelectedParameter()+"' "+
//					"order by B.PARAM_ORDER");
			condition.setQueryString("select * from  TBSYSPARAMETER B "+
					"where B.PARAM_TYPE = ? "+
					"order by B.PARAM_ORDER");
			condition.setString(1, inputVO.getSelectedParameter());
			List<Map<String, Object>> result = dam.exeQuery(condition);
			
			Iterator<Map<String, Object>> iter=result.iterator();
			List<Object[]> recordList=new ArrayList<Object[]>();
			while(iter.hasNext()){
				Object[] data_arr=new Object[3];
				Map<String, Object> map=iter.next();
				data_arr[0]="=\"" + map.get("PARAM_CODE")+ "\"" ;
				data_arr[1]="=\"" + map.get("PARAM_NAME")+ "\"";
				data_arr[2]="=\"" + map.get("PARAM_DESC")+ "\""; 
				recordList.add(data_arr);
			}						
			
			util.addRecordList(recordList);
//			sendRtnReport(util.generateCSV());
			notifyClientToDownloadFile(util.generateCSV(), "SIMGR009.csv");
		}
		else{
			if("TBFPS.SYS.RELCOEFFICIENT".equals(inputVO.getSelectedParameter())){
//				XmlInfo xmlInfo=new XmlInfo();
//				List<Object[]> recordList=new ArrayList<Object[]>();
//				Object[] firstLine=new Object[25];
//				java.util.Hashtable hashtable=xmlInfo.getVariable("TBFPS_PRODUCT.PROD_CAT", FormatHelper.FORMAT_3);
//				firstLine[0]="";
//				firstLine[1]=hashtable.get("01");
//				firstLine[2]=hashtable.get("02");
//				firstLine[3]=hashtable.get("03");
//				firstLine[4]=hashtable.get("04");
//				firstLine[5]=hashtable.get("05");
//				firstLine[6]=hashtable.get("06");
//				firstLine[7]=hashtable.get("07");
//				firstLine[8]=hashtable.get("08");
//				firstLine[9]=hashtable.get("09");
//				firstLine[10]=hashtable.get("10");
//				firstLine[11]=hashtable.get("11");
//				firstLine[12]=hashtable.get("12");
//				firstLine[13]=hashtable.get("13");
//				firstLine[14]=hashtable.get("14");
//				firstLine[15]=hashtable.get("15");
//				firstLine[16]=hashtable.get("16");
//				firstLine[17]=hashtable.get("17");
//				firstLine[18]=hashtable.get("18");
//				firstLine[19]=hashtable.get("19");
//				firstLine[20]=hashtable.get("20");
//				firstLine[21]=hashtable.get("21");
//				firstLine[22]=hashtable.get("22");
//				firstLine[23]=hashtable.get("23");
//				firstLine[24]=hashtable.get("24");
//				recordList.add(firstLine);
				
				//EleanorChang update
				XmlInfo xmlInfo=new XmlInfo();
				List<Object[]> recordList=new ArrayList<Object[]>();
				java.util.Hashtable hash_t=xmlInfo.getVariable("TBFPS.SYS.PROD_CAT_ROOT", FormatHelper.FORMAT_3);
				java.util.Hashtable hashtable=xmlInfo.getVariable("TBFPS_PRODUCT.PROD_CAT", FormatHelper.FORMAT_3);
				int temp_cat_cnt = hash_t.size();
				int temp_i;
				String temp_s = "";
				Object[] firstLine=new Object[temp_cat_cnt+1];
				firstLine[0]="";
				for(temp_i=1;temp_i<=temp_cat_cnt;temp_i++){
					if(String.valueOf(temp_i).length() == 1 ){
						temp_s = "0" + String.valueOf(temp_i);
					}
					else{
						temp_s = String.valueOf(temp_i);
					}
					firstLine[temp_i]=hashtable.get(temp_s);
				}
				recordList.add(firstLine);
				
				
				String old_prodcat_temp="";
				String new_prodcat_temp=null;
				//EleanorChang update
//				condition.setQueryString("select * from  TBSYSPARAMETER B "+
//						"where B.PARAM_TYPE ='"+inputVO.getSelectedParameter()+"' "+
//						"order by B.PARAM_CODE");
				condition.setQueryString("select * from  TBSYSPARAMETER B "+
						"where B.PARAM_TYPE = ? "+
						"order by B.PARAM_CODE");
				condition.setString(1, inputVO.getSelectedParameter());
				List<Map<String, Object>> result = dam.exeQuery(condition);
				Iterator<Map<String,Object>> iter=result.iterator();
				int index=0;
				Object[] csvData=null;
				while(iter.hasNext()){
					
					Map<String,Object> map=iter.next();
					new_prodcat_temp=((String)map.get("PARAM_CODE")).substring(0, 2);
					if(!old_prodcat_temp.equals(new_prodcat_temp)){
						if(csvData!=null){
							recordList.add(csvData);
						}
						//EleanorChang update
						//csvData=new Object[25];
						csvData=new Object[temp_cat_cnt +1];
						
						index=0;
						old_prodcat_temp=new_prodcat_temp;
						csvData[index++]=hashtable.get(new_prodcat_temp);
					}
					
					csvData[index++]=map.get("PARAM_NAME").toString();

					
					
					
				}
				//LAST RECORD
				if(csvData!=null){
					recordList.add(csvData);
				}

				util.addRecordList(recordList);
//				sendRtnReport(util.generateCSV());
				notifyClientToDownloadFile(util.generateCSV(), "SIMGR009.csv");
			}
			



			
		}

	}
	/**
	 * 通過匯入的CSV數據，修改參數數據
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void importCSV(Object body, IPrimitiveMap<?> header) throws JBranchException {
		SIMGR009InputVO inputVO=(SIMGR009InputVO) body;
		
		dam = this.getDataAccessManager();
		List<Object> args = new ArrayList<Object>();
		args.add(body);
		inputVO = (SIMGR009InputVO) dam.newTransactionExeMethod(this, "doImportCSV", args);

		if("0".equals(inputVO.getVarWorksType())&&!"2".equals(inputVO.getVarPtypeRange())){
			//2011-12-13 by Jacky 不立即執行XML更新,因平台jar平配合更新beanname
			//createXML(inputVO.getSelectedParameter());
		}	
		sendRtnObject(null);
	}
	
	public SIMGR009InputVO doImportCSV(SIMGR009InputVO inputVO) throws JBranchException{
		
		String filePath = (String) SysInfo
		.getInfoValue(SystemVariableConsts.TEMP_PATH);
		
		String cvsFileName = inputVO.getCsvFileName();
		
		//要匯入的CSV 數據
		List<?> csvData = CSVUtil.getBig5CSVFile(filePath, cvsFileName);
		
		//要匯入的CSV 數據
//		List csvData=inputVO.getCsvData();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		if("1".equals(inputVO.getVarWorksType())){
			TBSYSPARAMTYPEVO paramTypeVO=(TBSYSPARAMTYPEVO)dam.findByPKey(TBSYSPARAMTYPEVO.TABLE_UID, inputVO.getSelectedParameter());
			if(paramTypeVO!=null){
				paramTypeVO.setWORKS_DATE(new Timestamp(inputVO.getTipWorksDate().getTime()));
				dam.update(paramTypeVO);
			}
			else{
				throw new APException("ehl_01_simgr009_015");
			}
		}
		
		if("0".equals(inputVO.getVarCsvType())||"9".equals(inputVO.getVarCsvType())){
			if("1".equals(inputVO.getVarWorksType())){		
				//EleanorChang update
//					condition.setQueryString("update TBSYSPARAMETER B "+
//							"set B.PARAM_STATUS='1' "+
//							"where B.PARAM_TYPE ='"+inputVO.getSelectedParameter()+"'");
				condition.setQueryString("update TBSYSPARAMETER "+
						"set PARAM_STATUS='1' "+
						"where PARAM_TYPE = ? ");
				condition.setString(0, inputVO.getSelectedParameter());
				dam.exeUpdate(condition);
			}
			else{
				//EleanorChang update
//				condition.setQueryString("delete FROM TBSYSPARAMETER B "+
//						"where B.PARAM_TYPE ='"+inputVO.getSelectedParameter()+"'");
				condition.setQueryString("delete FROM TBSYSPARAMETER "+
						"where PARAM_TYPE = ? ");
				condition.setString(0, inputVO.getSelectedParameter());
				dam.exeUpdate(condition);
				
			}
			int i_temp=0;
//			Iterator iter=inputVO.getCsvData().iterator();
//			while(iter.hasNext()){
			for (int i = 0; i < csvData.size(); i++) {
				String[] data = (String[]) csvData.get(i);
//				Object[] data=(Object[])iter.next();
				TBSYSPARAMETERPK pk=new TBSYSPARAMETERPK();
				pk.setPARAM_TYPE(inputVO.getSelectedParameter());
				pk.setPARAM_CODE(data[0]);
				if("1".equals(inputVO.getVarWorksType())){
					
					TBSYSPARAMETERVO parameterVO=(TBSYSPARAMETERVO)dam.findByPKey(TBSYSPARAMETERVO.TABLE_UID, pk);
					if(parameterVO!=null){
						parameterVO.setPARAM_ORDER(i_temp);
						parameterVO.setPARAM_NAME_EDIT(data[1]);
						parameterVO.setPARAM_DESC(data[2]);
						parameterVO.setPARAM_STATUS("2");
						dam.update(parameterVO);
					}
					else{
						TBSYSPARAMETERVO insertVO=new TBSYSPARAMETERVO();
						insertVO.setcomp_id(pk);
						insertVO.setPARAM_ORDER(i_temp);
						insertVO.setPARAM_NAME(data[1]);
						insertVO.setPARAM_NAME_EDIT(data[1]);
						if(data.length>2){
							insertVO.setPARAM_DESC(data[2]);
						}
						
						insertVO.setPARAM_STATUS("3");
						dam.create(insertVO);
					}
					
					//20100113新增 升 批次生效 所以有生效日
					String LOG_TYPE_ID="PARA_LOG";
					SimpleDateFormat sDateS = new SimpleDateFormat("yyyyMMdd");
					String WorksDate= sDateS.format(inputVO.getTipWorksDate());
//					LogUtil.writeLog(LOG_TYPE_ID,LogUtil.LEVEL_INFO, inputVO.getSelectedParameter(), inputVO.getSelectedParameterlabel(), data[0],data[1],WorksDate);
				}
				else{
					TBSYSPARAMETERVO insertVO=new TBSYSPARAMETERVO();
					insertVO.setcomp_id(pk);
					insertVO.setPARAM_ORDER(i_temp);
					insertVO.setPARAM_NAME(data[1]);
					insertVO.setPARAM_NAME_EDIT(data[1]);
					if(data.length>2){
						insertVO.setPARAM_DESC(data[2]);
					}
					
					insertVO.setPARAM_STATUS("0");
					dam.create(insertVO);
					
					//20100113新增 升 即時生效 所以無生效日
					String LOG_TYPE_ID="PARA_LOG";
//					LogUtil.writeLog(LOG_TYPE_ID,LogUtil.LEVEL_INFO, inputVO.getSelectedParameter(), inputVO.getSelectedParameterlabel(), data[0],data[1]);
				}
				i_temp++;
			}
			
		}
		else{
			if("TBFPS.SYS.RELCOEFFICIENT".equals(inputVO.getSelectedParameter())){
				int  i_temp = 0, j_temp=0, k_temp=0;
//				Iterator iter=inputVO.getCsvData().iterator();
//				if(iter.hasNext()){
//					iter.next();
//				}
//				while(iter.hasNext()){
				//FIRST LINE TO LAST LINE
				for (int i = 1; i < csvData.size(); i++) {
					String[] data = (String[]) csvData.get(i);
//					Object[] data=(Object[])iter.next();
					// CELL 2~25
					for(k_temp=0;k_temp<csvData.size()-1;k_temp++){
						if (data[k_temp+1] != null){
							TBSYSPARAMETERPK pk=new TBSYSPARAMETERPK();
							pk.setPARAM_TYPE(inputVO.getSelectedParameter());
							pk.setPARAM_CODE(convertNumber(j_temp+1)+convertNumber(k_temp+1));
							TBSYSPARAMETERVO parameterVO=(TBSYSPARAMETERVO)dam.findByPKey(TBSYSPARAMETERVO.TABLE_UID, pk);
							if("1".equals(inputVO.getVarWorksType())){
								//EleanorChang								
//								parameterVO.setPARAM_ORDER(i_temp);
//								//B.PARAM_NAME_EDIT = CSV的第(k_temp + 2)個欄位
//								parameterVO.setPARAM_NAME_EDIT(data[k_temp+1]);
//								parameterVO.setPARAM_STATUS("2");
								
								if(parameterVO!=null){
									parameterVO.setPARAM_ORDER(i_temp);
									parameterVO.setPARAM_NAME_EDIT(data[k_temp+1]);
									parameterVO.setPARAM_STATUS("2");
									dam.update(parameterVO);
								}
								else{
									TBSYSPARAMETERVO insertVO=new TBSYSPARAMETERVO();
									insertVO.setcomp_id(pk);
									insertVO.setPARAM_ORDER(i_temp);
									insertVO.setPARAM_NAME_EDIT(data[k_temp+1]);
									insertVO.setPARAM_DESC("");
									insertVO.setPARAM_STATUS("3");
									dam.create(insertVO);
								}
								String LOG_TYPE_ID="PARA_LOG";
								SimpleDateFormat sDateS = new SimpleDateFormat("yyyyMMdd");
								String WorksDate= sDateS.format(inputVO.getTipWorksDate());
//								LogUtil.writeLog(LOG_TYPE_ID,LogUtil.LEVEL_INFO, inputVO.getSelectedParameter(), inputVO.getSelectedParameterlabel(), convertNumber(j_temp+1)+convertNumber(k_temp+1),data[k_temp+1],WorksDate);
							}
							else{
//								parameterVO.setPARAM_ORDER(i_temp);
//								parameterVO.setPARAM_NAME(data[k_temp+1]);
//								//B.PARAM_NAME_EDIT = CSV的第(k_temp + 2)個欄位
//								parameterVO.setPARAM_NAME_EDIT(data[k_temp+1]);
								
								if(parameterVO!=null){
									parameterVO.setPARAM_ORDER(i_temp);
									parameterVO.setPARAM_NAME(data[k_temp+1]);
									parameterVO.setPARAM_NAME_EDIT(data[k_temp+1]);
									dam.update(parameterVO);
								}
								else{
									TBSYSPARAMETERVO insertVO=new TBSYSPARAMETERVO();
									insertVO.setcomp_id(pk);
									insertVO.setPARAM_ORDER(i_temp);
									insertVO.setPARAM_NAME(data[k_temp+1]);
									insertVO.setPARAM_NAME_EDIT(data[k_temp+1]);
									insertVO.setPARAM_DESC("");
									insertVO.setPARAM_STATUS("0");
									dam.create(insertVO);
								}
								String LOG_TYPE_ID="PARA_LOG";
//								LogUtil.writeLog(LOG_TYPE_ID,LogUtil.LEVEL_INFO, inputVO.getSelectedParameter(), inputVO.getSelectedParameterlabel(), convertNumber(j_temp+1)+convertNumber(k_temp+1),data[k_temp+1]);
								
								
							}
							//dam.update(parameterVO);
							i_temp++;
							
						}else{
							break;
							//exit for;
						}
						
					}
					j_temp++;
				}
				
			}
			
		}
		
		//EleanorChang add 2009/12/28
		if("0".equals(inputVO.getVarWorksType())){
			TBSYSMARQUEEVO marqueeVO=new TBSYSMARQUEEVO();
			marqueeVO.setSUBJECT("業務參數異動更新");
			String hostdate = (String) SysInfo.getInfoValue(SystemVariableConsts.HOSTDATE);
			String hostdate_2 = (String.valueOf((Integer.valueOf(hostdate.substring(0,4))-1911))+ "/" + hostdate.substring(4,6)+ "/" + hostdate.substring(6,8));
			String hosttime = (String) SysInfo.getInfoValue(SystemVariableConsts.HOSTTIME);
			String hosttime_2 = hosttime.substring(0, 2) + ":" + hosttime.substring(2, 4);
			marqueeVO.setCONTENT("FPS系統於(" + hostdate_2 + ")(" +
					hosttime_2 + ")更新參數﹐請重新登入系統﹐啟動新參數內容");
			Timestamp ts = new Timestamp(new Date().getTime());
			marqueeVO.setSTART_DT(ts);
			marqueeVO.setEND_DT(ts);
			marqueeVO.setTELLERID((String) SysInfo.getInfoValue(SystemVariableConsts.LOGINID));
			marqueeVO.setSUSPEND("N");
			marqueeVO.setNEW_MSG("Y");
			marqueeVO.setMSG_TYPE("S");

			
			dam.create(marqueeVO);
		}
		return inputVO;
	}
	
	private String convertNumber(int number){
		String s=String.valueOf(number);
		if(s.length()==1){
			s="0"+s;
		}
		return s;
	}
	private void createXML(String paramType) throws JBranchException{
		/*
		Element elmtRoot = null;
		Document docJDOM = null;
		Element elmtChapter = null;
		StringBuilder writeXmlPath = null;
		dam = this.getDataAccessManager();
		String sqlQuery = "select PARAM_TYPE,PARAM_CODE,PARAM_NAME from TBSYSPARAMETER where PARAM_TYPE='"+paramType+"'";

		QueryConditionIF queryConditionIF = dam.getQueryCondition(dam.QUERY_LANGUAGE_TYPE_SQL);
		queryConditionIF.setQueryString(sqlQuery);
		List<Map> dataList = dam.executeQuery(queryConditionIF);
		Iterator<Map> iter=dataList.iterator();
		elmtRoot=new Element("options");
		docJDOM=new Document(elmtRoot);
		String paramCode=null;
		String paramName=null;
		while(iter.hasNext()){
			Map data=iter.next();
			paramCode=(String)data.get("PARAM_CODE");
			paramName=(String)data.get("PARAM_NAME");
			elmtChapter=new Element("option");
			elmtChapter.setAttribute("data", paramCode);
		    elmtChapter.setAttribute("label", paramName);
		    elmtRoot.addContent(elmtChapter);
		    
		    
		}
		
		writeXmlPath = new StringBuilder();
		writeXmlPath.append(DataManager.getRealPath()).append("/combobox/").append(paramType).append(".xml");
		File file = new File(writeXmlPath.toString());
		
		
		if(file.exists()){
			file.delete();
		}
		XMLOutputter fmt = new XMLOutputter(Format.getPrettyFormat());
		  OutputStreamWriter  writer = new OutputStreamWriter(new FileOutputStream(writeXmlPath.toString()),"UTF-8"); 
	      fmt.output(docJDOM, writer);
	      
	      writer.flush();
	      writer.close();
		*/
//		System.out.println("paramType" + paramType);
		XmlInfo info=new XmlInfo();
		info.refreshXML(paramType);
		
	}
	//檢查傳入參數
	private void checkInput(){
	}
	
}
