package com.systex.jbranch.app.server.fps.mgm111;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO;
import com.systex.jbranch.app.server.fps.mgm110.MGM110OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author Carley
 * @date 2018/03/01
 * 
 */
@Component("mgm111")
@Scope("request")
public class MGM111 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	
	//查詢活動詳情
	public void inquire (Object body, IPrimitiveMap header) throws JBranchException {
		MGM110InputVO inputVO = (MGM110InputVO) body;
		MGM110OutputVO outputVO = new MGM110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		//查詢活動資訊
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM TBMGM_ACTIVITY_MAIN WHERE ACT_SEQ = :act_seq ");				
		queryCondition.setObject("act_seq", inputVO.getAct_seq());
		queryCondition.setQueryString(sb.toString());		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		//查詢活動附件
		sb = new StringBuffer();
		sb.append("SELECT * FROM TBMGM_ACTIVITY_FILE WHERE ACT_SEQ = :act_seq ");		
		queryCondition.setQueryString(sb.toString());		
		outputVO.setFileList(dam.exeQuery(queryCondition));
		
		//查詢活動適用贈品
		sb = new StringBuffer();
		sb.append("SELECT ACT.*, GIFT.GIFT_NAME FROM ");
		sb.append("(SELECT * FROM TBMGM_ACTIVITY_GIFT WHERE ACT_SEQ = :act_seq ) ACT ");
		sb.append("LEFT JOIN TBMGM_GIFT_INFO GIFT ON ACT.GIFT_SEQ = GIFT.GIFT_SEQ ");
		queryCondition.setQueryString(sb.toString());		
		outputVO.setGiftList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	public void getFileView(Object body, IPrimitiveMap header) throws Exception {
		MGM110InputVO inputVO = (MGM110InputVO) body;
		MGM110OutputVO outputVO = new MGM110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ACT_FILE_NAME, ACT_FILE FROM TBMGM_ACTIVITY_FILE WHERE SEQ = :seq ");
		queryCondition.setObject("seq", inputVO.getSeq());
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String fileName = (String) list.get(0).get("ACT_FILE_NAME");
		int index = fileName.lastIndexOf(".");
		String data_name = fileName.substring(index); 
		
		String uuid = UUID.randomUUID().toString() + data_name;
		Blob blob = (Blob) list.get(0).get("ACT_FILE");
		int blobLength = (int) blob.length();  
		byte[] blobAsBytes = blob.getBytes(1, blobLength);
		
		File targetFile = new File(filePath, uuid);
		FileOutputStream fos = new FileOutputStream(targetFile);
	    fos.write(blobAsBytes);
	    fos.close();
//	    this.notifyClientToDownloadFile("temp//"+uuid, fileName);
	    outputVO.setPdfUrl("temp//"+uuid);
	    this.sendRtnObject(outputVO);
	}
	
}