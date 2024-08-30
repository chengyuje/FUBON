package com.systex.jbranch.app.server.fps.cmmgr014;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.systex.jbranch.app.server.fps.crm381.CRM381InputVO;
import com.systex.jbranch.app.server.fps.crm381.CRM381OutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSFTPVO;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * FTP傳檔設定 Notics： 修改歷程及說明
 * 
 * @author 施陽欽
 * @date 2009-10-30
 * @modifyDate 2009-11-01
 * @since Version
 * @spec 修改註記 V0.1 2009/10/27 初版 余嘉雯
 * 
 */
@Component("cmmgr014")
@Scope("request")
public class CMMGR014 extends BizLogic {
	private CMMGR014InputVO cMMGR014InputVO_obj = null;
	private DataAccessManager dam_obj = null;

	/**
	 * 初始化查詢畫面
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void init(Object body, IPrimitiveMap header) throws JBranchException {
		// 取得畫面資料
		cMMGR014InputVO_obj = (CMMGR014InputVO) body;

		// 商業邏輯
		dam_obj = this.getDataAccessManager();
		QueryConditionIF condition = dam_obj.getQueryCondition();
		condition.setQueryString("select HOSTID,IP from TBSYSREMOTEHOST where 1=1");
		List<Map<String, Object>> hostId_list = new ArrayList<Map<String, Object>>();
		hostId_list = dam_obj.exeQuery(condition);
		if (hostId_list.size() > 0) {
			CMMGR014OutputVO resultVO = new CMMGR014OutputVO();
			resultVO.setHostIdList(hostId_list);
			sendRtnObject(resultVO);
		} else {
			throw new APException("ehl_01_common_001");
		}
	}

	/**
	 * 查询功能
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		// 取得畫面資料
		cMMGR014InputVO_obj = (CMMGR014InputVO) body;

		// 商業邏輯
		findFromDB();
	}

	/**
	 * 操作(新增、修改、刪除)
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void operation(Object body, IPrimitiveMap header) throws JBranchException {
		// 取得畫面資料
		cMMGR014InputVO_obj = (CMMGR014InputVO) body;

		// 商業邏輯
		dam_obj = this.getDataAccessManager();
		if (cMMGR014InputVO_obj.getOperType().equals("Create")) {
			QueryConditionIF queryCondition = dam_obj.getQueryCondition();

			StringBuffer sql_sb = new StringBuffer();
			sql_sb.append("select *" + " from TBSYSFTP IT01" + " where IT01.FTPSETTINGID = ? ");
			queryCondition.setQueryString(sql_sb.toString());
			queryCondition.setString(1, cMMGR014InputVO_obj.getTipFtpSettingId().trim());
			List<Map<String, Object>> data_list = dam_obj.exeQuery(queryCondition);

			// 若有資料,顯示錯誤訊息common_005,程式結束
			if (data_list.size() > 0) {
				throw new APException("ehl_01_common_005");
			} else {
				TBSYSFTPVO tbsysftpVO = new TBSYSFTPVO();
				tbsysftpVO.setFTPSETTINGID(cMMGR014InputVO_obj.getTipFtpSettingId());
				tbsysftpVO.setSRCDIRECTORY(cMMGR014InputVO_obj.getTipSrcDirectory());
				tbsysftpVO.setSRCFILENAME(cMMGR014InputVO_obj.getTipSrcFileName());
				tbsysftpVO.setCHECKFILE(cMMGR014InputVO_obj.getTipCheckFile());
				tbsysftpVO.setDESDIRECTORY(cMMGR014InputVO_obj.getTipDesDirectory());
				tbsysftpVO.setDESFILENAME(cMMGR014InputVO_obj.getTipDesFileName());
				tbsysftpVO.setREPEAT(cMMGR014InputVO_obj.getTipRepeat());
				tbsysftpVO.setREPEATINTERVAL(cMMGR014InputVO_obj.getTipRepeatInterval());
				tbsysftpVO.setHOSTID(cMMGR014InputVO_obj.getCmbHostId());
				// tbsysftpVO.setSRCDELETE(cMMGR014InputVO_obj.getSrcDelete());
				dam_obj.create(tbsysftpVO);

				// 新增成功，重新查詢
				sendRtnMsg("ehl_01_common_001");
				sendRtnObject(null);

				updateSrcDelete(cMMGR014InputVO_obj.getTipFtpSettingId(), cMMGR014InputVO_obj.getSrcDelete());
			}
		} else if (cMMGR014InputVO_obj.getOperType().equals("Update")) {
			TBSYSFTPVO tbsysftpVO = new TBSYSFTPVO();
			tbsysftpVO = (TBSYSFTPVO) dam_obj.findByPKey(tbsysftpVO.TABLE_UID, cMMGR014InputVO_obj.getTipFtpSettingId());
			if (tbsysftpVO != null) {
				tbsysftpVO.setFTPSETTINGID(cMMGR014InputVO_obj.getTipFtpSettingId());
				tbsysftpVO.setSRCDIRECTORY(cMMGR014InputVO_obj.getTipSrcDirectory());
				tbsysftpVO.setSRCFILENAME(cMMGR014InputVO_obj.getTipSrcFileName());
				tbsysftpVO.setCHECKFILE(cMMGR014InputVO_obj.getTipCheckFile());
				tbsysftpVO.setDESDIRECTORY(cMMGR014InputVO_obj.getTipDesDirectory());
				tbsysftpVO.setDESFILENAME(cMMGR014InputVO_obj.getTipDesFileName());
				tbsysftpVO.setREPEAT(cMMGR014InputVO_obj.getTipRepeat());
				tbsysftpVO.setREPEATINTERVAL(cMMGR014InputVO_obj.getTipRepeatInterval());
				tbsysftpVO.setHOSTID(cMMGR014InputVO_obj.getCmbHostId());
				// tbsysftpVO.setSRCDELETE(cMMGR014InputVO_obj.getSrcDelete());
				dam_obj.update(tbsysftpVO);

				// 修改成功，重新查詢
				sendRtnMsg("ehl_01_common_002");
				sendRtnObject(null);

				updateSrcDelete(cMMGR014InputVO_obj.getTipFtpSettingId(), cMMGR014InputVO_obj.getSrcDelete());
			}
		} else if (cMMGR014InputVO_obj.getOperType().equals("Delete")) {
			// TBCRM_DKYC_QSTN_SET
			TBSYSFTPVO vo = new TBSYSFTPVO();
			vo = (TBSYSFTPVO) dam_obj.findByPKey(TBSYSFTPVO.TABLE_UID, cMMGR014InputVO_obj.getTipFtpSettingId());
			if (vo != null) {
				dam_obj.delete(vo);
			} else {
				// 顯示資料不存在
				throw new APException("ehl_01_common_001");
			}
			sendRtnObject(null);
		} else {
		}
	}

	/**
	 * 設置查詢SQL
	 * 
	 * @param queryCondition
	 * @return queryCondition
	 */
	private QueryConditionIF setQuerySQL(QueryConditionIF queryCondition) {

		String tipFtpSettingId = cMMGR014InputVO_obj.getTipFtpSettingId().trim();
		String tipSrcDirectory = cMMGR014InputVO_obj.getTipSrcDirectory().trim();
		String tipSrcFileName = cMMGR014InputVO_obj.getTipSrcFileName().trim();
		String tipCheckFile = cMMGR014InputVO_obj.getTipCheckFile().trim();
		String tipDesDirectory = cMMGR014InputVO_obj.getTipDesDirectory().trim();
		String tipDesFileName = cMMGR014InputVO_obj.getTipDesFileName().trim();
		Integer tipRepeat = cMMGR014InputVO_obj.getTipRepeat();
		Integer tipRepeatInterval = cMMGR014InputVO_obj.getTipRepeatInterval();
		String cmbHostId = cMMGR014InputVO_obj.getCmbHostId().trim();
		// Integer srcDelete = cMMGR014InputVO_obj.getSrcDelete();

		ArrayList<String> sql_list = new ArrayList<String>();
		StringBuffer sql_sb = new StringBuffer();
		sql_sb.append("select IT01.* from  TBSYSFTP IT01 where 1=1 ");

		if (tipFtpSettingId != null && !tipFtpSettingId.equals("")) {
			sql_sb.append(" and IT01.FTPSETTINGID like ? ");
			sql_list.add("%" + tipFtpSettingId + "%");
		}
		if (tipSrcDirectory != null && !tipSrcDirectory.equals("")) {
			sql_sb.append(" and IT01.SRCDIRECTORY like ? ");
			sql_list.add("%" + tipSrcDirectory + "%");
		}
		if (tipSrcFileName != null && !tipSrcFileName.equals("")) {
			sql_sb.append(" and IT01.SRCFILENAME like ? ");
			sql_list.add("%" + tipSrcFileName + "%");
		}
		if (tipCheckFile != null && !tipCheckFile.equals("")) {
			sql_sb.append(" and IT01.CHECKFILE like ? ");
			sql_list.add("%" + tipCheckFile + "%");
		}
		if (tipDesDirectory != null && !tipDesDirectory.equals("")) {
			sql_sb.append(" and IT01.DESDIRECTORY like ? ");
			sql_list.add("%" + tipDesDirectory + "%");
		}
		if (tipDesFileName != null && !tipDesFileName.equals("")) {
			sql_sb.append(" and IT01.DESFILENAME like ? ");
			sql_list.add("%" + tipDesFileName + "%");
		}
		if (tipRepeat != null) {
			sql_sb.append(" and IT01.REPEAT like ? ");
			sql_list.add("%" + tipRepeat + "%");
		}
		if (tipRepeatInterval != null) {
			sql_sb.append(" and IT01.REPEATINTERVAL like ? ");
			sql_list.add("%" + tipRepeatInterval + "%");
		}
		if (cmbHostId != null && !cmbHostId.equals("")) {
			sql_sb.append(" and IT01.HOSTID like ? ");
			sql_list.add("%" + cmbHostId + "%");
		}
		// if (srcDelete != null) {
		// sql_sb.append(" and IT01.SRCDELETE like ? ");
		// sql_list.add("%" + srcDelete + "%");
		// }
		sql_sb.append(" order by IT01.FTPSETTINGID");
		queryCondition.setQueryString(sql_sb.toString());
		for (int sql_i = 0; sql_i < sql_list.size(); sql_i++) {
			queryCondition.setString(sql_i + 1, sql_list.get(sql_i));
		}

		return queryCondition;
	}

	/**
	 * 查詢DB
	 * 
	 * @throws JBranchException
	 */
	private void findFromDB() throws JBranchException {
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition();

		queryCondition = setQuerySQL(queryCondition);

		ResultIF data_list = dam.executePaging(queryCondition, cMMGR014InputVO_obj.getCurrentPageIndex() + 1, cMMGR014InputVO_obj.getPageCount());

		// 將資料傳回client端
		if (data_list.size() != 0) {
			CMMGR014OutputVO outputVO = new CMMGR014OutputVO();
			outputVO.setCurrentPageIndex(cMMGR014InputVO_obj.getCurrentPageIndex());
			outputVO.setDataList(data_list);
			outputVO.setTotalPage(data_list.getTotalPage());
			outputVO.setTotalRecord(data_list.getTotalRecord());
			sendRtnObject(outputVO);
		} else {
			throw new APException("ehl_01_common_017");
		}
	}

	private void updateSrcDelete(String ftpsettingID, String YN) {
		System.out.println("### " + ftpsettingID + " == " + YN + "###");
		Connection conn = null;
		PreparedStatement st = null;
		try {
			com.systex.jbranch.platform.common.dataaccess.datasource.DataSource ds = new com.systex.jbranch.platform.common.dataaccess.datasource.DataSource();
			SessionFactory sf = (SessionFactory) PlatformContext.getBean(ds.getDataSource());
			DataSource dataSource = SessionFactoryUtils.getDataSource(sf);
			conn = dataSource.getConnection();
			st = conn.prepareStatement("update TBSYSFTP set SRCDELETE=? WHERE FTPSETTINGID = ?");
			st.setString(1, YN);
			st.setString(2, ftpsettingID);
			int row = st.executeUpdate();
			System.out.println("### " + ftpsettingID + " == " + YN + " ## " + row + "###");
			conn.commit();
			st.close();
		} catch (Exception e) {
			// this.logger.warn("updateMemberReviewLongRaw error", e);
		} finally {
			if (st != null)
				try {
					st.close();
				} catch (Exception e) {
				}
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e) {
				}
		}
	}

	// 查詢加密key值給前端做選取
	public void inquireEntryptKey(Object body, IPrimitiveMap header) throws JBranchException {
		cMMGR014InputVO_obj = (CMMGR014InputVO) body;
		CMMGR014OutputVO return_VO = new CMMGR014OutputVO();
		dam_obj = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select distinct KEY from TBSYSFTPKEY ");
		queryCondition.setQueryString(sql.toString());
		List list = dam_obj.exeQuery(queryCondition);
		return_VO.setKeyList(list);

		dam_obj = this.getDataAccessManager();
		sql.setLength(0);
		sql.append("select * from TBSYSFTPKEY ");
		sql.append("where FTPSETTINGID = '" + cMMGR014InputVO_obj.getTipFtpSettingId() + "'");
		queryCondition.setQueryString(sql.toString());
		List<Map> encryptKey = dam_obj.exeQuery(queryCondition);
		if (encryptKey.size() > 0) {
			return_VO.setEncryptKey(encryptKey.get(0).get("KEY").toString());
		}
		sendRtnObject(return_VO);
	}
	


	// 跟加密key值有關的operation 包含新增、刪除、修改
	public void operationEncryptKey(Object body, IPrimitiveMap header) throws JBranchException {
		cMMGR014InputVO_obj = (CMMGR014InputVO) body;
		
		if (cMMGR014InputVO_obj.getOperKeyType().equals("Create")) {
			exeUpdateForMap(getInsertKeySql(), getInsertKeyParamMap(cMMGR014InputVO_obj));

		}
		if (cMMGR014InputVO_obj.getOperKeyType().equals("Update")) {
			exeUpdateForMap(getUpdateKeySql(), getUpdateKeyParamMap(cMMGR014InputVO_obj));
		}

		if (cMMGR014InputVO_obj.getOperKeyType().equals("Delete")) {
			exeUpdateForMap(getDeleteKeySql(), getDeleteKeyParamMap(cMMGR014InputVO_obj));
		}
	}

	// 新增加密key值用sql
	private String getInsertKeySql() {
		return new StringBuffer().append("insert into TBSYSFTPKEY (FTPSETTINGID,KEY) values (:FTPSETTINGID,:KEY)").toString();
	}

	// 新增加密key值用map
	private Map getInsertKeyParamMap(CMMGR014InputVO cMMGR014InputVO_obj) throws JBranchException {
		HashMap param = new HashMap();

		param.put("KEY", cMMGR014InputVO_obj.getEncryptKey());
		param.put("FTPSETTINGID", cMMGR014InputVO_obj.getTipFtpSettingId());
		return param;
	}

	// 更新加密key值用sql
	private String getUpdateKeySql() {
		return new StringBuffer().append("update TBSYSFTPKEY ").append("set KEY = :KEY ").append("where FTPSETTINGID = :FTPSETTINGID ").toString();
	}

	// 更新加密key值用map
	private Map getUpdateKeyParamMap(CMMGR014InputVO cMMGR014InputVO_obj) throws JBranchException {
		HashMap param = new HashMap();

		param.put("KEY", cMMGR014InputVO_obj.getEncryptKey());
		param.put("FTPSETTINGID", cMMGR014InputVO_obj.getTipFtpSettingId());
		return param;
	}

	// 刪除加密key值用sql
	private String getDeleteKeySql() {
		return new StringBuffer().append("delete from TBSYSFTPKEY where FTPSETTINGID = :FTPSETTINGID").toString();
	}

	// 刪除加密key值用map
	private Map getDeleteKeyParamMap(CMMGR014InputVO cMMGR014InputVO_obj) throws JBranchException {
		HashMap param = new HashMap();
		param.put("FTPSETTINGID", cMMGR014InputVO_obj.getTipFtpSettingId());
		return param;
	}

}
