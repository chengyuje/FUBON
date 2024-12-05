package com.systex.jbranch.fubon.bth;

import com.systex.jbranch.app.common.fps.table.TBAPI_MPLUS_TRACEVO;
import com.systex.jbranch.fubon.commons.mplus.MPlusInputVO;
import com.systex.jbranch.fubon.commons.mplus.MPlusUtil;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository("btsqm410")
@Scope("prototype")
public class BTSQM410 extends BizLogic {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private DataAccessManager dam = null;

	public void pushAuthMessage(Object body, IPrimitiveMap<?> header) throws Exception {
		dam = this.getDataAccessManager();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

		//logger.info("M+ Log : " + sdf.format(new Date()) + " 發送  " + custID + " 之議價申請資訊給主管：" + empId);

		// 取得滿意度未處理資訊
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT OWNER_EMP_ID, PRIVILEGEID, COUNT(1) as CASE_COUNT ");
		sb.append("FROM ");
		sb.append("( ");
		sb.append("  SELECT DISTINCT A.CASE_NO, A.OWNER_EMP_ID, P.PRIVILEGEID ");
		sb.append("  FROM TBSQM_CSM_IMPROVE_MAST A ");
		sb.append("  left outer join tborg_member_role r on A.OWNER_EMP_ID = r.EMP_ID ");
		sb.append("  left join TBSYSSECUROLPRIASS p  on r.ROLE_ID=p.roleid ");
		sb.append("  WHERE A.DELETE_FLAG IS NULL AND A.HO_CHECK = 'Y' AND NVL(A.CASE_STATUS, ' ') <> 'N' ");
		sb.append("  AND A.CASE_NO IS NOT NULL AND A.OWNER_EMP_ID <> 'close' ");
		sb.append("  AND A.OWNER_EMP_ID NOT IN (SELECT EMP_ID FROM TBORG_AGENT WHERE AGENT_STATUS = 'S' AND SYSDATE BETWEEN START_DATE AND END_DATE) "); //排除休假中
		sb.append("  AND (P.PRIVILEGEID = '013' OR (P.PRIVILEGEID = '012' AND NVL(A.UHRM_YN, 'N') = 'N')) "); //處長或區長非私銀案件
		sb.append("UNION ALL ");
		sb.append("  SELECT DISTINCT A.CASE_NO, Ag.AGENT_ID, P.PRIVILEGEID ");
		sb.append("  FROM TBSQM_CSM_IMPROVE_MAST A  ");
		sb.append("  left outer join tborg_member_role r on A.OWNER_EMP_ID = r.EMP_ID ");
		sb.append("  left join TBSYSSECUROLPRIASS p  on r.ROLE_ID=p.roleid ");
		sb.append("  inner join TBORG_AGENT ag on a.OWNER_EMP_ID = ag.EMP_ID AND AGENT_STATUS = 'S' AND SYSDATE BETWEEN ag.START_DATE AND ag.END_DATE ");
		sb.append("  WHERE A.DELETE_FLAG IS NULL AND A.HO_CHECK = 'Y' AND NVL(A.CASE_STATUS, ' ') <> 'N' ");
		sb.append("  AND A.CASE_NO IS NOT NULL AND A.OWNER_EMP_ID <> 'close' ");
		sb.append("  AND (P.PRIVILEGEID = '013' OR (P.PRIVILEGEID = '012' AND NVL(A.UHRM_YN, 'N') = 'N')) "); //處長或區長非私銀案件
		sb.append(")TB GROUP BY OWNER_EMP_ID, PRIVILEGEID ");
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> mgrList = dam.exeQuery(queryCondition);

		//沒有資料則跳出
		if(CollectionUtils.isEmpty(mgrList))
			return;

		List<String> empList = new LinkedList<>();
		List<String> msgs = new LinkedList<>();

		for (Map<String, Object> map : mgrList) {
			StringBuffer msg = new StringBuffer();	//主管訊息
			String empstr = map.get("OWNER_EMP_ID") == null ? "" : map.get("OWNER_EMP_ID").toString();
			String privilegeid = map.get("PRIVILEGEID") == null ? "" : map.get("PRIVILEGEID").toString();
			String casecount = map.get("CASE_COUNT") == null ? "" : map.get("CASE_COUNT").toString();

			if("013".equals(privilegeid)) {
				//處主管
				msg.append("主管您好：\n");
				msg.append("【客戶服務滿意度調查】：\n");
				msg.append("  您轄下營運區現有客戶滿意度調查報告尚未批示，敬請於批示期限前完成，謝謝。\n\n");
				msg.append("請點【檢視詳情】批示");
			} else if("012".equals(privilegeid)) {
				//區督導
				msg.append("主管您好：\n");
				msg.append("【客戶服務滿意度調查】：\n");
				msg.append("  截至").append( sdf.format(new Date())).append("尚有").append(casecount);
				msg.append("件客戶滿意度調查報告尚未處理，敬請盡速完成報告覆核，以避免影響批示主管簽核時效，謝謝。\n\n");
				msg.append("請點【檢視詳情】覆核");
			}
			empList.add(empstr);
			msgs.add(msg.toString());

			MPlusInputVO inputVO = new MPlusInputVO();

			// FIXME: 以empId重DB中查出empPhones(目前table來源尚未確定)
		    // 若targetType=1,則必須上傳名單檔案。名單檔案格式為一行一個門號的txt檔
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("SELECT MPLUS_UID FROM TBAPI_MPLUS WHERE EMP_NUMBER = :empid ");
			queryCondition.setObject("empid", empstr);
			queryCondition.setQueryString(sb.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if(CollectionUtils.isEmpty(list)) continue;//查無MPLUS_UID資料

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
		    list_title.add(msg.toString());
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
	//	  	file_2.setFileCxt(bytes_2);
		  	file_2.setFileObj(jpg);
		  	inputVO.setIconFile1(file_2); // 第一組 info-push 圖檔 ,若 msgType=I 時必填, 須為 jpg 檔案

		  	// 從 TBSYSPARAMETER 中抓取 M_PLUS.URL (http://wmstest.taipeifubon.com.tw:9801/)
		  	XmlInfo xmlInfo = new XmlInfo();
			Map<String, String> m_plus_url = xmlInfo.doGetVariable("M_PLUS.URL", FormatHelper.FORMAT_3);

			String mPlusURL = m_plus_url.get("1");
//			String mPlusURL = "http://172.20.10.3:8080/";

		  	// 內容鏈結,若msgType=I,至少填入一組,須與Infopush內容數量對應,格式為正確的URL連結
		    JSONObject url = new JSONObject();
		    List<String> list_url = new ArrayList<String>();
		    list_url.add( mPlusURL + "SQM410Controller.html?loginEmpID="+empstr+"&privilegeID="+privilegeid);
		    logger.info("M+ Log : " + sdf.format(new Date()) + " 發送URL：" + list_url.get(0) );
		    url.put("url", list_url);
		    inputVO.setInfoUrl(url);
		    MPlusUtil mplusUtil = (MPlusUtil) PlatformContext.getBean("mplusutil");
			mplusUtil.send2MPlus(inputVO);
		}

		TBAPI_MPLUS_TRACEVO tracevo = new TBAPI_MPLUS_TRACEVO();
		tracevo.setEMP_ID(StringUtils.join(empList, ","));
		tracevo.setCONTENT(StringUtils.join(msgs, "\n\\\n"));
		dam.create(tracevo);
	}

}
