package com.systex.jbranch.platform.common.report.generator.linemode;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.platform.common.dataManager.Branch;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.PlatFormVO;
import com.systex.jbranch.platform.common.dataManager.PlatformVOHelper;
import com.systex.jbranch.platform.common.dataManager.ServerTransaction;
import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSREPORTDETAILPK;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSREPORTDETAILVO;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSREPORTMASTERPK;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSREPORTMASTERVO;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.common.util.ThreadDataPool;

public class LineModeReportDetailStore implements StoreIF{
	
	private Logger logger = LoggerFactory.getLogger(LineModeReportDetailStore.class);
	private DataAccessManager dataAccessManager;
	
	public String saveReportMaster(String txnCode, String reportName, String devId, int totalPageNum, String rePrint) throws Exception{
		return saveReportMaster(txnCode, reportName, devId, totalPageNum, rePrint, null);
	}
	public String saveReportMaster(String txnCode, String reportName, String devId, int totalPageNum, String rePrint, Object others) throws Exception{
		
		String reportId = null;
		
		UUID uuid = (UUID) ThreadDataPool.getData(ThreadDataPool.KEY_UUID);

		Branch branch = DataManager.getBranch(uuid);
		String branchId = null;
		if(branch != null){
			branchId = branch.getBrchID();
		}
		SerialNumberUtil serialNO =  new SerialNumberUtil();
		String snid = "RPT.SEQNO." + branchId;
		try{
			reportId = serialNO.getNextSerialNumber(snid);
		}catch(Exception e){
			//ignore
		}
		if(reportId == null){
			serialNO.createNewSerial(snid, "0", (Integer) null, (String) null, (Timestamp) null, new Integer(1), new Long(Long.MAX_VALUE), "Y", new Long(1), "SYSTEM");
			reportId = "1";
		}
		
		TBSYSREPORTMASTERPK masterPK = new TBSYSREPORTMASTERPK();
		TBSYSREPORTMASTERVO masterVO = new TBSYSREPORTMASTERVO();
		
		ServerTransaction st = DataManager.getServerTransaction(uuid);
		String requestId = null;
		String trmSEQ = null;
		if(st != null){
			PlatFormVO platformVO = st.getPlatFormVO();
			requestId = (String) platformVO.getVar(PlatformVOHelper.REQUEST_ID);
			trmSEQ = (String) platformVO.getVar(PlatformVOHelper.TRMSEQ);
		}else{
			trmSEQ ="99";
		}
		
		if("".equals(requestId)){
			requestId = null;
		}
		
		// David 2013/12/04, 將序號規則改成 '1'+'櫃台機編號'+'時分'+'流水號',共十位	
		StringBuilder gen_id= new StringBuilder();
		gen_id.append('1');
		gen_id.append(trmSEQ);		
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
		String timeString = sdf.format(date);
		gen_id.append(timeString);
		
		int sn = new BigDecimal(reportId).intValue() % 1000;
		
		gen_id.append(StringUtil.leftPad(Integer.toString(sn), 3, "0"));
		
		//masterPK.setGEN_ID(new BigDecimal(reportId));
		masterPK.setGEN_ID(new BigDecimal(gen_id.toString()));
		masterPK.setBRNO(branchId);
		masterVO.setcomp_id(masterPK);
		masterVO.setTXNCODE(txnCode);
		masterVO.setREPORTNAME(reportName);
		masterVO.setREQUEST_ID(requestId);
		masterVO.setTRMSEQ(trmSEQ);
		masterVO.setDEVICE(devId);
		masterVO.setREPRINT(rePrint);
		if(others != null){
			byte[] bytes = ObjectUtil.getBytes(others);
			Blob blob = ObjectUtil.byteArrToBlob(bytes);
			masterVO.setOTHERS(blob);
		}
		dataAccessManager.create(masterVO);
		//return reportId;
		return gen_id.toString();
	}
	
	@Override
	public void updateReportMasterToFinshed(String reportId) throws Exception {
		
		UUID uuid = (UUID) ThreadDataPool.getData(ThreadDataPool.KEY_UUID);

		Branch branch = DataManager.getBranch(uuid);
		String branchId = null;
		if(branch != null){
			branchId = branch.getBrchID();
		}
		
		TBSYSREPORTMASTERPK masterPK = new TBSYSREPORTMASTERPK();
		masterPK.setGEN_ID(new BigDecimal(reportId));
		masterPK.setBRNO(branchId);
		TBSYSREPORTMASTERVO masterVO = (TBSYSREPORTMASTERVO) dataAccessManager.findByPKey(TBSYSREPORTMASTERVO.TABLE_UID, masterPK);
		if(masterVO == null){
			throw new JBranchException("找不到reportId[" + reportId + "]");
		}
		masterVO.setFINISHED("Y");
		dataAccessManager.update(masterVO);
	}
	
	public void saveReportDetail(String json,int reportId, int pageNumber) throws Exception{
		throw new JBranchException("interface saveReportDetail(String json,int reportId, int pageNumber) was not supported any more!");
	}
	
	public void saveReportDetail(String json,String reportId, int pageNumber) throws Exception{
		
		UUID uuid = (UUID) ThreadDataPool.getData(ThreadDataPool.KEY_UUID);

		Branch branch = DataManager.getBranch(uuid);
		String branchId = null;
		if(branch != null){
			branchId = branch.getBrchID();
		}
		
		TBSYSREPORTDETAILVO detailVO = new TBSYSREPORTDETAILVO();
		TBSYSREPORTDETAILPK pk =new TBSYSREPORTDETAILPK();
		Blob blob = ObjectUtil.byteArrToBlob(json.getBytes("UTF-8"));
		pk.setGEN_ID(new BigDecimal(reportId));
		pk.setPAGENO(pageNumber);
		pk.setBRNO(branchId);
		detailVO.setcomp_id(pk);
		detailVO.setREPORTDATA(blob);
		dataAccessManager.create(detailVO);
		logger.debug("saveReportDetail rptSeqNo=="+reportId);
	}

	/**
	 * @return the dataAccessManager
	 */
	public DataAccessManager getDataAccessManager() {
		return dataAccessManager;
	}

	/**
	 * @param dataAccessManager the dataAccessManager to set
	 */
	public void setDataAccessManager(DataAccessManager dataAccessManager) {
		this.dataAccessManager = dataAccessManager;
	}
}
