package com.systex.jbranch.app.server.fps.mgm310;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBMGM_GIFT_IMGVO;
import com.systex.jbranch.app.common.fps.table.TBMGM_GIFT_INFOVO;
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
 * @author Carley
 * @date 2018/02/26
 * 
 */
@Component("mgm310")
@Scope("request")
public class MGM310 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	
	//檢核贈品代碼是否重複輸入
	public void checkGiftSeq (Object body, IPrimitiveMap header) throws JBranchException {
		MGM310InputVO inputVO = (MGM310InputVO) body;
		MGM310OutputVO outputVO = new MGM310OutputVO();
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		dam = this.getDataAccessManager();
		
		String gift_seq = inputVO.getGift_seq();
		if (StringUtils.isNotBlank(gift_seq)) {
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			
			sb.append("SELECT * FROM TBMGM_GIFT_INFO WHERE GIFT_SEQ = :gift_seq ");
			
			queryCondition.setObject("gift_seq", gift_seq);
			queryCondition.setQueryString(sb.toString());
			resultList = dam.exeQuery(queryCondition);
		}
		outputVO.setResultList(resultList);
		this.sendRtnObject(outputVO);
	}
	
	//取得贈品代碼
	public void getGiftSeq (Object body, IPrimitiveMap header) throws JBranchException {
		MGM310OutputVO outputVO = new MGM310OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT GIFT_SEQ, GIFT_NAME FROM TBMGM_GIFT_INFO WHERE (DELETE_YN <> 'Y' OR DELETE_YN IS NULL) ");
		
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	public void inquire (Object body, IPrimitiveMap header) throws JBranchException {
		MGM310InputVO inputVO = (MGM310InputVO) body;
		MGM310OutputVO outputVO = new MGM310OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT GIFT.GIFT_SEQ, GIFT.GIFT_NAME, GIFT.GIFT_KIND, GIFT.GIFT_COSTS, ");
		sb.append("NVL(GIFT.GIFT_AMOUNT, 0) AS GIFT_AMOUNT, NVL(GIFT.GIFT_TAKEN, 0) AS GIFT_TAKEN, ");
		sb.append("GIFT.GIFT_AMOUNT - NVL(GIFT.GIFT_TAKEN, 0) AS GIFT_REMAINING, ");
		sb.append("ORG.EMP_NAME, IMG.GIFT_PHOTO_NAME FROM TBMGM_GIFT_INFO GIFT ");
		sb.append("LEFT JOIN TBORG_MEMBER ORG ON GIFT.MODIFIER = ORG.EMP_ID ");
		sb.append("LEFT JOIN TBMGM_GIFT_IMG IMG ON GIFT.GIFT_SEQ = IMG.GIFT_SEQ ");
		sb.append("WHERE (GIFT.DELETE_YN <> 'Y' OR GIFT.DELETE_YN IS NULL) ");
		
		if (StringUtils.isNotBlank(inputVO.getGift_seq())) {
			sb.append("AND GIFT.GIFT_SEQ = :gift_seq ");
			queryCondition.setObject("gift_seq", inputVO.getGift_seq());
		}
		
		queryCondition.setQueryString(sb.toString());
		outputVO.setResultList(dam.exeQuery(queryCondition));
		this.sendRtnObject(outputVO);
	}
	
	public void save (Object body, IPrimitiveMap header) throws Exception {
		MGM310InputVO inputVO = (MGM310InputVO) body;
		String gift_seq = inputVO.getGift_seq();
		
		if (StringUtils.isNotBlank(gift_seq)) {
			dam = this.getDataAccessManager();			
			TBMGM_GIFT_INFOVO giftVO = (TBMGM_GIFT_INFOVO) getDataAccessManager().findByPKey(TBMGM_GIFT_INFOVO.TABLE_UID, gift_seq);
			
			if(null == giftVO){
				//新增
				giftVO = new TBMGM_GIFT_INFOVO();
				giftVO.setGIFT_SEQ(inputVO.getGift_seq());
				giftVO.setGIFT_INTRODUCTION(inputVO.getGift_introduction());
				giftVO.setGIFT_NAME(inputVO.getGift_name());
				giftVO.setGIFT_KIND(inputVO.getGift_kind());
				giftVO.setGIFT_COSTS(getBigDecimal(inputVO.getGift_costs()));
				giftVO.setGIFT_AMOUNT(getBigDecimal(inputVO.getGift_amount()));
				giftVO.setGIFT_TAKEN(getBigDecimal("0"));
				
				dam.create(giftVO);
				
				if (!StringUtils.isBlank(inputVO.getPictureName())) {
					String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
					String joinedPath = new File(tempPath, inputVO.getPictureName()).toString();
					Path path = Paths.get(joinedPath);
					byte[] data = Files.readAllBytes(path);
					
					TBMGM_GIFT_IMGVO imgVO = new TBMGM_GIFT_IMGVO();
					imgVO.setGIFT_SEQ(inputVO.getGift_seq());
					imgVO.setGIFT_PHOTO_NAME(inputVO.getRealpictureName());
					imgVO.setGIFT_PHOTO(ObjectUtil.byteArrToBlob(data));
						
					dam.create(imgVO);
				}
			} else {
				//修改
				giftVO.setGIFT_INTRODUCTION(inputVO.getGift_introduction());
				giftVO.setGIFT_NAME(inputVO.getGift_name());
				giftVO.setGIFT_KIND(inputVO.getGift_kind());
				giftVO.setGIFT_COSTS(getBigDecimal(inputVO.getGift_costs()));
				giftVO.setGIFT_AMOUNT(getBigDecimal(inputVO.getGift_amount()));
				
				dam.update(giftVO);
				
				if (!StringUtils.isBlank(inputVO.getPictureName())) {
					String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
					String joinedPath = new File(tempPath, inputVO.getPictureName()).toString();
					Path path = Paths.get(joinedPath);
					byte[] data = Files.readAllBytes(path);
					
					TBMGM_GIFT_IMGVO imgVO = (TBMGM_GIFT_IMGVO) getDataAccessManager().findByPKey(TBMGM_GIFT_IMGVO.TABLE_UID, gift_seq);
					if(null == imgVO){
						imgVO = new TBMGM_GIFT_IMGVO();
						imgVO.setGIFT_SEQ(inputVO.getGift_seq());
						imgVO.setGIFT_PHOTO_NAME(inputVO.getRealpictureName());
						imgVO.setGIFT_PHOTO(ObjectUtil.byteArrToBlob(data));
						dam.create(imgVO);
						
					} else {
						imgVO.setGIFT_PHOTO_NAME(inputVO.getRealpictureName());
						imgVO.setGIFT_PHOTO(ObjectUtil.byteArrToBlob(data));
						dam.update(imgVO);
					}
				}
			}
		} else {
			throw new APException("ehl_01_common_022");		//欄位檢核錯誤：*為必要輸入欄位,請輸入後重試
		}
		
		this.sendRtnObject(null);
	}
	
	//轉Decimal
	public BigDecimal getBigDecimal( Object value ) {		
        BigDecimal ret = null;
        if(value != null) {
            if(value instanceof BigDecimal) {
                ret = (BigDecimal) value;
            } else if(value instanceof String) {
                ret = new BigDecimal((String) value);
            } else if(value instanceof BigInteger) {
                ret = new BigDecimal((BigInteger) value);
            } else if(value instanceof Number) {
                ret = new BigDecimal(((Number)value).doubleValue());
            } else {
                throw new ClassCastException("Not possible to coerce ["+value+"] from class "+value.getClass()+" into a BigDecimal.");
            }
        }
        return ret;
    }
	
	public void getImgView(Object body, IPrimitiveMap header) throws Exception {
		MGM310InputVO inputVO = (MGM310InputVO) body;
		MGM310OutputVO outputVO = new MGM310OutputVO();
		
		if (StringUtils.isNotBlank(inputVO.getGift_seq())) {
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT GIFT_PHOTO_NAME, GIFT_PHOTO FROM TBMGM_GIFT_IMG WHERE GIFT_SEQ = :gift_seq ");
			queryCondition.setObject("gift_seq", inputVO.getGift_seq());
			queryCondition.setQueryString(sb.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			
			String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
			String fileName = (String) list.get(0).get("GIFT_PHOTO_NAME");
			String uuid = UUID.randomUUID().toString();
			Blob blob = (Blob) list.get(0).get("GIFT_PHOTO");
			int blobLength = (int) blob.length();  
			byte[] blobAsBytes = blob.getBytes(1, blobLength);
			
			File targetFile = new File(filePath, uuid);
			FileOutputStream fos = new FileOutputStream(targetFile);
		    fos.write(blobAsBytes);
		    fos.close();
//		    this.notifyClientToDownloadFile("temp//"+uuid, fileName);
		    outputVO.setPdfUrl("temp//"+uuid);
		    this.sendRtnObject(outputVO);
		}
	}
	
	//刪除
	public void delete(Object body, IPrimitiveMap header) throws JBranchException {
		MGM310InputVO inputVO = (MGM310InputVO) body;
		String gift_seq = inputVO.getGift_seq();
		
		if (StringUtils.isNotBlank(gift_seq)) {
			TBMGM_GIFT_INFOVO giftVO = (TBMGM_GIFT_INFOVO) getDataAccessManager().findByPKey(TBMGM_GIFT_INFOVO.TABLE_UID, gift_seq);
			if(null != giftVO){
				dam = this.getDataAccessManager();
				giftVO.setDELETE_YN("Y");	//是否已刪除   Y：已刪除
				dam.update(giftVO);
				
			} else {
				throw new APException("系統發生錯誤請洽系統管理員。");
			}
		} else {
			throw new APException("系統發生錯誤請洽系統管理員。");
		}
		this.sendRtnObject(null);
	}
}