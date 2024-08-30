package com.systex.jbranch.app.server.fps.prd252;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.systex.jbranch.app.common.fps.table.TBPRD_BOND_RISK_FILEVO;
import com.systex.jbranch.app.server.fps.prd252.PRD252InputVO;
import com.systex.jbranch.app.server.fps.prd252.PRD252OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * prd252
 * 
 */
@Component("prd252")
@Scope("request")
public class PRD252 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PRD252.class);
	Timestamp currentTM = new Timestamp(System.currentTimeMillis());
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		PRD252InputVO inputVO = (PRD252InputVO) body;
		PRD252OutputVO return_VO = new PRD252OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT C.PRD_ID, C.RISK_FILE, F.BOND_CNAME, C.CREATETIME, C.CREATOR, C.LASTUPDATE, C.MODIFIER ");
		sql.append(" FROM TBPRD_BOND_RISK_FILE C ");
		sql.append(" LEFT JOIN TBPRD_BOND F ON F.PRD_ID = C.PRD_ID ");
		sql.append(" WHERE 1=1 ");
		
		if (StringUtils.isNotBlank(inputVO.getPrdId())) {
			sql.append(" AND C.PRD_ID = :PRD_ID ");
			queryCondition.setObject("PRD_ID", inputVO.getPrdId().toUpperCase());
		}	
		sql.append("ORDER BY C.PRD_ID");
		
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void upload(Object body, IPrimitiveMap header) throws JBranchException, Exception, SQLException, IOException {
		PRD252InputVO inputVO = (PRD252InputVO) body;
//		PRD252OutputVO return_VO = new PRD252OutputVO();
		dam = this.getDataAccessManager();
		String prdID = inputVO.getPrdNo();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT COUNT(*) FROM TBPRD_BOND WHERE PRD_ID = :PRD_ID ");
		if (StringUtils.isNotBlank(inputVO.getFileRealName())) {
			queryCondition.setObject("PRD_ID", prdID);
		}
		queryCondition.setQueryString(sb.toString());
		List<Map<String,Object>> count = dam.exeQuery(queryCondition);	
		BigDecimal cnt = (BigDecimal) count.get(0).get("COUNT(*)");
		int prd_cnt = cnt.intValue();

		if(prd_cnt >= 1){
			TBPRD_BOND_RISK_FILEVO fileVO = (TBPRD_BOND_RISK_FILEVO) getDataAccessManager().findByPKey(TBPRD_BOND_RISK_FILEVO.TABLE_UID, prdID);
			
			if(null == fileVO){
				//新增
				fileVO = new TBPRD_BOND_RISK_FILEVO();
				fileVO.setPRD_ID(prdID);
				String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				String joinedPath = new File(tempPath, inputVO.getFileName()).toString();
				Path path = Paths.get(joinedPath);
				byte[] data = Files.readAllBytes(path);
				fileVO.setRISK_FILE(ObjectUtil.byteArrToBlob(data));
					
				dam.create(fileVO);
				
			} else {
				//修改
				String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				String joinedPath = new File(tempPath, inputVO.getFileName()).toString();
				Path path = Paths.get(joinedPath);
				byte[] data = Files.readAllBytes(path);
				fileVO.setRISK_FILE(ObjectUtil.byteArrToBlob(data));
			
				dam.update(fileVO);
			}
			
		}else{
			throw new APException("上傳檔名需為債券代號");
		}
		
		this.sendRtnObject(null);
	}
	

	public void download(Object body, IPrimitiveMap header) throws Exception {
		try {
			PRD252InputVO inputVO = (PRD252InputVO) body;
			dam = this.getDataAccessManager();
			
			String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
			String fileName = inputVO.getPrdId()+".pdf";
			String uuid = UUID.randomUUID().toString();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT RISK_FILE FROM TBPRD_BOND_RISK_FILE where PRD_ID = :id ");
			queryCondition.setObject("id", inputVO.getPrdId());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			Blob blob = (Blob) list.get(0).get("RISK_FILE");
			int blobLength = (int) blob.length();  
			byte[] blobAsBytes = blob.getBytes(1, blobLength);
			
			File targetFile = new File(filePath, uuid);
			FileOutputStream fos = new FileOutputStream(targetFile);
		    fos.write(blobAsBytes);
		    fos.close();
		    notifyClientToDownloadFile("temp//"+uuid, fileName);
		} catch(Exception e){
			logger.debug(e.getMessage(),e);
		}
	}
	
	public void delete(Object body, IPrimitiveMap header) throws Exception {
		PRD252InputVO inputVO = (PRD252InputVO) body;
		String prd_id = inputVO.getPrdId();
		dam = this.getDataAccessManager();
		TBPRD_BOND_RISK_FILEVO fileVO = (TBPRD_BOND_RISK_FILEVO) getDataAccessManager().findByPKey(TBPRD_BOND_RISK_FILEVO.TABLE_UID, prd_id);
		if(fileVO != null)
			dam.delete(fileVO);
		else
			throw new APException("ehl_01_common_001");
		
		this.sendRtnObject(null);
	}
	
	private String checkIsNull(Map map, String key) {
		if(StringUtils.isNotBlank(ObjectUtils.toString(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
	
}