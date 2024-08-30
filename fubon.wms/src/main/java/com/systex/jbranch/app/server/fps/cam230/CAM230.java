package com.systex.jbranch.app.server.fps.cam230;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.bth.ftp.BthFtpUtil;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.CAM997;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSFTPVO;
import com.systex.jbranch.platform.util.IPrimitiveMap;


/**
 * @author Ocean
 * @date 2016/05/27
 * 
 */
@Component("cam230")
@Scope("request")
public class CAM230 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	
	public void getCampList (Object body, IPrimitiveMap header) throws JBranchException, Exception {

		CAM230InputVO inputVO = (CAM230InputVO) body;
		CAM230OutputVO outputVO = new CAM230OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT SYS_HOST.HOSTID, SYS_HOST.IP, SYS_HOST.PORT, SYS_HOST.USERNAME, SYS_HOST.PASSWORD, SYS_FTP.FTPSETTINGID, SYS_FTP.SRCDIRECTORY, SYS_FTP.DESDIRECTORY ");
		sb.append("FROM TBSYSFTP SYS_FTP ");
		sb.append("LEFT JOIN TBSYSREMOTEHOST SYS_HOST ON SYS_FTP.HOSTID = SYS_HOST.HOSTID ");
		sb.append("WHERE SYS_FTP.FTPSETTINGID = 'CAM.CDS_UNICA' ");
		
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0) {
			BthFtpUtil ftpUtil = new BthFtpUtil((String) list.get(0).get("HOSTID"));
			ftpUtil.setRetryCount(0);
			ftpUtil.connect();
			
			TBSYSFTPVO vo = new TBSYSFTPVO();
			vo = (TBSYSFTPVO) dam.findByPKey(TBSYSFTPVO.TABLE_UID, (String) list.get(0).get("FTPSETTINGID"));
			
			List fileNameList = ftpUtil.listNames(vo.getSRCDIRECTORY(), Pattern.compile("T_SFA_.*"));
			List<Map<String, String>> fileList = new ArrayList<Map<String,String>>();
			for (int i = 0; i < fileNameList.size(); i++) {
				if (StringUtils.isNotBlank((String) fileNameList.get(i))) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("FILENAME", (String) fileNameList.get(i));
					
					fileList.add(map);
				}
			}
			
			outputVO.setFileList(fileList);
			
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			
			sb.append("SELECT PARAM_NAME ");
			sb.append("FROM TBSYSPARAMETER ");
			sb.append("WHERE PARAM_TYPE = 'CAM.ASSIGN_ROW' ");
			queryCondition.setQueryString(sb.toString());
			List<Map<String, Object>> asslist = dam.exeQuery(queryCondition);
			outputVO.setAssignRow(Integer.valueOf((String) asslist.get(0).get("PARAM_NAME")));
		}
		
		this.sendRtnObject(outputVO);
	}
	
	public void getAssignDtl (Object body, IPrimitiveMap header) throws JBranchException, FileNotFoundException, IOException, Exception {
		
		CAM230InputVO inputVO = (CAM230InputVO) body;
		CAM230OutputVO outputVO = new CAM230OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT SYS_HOST.HOSTID, SYS_HOST.IP, SYS_HOST.PORT, SYS_HOST.USERNAME, SYS_HOST.PASSWORD, SYS_FTP.FTPSETTINGID, SYS_FTP.SRCDIRECTORY, SYS_FTP.DESDIRECTORY ");
		sb.append("FROM TBSYSREMOTEHOST SYS_HOST ");
		sb.append("LEFT JOIN TBSYSFTP SYS_FTP ON SYS_FTP.HOSTID = SYS_HOST.HOSTID ");
		sb.append("WHERE SYS_FTP.FTPSETTINGID = 'CAM.CDS_UNICA' ");
		
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		BthFtpUtil ftpUtil = new BthFtpUtil((String) list.get(0).get("HOSTID"));
		ftpUtil.setRetryCount(0);
		ftpUtil.connect();
		
		TBSYSFTPVO vo = new TBSYSFTPVO();
		vo = (TBSYSFTPVO) dam.findByPKey(TBSYSFTPVO.TABLE_UID, (String) list.get(0).get("FTPSETTINGID"));
		
		List<String> lines = ftpUtil.readFtpFile(vo.getSRCDIRECTORY(), inputVO.getCampID()); //FileUtils.readLines(new File(vo.getDESDIRECTORY() + inputVO.getCampID()), "big5");
		List<Map<String, Object>> custList = new ArrayList<Map<String, Object>>();

		if (lines.size() > inputVO.getAssignRow()) {
			throw new JBranchException("該名單總數(" + lines.size() + "筆) > 最大試算名單數(" + inputVO.getAssignRow() + "筆)，無法試算。");
		} else {
			for(Integer i = 0; i < lines.size(); i++){
				Map<String, Object> custMap = new HashMap<String, Object>();
				
				String str[] = lines.get(i).split(",", -1);
				custMap.put("CUST_ID", str[0]);
				custMap.put("AO_CODE", "");
				custMap.put("START_DATE", str[1].substring(0, 8));
				custMap.put("END_DATE", str[2].substring(0, 8));
				
				
				custList.add(custMap);
			}
			CAM997 cam997 = new CAM997();
			Map<String, Integer> assignDtl = cam997.getAssignDtl(dam, custList, "", "");
			
			outputVO.setTotalNum(assignDtl.get("totalNum"));
			outputVO.setSuccessNum(assignDtl.get("successNum"));
			outputVO.setFailureNum(assignDtl.get("failureNum"));
		}
		
		this.sendRtnObject(outputVO);
	}
	
	public String base64Encode(String token) {
		
		byte[] encodedBytes = Base64.encodeBase64(token.getBytes());
		 
		return new String(encodedBytes, Charset.forName("UTF-8"));
	}

	public String base64Decode(String token) {
		
		byte[] decodedBytes = Base64.decodeBase64(token.getBytes());
		
		return new String(decodedBytes, Charset.forName("UTF-8"));
	}
	
}
