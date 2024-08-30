package com.systex.jbranch.app.server.fps.cam140;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.util.TextUtils;
import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_CAMPAIGNPK;
import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_CAMPAIGNVO;
import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_CAMP_DOC_MAPPPK;
import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_CAMP_DOC_MAPPVO;
import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_CAMP_RESPONSEPK;
import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_CAMP_RESPONSEVO;
import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_LEADS_IMPVO;
import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_PARAMETERVO;
import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_PARA_DOC_MAPPPK;
import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_PARA_DOC_MAPPVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_MASTVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_FILE_DETAILVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_FILE_MAINVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_QST_QUESTIONVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_QUESTIONNAIREPK;
import com.systex.jbranch.app.common.fps.table.TBSYS_QUESTIONNAIREVO;
import com.systex.jbranch.fubon.bth.GenFileTools;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.CAM996;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * CAM140 手動匯入名單
 * 
 * @author Ocean
 * @date 20160509
 * @spec 
 */
@Component("cam140")
@Scope("request")
public class CAM140 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	Logger logger = LoggerFactory.getLogger(CAM140.class);
	
	public void execBatch(Object body, IPrimitiveMap header) throws Exception {
		CAM140InputVO inputVO = (CAM140InputVO) body;
		dam = this.getDataAccessManager();
		
		CAM996 cam996 = new CAM996();
		BigDecimal seqNo = cam996.getCampaignSEQ(dam);
		
		//====儲存參考文件
		String leadPara2 = "";
		for (int i = 0; i < inputVO.getFileName().size(); i++) {
			// 2017/4/25 add other type
			if("1".equals(ObjectUtils.toString(inputVO.getFileName().get(i).get("TYPE")))) {
				String SN = getFileSN();
				byte[] data = Files.readAllBytes(Paths.get(new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName().get(i).get("OTHER")).toString()));
				
				TBSYS_FILE_MAINVO fvo = new TBSYS_FILE_MAINVO();
				fvo.setDOC_ID(SN);
				fvo.setDOC_NAME(inputVO.getFileName().get(i).get("DOC_NAME"));
				fvo.setSUBSYSTEM_TYPE("CAM");
				fvo.setDOC_TYPE("01");
				dam.create(fvo);
				
				TBSYS_FILE_DETAILVO dvo = new TBSYS_FILE_DETAILVO();
				dvo.setDOC_ID(SN);
				dvo.setDOC_VERSION_STATUS("2");
				dvo.setDOC_FILE_TYPE("D");
				dvo.setDOC_FILE(ObjectUtil.byteArrToBlob(data));
				dam.create(dvo);
				
				TBCAM_SFA_CAMP_DOC_MAPPVO mappVo = new TBCAM_SFA_CAMP_DOC_MAPPVO();
				TBCAM_SFA_CAMP_DOC_MAPPPK mappPk = new TBCAM_SFA_CAMP_DOC_MAPPPK();
				mappPk.setCAMPAIGN_ID(inputVO.getCamp_id());
				mappPk.setSTEP_ID("00001");
				mappPk.setSFA_DOC_ID(SN);
				mappVo.setcomp_id(mappPk);
				dam.create(mappVo);
				
				leadPara2 = "Y"; // add by ocean 2016-10-27 for 共用
			}
			// web
			else if("2".equals(ObjectUtils.toString(inputVO.getFileName().get(i).get("TYPE")))) {
				String SN = getFileSN();
				
				TBSYS_FILE_MAINVO fvo = new TBSYS_FILE_MAINVO();
				fvo.setDOC_ID(SN);
				fvo.setDOC_NAME(inputVO.getFileName().get(i).get("DOC_NAME"));
				fvo.setSUBSYSTEM_TYPE("CAM");
				fvo.setDOC_TYPE("01");
				dam.create(fvo);
				
				TBSYS_FILE_DETAILVO dvo = new TBSYS_FILE_DETAILVO();
				dvo.setDOC_ID(SN);
				dvo.setDOC_VERSION_STATUS("2");
				dvo.setDOC_FILE_TYPE("U");
				if(inputVO.getFileName().get(i).get("DOC_NAME").indexOf("http://") == -1 && inputVO.getFileName().get(i).get("DOC_NAME").indexOf("https://") == -1)
					dvo.setDOC_URL("http://"+inputVO.getFileName().get(i).get("DOC_NAME"));
				else
					dvo.setDOC_URL(inputVO.getFileName().get(i).get("DOC_NAME"));
				dam.create(dvo);
				
				TBCAM_SFA_CAMP_DOC_MAPPVO mappVo = new TBCAM_SFA_CAMP_DOC_MAPPVO();
				TBCAM_SFA_CAMP_DOC_MAPPPK mappPk = new TBCAM_SFA_CAMP_DOC_MAPPPK();
				mappPk.setCAMPAIGN_ID(inputVO.getCamp_id());
				mappPk.setSTEP_ID("00001");
				mappPk.setSFA_DOC_ID(SN);
				mappVo.setcomp_id(mappPk);
				dam.create(mappVo);
				
				leadPara2 = "Y"; // add by ocean 2016-10-27 for 共用
			}
			// pick
			else if("3".equals(ObjectUtils.toString(inputVO.getFileName().get(i).get("TYPE")))) {
				TBCAM_SFA_CAMP_DOC_MAPPVO mappVo = new TBCAM_SFA_CAMP_DOC_MAPPVO();
				TBCAM_SFA_CAMP_DOC_MAPPPK mappPk = new TBCAM_SFA_CAMP_DOC_MAPPPK();
				mappPk.setCAMPAIGN_ID(inputVO.getCamp_id());
				mappPk.setSTEP_ID("00001");
				mappPk.setSFA_DOC_ID(inputVO.getFileName().get(i).get("OTHER"));
				mappVo.setcomp_id(mappPk);
				dam.create(mappVo);
				
				leadPara2 = "Y"; // add by ocean 2016-10-27 for 共用
			}
		}
		//====
		
		//====取得名單
		logger.info("開始執行取得名單");
		BigDecimal leTotCnt = new BigDecimal(BigInteger.ZERO);
		if(StringUtils.isNotBlank(inputVO.getTempName())) {
			GenFileTools gft = new GenFileTools();
			Connection conn = gft.getConnection();
			conn.setAutoCommit(false);
			// 2018/6/14 russle change
			StringBuffer sql = new StringBuffer();
			sql.append("INSERT INTO TBCAM_SFA_LE_IMP_TEMP(SEQNO, IMP_SEQNO, CUST_ID, CUST_NAME, BRANCH_ID, AO_CODE, START_DATE, END_DATE, LEAD_TYPE, LEAD_ID, ");
			sql.append("VAR_FIELD_LABEL1, VAR_FIELD_VALUE1, VAR_FIELD_LABEL2, VAR_FIELD_VALUE2, VAR_FIELD_LABEL3, VAR_FIELD_VALUE3, VAR_FIELD_LABEL4, VAR_FIELD_VALUE4, VAR_FIELD_LABEL5, VAR_FIELD_VALUE5, VAR_FIELD_LABEL6, VAR_FIELD_VALUE6, VAR_FIELD_LABEL7, VAR_FIELD_VALUE7, VAR_FIELD_LABEL8, VAR_FIELD_VALUE8, VAR_FIELD_LABEL9, VAR_FIELD_VALUE9, ");
			sql.append("VAR_FIELD_LABEL10, VAR_FIELD_VALUE10, VAR_FIELD_LABEL11, VAR_FIELD_VALUE11, VAR_FIELD_LABEL12, VAR_FIELD_VALUE12, VAR_FIELD_LABEL13, VAR_FIELD_VALUE13, VAR_FIELD_LABEL14, VAR_FIELD_VALUE14, VAR_FIELD_LABEL15, VAR_FIELD_VALUE15, VAR_FIELD_LABEL16, VAR_FIELD_VALUE16, VAR_FIELD_LABEL17, VAR_FIELD_VALUE17, VAR_FIELD_LABEL18, VAR_FIELD_VALUE18, VAR_FIELD_LABEL19, VAR_FIELD_VALUE19, VAR_FIELD_LABEL20, VAR_FIELD_VALUE20, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE) ");
			sql.append("VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
			sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
			sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, sysdate, ?, ?, sysdate) ");
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			try {
				Integer custsNum = 0;
				String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getTempName());
				if(!dataCsv.isEmpty()) {
					for(int i = 0;i < dataCsv.size();i++) {
						String[] str = dataCsv.get(i);
						if(i == 0) {
							try {
								if(!"客戶ID".equals(str[0].trim()))
									throw new Exception(str[0]);
								else if(!"分行代碼".equals(str[1].trim()))
									throw new Exception(str[1]);
								else if(!"AO_CODE或員編".equals(str[2].trim()))
									throw new Exception(str[2]);
								// else if(!"變動欄位名稱1".equals(str[3].trim()))
								for(int col=1; col <= 20; col++) {
									if(!("變動欄位名稱"+col).equals(str[col*2+1].trim()))
										throw new Exception(str[col*2+1]);
									else if(!("變動欄位值"+col).equals(str[col*2+2].trim()))
										throw new Exception(str[col*2+2]);
								}
							} catch(Exception ex) {
								throw new APException(ex.getMessage() + ":上傳格式錯誤，請下載範例檔案");
							}
							continue;
						}
						// check cust_id 請PG判斷，第一欄若為空，就跳過去不判斷。
						if(StringUtils.isBlank(str[0]))
							throw new JBranchException("上傳客戶檔案第 " + (i+1) + " 筆有誤，客戶ID為空。");
						// TBCRM_CUST_MAST
//						TBCRM_CUST_MASTVO cust_vo = (TBCRM_CUST_MASTVO) dam.findByPKey(TBCRM_CUST_MASTVO.TABLE_UID, str[0].trim());
						// 2017/11/3 mantis 3861 本行或非本行客戶都能填入與吃進去
//						if (custList.size() == 0)
//							throw new JBranchException("上傳客戶檔案第 " + (i+1) + " 筆有誤，客戶主檔不存在該客戶ID。");
						
						// 2017/8/31 change outside
						QueryConditionIF sq_con = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sq_con.setQueryString("SELECT SQ_TBCAM_SFA_LE_IMP_TEMP.nextval AS SEQNO1, SQ_TBCAM_SFA_LE_IMP_TEMP_LEAD.nextval AS SEQNO2 FROM DUAL");
						List<Map<String, Object>> sq_list = dam.exeQuery(sq_con);
						
						pstmt.setBigDecimal(1, (BigDecimal) sq_list.get(0).get("SEQNO1"));
						pstmt.setBigDecimal(2, seqNo);
						pstmt.setString(3, str[0].trim());
						// 客戶姓名
						pstmt.setNull(4, Types.VARCHAR);
						// 指派分行
						if(StringUtils.isNotBlank(str[1])) {
							if(utf_8_length(str[1]) > 10)
								throw new JBranchException("上傳客戶檔案第 " + (i+1) + " 筆有誤，指派分行欄位太長(" + utf_8_length(str[1]) + "/10)。");
							else
								pstmt.setString(5, str[1].trim());
						} else
							pstmt.setNull(5, Types.VARCHAR);
						// 指派AO_CODE
						if(StringUtils.isNotBlank(str[2])) {
							if(utf_8_length(str[2]) > 11)
								throw new JBranchException("上傳客戶檔案第 " + (i+1) + " 筆有誤，指派AO_CODE欄位太長(" + utf_8_length(str[2]) + "/11)。");
							else
								pstmt.setString(6, str[2].trim());
						} else
							pstmt.setNull(6, Types.VARCHAR);
						// old code useing custVO
//						pstmt.setString(4, cust_vo != null ? cust_vo.getCUST_NAME() : "");
//						// 指派分行
//						if (cust_vo != null) {
//							if(StringUtils.isBlank(cust_vo.getBRA_NBR())) {
//								if(StringUtils.isNotBlank(str[1])) {
//									if(utf_8_length(str[1]) > 10)
//										throw new JBranchException("上傳客戶檔案第 " + (i+1) + " 筆有誤，指派分行欄位太長(" + utf_8_length(str[1]) + "/10)。");
//									else
//										pstmt.setString(5, str[1].trim());
//								} else
//									throw new JBranchException("上傳客戶檔案第 " + (i+1) + " 筆有誤，該客戶無所屬分行且上傳的指派分行欄位為空。");
//							} else
//								pstmt.setString(5, cust_vo.getBRA_NBR());
//						} else {
//							if(StringUtils.isNotBlank(str[1])) {
//								if(utf_8_length(str[1]) > 10)
//									throw new JBranchException("上傳客戶檔案第 " + (i+1) + " 筆有誤，指派分行欄位太長(" + utf_8_length(str[1]) + "/10)。");
//								else
//									pstmt.setString(5, str[1].trim());
//							} else
//								throw new JBranchException("上傳客戶檔案第 " + (i+1) + " 筆有誤，上傳非本行客戶的指派分行欄位不得為空。");
//						}
//						// 指派AO_CODE
//						if(StringUtils.isNotBlank(str[2])) {
//							if(utf_8_length(str[2]) > 11)
//								throw new JBranchException("上傳客戶檔案第 " + (i+1) + " 筆有誤，指派AO_CODE欄位太長(" + utf_8_length(str[2]) + "/11)。");
//							else
//								pstmt.setString(6, str[2].trim());
//						} else {
//							if (cust_vo != null)
//								pstmt.setString(6, cust_vo.getAO_CODE());
//						}
						//
						pstmt.setTimestamp(7, new Timestamp(inputVO.getsDate().getTime())); // 名單起始日
						pstmt.setTimestamp(8, new Timestamp(inputVO.geteDate().getTime())); // 名單到期日
						pstmt.setString(9, inputVO.getType()); // 名單類型
						pstmt.setString(10, "USR" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + addZeroForNum(((BigDecimal) sq_list.get(0).get("SEQNO2")).toString(), 14)); // 來源系統的名單代碼
						// russle:1-15
						for(int col=1; col <= 15; col++) {
							if(utf_8_length(str[col*2+1]) > 60)
								throw new JBranchException("上傳客戶檔案第 " + (i+1) + " 筆有誤，名稱"+col+"欄位太長(" + utf_8_length(str[col*2+1]) + "/60)。");
							else if(StringUtils.isNotBlank(str[col*2+1]))
								pstmt.setString(10+col*2-1, str[col*2+1]);
							else
								pstmt.setNull(10+col*2-1, Types.VARCHAR);
							//
							if(utf_8_length(str[col*2+2]) > 240)
								throw new JBranchException("上傳客戶檔案第 " + (i+1) + " 筆有誤，值"+col+"欄位太長(" + utf_8_length(str[col*2+2]) + "/240)。");
							else if(StringUtils.isNotBlank(str[col*2+2]))
								pstmt.setString(11+col*2-1, str[col*2+2]);
							else
								pstmt.setNull(11+col*2-1, Types.VARCHAR);
						}
						// 16-20
						for(int col=16; col <= 20; col++) {
							if(utf_8_length(str[col*2+1]) > 60)
								throw new JBranchException("上傳客戶檔案第 " + (i+1) + " 筆有誤，名稱"+col+"欄位太長(" + utf_8_length(str[col*2+1]) + "/60)。");
							else if(StringUtils.isNotBlank(str[col*2+1]))
								pstmt.setString(9+col*2, str[col*2+1]);
							else
								pstmt.setNull(9+col*2, Types.VARCHAR);
							//
							if(utf_8_length(str[col*2+2]) > 4000)
								throw new JBranchException("上傳客戶檔案第 " + (i+1) + " 筆有誤，值"+col+"欄位太長(" + utf_8_length(str[col*2+2]) + "/4000)。");
							else if(StringUtils.isNotBlank(str[col*2+2]))
								pstmt.setString(10+col*2, str[col*2+2]);
							else
								pstmt.setNull(10+col*2, Types.VARCHAR);
						}
						pstmt.setString(51, SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID).toString());
						pstmt.setString(52, SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID).toString());
						pstmt.addBatch();
						
						custsNum++;
						if (custsNum % 1000 == 0) {
							pstmt.executeBatch();
							conn.commit();
						}
						logger.info("完成一筆名單");
					}
					pstmt.executeBatch();
					conn.commit();
					leTotCnt = new BigDecimal(custsNum); // add by ocean 2016-10-27 by 共用
				}
			} catch(Exception e) {
				if (conn != null) try { conn.rollback(); } catch (Exception e2) {}
				e.printStackTrace();
			} finally {
				if (pstmt != null) try { pstmt.close(); } catch (Exception e) {}
		        if (conn != null) try { conn.close(); } catch (Exception e) {}
			}
		}
		logger.info("結束執行取得名單");
		//====

		// add by ocean 2016-10-27 by 共用
		cam996.saveCampaign(dam, 
							seqNo, 
							inputVO.getCamp_id(), 
							inputVO.getCamp_name(), 
							inputVO.getCamp_desc(), 
							"00001", 
							inputVO.getSource_id(), 
							inputVO.getsDate(), 
							inputVO.geteDate(), 
							inputVO.getType(), 
							inputVO.getLead_para1(), 
							leadPara2,
							inputVO.getExam_id(), 
							inputVO.getSales_pitch(), 
							inputVO.getChannel(), 
							TextUtils.join(",", inputVO.getChkCode()), 
							"IN", 
							StringUtils.equals("UX", inputVO.getType()) ? "00" : "01", //#0000310: WMS-CR-20200714-01_優化線上留資名單功能:名單類型增加『電銷匯入留資名單』。該類型名單毋須經過放行。
							inputVO.getGift_camp_id(), 
							leTotCnt, 
							inputVO.getResponseCode());
		
		this.sendRtnObject(null);
	}
	
	public void getExample (Object body, IPrimitiveMap header) throws Exception {
		String[] csvHeader = {"客戶ID", "分行代碼", "AO_CODE或員編", "變動欄位名稱1", "變動欄位值1", "變動欄位名稱2", "變動欄位值2", "變動欄位名稱3", "變動欄位值3", "變動欄位名稱4", "變動欄位值4",
				"變動欄位名稱5", "變動欄位值5", "變動欄位名稱6", "變動欄位值6", "變動欄位名稱7", "變動欄位值7", "變動欄位名稱8", "變動欄位值8", "變動欄位名稱9", "變動欄位值9", "變動欄位名稱10", "變動欄位值10",
				"變動欄位名稱11", "變動欄位值11", "變動欄位名稱12", "變動欄位值12", "變動欄位名稱13", "變動欄位值13", "變動欄位名稱14", "變動欄位值14", "變動欄位名稱15", "變動欄位值15",
				"變動欄位名稱16", "變動欄位值16", "變動欄位名稱17", "變動欄位值17", "變動欄位名稱18", "變動欄位值18", "變動欄位名稱19", "變動欄位值19", "變動欄位名稱20", "變動欄位值20"};
		CSVUtil csv = new CSVUtil();
		// 設定表頭
		csv.setHeader(csvHeader);
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, "名單.csv");
		this.sendRtnObject(null);
	}
	
	public void getImpDtl(Object body, IPrimitiveMap header) throws Exception {
		
		CAM140OutputVO outputVO = new CAM140OutputVO();
		CAM140InputVO inputVO = (CAM140InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
//		sb.append("SELECT SEQNO, CAMPAIGN_ID, CAMPAIGN_NAME, CAMPAIGN_DESC, LEAD_SOURCE_ID, LEAD_TYPE, LEAD_PARA1, LEAD_PARA2, SALES_PITCH, FIRST_CHANNEL, SECOND_CHANNEL, START_DATE, END_DATE, GIFT_CAMPAIGN_ID ");
//		sb.append("FROM TBCAM_SFA_LEADS_IMP ");
//		sb.append("WHERE SEQNO = :seqNo ");
		
		switch (inputVO.getInterType()) {
		case "view":
			System.out.println("view");
			sb.append("SELECT I.SEQNO, I.CAMPAIGN_ID, I.STEP_ID, I.CAMPAIGN_NAME, I.CAMPAIGN_DESC, I.LEAD_SOURCE_ID, I.LEAD_TYPE, I.LEAD_PARA1, I.LEAD_PARA2, ");
			sb.append("       I.SALES_PITCH, I.FIRST_CHANNEL, I.SECOND_CHANNEL, I.START_DATE, I.END_DATE, I.GIFT_CAMPAIGN_ID, I.LEAD_RESPONSE_CODE ");
			break;
		case "updateImp" :
			System.out.println("updateImp");
			sb.append("SELECT I.SEQNO, C.CAMPAIGN_ID, C.STEP_ID, C.CAMPAIGN_NAME, C.CAMPAIGN_DESC, C.LEAD_SOURCE_ID, C.LEAD_TYPE, C.LEAD_PARA1, C.LEAD_PARA2, ");
			sb.append("       C.SALES_PITCH, C.FIRST_CHANNEL, C.SECOND_CHANNEL, C.START_DATE, C.END_DATE, C.GIFT_CAMPAIGN_ID, C.LEAD_RESPONSE_CODE ");
			
			break;
		}
		sb.append("FROM TBCAM_SFA_LEADS_IMP I ");
		sb.append("LEFT JOIN TBCAM_SFA_CAMPAIGN C ON I.CAMPAIGN_ID = C.CAMPAIGN_ID AND I.STEP_ID = C.STEP_ID ");
		sb.append("WHERE I.SEQNO = :seqNo ");
		condition.setQueryString(sb.toString());
		condition.setObject("seqNo", inputVO.getSeqNo());
		List<Map<String, Object>> list = dam.exeQuery(condition);
		
		outputVO.setSeqNo((BigDecimal) list.get(0).get("SEQNO"));
		outputVO.setCamp_id((String) list.get(0).get("CAMPAIGN_ID"));
		outputVO.setCamp_name((String) list.get(0).get("CAMPAIGN_NAME"));
		outputVO.setCamp_desc((String) list.get(0).get("CAMPAIGN_DESC"));
		outputVO.setSource_id((String) list.get(0).get("LEAD_SOURCE_ID"));
		outputVO.setType((String) list.get(0).get("LEAD_TYPE"));
		outputVO.setLead_para1((String) list.get(0).get("LEAD_PARA1"));
		outputVO.setLead_para2((String) list.get(0).get("LEAD_PARA2"));
		outputVO.setExam_id((String) list.get(0).get("EXAM_ID"));
		outputVO.setSales_pitch((String) list.get(0).get("SALES_PITCH"));
		outputVO.setChannel((String) list.get(0).get("FIRST_CHANNEL"));
		outputVO.setChkCode(null != list.get(0).get("SECOND_CHANNEL") ? ((String) list.get(0).get("SECOND_CHANNEL")).split(",", -1) : null);
		outputVO.setsDate((Timestamp) list.get(0).get("START_DATE"));
		outputVO.seteDate((Timestamp) list.get(0).get("END_DATE"));
		outputVO.setGift_camp_id((String) list.get(0).get("GIFT_CAMPAIGN_ID"));
		outputVO.setResponse_code((String) list.get(0).get("LEAD_RESPONSE_CODE"));
		
		System.out.println(sb.toString());
		this.sendRtnObject(outputVO);
	}
	
	public void getImpUpdateFile(Object body, IPrimitiveMap header) throws Exception {
		
		CAM140InputVO inputVO = (CAM140InputVO) body;
		CAM140OutputVO outputVO = new CAM140OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT FMAIN.DOC_ID, FMAIN.DOC_NAME, FDTL.DOC_FILE_TYPE ");
		sb.append("FROM TBCAM_SFA_CAMP_DOC_MAPP MAPP ");
		sb.append("LEFT JOIN TBCAM_SFA_CAMPAIGN CAMP ON MAPP.CAMPAIGN_ID = CAMP.CAMPAIGN_ID AND MAPP.STEP_ID = CAMP.STEP_ID ");
		sb.append("LEFT JOIN TBCAM_SFA_LEADS_IMP IMP ON MAPP.CAMPAIGN_ID = IMP.CAMPAIGN_ID AND MAPP.STEP_ID = IMP.STEP_ID ");
		sb.append("LEFT JOIN TBSYS_FILE_MAIN FMAIN ON MAPP.SFA_DOC_ID = FMAIN.DOC_ID ");
		sb.append("LEFT JOIN TBSYS_FILE_DETAIL FDTL ON FMAIN.DOC_ID = FDTL.DOC_ID ");
		sb.append("WHERE IMP.SEQNO = :seqNo");
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("seqNo", inputVO.getSeqNo());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		for(Map<String, Object> map : list) {
			if (map.get("DOC_FILE") != null) {
				Blob blob = (Blob) map.get("DOC_FILE");
				int blobLength = (int) blob.length();  
				byte[] blobAsBytes = blob.getBytes(1, blobLength);
				map.put("DOC_FILE", blobAsBytes);
				blob.free();
			}
		}
		outputVO.setFileList(list);
		
		this.sendRtnObject(outputVO);
	}
	
	public void updImp(Object body, IPrimitiveMap header) throws Exception {
		CAM140InputVO inputVO = (CAM140InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		TBCAM_SFA_LEADS_IMPVO vo = new TBCAM_SFA_LEADS_IMPVO();
		vo = (TBCAM_SFA_LEADS_IMPVO) dam.findByPKey(TBCAM_SFA_LEADS_IMPVO.TABLE_UID, inputVO.getSeqNo());
		
		if (null != vo) {
			TBCAM_SFA_CAMPAIGNPK cPK = new TBCAM_SFA_CAMPAIGNPK();
			cPK.setCAMPAIGN_ID(vo.getCAMPAIGN_ID());
			cPK.setSTEP_ID(vo.getSTEP_ID());
			TBCAM_SFA_CAMPAIGNVO cVO = new TBCAM_SFA_CAMPAIGNVO();
			cVO.setcomp_id(cPK);
			cVO = (TBCAM_SFA_CAMPAIGNVO) dam.findByPKey(TBCAM_SFA_CAMPAIGNVO.TABLE_UID, cVO.getcomp_id());
			cVO.setSALES_PITCH(inputVO.getSales_pitch());
			dam.update(cVO);
			
			vo.setSALES_PITCH(inputVO.getSales_pitch()); // 話術
			dam.update(vo);
			
			// 檔案更動
			for (int i = 0; i < inputVO.getFileName().size(); i++) {
				// 2017/4/25 add other type
				if("1".equals(ObjectUtils.toString(inputVO.getFileName().get(i).get("TYPE")))) {
					String SN = getFileSN();
					byte[] data = Files.readAllBytes(Paths.get(new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName().get(i).get("OTHER")).toString()));
					
					TBSYS_FILE_MAINVO fvo = new TBSYS_FILE_MAINVO();
					fvo.setDOC_ID(SN);
					fvo.setDOC_NAME(inputVO.getFileName().get(i).get("DOC_NAME"));
					fvo.setSUBSYSTEM_TYPE("CAM");
					fvo.setDOC_TYPE("01");
					dam.create(fvo);
					
					TBSYS_FILE_DETAILVO dvo = new TBSYS_FILE_DETAILVO();
					dvo.setDOC_ID(SN);
					dvo.setDOC_VERSION_STATUS("2");
					dvo.setDOC_FILE_TYPE("D");
					dvo.setDOC_FILE(ObjectUtil.byteArrToBlob(data));
					dam.create(dvo);
	
					TBCAM_SFA_CAMP_DOC_MAPPVO mappVo = new TBCAM_SFA_CAMP_DOC_MAPPVO();
					TBCAM_SFA_CAMP_DOC_MAPPPK mappPk = new TBCAM_SFA_CAMP_DOC_MAPPPK();
					mappPk.setCAMPAIGN_ID(vo.getCAMPAIGN_ID());
					mappPk.setSTEP_ID(vo.getSTEP_ID());
//					mappPk.setCAMPAIGN_ID(inputVO.getCamp_id());
//					mappPk.setSTEP_ID("00001");
					mappPk.setSFA_DOC_ID(SN);
					mappVo.setcomp_id(mappPk);
					dam.create(mappVo);
				}
				// web
				else if("2".equals(ObjectUtils.toString(inputVO.getFileName().get(i).get("TYPE")))) {
					String SN = getFileSN();
					
					TBSYS_FILE_MAINVO fvo = new TBSYS_FILE_MAINVO();
					fvo.setDOC_ID(SN);
					fvo.setDOC_NAME(inputVO.getFileName().get(i).get("DOC_NAME"));
					fvo.setSUBSYSTEM_TYPE("CAM");
					fvo.setDOC_TYPE("01");
					dam.create(fvo);
					
					TBSYS_FILE_DETAILVO dvo = new TBSYS_FILE_DETAILVO();
					dvo.setDOC_ID(SN);
					dvo.setDOC_VERSION_STATUS("2");
					dvo.setDOC_FILE_TYPE("U");
					if(inputVO.getFileName().get(i).get("DOC_NAME").indexOf("http://") == -1 && inputVO.getFileName().get(i).get("DOC_NAME").indexOf("https://") == -1)
						dvo.setDOC_URL("http://"+inputVO.getFileName().get(i).get("DOC_NAME"));
					else
						dvo.setDOC_URL(inputVO.getFileName().get(i).get("DOC_NAME"));
					dam.create(dvo);
	
					TBCAM_SFA_CAMP_DOC_MAPPVO mappVo = new TBCAM_SFA_CAMP_DOC_MAPPVO();
					TBCAM_SFA_CAMP_DOC_MAPPPK mappPk = new TBCAM_SFA_CAMP_DOC_MAPPPK();
					mappPk.setCAMPAIGN_ID(vo.getCAMPAIGN_ID());
					mappPk.setSTEP_ID(vo.getSTEP_ID());
					mappPk.setSFA_DOC_ID(SN);
					mappVo.setcomp_id(mappPk);
					dam.create(mappVo);
				}
				// pick
				else if("3".equals(ObjectUtils.toString(inputVO.getFileName().get(i).get("TYPE")))) {
					TBCAM_SFA_CAMP_DOC_MAPPVO mappVo = new TBCAM_SFA_CAMP_DOC_MAPPVO();
					TBCAM_SFA_CAMP_DOC_MAPPPK mappPk = new TBCAM_SFA_CAMP_DOC_MAPPPK();
					mappPk.setCAMPAIGN_ID(vo.getCAMPAIGN_ID());
					mappPk.setSTEP_ID(vo.getSTEP_ID());
					mappPk.setSFA_DOC_ID(inputVO.getFileName().get(i).get("OTHER"));
					mappVo.setcomp_id(mappPk);
					dam.create(mappVo);
				}
				vo.setLEAD_PARA2("Y"); //行銷活動參考文件
				dam.update(vo);
				
				cVO.setLEAD_PARA2("Y");
				dam.update(cVO);
				
				// 刪除舊有
				if(inputVO.getDelId().size() > 0) {
					for (String id : inputVO.getDelId()) {
						TBCAM_SFA_CAMP_DOC_MAPPVO mappVo = new TBCAM_SFA_CAMP_DOC_MAPPVO();
						TBCAM_SFA_CAMP_DOC_MAPPPK mappPk = new TBCAM_SFA_CAMP_DOC_MAPPPK();
						
						mappPk.setCAMPAIGN_ID(vo.getCAMPAIGN_ID());
						mappPk.setSTEP_ID(vo.getSTEP_ID());
						mappPk.setSFA_DOC_ID(id);
						mappVo.setcomp_id(mappPk);
						mappVo = (TBCAM_SFA_CAMP_DOC_MAPPVO) dam.findByPKey(TBCAM_SFA_CAMP_DOC_MAPPVO.TABLE_UID, mappVo.getcomp_id());
						if (mappVo != null) {
							dam.delete(mappVo);
						}
					}
				}
			}
			
			// 若由CAM160來修改活動內容，則修改imp狀態
			if ("CAM160".equals(inputVO.getAction())) {
				TBCAM_SFA_LEADS_IMPVO impVO = new TBCAM_SFA_LEADS_IMPVO();
				impVO = (TBCAM_SFA_LEADS_IMPVO) dam.findByPKey(TBCAM_SFA_LEADS_IMPVO.TABLE_UID, inputVO.getSeqNo());
				impVO.setRE_LOG("M3"); // 移除/修改記錄：M3：活動中修改
				
				dam.update(impVO);
			}
		}
		if (null != vo) {

		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_001");
		}
		
		this.sendRtnObject(null);
	}
	
	public void addParameter(Object body, IPrimitiveMap header) throws Exception {
		CAM140InputVO inputVO = (CAM140InputVO) body;
		CAM140OutputVO outputVO = new CAM140OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		if ("checkParameter".equals(inputVO.getAction())) {
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT SFA_PARA_ID, CAMPAIGN_ID, CAMPAIGN_NAME, CAMPAIGN_DESC, LEAD_SOURCE_ID, LEAD_TYPE, LEAD_PARA1, LEAD_PARA2, EXAM_ID, SALES_PITCH, FIRST_CHANNEL, SECOND_CHANNEL, START_DT, END_DT, GIFT_CAMPAIGN_ID ");
			sb.append("FROM TBCAM_SFA_PARAMETER ");
			sb.append("WHERE CAMPAIGN_ID = :camp_id ");
			condition.setQueryString(sb.toString());
			condition.setObject("camp_id", inputVO.getCamp_id());
			List<Map<String, Object>> list = dam.exeQuery(condition);
			
			if (list.size() > 0) 
				outputVO.setCheckStatus(true);
			
			this.sendRtnObject(outputVO);
		} else {
			 //產生名單參數代碼
			String sfaParaID = getSeqNum("SYS_PARAID");
			TBCAM_SFA_PARAMETERVO vo = new TBCAM_SFA_PARAMETERVO();
			vo.setSFA_PARA_ID(sfaParaID); // 名單參數代碼
			vo.setCAMPAIGN_ID(inputVO.getCamp_id());// 行銷活動代碼
			vo.setCAMPAIGN_NAME(inputVO.getCamp_name()); // 行銷活動名稱
			vo.setCAMPAIGN_DESC(inputVO.getCamp_desc()); // 簡要說明
			vo.setLEAD_SOURCE_ID(inputVO.getSource_id()); //名單來源
			vo.setLEAD_TYPE(inputVO.getType()); // 名單類型
			vo.setLEAD_PARA1(inputVO.getLead_para1()); //附帶資料蒐集
			vo.setEXAM_ID(inputVO.getExam_id()); // 問卷
			vo.setSALES_PITCH(inputVO.getSales_pitch()); // 話術
			vo.setFIRST_CHANNEL(inputVO.getChannel()); // 使用部隊
			vo.setSECOND_CHANNEL(TextUtils.join(",", inputVO.getChkCode())); // 第二使用通路
			if(inputVO.getsDate() != null)
				vo.setSTART_DT(new Timestamp(inputVO.getsDate().getTime()));// 專案起始日
			if(inputVO.geteDate() != null)
				vo.setEND_DT(new Timestamp(inputVO.geteDate().getTime()));// 專案截止日
			vo.setGIFT_CAMPAIGN_ID(inputVO.getGift_camp_id()); //票券/贈品/講座活動代碼
			vo.setLEAD_RESPONSE_CODE(inputVO.getResponseCode());
	
			// 儲存參考文件
			for (int i = 0; i < inputVO.getFileName().size(); i++) {
				// 2017/4/25 add other type
				if("1".equals(ObjectUtils.toString(inputVO.getFileName().get(i).get("TYPE")))) {
					String SN = getFileSN();
					byte[] data = Files.readAllBytes(Paths.get(new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName().get(i).get("OTHER")).toString()));
					
					TBSYS_FILE_MAINVO fvo = new TBSYS_FILE_MAINVO();
					fvo.setDOC_ID(SN);
					fvo.setDOC_NAME(inputVO.getFileName().get(i).get("DOC_NAME"));
					fvo.setSUBSYSTEM_TYPE("CAM");
					fvo.setDOC_TYPE("01");
					dam.create(fvo);
					
					TBSYS_FILE_DETAILVO dvo = new TBSYS_FILE_DETAILVO();
					dvo.setDOC_ID(SN);
					dvo.setDOC_VERSION_STATUS("2");
					dvo.setDOC_FILE_TYPE("D");
					dvo.setDOC_FILE(ObjectUtil.byteArrToBlob(data));
					dam.create(dvo);
	
					TBCAM_SFA_PARA_DOC_MAPPVO mappVO = new TBCAM_SFA_PARA_DOC_MAPPVO();
					TBCAM_SFA_PARA_DOC_MAPPPK mappPK = new TBCAM_SFA_PARA_DOC_MAPPPK();
					mappPK.setSFA_PARA_ID(sfaParaID);
					mappPK.setSFA_DOC_ID(SN);
					mappVO.setcomp_id(mappPK);
					dam.create(mappVO);
					
					vo.setLEAD_PARA2("Y"); // 行銷活動參考文件
				}
				// web
				else if("2".equals(ObjectUtils.toString(inputVO.getFileName().get(i).get("TYPE")))) {
					String SN = getFileSN();
					
					TBSYS_FILE_MAINVO fvo = new TBSYS_FILE_MAINVO();
					fvo.setDOC_ID(SN);
					fvo.setDOC_NAME(inputVO.getFileName().get(i).get("DOC_NAME"));
					fvo.setSUBSYSTEM_TYPE("CAM");
					fvo.setDOC_TYPE("01");
					dam.create(fvo);
					
					TBSYS_FILE_DETAILVO dvo = new TBSYS_FILE_DETAILVO();
					dvo.setDOC_ID(SN);
					dvo.setDOC_VERSION_STATUS("2");
					dvo.setDOC_FILE_TYPE("U");
					if(inputVO.getFileName().get(i).get("DOC_NAME").indexOf("http://") == -1 && inputVO.getFileName().get(i).get("DOC_NAME").indexOf("https://") == -1)
						dvo.setDOC_URL("http://"+inputVO.getFileName().get(i).get("DOC_NAME"));
					else
						dvo.setDOC_URL(inputVO.getFileName().get(i).get("DOC_NAME"));
					dam.create(dvo);
	
					TBCAM_SFA_PARA_DOC_MAPPVO mappVO = new TBCAM_SFA_PARA_DOC_MAPPVO();
					TBCAM_SFA_PARA_DOC_MAPPPK mappPK = new TBCAM_SFA_PARA_DOC_MAPPPK();
					mappPK.setSFA_PARA_ID(sfaParaID);
					mappPK.setSFA_DOC_ID(SN);
					mappVO.setcomp_id(mappPK);
					dam.create(mappVO);
					
					vo.setLEAD_PARA2("Y"); // 行銷活動參考文件
				}
				// pick
				else if("3".equals(ObjectUtils.toString(inputVO.getFileName().get(i).get("TYPE")))) {
					TBCAM_SFA_PARA_DOC_MAPPVO mappVO = new TBCAM_SFA_PARA_DOC_MAPPVO();
					TBCAM_SFA_PARA_DOC_MAPPPK mappPK = new TBCAM_SFA_PARA_DOC_MAPPPK();
					mappPK.setSFA_PARA_ID(sfaParaID);
					mappPK.setSFA_DOC_ID(inputVO.getFileName().get(i).get("OTHER"));
					mappVO.setcomp_id(mappPK);
					dam.create(mappVO);
					
					vo.setLEAD_PARA2("Y"); // 行銷活動參考文件
				}
			}
			
			dam.create(vo);
			
			this.sendRtnObject(null);
		}
	}
	
	public void getParameterDtl(Object body, IPrimitiveMap header) throws Exception {
		
		CAM140OutputVO outputVO = new CAM140OutputVO();
		CAM140InputVO inputVO = (CAM140InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT SFA_PARA_ID, CAMPAIGN_ID, CAMPAIGN_NAME, CAMPAIGN_DESC, LEAD_SOURCE_ID, LEAD_RESPONSE_CODE, LEAD_TYPE, LEAD_PARA1, LEAD_PARA2, EXAM_ID, SALES_PITCH, FIRST_CHANNEL, SECOND_CHANNEL, START_DT, END_DT, GIFT_CAMPAIGN_ID ");
		sb.append("FROM TBCAM_SFA_PARAMETER ");
		sb.append("WHERE SFA_PARA_ID = :sfaParaID ");
		condition.setQueryString(sb.toString());
		condition.setObject("sfaParaID", inputVO.getSfaParaID());
		List<Map<String, Object>> list = dam.exeQuery(condition);
		
		if (list.size() > 0) {
			outputVO.setSfaParaID(inputVO.getSfaParaID());
			outputVO.setCamp_id((String) list.get(0).get("CAMPAIGN_ID"));
			outputVO.setCamp_name((String) list.get(0).get("CAMPAIGN_NAME"));
			outputVO.setCamp_desc((String) list.get(0).get("CAMPAIGN_DESC"));
			outputVO.setSource_id((String) list.get(0).get("LEAD_SOURCE_ID"));
			outputVO.setResponse_code((String) list.get(0).get("LEAD_RESPONSE_CODE"));
			outputVO.setType((String) list.get(0).get("LEAD_TYPE"));
			outputVO.setLead_para1((String) list.get(0).get("LEAD_PARA1"));
			outputVO.setLead_para2((String) list.get(0).get("LEAD_PARA2"));
			outputVO.setExam_id((String) list.get(0).get("EXAM_ID"));
			outputVO.setSales_pitch((String) list.get(0).get("SALES_PITCH"));
			outputVO.setChannel((String) list.get(0).get("FIRST_CHANNEL"));
			outputVO.setChkCode(null != list.get(0).get("SECOND_CHANNEL") ? ((String) list.get(0).get("SECOND_CHANNEL")).split(",", -1) : null);
			outputVO.setsDate((Timestamp) list.get(0).get("START_DT"));
			outputVO.seteDate((Timestamp) list.get(0).get("END_DT"));
			outputVO.setGift_camp_id((String) list.get(0).get("GIFT_CAMPAIGN_ID"));
		}
		
		this.sendRtnObject(outputVO);
	}
	
	public void getParameterUpdateFile(Object body, IPrimitiveMap header) throws Exception {
		
		CAM140InputVO inputVO = (CAM140InputVO) body;
		CAM140OutputVO outputVO = new CAM140OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT FMAIN.DOC_ID, FMAIN.DOC_NAME, FDTL.DOC_FILE_TYPE ");
		sb.append("FROM TBCAM_SFA_PARA_DOC_MAPP MAPP ");
		sb.append("LEFT JOIN TBSYS_FILE_MAIN FMAIN ON MAPP.SFA_DOC_ID = FMAIN.DOC_ID ");
		sb.append("LEFT JOIN TBSYS_FILE_DETAIL FDTL ON FMAIN.DOC_ID = FDTL.DOC_ID ");
		sb.append("WHERE MAPP.SFA_PARA_ID = :sfaParaID ");
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("sfaParaID", inputVO.getSfaParaID());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		for(Map<String, Object> map : list) {
			if (map.get("DOC_FILE") != null) {
				Blob blob = (Blob) map.get("DOC_FILE");
				int blobLength = (int) blob.length();  
				byte[] blobAsBytes = blob.getBytes(1, blobLength);
				map.put("DOC_FILE", blobAsBytes);
				blob.free();
			}
		}
		outputVO.setFileList(list);
		
		this.sendRtnObject(outputVO);
	}
	
	public void updParameter(Object body, IPrimitiveMap header) throws Exception {
		CAM140InputVO inputVO = (CAM140InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		TBCAM_SFA_PARAMETERVO vo = new TBCAM_SFA_PARAMETERVO();
		vo = (TBCAM_SFA_PARAMETERVO) dam.findByPKey(TBCAM_SFA_PARAMETERVO.TABLE_UID, inputVO.getSfaParaID());
		
		if (null != vo) {
			vo.setCAMPAIGN_NAME(inputVO.getCamp_name()); // 行銷活動名稱
			vo.setCAMPAIGN_DESC(inputVO.getCamp_desc()); // 簡要說明
			vo.setLEAD_SOURCE_ID(inputVO.getSource_id()); //名單來源
			vo.setLEAD_TYPE(inputVO.getType()); // 名單類型
			vo.setLEAD_PARA1(inputVO.getLead_para1()); // 附帶資料蒐集
			vo.setEXAM_ID(inputVO.getExam_id()); // 問卷代碼
			vo.setSALES_PITCH(inputVO.getSales_pitch()); // 話術
			vo.setFIRST_CHANNEL(inputVO.getChannel()); // 使用部隊
			vo.setSECOND_CHANNEL(TextUtils.join(",", inputVO.getChkCode())); // 第二使用通路
			if(inputVO.getsDate() != null)
				vo.setSTART_DT(new Timestamp(inputVO.getsDate().getTime()));// 專案起始日
			if(inputVO.geteDate() != null)
				vo.setEND_DT(new Timestamp(inputVO.geteDate().getTime()));// 專案截止日
			vo.setGIFT_CAMPAIGN_ID(inputVO.getGift_camp_id()); //票券/贈品/講座活動代碼
			
			dam.update(vo);
			
			// 檔案更動
			for (int i = 0; i < inputVO.getFileName().size(); i++) {
				// 2017/4/25 add other type
				if("1".equals(ObjectUtils.toString(inputVO.getFileName().get(i).get("TYPE")))) {
					String SN = getFileSN();
					byte[] data = Files.readAllBytes(Paths.get(new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName().get(i).get("OTHER")).toString()));
					
					TBSYS_FILE_MAINVO fvo = new TBSYS_FILE_MAINVO();
					fvo.setDOC_ID(SN);
					fvo.setDOC_NAME(inputVO.getFileName().get(i).get("DOC_NAME"));
					fvo.setSUBSYSTEM_TYPE("CAM");
					fvo.setDOC_TYPE("01");
					dam.create(fvo);
					
					TBSYS_FILE_DETAILVO dvo = new TBSYS_FILE_DETAILVO();
					dvo.setDOC_ID(SN);
					dvo.setDOC_VERSION_STATUS("2");
					dvo.setDOC_FILE_TYPE("D");
					dvo.setDOC_FILE(ObjectUtil.byteArrToBlob(data));
					dam.create(dvo);
	
					TBCAM_SFA_PARA_DOC_MAPPVO mappVO = new TBCAM_SFA_PARA_DOC_MAPPVO();
					TBCAM_SFA_PARA_DOC_MAPPPK mappPK = new TBCAM_SFA_PARA_DOC_MAPPPK();
					mappPK.setSFA_PARA_ID(vo.getSFA_PARA_ID());
					mappPK.setSFA_DOC_ID(SN);
					mappVO.setcomp_id(mappPK);
					dam.create(mappVO);
				}
				// web
				else if("2".equals(ObjectUtils.toString(inputVO.getFileName().get(i).get("TYPE")))) {
					String SN = getFileSN();
					
					TBSYS_FILE_MAINVO fvo = new TBSYS_FILE_MAINVO();
					fvo.setDOC_ID(SN);
					fvo.setDOC_NAME(inputVO.getFileName().get(i).get("DOC_NAME"));
					fvo.setSUBSYSTEM_TYPE("CAM");
					fvo.setDOC_TYPE("01");
					dam.create(fvo);
					
					TBSYS_FILE_DETAILVO dvo = new TBSYS_FILE_DETAILVO();
					dvo.setDOC_ID(SN);
					dvo.setDOC_VERSION_STATUS("2");
					dvo.setDOC_FILE_TYPE("U");
					if(inputVO.getFileName().get(i).get("DOC_NAME").indexOf("http://") == -1 && inputVO.getFileName().get(i).get("DOC_NAME").indexOf("https://") == -1)
						dvo.setDOC_URL("http://"+inputVO.getFileName().get(i).get("DOC_NAME"));
					else
						dvo.setDOC_URL(inputVO.getFileName().get(i).get("DOC_NAME"));
					dam.create(dvo);
	
					TBCAM_SFA_PARA_DOC_MAPPVO mappVO = new TBCAM_SFA_PARA_DOC_MAPPVO();
					TBCAM_SFA_PARA_DOC_MAPPPK mappPK = new TBCAM_SFA_PARA_DOC_MAPPPK();
					mappPK.setSFA_PARA_ID(vo.getSFA_PARA_ID());
					mappPK.setSFA_DOC_ID(SN);
					mappVO.setcomp_id(mappPK);
					dam.create(mappVO);
				}
				// pick
				else if("3".equals(ObjectUtils.toString(inputVO.getFileName().get(i).get("TYPE")))) {
					TBCAM_SFA_PARA_DOC_MAPPVO mappVO = new TBCAM_SFA_PARA_DOC_MAPPVO();
					TBCAM_SFA_PARA_DOC_MAPPPK mappPK = new TBCAM_SFA_PARA_DOC_MAPPPK();
					mappPK.setSFA_PARA_ID(vo.getSFA_PARA_ID());
					mappPK.setSFA_DOC_ID(inputVO.getFileName().get(i).get("OTHER"));
					mappVO.setcomp_id(mappPK);
					dam.create(mappVO);
				}
				vo.setLEAD_PARA2("Y"); // 行銷活動參考文件
				
				dam.update(vo);
				
				// 刪除舊有
				if(inputVO.getDelId().size() > 0) {
					for (String id : inputVO.getDelId()) {
						TBCAM_SFA_PARA_DOC_MAPPVO mvo = new TBCAM_SFA_PARA_DOC_MAPPVO();
						TBCAM_SFA_PARA_DOC_MAPPPK mPK = new TBCAM_SFA_PARA_DOC_MAPPPK();
						
						mPK.setSFA_PARA_ID(vo.getSFA_PARA_ID());
						mPK.setSFA_DOC_ID(id);
						mvo.setcomp_id(mPK);
						mvo = (TBCAM_SFA_PARA_DOC_MAPPVO) dam.findByPKey(TBCAM_SFA_PARA_DOC_MAPPVO.TABLE_UID, mvo.getcomp_id());
						if (mvo != null) {
							dam.delete(mvo);
						}
					}
				}
			}
			
			// 若由CAM160來修改參數設定，則修改imp狀態
			if ("CAM160".equals(inputVO.getAction())) {
				TBCAM_SFA_LEADS_IMPVO impVO = new TBCAM_SFA_LEADS_IMPVO();
				impVO = (TBCAM_SFA_LEADS_IMPVO) dam.findByPKey(TBCAM_SFA_LEADS_IMPVO.TABLE_UID, inputVO.getSeqNo());
				impVO.setRE_STATUS("UP"); // 活動移除/修改狀態：參數-已修改
				impVO.setRE_LOG("M4"); // 移除/修改記錄：M4：原始參數修改
				
				dam.update(impVO);
			}
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_001");
		}
		
		this.sendRtnObject(null);
	}
	
	private String getFileSN() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SQ_TBSYS_FILE_MAIN.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}

	public void initial(Object body, IPrimitiveMap header) throws JBranchException {
		CAM140OutputVO outputVO = new CAM140OutputVO();
		dam = this.getDataAccessManager();
		
		// 序號命名規則：系統日字串(YYYYMMDD)+下一序號(0000~9999)
		outputVO.setSeqNum(new SimpleDateFormat("yyyyMMdd").format(new Date()) + StringUtils.leftPad(getSeqNum("SYS_CAMPID"), 4, "0"));
		
		sendRtnObject(outputVO);
	}
	
	private String getSeqNum(String TXN_ID) throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		switch(TXN_ID) {
			case "SYS_PARAID":
				sql.append("SELECT SQ_SYS_PARAID.nextval AS SEQ FROM DUAL ");
				break;
			case "SYS_CAMPID":
				sql.append("SELECT SQ_SYS_CAMPID.nextval AS SEQ FROM DUAL ");
				break;
			case "SYS_QUESTIONNAIRE":
				sql.append("SELECT SQ_SYS_QUESTIONNAIRE.nextval AS SEQ FROM DUAL ");
				break;
		}
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
	
	public void getRes(Object body, IPrimitiveMap header) throws JBranchException {
		
		CAM140InputVO inputVO = (CAM140InputVO) body;
		CAM140OutputVO outputVO = new CAM140OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT CAMPAIGN_ID, LEAD_STATUS, RESPONSE_NAME, RESPONSE_MEAN, RESPONSE_ENABLE ");
		sql.append("FROM TBCAM_SFA_CAMP_RESPONSE ");
		sql.append("WHERE CAMPAIGN_ID IN (:responseCode, :id) ");
		
		condition.setQueryString(sql.toString());
		condition.setObject("responseCode", inputVO.getResponseCode());
		condition.setObject("id", inputVO.getCamp_id());
		
		outputVO.setResultList(dam.exeQuery(condition)); // data
		
		this.sendRtnObject(outputVO);
	}
	
	public void delRes(Object body, IPrimitiveMap header) throws JBranchException {
		CAM140InputVO inputVO = (CAM140InputVO) body;
		dam = this.getDataAccessManager();
		
		TBCAM_SFA_CAMP_RESPONSEVO vo = new TBCAM_SFA_CAMP_RESPONSEVO();
		TBCAM_SFA_CAMP_RESPONSEPK pk = new TBCAM_SFA_CAMP_RESPONSEPK();
		pk.setCAMPAIGN_ID(inputVO.getCamp_id());
		pk.setLEAD_STATUS(inputVO.getLead());
		vo = (TBCAM_SFA_CAMP_RESPONSEVO) dam.findByPKey(
				TBCAM_SFA_CAMP_RESPONSEVO.TABLE_UID, pk);
		if (vo != null) {
			dam.delete(vo);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_005");
		}
		this.sendRtnObject(null);
	}
	
	public void updateRES(Object body, IPrimitiveMap header) throws JBranchException {
		CAM140InputVO inputVO = (CAM140InputVO) body;
		dam = this.getDataAccessManager();
		
		TBCAM_SFA_CAMP_RESPONSEVO vo = new TBCAM_SFA_CAMP_RESPONSEVO();
		TBCAM_SFA_CAMP_RESPONSEPK pk = new TBCAM_SFA_CAMP_RESPONSEPK();
		pk.setCAMPAIGN_ID(inputVO.getCamp_id());
		pk.setLEAD_STATUS(inputVO.getLead());
		vo = (TBCAM_SFA_CAMP_RESPONSEVO) dam.findByPKey(
				TBCAM_SFA_CAMP_RESPONSEVO.TABLE_UID, pk);
		// exist update
		if (vo != null) {
			vo.setRESPONSE_ENABLE(inputVO.getEnable());
			vo.setRESPONSE_MEAN(inputVO.getMean());
			vo.setRESPONSE_NAME(inputVO.getName());
			dam.update(vo);
		} else {
			vo = new TBCAM_SFA_CAMP_RESPONSEVO();
			vo.setcomp_id(pk);
			vo.setRESPONSE_ENABLE(inputVO.getEnable());
			vo.setRESPONSE_MEAN(inputVO.getMean());
			vo.setRESPONSE_NAME(inputVO.getName());
			dam.create(vo);
		}
		this.sendRtnObject(null);
	}
	
	public void getResponseCountRow (Object body, IPrimitiveMap header) throws JBranchException {
		
		CAM140InputVO inputVO = (CAM140InputVO) body;
		CAM140OutputVO outputVO = new CAM140OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT COUNT(1) AS RESPONSE_COUNTS ");
		sql.append("FROM TBCAM_SFA_CAMP_RESPONSE ");
		sql.append("WHERE CAMPAIGN_ID = :id ");
		
		condition.setQueryString(sql.toString());
		condition.setObject("id", inputVO.getCamp_id());
		
		List<Map<String, Object>> list = dam.exeQuery(condition);
		
		outputVO.setResponseCounts((BigDecimal) list.get(0).get("RESPONSE_COUNTS")); // data
		
		this.sendRtnObject(outputVO);
	}
	
	//=== 問卷
	public void getQuestionList (Object body, IPrimitiveMap header) throws JBranchException {
		
		CAM140InputVO inputVO = (CAM140InputVO) body;
		CAM140OutputVO outputVO = new CAM140OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		// 為了ORDER BY QUESTION_VERSION 後，能拿到正確的ROWNUM，所以多一層SELECT
		sb.append("SELECT ROWNUM, QUESTION_VERSION, QUESTION_DESC, QUESTION_TYPE, ANS_OTHER_FLAG, ANS_MEMO_FLAG, STATUS, CREATOR, CREATETIME ");
		sb.append("FROM ( ");
		sb.append("SELECT QUESTION_VERSION, QUESTION_DESC, QUESTION_TYPE, ANS_OTHER_FLAG, ANS_MEMO_FLAG, STATUS, CREATOR, CREATETIME ");
		sb.append("FROM TBSYS_QST_QUESTION ");
		sb.append("WHERE 1=1 ");
		
		if (StringUtils.isNotBlank(inputVO.getModuleCategory()))
			sb.append("AND MODULE_CATEGORY = :moduleCategory ");
		
		sb.append("ORDER BY QUESTION_VERSION");
		sb.append(") ");
		queryCondition.setQueryString(sb.toString());
		
		if (StringUtils.isNotBlank(inputVO.getModuleCategory()))
			queryCondition.setObject("moduleCategory", inputVO.getModuleCategory());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		for (Map<String, Object> map : list) {
			
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
			sb = new StringBuffer();
			sb.append("SELECT QUESTION_VERSION, ANSWER_SEQ, ANSWER_DESC ");
			sb.append("FROM TBSYS_QST_ANSWER ");
			sb.append("WHERE QUESTION_VERSION = :questionVersion ");
			sb.append("AND ANSWER_DESC <> '其他' ");
			sb.append("ORDER BY QUESTION_VERSION, ANSWER_SEQ ");
			
			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("questionVersion", map.get("QUESTION_VERSION"));
			
			List<Map<String, Object>> tempList = dam.exeQuery(queryCondition);
			String str = "";
			for (Map<String, Object> tMap : tempList) {
				str = str + tMap.get("ANSWER_DESC") + " \n";
			}

			map.put("ANSWER_DESC", str);
		}
		
		outputVO.setQuestionList(list); // data
		
		this.sendRtnObject(outputVO);
	}

	public void addQuestion (Object body, IPrimitiveMap header) throws JBranchException {
		
		CAM140InputVO inputVO = (CAM140InputVO) body;
		CAM140OutputVO outputVO = new CAM140OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("DELETE FROM TBSYS_QUESTIONNAIRE WHERE EXAM_VERSION = :examVersion");
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("examVersion",inputVO.getExamVersion());
		dam.exeUpdate(queryCondition);
		
		for (int i = 0; i < inputVO.getQuestionVersionList().length; i++) {
			// 題目狀態更正為使用中
			TBSYS_QST_QUESTIONVO qVO = new TBSYS_QST_QUESTIONVO();
			qVO = (TBSYS_QST_QUESTIONVO) dam.findByPKey(TBSYS_QST_QUESTIONVO.TABLE_UID, inputVO.getQuestionVersionList()[i]);
			qVO.setSTATUS("Y");
			
			dam.update(qVO);

			// 新增題目
			TBSYS_QUESTIONNAIREVO vo = new TBSYS_QUESTIONNAIREVO();
			TBSYS_QUESTIONNAIREPK pk = new TBSYS_QUESTIONNAIREPK();
			pk.setEXAM_VERSION(inputVO.getExamVersion());
			pk.setQUESTION_VERSION(inputVO.getQuestionVersionList()[i]);
			
			vo.setcomp_id(pk);
			vo.setEXAM_NAME(inputVO.getExamName());
			vo.setQUEST_TYPE("01"); //01行銷問卷
			vo.setESSENTIAL_FLAG("N"); //非必需
			vo.setSTATUS("03"); //已生效
			vo.setACTIVE_DATE(new Timestamp(System.currentTimeMillis()));
			vo.setQST_NO(new BigDecimal(i).add(new BigDecimal(1)));
			
			dam.create(vo);
		}
		
		this.sendRtnObject(outputVO);
	}
	
	public void getExam (Object body, IPrimitiveMap header) throws JBranchException {
		
		CAM140InputVO inputVO = (CAM140InputVO) body;
		CAM140OutputVO outputVO = new CAM140OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT Q.EXAM_VERSION, ");
		sb.append("(SELECT MAX(LTRIM(SYS_CONNECT_BY_PATH(TEMP.QUESTION_VERSION, ','), ',')) AS QUERTION_VERSION ");
		sb.append("FROM (SELECT EXAM_VERSION, QUESTION_VERSION, EXAM_NAME, QUEST_TYPE, QST_NO FROM TBSYS_QUESTIONNAIRE WHERE EXAM_VERSION = Q.EXAM_VERSION ORDER BY QST_NO) TEMP ");
		sb.append("START WITH TEMP.QST_NO = 1 ");
		sb.append("CONNECT BY PRIOR TEMP.QST_NO + 1 = TEMP.QST_NO) AS QUERTION_VERSION_LIST, ");
		sb.append("Q.EXAM_NAME ");
		sb.append("FROM TBSYS_QUESTIONNAIRE Q ");
		sb.append("WHERE Q.EXAM_VERSION = :examVersion ");
		sb.append("GROUP BY Q.EXAM_VERSION, Q.EXAM_NAME ");
		
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("examVersion",inputVO.getExamVersion());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0) {
			outputVO.setExamVersion((String) list.get(0).get("EXAM_VERSION"));
			outputVO.setExamName((String) list.get(0).get("EXAM_NAME"));
			outputVO.setQuestionVersionList(null != list.get(0).get("QUERTION_VERSION_LIST") ? ((String) list.get(0).get("QUERTION_VERSION_LIST")).split(",", -1) : null);
		}
		
		this.sendRtnObject(outputVO);
	}

	public void getExamVersion (Object body, IPrimitiveMap header) throws JBranchException {
		CAM140OutputVO outputVO = new CAM140OutputVO();
		dam = this.getDataAccessManager();
		
		outputVO.setExamVersion("SYS" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + StringUtils.leftPad(getSeqNum("SYS_QUESTIONNAIRE"), 4, "0"));
		
		sendRtnObject(outputVO);
	}
	
	public void getDoc(Object body, IPrimitiveMap header) throws JBranchException {
		CAM140OutputVO return_VO = new CAM140OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("select DOC_ID,DOC_NAME from TBSYS_FILE_MAIN where DOC_TYPE = '02' ");
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		
		this.sendRtnObject(return_VO);
	}
	
	private String addZeroForNum(String str, int strLength) {
	    int strLen = str.length();
	    if (strLen < strLength) {
	        while (strLen < strLength) {
	            StringBuffer sb = new StringBuffer();
	            sb.append("0").append(str);// 左補0
	            // sb.append(str).append("0");//右補0
	            str = sb.toString();
	            strLen = str.length();
	        }
	    }
	    return str;
	}
	
	
}