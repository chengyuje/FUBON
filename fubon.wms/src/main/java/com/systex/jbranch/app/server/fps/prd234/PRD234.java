package com.systex.jbranch.app.server.fps.prd234;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import javax.sql.rowset.serial.SerialException;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;








import com.systex.jbranch.app.common.fps.table.TBPRD_FUND_CSS_FILEVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_FILE_DETAILVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_FILE_MAINVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_PRD_LINKPK;
import com.systex.jbranch.app.common.fps.table.TBSYS_PRD_LINKVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_PRD_SHARED_LINKPK;
import com.systex.jbranch.app.common.fps.table.TBSYS_PRD_SHARED_LINKVO;
import com.systex.jbranch.app.server.fps.cus130.CUS130InputVO;
import com.systex.jbranch.app.server.fps.prd220.PRD220InputVO;
import com.systex.jbranch.app.server.fps.prd234.PRD234InputVO;
import com.systex.jbranch.app.server.fps.prd234.PRD234OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * prd234
 * 
 * @author moron
 * @date 2016/09/1
 * @spec null
 */
@Component("prd234")
@Scope("request")
public class PRD234 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PRD234.class);
	Timestamp currentTM = new Timestamp(System.currentTimeMillis());
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		PRD234InputVO inputVO = (PRD234InputVO) body;
		PRD234OutputVO return_VO = new PRD234OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT C.PRD_ID, C.CSS_FILE, F.FUND_CNAME, C.CREATETIME,C.CREATOR,C.LASTUPDATE,C.MODIFIER ");
		sql.append(" FROM TBPRD_FUND_CSS_FILE C ");
		sql.append(" LEFT JOIN TBPRD_FUND F ON F.PRD_ID = C.PRD_ID ");
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
		PRD234InputVO inputVO = (PRD234InputVO) body;
//		PRD234OutputVO return_VO = new PRD234OutputVO();
		dam = this.getDataAccessManager();
		String prd_name = inputVO.getPrdNo();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT COUNT(*) FROM TBPRD_FUND WHERE PRD_ID = :PRD_ID ");
		if (StringUtils.isNotBlank(inputVO.getFileRealName())) {
			queryCondition.setObject("PRD_ID", prd_name);
		}
		queryCondition.setQueryString(sb.toString());
		List<Map<String,Object>> count = dam.exeQuery(queryCondition);	
		BigDecimal cnt = (BigDecimal) count.get(0).get("COUNT(*)");
		int prd_cnt = cnt.intValue();

		if(prd_cnt >= 1){
			TBPRD_FUND_CSS_FILEVO fileVO = (TBPRD_FUND_CSS_FILEVO) getDataAccessManager().findByPKey(TBPRD_FUND_CSS_FILEVO.TABLE_UID, prd_name);
			
			if(null == fileVO){
				//新增
				fileVO = new TBPRD_FUND_CSS_FILEVO();
				fileVO.setPRD_ID(prd_name);
				String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				String joinedPath = new File(tempPath, inputVO.getFileName()).toString();
				Path path = Paths.get(joinedPath);
				byte[] data = Files.readAllBytes(path);
				fileVO.setCSS_FILE(ObjectUtil.byteArrToBlob(data));
					
				dam.create(fileVO);
				
			} else {
				//修改
				String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				String joinedPath = new File(tempPath, inputVO.getFileName()).toString();
				Path path = Paths.get(joinedPath);
				byte[] data = Files.readAllBytes(path);
				fileVO.setCSS_FILE(ObjectUtil.byteArrToBlob(data));
			
				dam.update(fileVO);
			}
			
		}else{
			throw new APException("上傳檔名須為基金代號");
		}
		
		this.sendRtnObject(null);
	}
	

	public void download(Object body, IPrimitiveMap header) throws Exception {
		try {
			PRD234InputVO inputVO = (PRD234InputVO) body;
			dam = this.getDataAccessManager();
			
			String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
			String fileName = inputVO.getPrdId()+".pdf";
			String uuid = UUID.randomUUID().toString();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT CSS_FILE FROM TBPRD_FUND_CSS_FILE where PRD_ID = :id ");
			queryCondition.setObject("id", inputVO.getPrdId());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			Blob blob = (Blob) list.get(0).get("CSS_FILE");
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
		PRD234InputVO inputVO = (PRD234InputVO) body;
		String prd_id = inputVO.getPrdId();
		dam = this.getDataAccessManager();
		TBPRD_FUND_CSS_FILEVO fileVO = (TBPRD_FUND_CSS_FILEVO) getDataAccessManager().findByPKey(TBPRD_FUND_CSS_FILEVO.TABLE_UID, prd_id);
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