package com.systex.jbranch.app.server.fps.crm451;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.imageio.ImageIO;

import com.systex.jbranch.app.common.fps.table.TBAPI_MPLUS_TRACEVO;
import com.systex.jbranch.app.server.fps.oth001.OTH001;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.icu.text.NumberFormat;
import com.systex.jbranch.app.server.fps.crm421.CRM421;
import com.systex.jbranch.app.server.fps.crm421.CRM421InputVO;
import com.systex.jbranch.app.server.fps.crm421.CRM421OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.mplus.MPlusInputVO;
import com.systex.jbranch.fubon.commons.mplus.MPlusUtil;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;

import static com.systex.jbranch.fubon.jlb.DataFormat.getNameForHighRisk;

@Component("crm451")
@Scope("request")
public class CRM451 extends FubonWmsBizLogic {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private DataAccessManager dam = null;

	public void pushAuthMessage(String empId, String type, String custID, String creator, String seqNum, String createrType, String applyBrh) throws JBranchException, IOException {
		if (PlatformContext.getBean(OTH001.class).skipMPlusBargainAuth())
			return;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//		logger.info("M+ Log : " + sdf.format(new Date()) + " 發送  " + custID + " 之議價申請資訊給主管：" + empId);

		dam = this.getDataAccessManager();
		if(StringUtils.isBlank(seqNum)) {
			seqNum = "";
		}
		// 取得理專資訊
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT BRANCH_NBR, BRANCH_NAME, AO_CODE, EMP_NAME, EMP_ID ");
		sb.append("FROM VWORG_BRANCH_EMP_DETAIL_INFO WHERE EMP_ID = :emp_id ");

		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("emp_id", creator);

		List<Map<String, Object>> creatorList = dam.exeQuery(queryCondition);

		// 取得申請分行資訊
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT DEPT_ID, DEPT_NAME ");
		sb.append("FROM TBORG_DEFN ");
		sb.append("WHERE DEPT_ID = :deptID ");
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("deptID", applyBrh);

		List<Map<String, Object>> applyBrhList = dam.exeQuery(queryCondition);

		// 取得客戶資訊
		CRM421OutputVO outputVO_crm421 = new CRM421OutputVO();
		CRM421InputVO inputVO_crm421 = new CRM421InputVO();
		inputVO_crm421.setCust_id(custID);

		CRM421 crm421 = (CRM421) PlatformContext.getBean("crm421");
		outputVO_crm421 = crm421.getCustDtl(inputVO_crm421);

		List<Map<String, Object>> custDTL = outputVO_crm421.getCustList();
		// 文訊息內容,當msgType=I時為必填,支援多組訊息,順序按Array中之排序
		StringBuffer cust_sb = new StringBuffer();
		cust_sb.append((StringUtils.equals("period", type) ? "期間議價授權通知" : "單次議價授權通知")).append("\n");
		cust_sb.append("\n");
		if (custDTL.size() > 0 && applyBrhList.size() > 0) {
			String custName = getNameForHighRisk(StringUtils.defaultString((String) custDTL.get(0).get("CUST_NAME")));
			cust_sb.append("客戶姓名　　：").append(custName).append("\n");
			cust_sb.append("貢獻度等級　：").append(custDTL.get(0).get("CON_DEGREE_NAME")).append("\n");
			cust_sb.append("理財會員等級：").append(custDTL.get(0).get("VIP_DEGREE_NAME")).append("\n");
			if(StringUtils.isNotBlank(ObjectUtils.toString(custDTL.get(0).get("Y_PROFEE")))){
				double y_profee = Double.parseDouble(custDTL.get(0).get("Y_PROFEE").toString());
				cust_sb.append("近一年手收　：").append(NumberFormat.getNumberInstance(Locale.US).format(y_profee)).append("\n");
			}else{
				cust_sb.append("近一年手收　：0").append("\n");
			}
			if(StringUtils.isNotBlank(ObjectUtils.toString(custDTL.get(0).get("AUM_AMT")))){
				double aum_amt = Double.parseDouble(custDTL.get(0).get("AUM_AMT").toString());
				cust_sb.append("前日ＡＵＭ　：").append(NumberFormat.getNumberInstance(Locale.US).format(aum_amt));
			}else{
				cust_sb.append("前日ＡＵＭ　： 0");
			}
		}
		cust_sb.append("\n");
		cust_sb.append("申請分行　　：").append(applyBrhList.get(0).get("DEPT_ID")).append(" ").append(applyBrhList.get(0).get("DEPT_NAME"));
		cust_sb.append("\n");
		Object empName = getNameForHighRisk(StringUtils.defaultString((String) creatorList.get(0).get("EMP_NAME")));
		switch (createrType) {
			case "0" :
				cust_sb.append("申請理專　　：").append(creatorList.get(0).get("AO_CODE")).append(" ").append(empName);
				break;
			case "9" :
				cust_sb.append("申請理專　　：").append(creatorList.get(0).get("EMP_ID")).append(" ").append(empName);
				break;
		}

		cust_sb.append("\n");

		cust_sb.append("欲進行授權請點選下方檢視詳情。");

		String [] empArray = empId.split(",");
		List<String> tempArray = new ArrayList<String>();	//暫存覆核主管EMPID及代理人EMPID

		//只要是代理狀態存在，m+就不通知被代理人 20170921
		for(String empstr : empArray){

			//取代理人
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("SELECT AGT.AGENT_ID FROM TBORG_AGENT AGT WHERE 1=1  ");
			sb.append("AND AGT.EMP_ID = :empID AND AGT.AGENT_STATUS = 'S' ");

			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("empID", empstr);

			List<Map<String, Object>> agtList = dam.exeQuery(queryCondition);

			if(agtList.size() > 0){
				for(int a = 0; a < agtList.size(); a++){
					tempArray.add(agtList.get(a).get("AGENT_ID").toString()); //覆核主管的代理人EMPID
				}
			}else {
				tempArray.add(empstr);	//覆核主管EMPID
			}

		}

		// M+留軌
		TBAPI_MPLUS_TRACEVO tracevo = new TBAPI_MPLUS_TRACEVO();
		tracevo.setCUST_ID(custID);
		tracevo.setEMP_ID(StringUtils.join(tempArray, ","));
		tracevo.setCONTENT(cust_sb.toString());
		dam.create(tracevo);

		for(String empstr : tempArray) {

//			logger.info("M+ Log : " + sdf.format(new Date()) + " 發送  " + custID + " 之議價申請資訊給主管：" + empstr);
			MPlusInputVO inputVO = new MPlusInputVO();

			// FIXME: 以empId重DB中查出empPhones(目前table來源尚未確定)
	        // 若targetType=1,則必須上傳名單檔案。名單檔案格式為一行一個門號的txt檔
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("SELECT MPLUS_UID FROM TBAPI_MPLUS WHERE EMP_NUMBER = :empid ");
			queryCondition.setObject("empid", empstr);
			queryCondition.setQueryString(sb.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if(list.size() == 0)
				continue;

			inputVO.setTargetType("5"); // 發送目標型態 0: 全員發送 1: 指定名單 2: 群組 3: 部門
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(ObjectUtils.toString(list.get(0).get("MPLUS_UID")).getBytes());
			byte[] bytes_1 = baos.toByteArray();
	        MPlusInputVO.BinaryFile file_1 = inputVO.new BinaryFile();
	        file_1.setFileName("target.txt");
	        file_1.setFileCxt(bytes_1);
	        inputVO.setTarget(file_1); // 若 targetType=1,則必須上傳名單檔案。名單檔案格式為一行一個門號的 txt 檔

	        inputVO.setMsgType("I"); // 訊息型態 T: 文字 P: 圖片 I: InfoPush
			inputVO.setInfopushCount("1"); //InfoPush 內容個數；若 msgType=I 必填
			inputVO.setTemplateType("1"); //手機上訊息呈現的版型 1:版型一(內嵌中繼頁, 連結另開網 頁) 2: 版型二(另開網頁),若 msgType=I,則必填

			// 文訊息內容,當msgType=I時為必填,支援多組訊息,順序按Array中之排序
			JSONObject title = new JSONObject();
	        List<String> list_title = new ArrayList<String>();
	        list_title.add(cust_sb.toString());

	        title.put("title", list_title);
	        inputVO.setHeadline(title);

	        //第一組 info-push圖檔,若msgType=I時必填,須為jpg檔案
	  		File jpg = new File(DataManager.getRealPath() + "/assets/images/logo_01.jpg");
	  		BufferedImage bufferedImage = ImageIO.read(jpg);
	  		ByteArrayOutputStream img = new ByteArrayOutputStream();
	  		ImageIO.write(bufferedImage, "jpg", img);
	  		img.flush();
	  		byte[] bytes_2 = img.toByteArray();
	  		MPlusInputVO.BinaryFile file_2 = inputVO.new BinaryFile();
	  		file_2.setFileName("logo_01.jpg");
//	  		file_2.setFileCxt(bytes_2);
	  		file_2.setFileObj(jpg);
	  		inputVO.setIconFile1(file_2); // 第一組 info-push 圖檔 ,若 msgType=I 時必填, 須為 jpg 檔案

	  		// 從 TBSYSPARAMETER 中抓取 M_PLUS.URL (http://203.74.183.85:9801/)
	  		XmlInfo xmlInfo = new XmlInfo();
			Map<String, String> m_plus_url = xmlInfo.doGetVariable("M_PLUS.URL", FormatHelper.FORMAT_3);

			String mPlusURL = m_plus_url.get("1");

	  		// 內容鏈結,若msgType=I,至少填入一組,須與Infopush內容數量對應,格式為正確的URL連結
	        JSONObject url = new JSONObject();
	        List<String> list_url = new ArrayList<String>();
	        list_url.add( mPlusURL + "CRM452.html?empID=" + empstr + "&type=" + type + "&custID=" + custID + (StringUtils.isBlank(seqNum) ? "" : ("&seqNum=" + seqNum) ) + "&time=" + new Date().getTime());
	        logger.info("M+ Log : " + sdf.format(new Date()) + " 發送URL：" + list_url.get(0) );
	        url.put("url", list_url);
	        inputVO.setInfoUrl(url);
	        MPlusUtil mplusUtil = (MPlusUtil) PlatformContext.getBean("mplusutil");
			mplusUtil.send2MPlus(inputVO);

		}
	}

}
