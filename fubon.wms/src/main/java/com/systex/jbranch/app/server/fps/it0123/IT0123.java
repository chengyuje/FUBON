package com.systex.jbranch.app.server.fps.it0123;

import com.systex.jbranch.app.common.fps.table.TBKYC_INVESTOREXAM_MVO;
import com.systex.jbranch.app.server.fps.kyc310.KYC310;
import com.systex.jbranch.app.server.fps.kyc311.KYC311InputVO;
import com.systex.jbranch.app.server.fps.kycoperation.KycMonitor;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.platform.common.dataManager.Branch;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.commons.lang.StringUtils.*;

/**
 * 自動化管理工具 (IT 專用)
 *
 * @author eli
 * @date 20181213 KYC 自動生效功能
 * @date 20181220 自動調整商品募集期、自動新增錄音序號
 * @date 20190806 上傳自訂義基金
 * @date 20200311 上線必要更新模組
 */
@Component
@Scope("request")
@SuppressWarnings({ "unchecked" })
public class IT0123 extends FubonWmsBizLogic {
	@Autowired
	private CBSService cbsService;

	private IT0123InputVO inputVO;

	@Autowired
	private IT0123OutputVO outputVO;

	@Autowired
	private SOT701 sot701;

	@Autowired
	private KYC310 kyc310;
	
	Logger logger = LoggerFactory.getLogger(this.getClass());

	/** 必要更新檔案放置位置（23 AP 主機，測試時請用相應本機測試路徑） **/
	private final String MODIFY_PATH = File.separator + "home" + File.separator + "wasadmin" + File.separator + "ModifyList";
	// private final String MODIFY_PATH = "D:\\test";
	/** 說明文件 **/
	private final String INTRO_DOC = "Eli_Intro_Doc.docx";

	/**
	 * 設置 InputVO
	 **/
	private void setInputVO(Object body) {
		this.inputVO = (IT0123InputVO) body;
	}

	/**
	 * 生效指定客戶的KYC 利用網銀既有方法來生效指定客戶，其方法參數格式可參考 TBKYC_LOG.INPUT_DATA
	 * 主要兩項參數作格式化，分別為題目和答案 : 題目: 將客戶可作的KYC問卷所有題目其序號使用'^'相連。 答案:
	 * 題目可達到最高分的答案序號使用'^'相連，多選題使用'-'相連 再依照輸入參數的規格，將'^'轉為'.94.'、'-'轉為'.45.'
	 *
	 * @param body
	 * @param header
	 */
	public void execute(Object body, IPrimitiveMap header) throws Exception {
		setInputVO(body);

		try {
			String xml = PlatformContext.getBean(KycMonitor.class)
					.callApi(inputVO.getCustId(), "1", "自動化IT0123");
			if (xml.contains("<soapenv:Fault>"))
				throw new APException(xml);

			this.sendRtnObject("執行完成!");
		} catch (Exception e) {
			this.sendRtnObject("執行錯誤! " + e.getMessage());
		}
	}

	/**
	 * 自動化調整商品募集期 商品ID是唯一值，故取得商品募集期所有更新的SQL去做更新
	 *
	 * @param body
	 * @param header
	 */
	public void changeMaturity(Object body, IPrimitiveMap header) {
		setInputVO(body);
		try {
			for (String sql : getSqlGroupForChangingMaturity())
				exeUpdateForQcf(genDefaultQueryConditionIF().setQueryString(sql).setObject("prdId", inputVO.getPrdId()));
			this.sendRtnObject("執行完成!");
		} catch (Exception e) {
			this.sendRtnObject("執行錯誤! " + e.getMessage());
		}
	}

	/**
	 * 更新商品募集期的所有SQL (商品SI、BOND、SN)
	 **/
	private String[] getSqlGroupForChangingMaturity() {
		return new String[] { "update TBPRD_SI set DATE_OF_MATURITY = sysdate + 100 where PRD_ID = :prdId", "update TBPRD_SIINFO set INV_SDATE = sysdate - 1, INV_EDATE = sysdate + 100 where PRD_ID = :prdId",

		"update TBPRD_BOND set DATE_OF_MATURITY = sysdate + 100 where PRD_ID = :prdId", "update TBPRD_BONDINFO set INV_SDATE = sysdate - 1, INV_EDATE = sysdate + 100 where PRD_ID = :prdId",

		"update TBPRD_SN set DATE_OF_MATURITY = sysdate + 100 where PRD_ID = :prdId", "update TBPRD_SNINFO set INV_SDATE = sysdate - 1, INV_EDATE = sysdate + 100 where PRD_ID = :prdId" };
	}

	/**
	 * 自動新增錄音序號
	 *
	 * @param body
	 * @param header
	 */
	public void insertRecording(Object body, IPrimitiveMap header) throws JBranchException {
		setInputVO(body);
		String date = substring(cbsService.getCBSTestDate(), 0, 8);
		String seq = getRecordingSeq(date);
		Manager.manage(getDataAccessManager()).append("insert into tbsys_rec_log(TRANSSEQ, TRANSDATE, BRANCHNBR, CUSTID, PRODTYPE, PRODID) ").append("values(:seq, :date, 'XXX', :custId, :prdType, :prodId) ").put("seq", seq).put("date", date).put("custId", inputVO.getCustId()).put("prdType", inputVO.getPrdType()).put("prodId", defaultIfEmpty(inputVO.getPrdId(), inputVO.getPrdType())).update();
		this.sendRtnObject(String.format("客戶ID:%s、商品類別:%s、錄音序號:%s", inputVO.getCustId(), inputVO.getPrdType(), seq));
	}

	/**
	 * 取得今日錄音序號最大值。 錄音序號 : 今日日期六碼 + 流水號六碼 自訂序號從900000開始，如果最大值大於此數，返回最大值加1，否則返回此數
	 *
	 * @param date
	 */
	private String getRecordingSeq(String date) throws JBranchException {
		return ((Map) (Manager.manage(getDataAccessManager()).append("select substr(:date, 3, 6) || ").append("       case when MAX_SEQ >= 900000 then MAX_SEQ + 1 else 900000 end SEQ ").append("from ( ").append("    select max(substr(TRANSSEQ, '7', '6')) MAX_SEQ ").append("    from TBSYS_REC_LOG ").append("    where TRANSDATE = :date ").append("      and regexp_like(TRANSSEQ, '[[:digit:]]{12}')) ").put("date", date).query().get(0))).get("SEQ").toString();
	}

	/**
	 * 下載【上傳檔案已新增基金資訊】範例檔
	 **/
	public void uploadFundInfoExample(Object body, IPrimitiveMap header) throws JBranchException {
		CSVUtil csv = new CSVUtil();
		csv.setFileName(UUID.randomUUID().toString());
		csv.setHeader(new String[] { "商品代碼", "基金中文名稱", "商品風險等級", "計價幣別" });
		csv.addRecord(new String[] { "TEST", "測試基金", "P1", "TWD" });
		notifyClientToDownloadFile(csv.generateCSV(), "上傳指定商品代碼範例.csv");
	}

	/**
	 * 上傳檔案以新增基金資訊
	 **/
	public void uploadFundInfo(Object body, IPrimitiveMap header) throws JBranchException {
		setInputVO(body);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(AccessContext.tempPath, inputVO.getFileName());
		Map<String, List> msgMap = new HashMap();
		msgMap.put("VALID", new ArrayList()); // 有執行匯入
		msgMap.put("INVALID", new ArrayList()); // 資料項格式不對，無執行匯入
		/** 忽略 Header，若有資料則執行 **/
		if (dataCsv.size() > 1)
			for (int i = 1; i < dataCsv.size(); i++) {
				if (fundDataEnough(dataCsv.get(i))) {
					Map fundMap = getFundMap(dataCsv.get(i));
					try {
						insertFund(fundMap);
						insertFundBonusInfo(fundMap);
						fundMap.put("MSG", "匯入成功！");
					} catch (Exception e) {
						fundMap.put("MSG", e.getMessage());
					}
					msgMap.get("VALID").add(fundMap);
				} else
					msgMap.get("INVALID").add(String.format("第 %s 行格式錯誤！", i));
			}
		this.sendRtnObject(msgMap);
	}

	/**
	 * 將上傳的基金資訊新增到 TBPRD_FUND_BONUSINFO
	 **/
	private void insertFundBonusInfo(Map fundMap) throws JBranchException {
		exeUpdateForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder().append("insert into TBPRD_FUND_BONUSINFO(PRD_ID, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE) ").append("values (:prdId, 1, sysdate, 'IT0123', 'IT0123', sysdate) ").toString()).setObject("prdId", fundMap.get("PRD_ID")));
	}

	/**
	 * 將上傳的基金資訊新增到 TBPRD_FUND
	 **/
	private void insertFund(Map fundMap) throws JBranchException {
		exeUpdateForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder().append("insert into TBPRD_FUND(PRD_ID, FUND_CNAME, IS_SALE, RISKCATE_ID, CURRENCY_STD_ID, QUOTAS, FLAG, ").append("                       VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE) ").append("values (:prdId, :fundName, 1, :riskId, :currencyId, 1, 2, 1, sysdate, 'IT0123', 'IT0123', sysdate) ").toString()).setObject("prdId", fundMap.get("PRD_ID")).setObject("fundName", fundMap.get("FUND_CNAME")).setObject("riskId", fundMap.get("RISKCATE_ID")).setObject("currencyId", fundMap.get("CURRENCY_STD_ID")));
	}

	/**
	 * 將上傳基金檔的資料欄對應成 Map
	 **/
	private Map getFundMap(String[] row) {
		Map fundMap = new HashMap();
		fundMap.put("PRD_ID", row[0]);
		fundMap.put("FUND_CNAME", row[1]);
		fundMap.put("RISKCATE_ID", row[2]);
		fundMap.put("CURRENCY_STD_ID", row[3]);
		return fundMap;
	}

	/**
	 * 基金資料是否備齊
	 **/
	private boolean fundDataEnough(String[] srcData) {
		final int length = 4;
		for (int i = 0; i < length; i++)
			if (StringUtils.isBlank(srcData[i]))
				return false;
		return true;
	}

	/**
	 * 查詢上線必要更新清單
	 */
	public void queryProdMustDo(Object body, IPrimitiveMap header) throws JBranchException {
		setInputVO(body);
		ELI_MUST_UPDATE_RECORD vo = inputVO.getMustUpdateRecord();

		outputVO.setRecordList(configureQueryMustDoRecord(vo).query());
		sendRtnObject(outputVO);
	}

	/** 配置查詢「上線必要更新表」SQL 與參數 **/
	private Manager configureQueryMustDoRecord(ELI_MUST_UPDATE_RECORD vo) throws JBranchException {
		return Manager.manage(getDataAccessManager()).append("select REC.SEQ, REC.SNAP_DATE, REC.REDMINE, REC.PRINCIPAL, REC.CLIENT, REC.TYPE, ").append("       REC.FILE_NAME, REC.MEMO, REC.CREATOR, REC.CREATETIME, REC.MODIFIER, REC.LASTUPDATE ").append("from ELI_MUST_UPDATE_RECORD REC where 1=1 ").condition(isNotBlank(vo.getSNAP_DATE()), "and regexp_like(REC.SNAP_DATE, :snapDate) ", "snapDate", vo.getSNAP_DATE()).condition(isNotBlank(vo.getREDMINE()), "and regexp_like(REC.REDMINE, :redmine) ", "redmine", vo.getREDMINE()).condition(isNotBlank(vo.getPRINCIPAL()), "and regexp_like(REC.PRINCIPAL, :principal) ", "principal", vo.getPRINCIPAL()).condition(isNotBlank(vo.getCLIENT()), "and regexp_like(REC.CLIENT, :client) ", "client", vo.getCLIENT()).condition(isNotBlank(vo.getTYPE()), "and regexp_like(REC.TYPE, :type) ", "type", vo.getTYPE()).condition(isNotBlank(vo.getFILE_NAME()), "and regexp_like(REC.FILE_NAME, :fileName) ", "fileName", vo.getFILE_NAME()).condition(isNotBlank(vo.getMEMO()), "and regexp_like(REC.MEMO, :memo) ", "memo", vo.getMEMO()).condition(isNotBlank(vo.getCREATOR()), "and regexp_like(REC.CREATOR, :creator) ", "creator", vo.getCREATOR()).condition(isNotBlank(vo.getMODIFIER()), "and regexp_like(REC.MODIFIER, :modifier) ", "modifier", vo.getMODIFIER()).append("order by REC.SNAP_DATE desc, REC.CREATETIME desc, REC.LASTUPDATE desc ");
	}

	/**
	 * 新增資料 TO 上線必要更新清單
	 */
	public void insertProdMustDo(Object body, IPrimitiveMap header) throws JBranchException, IOException {
		setInputVO(body);
		ELI_MUST_UPDATE_RECORD vo = inputVO.getMustUpdateRecord();
		// 將 Temp 路徑下的上傳暫存檔移至 Modify Path 路徑下
		Files.move(getFileTempPath(vo.getFILE_NAME()), getFileModifyPath(vo.getREAL_NAME()));

		configureInsertMustDoRecord(vo).update();
		sendRtnObject(null);
	}

	/** 配置新增「上線必要更新表」SQL 與參數 **/
	private Manager configureInsertMustDoRecord(ELI_MUST_UPDATE_RECORD vo) throws JBranchException {
		return Manager.manage(getDataAccessManager()).append("insert into ELI_MUST_UPDATE_RECORD( ").append("    SNAP_DATE, REDMINE, PRINCIPAL, CLIENT, TYPE, FILE_NAME, MEMO, CREATOR, CREATETIME ").append(") values (:snapDate, :redmine, :principal, :client, :type, :fileName, :desc, :creator, sysdate) ").put("snapDate", vo.getSNAP_DATE()).put("redmine", vo.getREDMINE()).put("principal", vo.getPRINCIPAL()).put("client", vo.getCLIENT()).put("type", vo.getTYPE()).put("fileName", vo.getREAL_NAME()).put("desc", vo.getMEMO()).put("creator", SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID));
	}

	/**
	 * 維護指定上線必要更新資料
	 */
	public void updateProdMustDo(Object body, IPrimitiveMap header) throws JBranchException, IOException {
		setInputVO(body);
		ELI_MUST_UPDATE_RECORD vo = inputVO.getMustUpdateRecord();

		// 先確保有資料，再執行檔案搬遷與刪除
		configureUpdateMustDoRecord(vo).update();

		// REAL_NAME 為上傳檔案完成後才會指派的變數，當有上傳檔案時需要作以下動作
		if (isNotBlank(vo.getREAL_NAME())) {
			// 刪除原始附件檔案
			Files.deleteIfExists(getFileModifyPath(vo.getPREV_FILE_NAME()));

			// 將 Temp 路徑下的上傳暫存檔移至 Modify Path 路徑下
			Files.move(getFileTempPath(vo.getFILE_NAME()), getFileModifyPath(vo.getREAL_NAME()));
		}
		sendRtnObject(null);
	}

	/** 配置更新「上線必要更新表」SQL 與參數 **/
	private Manager configureUpdateMustDoRecord(ELI_MUST_UPDATE_RECORD vo) throws JBranchException {
		return Manager.manage(getDataAccessManager()).append("update ELI_MUST_UPDATE_RECORD REC ").append("set REC.SNAP_DATE = :snapDate, REC.REDMINE = :redmine, REC.PRINCIPAL = :principal, ").append("    REC.CLIENT = :client, REC.TYPE = :type, REC.FILE_NAME = :fileName, REC.MEMO = :memo, ").append("    REC.MODIFIER = :modifier, REC.LASTUPDATE = sysdate ").append("where REC.SEQ = :seq ").put("snapDate", vo.getSNAP_DATE()).put("redmine", vo.getREDMINE()).put("principal", vo.getPRINCIPAL()).put("client", vo.getCLIENT()).put("type", vo.getTYPE()).put("fileName", defaultString(vo.getREAL_NAME(), vo.getFILE_NAME())) // 未上傳檔案，就更新原始附檔名即可
				.put("memo", vo.getMEMO()).put("modifier", SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID)).put("seq", vo.getSEQ());
	}

	/** 下載指定上線必要更新附檔 **/
	public void downloadProdMustDoFile(Object body, IPrimitiveMap header) throws IOException {
		setInputVO(body);
		String fileName = inputVO.getMustUpdateRecord().getFILE_NAME();
		String uuidName = UUID.randomUUID().toString();
		// 將 Modify Path 路徑下的指定檔案複製到 Temp 路徑下以便使用瀏覽器下載
		Files.copy(getFileModifyPath(fileName), getFileTempPath(uuidName));
		notifyClientToDownloadFile("temp" + File.separator + uuidName, fileName);
	}

	/** 取得指定 fileName 在系統 Temp 路徑下的 Path 物件 **/
	private Path getFileTempPath(String fileName) {
		return Paths.get(AccessContext.tempPath).resolve(fileName);
	}

	/** 取得指定 fileName 在系統 MODIFY_PATH 路徑下的 Path 物件 **/
	private Path getFileModifyPath(String fileName) {
		return Paths.get(MODIFY_PATH).resolve(fileName);
	}

	/** 下載上線必要更新說明文件 **/
	public void downloadPrepareProdIntroDoc(Object body, IPrimitiveMap header) throws IOException {
		String uuidName = UUID.randomUUID().toString();
		// 將 Modify Path 路徑下的說明文件複製到 Temp 路徑下以便使用瀏覽器下載
		Files.copy(getFileModifyPath(INTRO_DOC), getFileTempPath(uuidName));
		notifyClientToDownloadFile("temp" + File.separator + uuidName, "說明文件.docx");
	}

	/** 刪除指定上線必要更新紀錄 **/
	public void deleteProdMustDoRecord(Object body, IPrimitiveMap header) throws IOException, JBranchException {
		setInputVO(body);
		ELI_MUST_UPDATE_RECORD vo = inputVO.getMustUpdateRecord();

		configureDeleteMustDoRecord(vo).update();

		Files.deleteIfExists(getFileModifyPath(vo.getFILE_NAME()));
		sendRtnObject(null);
	}

	/** 配置刪除「上線必要更新表」SQL 與參數 **/
	private Manager configureDeleteMustDoRecord(ELI_MUST_UPDATE_RECORD vo) throws JBranchException {
		return Manager.manage(getDataAccessManager()).append("delete ELI_MUST_UPDATE_RECORD REC ").append("where REC.SEQ = :seq ").put("seq", vo.getSEQ());
	}

	/**
	 * 更改客戶C值
	 *
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void changeCValue(Object body, IPrimitiveMap header) throws Exception {
        setInputVO(body);
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat ft2 = new SimpleDateFormat("ddMMyyyy");
        logger.info("IT0123_ChangeCValue:  更新TBKYC_INVESTOREXAM_M以及TBKYC_INVESTOREXAM_M_HIS");
        //更新TBKYC_INVESTOREXAM_M以及TBKYC_INVESTOREXAM_M_HIS
        DataAccessManager dam = getDataAccessManager();
        QueryConditionIF qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sql = new StringBuffer().append(" select * from TBKYC_INVESTOREXAM_M ").append(" where CUST_ID = :custID ");
        qc.setObject("custID", inputVO.getCustId());
        qc.setQueryString(sql.toString());
        List<Map<String, Object>> list = dam.exeQuery(qc);
        Date storeDate = null;
        if(list.size() < 1){
        	String seq = kyc310.nextSeqLpad();
        	TBKYC_INVESTOREXAM_MVO MainVO = new TBKYC_INVESTOREXAM_MVO();
        	MainVO.setSEQ(seq);
        	MainVO.setCUST_ID(inputVO.getCustId());
        	MainVO.setEXAM_VERSION("KYC202004145861");
        	MainVO.setCUST_RISK_AFR(inputVO.getcValue());
            MainVO.setCreator("IT0123");
            MainVO.setSTATUS("03");
            //FOR CBS測試日期修改
     		Date testDate = ft.parse(cbsService.getCBSTestDate());
     		MainVO.setCREATE_DATE(new Timestamp(testDate.getTime()));
     		Calendar cal = Calendar.getInstance();
     		cal.setTime(testDate);
     		cal.add(cal.DATE, 364); //KYC截止日(加十二個月減一天)
     		storeDate = cal.getTime();
     		MainVO.setEXPIRY_DATE(new Timestamp(storeDate.getTime()));//KYC截止日(加十二個月減一天
     		dam.create(MainVO);
     		exeUpdateForQcf(genDefaultQueryConditionIF().setQueryString(" Insert into TBKYC_INVESTOREXAM_M_HIS  Select * from TBKYC_INVESTOREXAM_M  Where cust_id = :cust_id ").setObject("cust_id", inputVO.getCustId().toUpperCase()));
        }else{
        TBKYC_INVESTOREXAM_MVO MainVO = (TBKYC_INVESTOREXAM_MVO) dam.findByPKey(TBKYC_INVESTOREXAM_MVO.TABLE_UID, list.get(0).get("SEQ").toString());
        MainVO.setCUST_RISK_AFR(inputVO.getcValue());
        MainVO.setCreator("IT0123");
        MainVO.setSTATUS("03");
        //FOR CBS測試日期修改
 		Date testDate = ft.parse(cbsService.getCBSTestDate());
 		MainVO.setCREATE_DATE(new Timestamp(testDate.getTime()));
 		Calendar cal = Calendar.getInstance();
 		cal.setTime(testDate);
 		cal.add(cal.DATE, 364); //KYC截止日(加十二個月減一天)
 		storeDate = cal.getTime();
 		MainVO.setEXPIRY_DATE(new Timestamp(storeDate.getTime()));//KYC截止日(加十二個月減一天
 		dam.update(MainVO);
        exeUpdateForQcf(genDefaultQueryConditionIF().setQueryString(" delete from TBKYC_INVESTOREXAM_M_HIS  Where cust_id = :cust_id and SEQ =:seq ").setObject("cust_id", inputVO.getCustId().toUpperCase()).setObject("seq", MainVO.getSEQ()));
 		exeUpdateForQcf(genDefaultQueryConditionIF().setQueryString(" Insert into TBKYC_INVESTOREXAM_M_HIS  Select * from TBKYC_INVESTOREXAM_M  Where cust_id = :cust_id ").setObject("cust_id", inputVO.getCustId().toUpperCase()));
        }
        
        
        
        logger.info("IT0123_ChangeCValue:  上送電文");
        //上送電文
        send067157(ft2.format(storeDate));
        logger.info("IT0123_ChangeCValue:  執行完畢");
        this.sendRtnObject(ft.format(new Date())  +"  修改完畢!");
    }

	public void send067157(String expiryDate) throws Exception {
		String filDate = cbsService.getCBSTestDate().substring(0,8);  //申請日 yyyymmdd
		KYC311InputVO kyc311InputVO = new KYC311InputVO();
		kyc311InputVO.setCUST_ID(inputVO.getCustId());
		kyc311InputVO.setCUST_RISK_AFR(inputVO.getcValue());
		kyc311InputVO.setBRANCH("999");
		kyc311InputVO.setKYC_TEST_DATE(cbsService.changeDate(filDate, 1));
		kyc311InputVO.setEXPIRY_DATE(expiryDate);
		sot701.kycUpdateCValue(kyc311InputVO);
	}

	public void printWorkstationInfo(Object body, IPrimitiveMap header) {
		Map<String, Branch> branchs = DataManager.getBranch();
		String msg = "WorkStation Info：" + branchs.toString();
		logger.info(msg + "\n" + "size：" + msg.length());
		this.sendRtnObject(msg);
	}
}