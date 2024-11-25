package com.systex.jbranch.app.server.fps.sot701;

import com.systex.jbranch.app.server.fps.kyc311.KYC311InputVO;
import com.systex.jbranch.app.server.fps.sot701.ruler.AcctRuler;
import com.systex.jbranch.app.server.fps.sot709.PeriodFeeRateVO;
import com.systex.jbranch.app.server.fps.sot709.SOT709;
import com.systex.jbranch.app.server.fps.sot709.SOT709InputVO;
import com.systex.jbranch.app.server.fps.sot709.SOT709OutputVO;
import com.systex.jbranch.app.server.fps.sot710.SOT710;
import com.systex.jbranch.app.server.fps.sot710.SOT710InputVO;
import com.systex.jbranch.app.server.fps.sot710.SOT710OutputVO;
import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.cbs.dao.*;
import com.systex.jbranch.fubon.commons.cbs.service.*;
import com.systex.jbranch.fubon.commons.cbs.vo._060425_060433.CBS060433OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._062171_062171.CBS062171OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067157_067157.CBS067157OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._085081_085105.CBS085105OutputDetailsVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.TxHeadVO;
import com.systex.jbranch.fubon.commons.esb.vo.MVC110001.MVC110001InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.afee011.AFEE011InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.afee011.AFEE011OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb032168.EB032168InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb032168.EB032168OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc032151.FC032151InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc032151.FC032151OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc032153.FC032153InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc032154.FC032154InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc032154.FC032154OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc032154.FC032154OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc032671.FC032671InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc032675.FC032675InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc032675.FC032675OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc032675.FC032675OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc81.FC81InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc81.FC81OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fp032151.FP032151OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fp032671.FP032671OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.fp032671.FP032671OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fp032675.FP032675OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nb052650.NB052650InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nb052650.NB052650OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfee011.NFEE011InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfee011.NFEE011OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfee012.NFEE012InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfee012.NFEE012OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfei002.NFEI002InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfei002.NFEI002OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfei002.NFEI002OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfei003.NFEI003InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfei003.NFEI003OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfei003.NFEI003OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njchklc2.NJCHKLC2InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njchklc2.NJCHKLC2OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nkne01.NKNE01InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nkne01.NKNE01OutputVODetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp6a.NMVP6AInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp6a.NMVP6AOutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp6a.NMVP6AOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp8a.NMVP8AInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp8a.NMVP8AOutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp8a.NMVP8AOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sdactq8.SDACTQ8InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sdactq8.SDACTQ8OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.tp032675.TP032675InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms032154.WMS032154InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms032154.WMS032154OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms032275.WMS032275OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms032675.WMS032675OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms032675.WMS032675OutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.validator.routines.BigDecimalValidator;
import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.systex.jbranch.fubon.commons.esb.cons.EsbCrmCons.*;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.*;
import static com.systex.jbranch.platform.server.info.SystemVariableConsts.LOGINBRH;
import static org.apache.commons.lang.StringUtils.*;

/**
 * Created by SebastianWu on 2016/8/10.
 *
 * modified by Sebastian 2016-10-14 查詢客戶註記資料取得客戶姓名改由用 CUST_ID
 * 至資料庫查詢,因該電文不會再回覆客戶相關資料
 *
 * modified by Cathy 2016-10-20 FC032675VO回傳custProType資料 - 專業投資人類型
 *
 * modified by Cathy 2016-10-26 KYC效期取得TABLE修正
 */
@Component("sot701")
@Scope("request")
public class SOT701 extends EsbUtil {
	@Autowired
	private CBSService cbsservice;
	@Autowired
	private MediatorService mediatorService;
	@Autowired
	private FP032671Service fp032671Service;
	@Autowired
	private FC81Service fc81Service;
	@Autowired
	private FP032151Service fp032151Service;
	@Autowired
	private FP032675Service fp032675Service;
	@Autowired
	private WMS032275Service wms032275Service;
	@Autowired
	private FC032659Service fc032659Service;
	@Autowired
	private WMS032675Service wms032675Service;
	@Autowired
	private WMS032154Service wms032154Service;
	@Autowired
    private _067050_067115DAO _067050_067115dao;
	@Autowired
	private _085081_085105DAO _085081_085105dao;
	@Autowired
	private _067050_067101DAO _067050_067101dao;
	@Autowired
	private _067050_067102DAO _067050_067102dao;
	@Autowired
	private _067050_067000DAO _067050_067000dao;
	@Autowired
	private _067050_067112DAO _067050_067112dao;
	@Autowired
    private _067157_067157DAO _067157_067157dao;
	@Autowired
    private _060425_060433DAO _060425_060433dao;
	@Autowired
	private _062171_062171DAO _062171_062171dao;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private String thisClaz = this.getClass().getSimpleName() + ".";
	private DataAccessManager dam = null;
	private SOT701InputVO sot701InputVO;
	private SOT701OutputVO sot701OutputVO;

	private Set<String> trustAcctList;
	private Set<String> debitAcctList;
	private List<AcctVO> acct12List;

	/* const */
	private String ESB_TYPE = EsbFmpJRunConfiguer.ESB_TYPE;

	/**
	 * 與系統日期做比較 傳入日大於等於系統日: TRUE 傳入日小於系統日: FALSE
	 *
	 * @param date
	 * @return Boolean
	 */
	private Boolean compareWithSysDate(Date date) {
		Date sysdate = new Date();

		if (date.compareTo(sysdate) > 0) {
			return Boolean.TRUE;
		} else if (date.compareTo(sysdate) == 0) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	/* 電文log畫面查詢 - start */
	/**
	 * 查詢 ESB log
	 *
	 * @param body
	 * @param header
	 */
	public void queryEsbData(Object body, IPrimitiveMap header) throws JBranchException {
		sot701InputVO = (SOT701InputVO) body;
		sot701OutputVO = new SOT701OutputVO();

		String sno = sot701InputVO.getSno();
		Date esbDate = sot701InputVO.getEsbDate();

		if (StringUtils.isBlank(sno) && esbDate == null) {
			throw new JBranchException("必須輸入交易序號或日期");
		}

		dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);

		StringBuffer sql = new StringBuffer().append("select ESB_SNO, ESB_HTXTID, VERSION, ").append("CREATETIME, CREATOR, MODIFIER, LASTUPDATE ").append("from TBSYS_ESB_LOG where 1=1 ");

		int idx = 1;
		if (StringUtils.isNotBlank(sno)) {
			sql.append("and ESB_SNO like ? ");
			condition.setString(idx++, sno + "%");
		}

		if (esbDate != null) {
			sql.append("and trunc(CREATETIME) = trunc(?) ");
			condition.setDate(idx++, new java.sql.Date(esbDate.getTime()));
		}

		sql.append("group by ESB_SNO, ESB_HTXTID, VERSION, ").append("CREATETIME, CREATOR, MODIFIER, LASTUPDATE ").append("order by ESB_SNO, ESB_HTXTID desc ");

		condition.setQueryString(sql.toString());
		sot701OutputVO.setResultList(dam.exeQuery(condition));

		ResultIF list = dam.executePaging(condition, sot701InputVO.getCurrentPageIndex() + 1, sot701InputVO.getPageCount());

		if (CollectionUtils.isNotEmpty(list)) {
			int totalPage = list.getTotalPage();
			sot701OutputVO.setTotalPage(totalPage);
			sot701OutputVO.setTotalRecord(list.getTotalRecord());

			sot701OutputVO.setCurrentPageIndex(sot701InputVO.getCurrentPageIndex());
			sendRtnObject(sot701OutputVO);
		} else {
			throw new APException("ehl_01_common_009");
		}
	}

	/**
	 * 電文內容
	 *
	 * @param body
	 * @param header
	 */
	public void readEsbFile(Object body, IPrimitiveMap header) throws JBranchException, IOException {
		sot701InputVO = (SOT701InputVO) body;
		sot701OutputVO = new SOT701OutputVO();

		String sno = sot701InputVO.getSno();
		Date esbDate = sot701InputVO.getEsbDate();
		String status = sot701InputVO.getStatus();

		dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sql = new StringBuffer().append("select ESB_FILE ").append("from TBSYS_ESB_LOG where 1=1 ").append("and ESB_SNO = :ESB_SNO ").append("and CREATETIME = :CREATETIME ").append("and ESB_TYPE  = :ESB_TYPE ");

		condition.setObject("ESB_SNO", sno);
		condition.setObject("CREATETIME", esbDate);
		condition.setObject("ESB_TYPE", status);
		condition.setQueryString(sql.toString());

		List<Map<String, Object>> list = dam.exeQuery(condition);

		List resList = new ArrayList();
		Map map = new HashMap();
		if (CollectionUtils.isNotEmpty(list)) {
			// ByteArrayOutputStream out = null;
			try {
				byte[] byteArr = ObjectUtil.blobToByteArr((Blob) list.get(0).get("ESB_FILE"));
				// out = new ByteArrayOutputStream();
				// out.write(byteArr);

				map.put("ESB_FILE", byteArr);
				resList.add(map);
				// } catch (IOException e) {
				// e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				// out.close();
			}
		}

		sot701OutputVO.setResultList(resList);
		sendRtnObject(sot701OutputVO);
	}

	/* 電文log畫面查詢 - end */

	/* 發送電文功能 - start */
	/**
	 * 取得客戶所有資料
	 *
	 * for js client
	 *
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void getSOTCustInfo(Object body, IPrimitiveMap header) throws Exception {
		sot701OutputVO = new SOT701OutputVO();
		sot701OutputVO = this.getSOTCustInfo(body);

		sendRtnObject(sot701OutputVO);
	}

	/**
	 * 取得客戶所有資料: 1. 電文FC032675回傳客戶資料 2. 客戶KYC資料 3. W-8BEN & FACTA資料 4. 期間議價效期 5.
	 * 客戶帳號資料 6. 客戶停損停利資料 7. 查詢客戶是否為首購
	 *
	 * @param body
	 * @return SOT701OutputVO
	 */
	public SOT701OutputVO getSOTCustInfo(Object body) throws Exception {
		sot701OutputVO = new SOT701OutputVO();
		sot701InputVO = (SOT701InputVO) body;

		// 先查客戶主檔是否存在
		String custName = getCustNameByID(sot701InputVO.getCustID());
		if (custName != null) {
			// 查詢客戶註記資料
			sot701OutputVO.setFp032675DataVO(this.getFP032675Data(sot701InputVO));
			// 查詢：KYC等級、KYC效期
			sot701OutputVO.setCustKYCDataVO(this.getCustKycData(sot701InputVO));
			// 查詢客戶W-8BEN //20191120 麗文說走回原本電文取值
			sot701OutputVO.setW8BenDataVO(this.getW8BenData(sot701InputVO)); // this.getCustW8BenFATCA(sot701InputVO)
			// 查詢FATCA客戶註記檢核
			sot701OutputVO.setFatcaDataVO(this.getFatcaData(sot701InputVO));
			// 查詢客戶期間議價效期
			sot701OutputVO.setDueDate(this.getCustFeePrdDueDate(sot701InputVO));
			// 查詢客戶是否為首購
			sot701OutputVO.setIsFirstTrade(this.getIsCustFirstTrade(sot701InputVO));
			// 取得帳號需傳入是否為OBU參數
			String isOBU = sot701OutputVO.getFp032675DataVO().getObuFlag();
			sot701InputVO.setIsOBU(isOBU);

			// 查詢客戶帳號資訊
			sot701OutputVO.setCustAcctDataVO(this.getCustAcctData(sot701InputVO));

			String prodType = sot701InputVO.getProdType();
			String tradeType = sot701InputVO.getTradeType();

			// 客戶停損停利資料
			// 先不預設帶出客戶停損停利資料，待KYC回傳400做完後，網行銀一致，再看要不要開放 2017/06/20
			// 基金申購、贖回再申購；ETF申購，需取得停損停利資料
			// if( (StringUtils.isNotBlank(prodType) &&
			// StringUtils.equals(prodType, "1") &&
			// StringUtils.isNotBlank(tradeType) && tradeType.matches("1|2|5"))
			// || (StringUtils.isNotBlank(prodType) &&
			// StringUtils.equals(prodType, "2") &&
			// StringUtils.isNotBlank(tradeType) && StringUtils.equals("1",
			// tradeType)) ) {
			// sot701OutputVO.setCustPLDataVO(this.getCustPLData(sot701InputVO));
			// } else {
			sot701OutputVO.setCustPLDataVO(new CustPLDataVO());
			// }

			// SI/SN下單需取得客戶Q值
			// if(StringUtils.isNotBlank(prodType) && prodType.matches("4|5")){
			// sot701OutputVO.setGetCustQValue(this.getCustQValue(sot701InputVO));
			// }

			// SI下單需取得客戶利害關係人註記
			// if(StringUtils.isNotBlank(prodType) && StringUtils.equals("4",
			// prodType)){
			// sot701OutputVO.setCustStackholder(this.isCustStakeholder(sot701InputVO));
			// }
			// ETF申購/SI/FCI下單需取得客戶利害關係人註記
			if (StringUtils.isNotBlank(prodType) && prodType.matches("2|4|7")) {
				sot701OutputVO.setCustStakeholder(this.isCustStakeholder(sot701InputVO));
			}
			
			//#1695貸轉投  法人戶不打電文
			if (StringUtils.isNotBlank(sot701InputVO.getCustID()) 
					&& !(cbsservice.getCBSIDCode(sot701InputVO.getCustID()).matches("21|22|23|29|31|32|39"))
					&& sot701InputVO.isNeedFlagNumber()
					) {
				sot701OutputVO.setFlagNumber(getFlagNumber(sot701InputVO.getCustID(), cbsservice.getCBSIDCode(sot701InputVO.getCustID())));	
			} else {
				sot701OutputVO.setFlagNumber(null);
			}
			
		} else {
			// 若客戶不存在，則出錯誤"客戶尚未開戶"
			throw new APException("ehl_01_common_029");
		}
		return sot701OutputVO;
	}

//	/** CBS  刪除此方法，無 JS 用到
//	 * 查詢客戶註記資料
//	 *
//	 * for js client 使用電文: FC032675
//	 *
//	 *
//	 * @param body
//	 * @param header
//	 */
//	public void getFC032675Data(Object body, IPrimitiveMap header) throws Exception {
//		sot701OutputVO = new SOT701OutputVO();
//		sot701OutputVO.setFc032675DataVO(this.getFC032675Data(body));
//
//		sendRtnObject(sot701OutputVO);
//	}

//	/** CBS 刪除此方法呼叫，改用 getFP032675Data 方法
//	 * 查詢客戶註記資料
//	 *
//	 * 使用電文: FC032675
//	 *
//	 * @param body
//	 * @return FC032675DataVO
//	 */
//	public FC032675DataVO getFC032675Data(Object body) throws Exception {
//		sot701InputVO = (SOT701InputVO) body;
//		String custID = sot701InputVO.getCustID();
//		FC032675DataVO fc032675VO = new FC032675DataVO();
//
//		// 2017/9/21 判斷如果 custID 是空，就不繼續執行
//		if (StringUtils.isNotBlank(custID)) {
//
//			// 電文代號
//			String htxtid = FC032675_DATA;
//
//			// init util
//			ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
//			esbUtilInputVO.setModule("SOT701.getFC032675Data");
//
//			// head
//			TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
//			txHead.setDefaultTxHead();
//			esbUtilInputVO.setTxHeadVO(txHead);
//
//			// body
//			FC032675InputVO fc032675InputVO = new FC032675InputVO();
//			fc032675InputVO.setIDU_COD("CRRAMS");
//			fc032675InputVO.setCUSID(custID); // 客戶ID
//			esbUtilInputVO.setFc032675InputVO(fc032675InputVO);
//
//			// 發送電文
//			List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
//
//			String plType = ""; // 取得專業投資人類型
//			String custName = null;
//			FC032675OutputVO fc032675OutputVO = new FC032675OutputVO();
//
//			for (ESBUtilOutputVO esbUtilOutputVO : vos) {
//				fc032675OutputVO = esbUtilOutputVO.getFc032675OutputVO();
//				fc032675OutputVO.setDESC(fc032675OutputVO.getDESC().trim());
//
//				custName = getCustNameByID(custID);
//
//				// 取得專業投資人類型
//				// TX_FLG = "Y"，則為專業投資人
//				// TX_FLG = "Y" 或 (DESC = "J1" or "J2")，則為小專投 ；其他為大專投
//				if (!StringUtils.isEmpty(fc032675OutputVO.getTX_FLG()) && "Y".equals(fc032675OutputVO.getTX_FLG())) {
//					if (!StringUtils.isEmpty(fc032675OutputVO.getDESC()) && ("J1".equals(fc032675OutputVO.getDESC()) || "J2".equals(fc032675OutputVO.getDESC())))
//						plType = "2";
//					else
//						plType = "1";
//				}
//
//				logger.debug("專業投資人類型：" + plType);
//
//				/* 電文VO與交易VO - 資料轉換 */
//				fc032675VO.setCustName(custName);
//				fc032675VO.setCustRemarks(fc032675OutputVO.getVUL_FLAG());
//				// 其中一個非為Y就是弱勢，modify by jimmy at 2017/03/24
//				fc032675VO.setCustRemarks(!"Y".equals(fc032675OutputVO.getAGE_UN70_FLAG()) || !"Y".equals(fc032675OutputVO.getEDU_OV_JR_FLAG()) || !"Y".equals(fc032675OutputVO.getHEALTH_FLAG()) ? "Y" : "N");
//				fc032675VO.setCustProFlag(StringUtils.equals("Y", fc032675OutputVO.getTX_FLG()) ? "Y" : "N");
//				fc032675VO.setCustProType(plType);
//				fc032675VO.setCustProDate((StringUtils.isBlank(fc032675OutputVO.getDUE_END_DATE())) ? null : (new SimpleDateFormat("yyyyMMdd").parse(fc032675OutputVO.getDUE_END_DATE())));
//				fc032675VO.setCustTxFlag(("Y".equals(fc032675OutputVO.getTX_FLG()) || "Y".equals(fc032675OutputVO.getDESC())) ? "Y" : "N");
//				fc032675VO.setCustProRemark(fc032675OutputVO.getDESC());
//				fc032675VO.setRejectProdFlag(fc032675OutputVO.getPROD_TYP());
//				fc032675VO.setDeathFlag(fc032675OutputVO.getDEAD_TYP());
//				fc032675VO.setNoSale(fc032675OutputVO.getREJ_TYP());
//				fc032675VO.setBillType(fc032675OutputVO.getBILLS_CHECK());
//				fc032675VO.setObuFlag(fc032675OutputVO.getOBU_FLG());
//				fc032675VO.setInvestFlag(fc032675OutputVO.getINVEST_FLG());
//				fc032675VO.setInvestType(fc032675OutputVO.getINVEST_TYP());
//				fc032675VO.setInvestExp(fc032675OutputVO.getINVEST_EXP());
//				fc032675VO.setInvestDue(fc032675OutputVO.getINVEST_DUE());
//				fc032675VO.setAgeUnder70Flag(fc032675OutputVO.getAGE_UN70_FLAG());
//				fc032675VO.setEduJrFlag(fc032675OutputVO.getEDU_OV_JR_FLAG());
//				fc032675VO.setHealthFlag(fc032675OutputVO.getHEALTH_FLAG());
//				fc032675VO.setSickType(fc032675OutputVO.getSICK_TYPE());
//				fc032675VO.setDmFlag(fc032675OutputVO.getDM_FLG());
//				fc032675VO.setEdmFlag(fc032675OutputVO.getEDM_FLG());
//				fc032675VO.setSmsFlag(fc032675OutputVO.getSMS_FLG());
//				fc032675VO.setTmFlag(fc032675OutputVO.getTM_FLG());
//				fc032675VO.setInfoFlag(fc032675OutputVO.getINFO_FLG());
//				fc032675VO.setAcc1Flag(fc032675OutputVO.getACC1_FLG());
//				fc032675VO.setAcc2Flag(fc032675OutputVO.getACC2_FLG());
//				fc032675VO.setAcc3Flag(fc032675OutputVO.getACC3_FLG());
//				fc032675VO.setAcc4Flag(fc032675OutputVO.getACC4_FLG());
//				fc032675VO.setAcc5Flag(fc032675OutputVO.getACC5_FLG());
//				fc032675VO.setAcc6Flag(fc032675OutputVO.getACC6_FLG());
//				fc032675VO.setTrustFlag(fc032675OutputVO.getTRUST_FLAG());
//				fc032675VO.setAcc7Flag(fc032675OutputVO.getACC7_FLG());
//				fc032675VO.setAcc8Flag(fc032675OutputVO.getACC8_FLG());
//				fc032675VO.setAcc1Other(fc032675OutputVO.getACC1_OTHER());
//				fc032675VO.setAcc2Other(fc032675OutputVO.getACC2_OTHER());
//				fc032675VO.setAcc3Other(fc032675OutputVO.getACC3_OTHER());
//				fc032675VO.setAcc4Other(fc032675OutputVO.getACC4_OTHER());
//				fc032675VO.setAcc6Other(fc032675OutputVO.getACC6_OTHER());
//				fc032675VO.setAcc7Other(fc032675OutputVO.getACC7_OTHER());
//				fc032675VO.setAcc8Other(fc032675OutputVO.getACC8_OTHER());
//				fc032675VO.setBondFlag(fc032675OutputVO.getBOND_FLAG());
//				fc032675VO.setKycEndDate((StringUtils.isBlank(fc032675OutputVO.getEND_DATE())) ? null : (new SimpleDateFormat("yyyyMMdd").parse(fc032675OutputVO.getEND_DATE())));
//				fc032675VO.setIdnF(fc032675OutputVO.getIDN_F());
//				fc032675VO.setDegrade(fc032675OutputVO.getDEGRADE());// 免降等註記
//				fc032675VO.setProCorpInv("N");
//				fc032675VO.setHighYieldCorp("N");
//				fc032675VO.setSiProCorp("N");
//				fc032675VO.setTrustProCorp("N");
//				fc032675VO.setPro3000("N");
//				fc032675VO.setPro1500("N");
//
//				// 專業投資人為有效 (TX_FLG=Y)且專業投資人註記(DESC)
//				// a. 專業機構投資人 (DESC=01-16)
//				// b. 高淨值法人 (DESC=24)
//				// c. 具衍商資格專業法人 (DESC=17, 18, 20)；(DESC=31, 32 信託專業法人，目前沒有用到)
//				// d. 專業自然人DBU/OBU提供3000萬財力證明(DESC=L1, J1)
//				// e. 專業自然人DBU/OBU提供1500萬財力證明(DESC=L2, J2)
//				if (StringUtils.equals("Y", fc032675OutputVO.getTX_FLG())) {
//					switch (fc032675OutputVO.getDESC()) {
//					case "01":
//					case "02":
//					case "03":
//					case "04":
//					case "05":
//					case "06":
//					case "07":
//					case "08":
//					case "09":
//					case "10":
//					case "11":
//					case "12":
//					case "13":
//					case "14":
//					case "15":
//					case "16":
//						fc032675VO.setProCorpInv("Y");
//						break;
//					case "24":
//						fc032675VO.setHighYieldCorp("Y");
//						break;
//					case "17":
//					case "18":
//					case "20":
//						fc032675VO.setSiProCorp("Y");
//						break;
//					case "31":
//					case "32":
//						fc032675VO.setTrustProCorp("Y");
//						break;
//					case "L1":
//					case "J1":
//						fc032675VO.setPro3000("Y");
//						break;
//					case "L2":
//					case "J2":
//						fc032675VO.setPro1500("Y");
//						break;
//					default:
//					}
//				}
//			}
//		}
//
//		return fc032675VO;
//	}

	/**
	 * 客戶註記資料-查詢客戶姓名
	 *
	 * @param custID
	 * @return
	 * @throws JBranchException
	 */
	private String getCustNameByID(String custID) throws JBranchException {
		String sql = new String("select CUST_NAME " + "  from TBCRM_CUST_MAST " + " where CUST_ID = :CUST_ID ");

		dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setObject("CUST_ID", custID);
		condition.setQueryString(sql);

		List<Map> list = dam.exeQuery(condition);

		String custName = null;
		if (CollectionUtils.isNotEmpty(list)) {
			custName = (String) list.get(0).get("CUST_NAME");
		}

		return custName;
	}

	/**
	 * 查詢：KYC等級、KYC效期
	 *
	 * for js client
	 *
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void getCustKycData(Object body, IPrimitiveMap header) throws Exception {
		sot701OutputVO = new SOT701OutputVO();
		sot701OutputVO.setCustKYCDataVO(getCustKycData(body));

		sendRtnObject(sot701OutputVO);
	}

	// by PRD110-150 國維
	public CustKYCDataVO getCustKycData(String custId) throws Exception {

		SOT701InputVO inputVO = new SOT701InputVO();
		inputVO.setCustID(custId);
		return getCustKycData(inputVO);
	}

	/**
	 * 查詢：KYC等級、KYC效期
	 *
	 * @param body
	 * @return CustKYCDataVO
	 * @throws Exception
	 */
	public CustKYCDataVO getCustKycData(Object body) throws Exception {
		sot701InputVO = (SOT701InputVO) body;
		String custID = sot701InputVO.getCustID();

		if (StringUtils.isBlank(custID)) {
			throw new JBranchException("未輸入客戶ID");
		}


		CustKYCDataVO custKYCDataVO = new CustKYCDataVO();

		List<CBSUtilOutputVO> list = _067157_067157dao.search(custID, cbsservice.getCBSIDCode(custID));
		CBS067157OutputVO outputVO = list.get(0).getCbs067157OutputVO();

		if (isNotBlank(outputVO.getIdno())) {
			SimpleDateFormat ft = new SimpleDateFormat("ddMMyyyy");
			custKYCDataVO.setKycLevel(outputVO.getResult1());
			custKYCDataVO.setKycDueDate(ft.parse(outputVO.getExpiryDate1()));

			XmlInfo xmlInfo = new XmlInfo();
			String riskFit = (String) xmlInfo.getVariable("SOT.RISK_FIT_CONFIG", custKYCDataVO.getKycLevel(), "F3");
			custKYCDataVO.setRISK_FIT(riskFit);
		}
		// FOR CBS測試日期修改
		try {
			custKYCDataVO.setKycDueDateUseful(isKycDateUseful(custKYCDataVO.getKycDueDate()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return custKYCDataVO;
	}

	// FOR CBS測試日期修改 KYC校期
	private boolean isKycDateUseful(Date kycDate) throws Exception {
		if(kycDate == null) {
			return true;
		} else {
			SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
			Date CBSTestDate = ft.parse(cbsservice.getCBSTestDate().substring(0, 8));
			// 參考SOT110.js原有邏輯 if ($scope.toJsDate(tota[0].body.kycDueDate) <
			// $scope.toJsDate($scope.toDay)) { //若KYC過期，需有訊息提示客戶，並清空客戶ID。
			if (kycDate.before(CBSTestDate)) {
				return true;
			} else {
				return false;
			}
		}
	}
	

	public String getFlagNumber(String id, String idType) throws Exception {
		return  _062171_062171dao.search(id, idType)
							     .get(0).getCbs062171OutputVO()
							     .getDetail()
							     .get(4)
							     .getFlagNumber();
	}

	/**
	 * 客戶停損停利資料
	 *
	 * for js client 使用電文: NFEE011/AFEE011
	 *
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void getCustPLData(Object body, IPrimitiveMap header) throws Exception {
		sot701OutputVO = new SOT701OutputVO();
		sot701OutputVO.setCustPLDataVO(getCustPLData(body));
		sendRtnObject(sot701OutputVO);
	}

	/**
	 * 客戶停損停利資料
	 *
	 * 使用電文: NFEE011/AFEE011
	 *
	 * @param body
	 * @return CustPLDataVO
	 */
	public CustPLDataVO getCustPLData(Object body) throws Exception {
		sot701InputVO = (SOT701InputVO) body;
		dam = getDataAccessManager();
		QueryConditionIF condition = null;

		String htxtid = null;
		Boolean isOBU = null;
		String custID = sot701InputVO.getCustID();

		if (StringUtils.isBlank(custID)) {
			throw new JBranchException("未輸入客戶ID");
		}

		/* variables for logic */
		String isPLSetup = ""; // 停損停利設定
		BigDecimal takeProfitPerc = new BigDecimal("0"); // 停利點
		BigDecimal stopLossPerc = new BigDecimal("0"); // 停損點

		// 取得KYC中停損停利設定值
		StringBuffer sql1 = new StringBuffer().append("select ").append("coalesce(STOP_LOSS_PT, 0) as STOP_LOSS_PT, coalesce(TAKE_PRFT_PT, 0) as TAKE_PRFT_PT ").append("FROM TBCRM_CUST_MAST ").append("WHERE CUST_ID = :CUST_ID");

		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setQueryString(sql1.toString());
		condition.setObject("CUST_ID", custID);

		List<Map> results = dam.exeQuery(condition);

		if (CollectionUtils.isNotEmpty(results)) {
			takeProfitPerc = (BigDecimal) results.get(0).get("TAKE_PRFT_PT");
			stopLossPerc = (BigDecimal) results.get(0).get("STOP_LOSS_PT");
		}

		/*
		 * 判斷使用電文代號
		 *
		 * 查詢OBU客戶停損停利設定電文 sot701InputVO.isOBU="Y" 發送電文: AFEE011
		 *
		 * 查詢DBU客戶停損停利設定電文 sot701InputVO.isOBU="N" 發送電文: NFEE011
		 */
		isOBU = ("Y".equals(sot701InputVO.getIsOBU())) ? Boolean.TRUE : Boolean.FALSE;
		htxtid = (isOBU) ? CUST_PL_DATA_OBU : CUST_PL_DATA_DBU;

		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
		esbUtilInputVO.setModule("SOT701.getCustPLData");

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();

		// body
		if (isOBU) {
			AFEE011InputVO txBodyVO = new AFEE011InputVO();
			esbUtilInputVO.setAfee011InputVO(txBodyVO);
			txBodyVO.setCustId(custID);
		} else {
			NFEE011InputVO txBodyVO = new NFEE011InputVO();
			esbUtilInputVO.setNfee011InputVO(txBodyVO);
			txBodyVO.setCustId(custID);
		}

		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			Object outputVO = (isOBU) ? esbUtilOutputVO.getAfee011OutputVO() : esbUtilOutputVO.getNfee011OutputVO();

			// 取得個電文欄位放入區域變數,供後續處理使用
			if (outputVO instanceof AFEE011OutputVO) {
				isPLSetup = ((AFEE011OutputVO) outputVO).getNotice();

			} else if (outputVO instanceof NFEE011OutputVO) {
				isPLSetup = ((NFEE011OutputVO) outputVO).getNotice();
			}
		}

		Map transMap = new HashMap();
		String plMsg = "";

		if ("Y".equals(isPLSetup)) {
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			// email
			StringBuffer sql = new StringBuffer().append("select EMAIL ").append("from TBCRM_CUST_CONTACT ").append("where CUST_ID = :CUST_ID");
			condition.setQueryString(sql.toString());
			condition.setObject("CUST_ID", custID);

			List<Map> emails = dam.exeQuery(condition);
			String email = (CollectionUtils.isNotEmpty(emails)) ? (String) emails.get(0).get("EMAIL") : "";

			// 是否有網銀設定
			sql = new StringBuffer().append("select EBANK_YN ").append("from TBCRM_CUST_NOTE ").append("where CUST_ID = :CUST_ID");
			condition.setQueryString(sql.toString());
			condition.setObject("CUST_ID", custID);

			List<Map> eBankYNs = dam.exeQuery(condition);
			String isEBankYN = (CollectionUtils.isNotEmpty(eBankYNs)) ? (String) eBankYNs.get(0).get("EBANK_YN") : "";

			// 有設定網銀,且email有值
			if ("Y".equals(isEBankYN) && StringUtils.isNotBlank(email)) {
				plMsg = new StringBuffer("網銀訊息通知").append("Email:").append(email).toString();
			}
		}

		/* 電文VO與交易VO - 資料轉換 */
		CustPLDataVO custPLDataVO = new CustPLDataVO(takeProfitPerc, // 停利點
				stopLossPerc, // 停損點
				plMsg // 停損停利通知方式
		);

		return custPLDataVO;
	}

	/**
	 * 查詢客戶W-8BEN
	 *
	 * for js client 使用電文: NKNE01
	 *
	 * @param body
	 * @param header
	 */
	public void getW8BenData(Object body, IPrimitiveMap header) throws Exception {
		sot701OutputVO = new SOT701OutputVO();
		sot701OutputVO.setW8BenDataVO(this.getW8BenData(body));
		sendRtnObject(sot701OutputVO);
	}

	/**
	 * 查詢客戶W-8BEN
	 *
	 * 使用電文: NKNE01
	 *
	 * @param body
	 * @return W8BenDataVO
	 */
	public W8BenDataVO getW8BenData(Object body) throws Exception {
		sot701InputVO = (SOT701InputVO) body;
		String htxtid = CUST_W8_BEAN;
		String custID = sot701InputVO.getCustID();
		W8BenDataVO w8BenDataVO = new W8BenDataVO();

		if (StringUtils.isBlank(custID)) {
			throw new JBranchException("未輸入客戶ID");
		}

		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
		esbUtilInputVO.setModule("SOT701.getW8BenData");

		// head
		TxHeadVO txhead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txhead);
		txhead.setDefaultTxHead();

		// body
		NKNE01InputVO txBodyVO = new NKNE01InputVO();
		esbUtilInputVO.setNkne01InputVO(txBodyVO);
		txBodyVO.setCustId(custID); // 客戶ID

		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NKNE01OutputVODetailsVO outputVO = esbUtilOutputVO.getNkne01OutputVO().getDetails();

			if (outputVO != null) {
				w8BenDataVO.setW8BenEffYN(outputVO.getW_8BEN());
				w8BenDataVO.setW8benStartDate((StringUtils.isBlank(outputVO.getSignStartDate())) || StringUtils.equals(outputVO.getSignStartDate(), "00000000") ? null : (new SimpleDateFormat("yyyyMMdd").parse(outputVO.getSignStartDate())));
				w8BenDataVO.setW8benEndDate((StringUtils.isBlank(outputVO.getSignEndDate())) || StringUtils.equals(outputVO.getSignEndDate(), "00000000") ? null : (new SimpleDateFormat("yyyyMMdd").parse(outputVO.getSignEndDate())));
			}

		}
		return w8BenDataVO;
	}

	/**
	 * 查詢客戶W-8BEN和FATCA客戶註記檢核
	 *
	 * for js client 使用電文: FC032275
	 *
	 * @param body
	 * @param header
	 */
	public void getCustW8BenFATCA(Object body, IPrimitiveMap header) throws Exception {
		sot701OutputVO = new SOT701OutputVO();
		sot701OutputVO.setW8BenDataVO(this.getCustW8BenFATCA(body));
		sendRtnObject(sot701OutputVO);
	}

	/**
	 * 查詢客戶W-8BEN和FATCA客戶註記檢核
	 * 
	 * 使用電文: FC032275
	 *
	 *#1981 不合作例外帳戶例外管理 by SamTu 2024.05.03
	 * @param body
	 * @return W8BenDataVO
	 */
	public W8BenDataVO getCustW8BenFATCA(Object body) throws Exception {
		sot701InputVO = (SOT701InputVO) body;
		String custID = sot701InputVO.getCustID();

		String exceptionAcctFlag  = getExceptionAcctFlag(custID);
		W8BenDataVO w8BenDataVO = new W8BenDataVO();
		WMS032275OutputVO wms032275OutputVO = queryCustW8BenFATCA(custID);

		if (wms032275OutputVO != null) {
			// for (WMS032275OutputDetailsVO detailsVO :
			// wms032275OutputVO.getDetails()) {
			// DOC_NO
			String docNo = wms032275OutputVO.getDOC_NO1();
			// 簽署是否過期
			String effYN = "";

			// 判斷有效日期
			String effDate = wms032275OutputVO.getEFF_DATE1();
			if ((StringUtils.isNotBlank(effDate))) {

				// 將EFF_DATE1(西元) 改順序
				effDate = Integer.toString(Integer.valueOf(effDate.substring(4, 8))) + "/" + effDate.substring(2, 4) + "/" + effDate.substring(0, 2);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

				/*
				 * 判斷傳入日期日否大於等於系統日; 大於等於: 未過期 小於: 已過期
				 */
				Boolean isEffYN = this.compareWithSysDate(sdf.parse(effDate));

				if (isEffYN) {
					effYN = "Y";
					logger.debug("已簽署 W-8BEN 未過期");
				} else {
					effYN = "N";
					logger.debug("已簽署 W-8BEN 已過期");
				}
			}

			// 自然人 - 簽署是否過期註記
			if (custID.length() == 10 && (StringUtils.isNotBlank(docNo) || "A2".equals(docNo) || "A6".equals(docNo) || "H".equals(docNo))) {

				w8BenDataVO.setW8BenEffYN(effYN);
			}
			// 法人 - 簽署是否過期註記
			if (custID.length() == 8 && (StringUtils.isNotBlank(docNo) || "A3".equals(docNo) || "A4".equals(docNo) || "A5".equals(docNo) || "A6".equals(docNo) || "H".equals(docNo))) {

				w8BenDataVO.setW8BenEffYN(effYN);
			}
			// }

			// 目前FATCA身分辨識狀態
			String idfS = wms032275OutputVO.getIDF_S();
			// 目前FATCA身分
			String idfN = wms032275OutputVO.getIDF_N();
			// 前次辨識身分
			String idfP = wms032275OutputVO.getIDF_P();

			w8BenDataVO.setIDF_N(idfN);
			w8BenDataVO.setIDF_P(idfP);
			w8BenDataVO.setIDF_S(idfS);

			// 辨識完成
			if ("辨識完成".equals(idfS) && StringUtils.isNotBlank(idfN)) {
				// 判斷身份
				if (idfN.matches("1201|1202|1203|1204|1205|C010|C020|C030|C040|C050|C060|C070|C090") && "N".equals(exceptionAcctFlag)) {
					w8BenDataVO.setFatcaType("N"); // 不合作
				} else if (idfN.matches("1301|1302|1303|B010")) {
					w8BenDataVO.setFatcaType("Y"); // 美國人
				} else if (idfN.matches("0800|1206|D013|C013|D023|C023")) {
					w8BenDataVO.setFatcaType("X"); // 未簽署
				}
			}

		}
		return w8BenDataVO;
	}

	/**
	 * 查詢客戶 W-8BEN 和 FATCA 客戶註記檢核
	 */
	private WMS032275OutputVO queryCustW8BenFATCA(String custId) throws Exception {
		if (StringUtils.isBlank(custId)) {
			throw new JBranchException("未輸入客戶 ID");
		}
		return wms032275Service.search(custId);
	}

	/**
	 * 查詢FATCA客戶註記檢核
	 *
	 * for js client 使用電文: FC032275
	 *
	 * @param body
	 * @param header
	 */
	public void getFatcaData(Object body, IPrimitiveMap header) throws Exception {
		sot701OutputVO = new SOT701OutputVO();
		sot701OutputVO.setFatcaDataVO(this.getFatcaData(body));
		sendRtnObject(sot701OutputVO);
	}

	/**
	 * 查詢FATCA客戶註記檢核
	 *
	 * 使用電文: FC032275
	 *
	 *#1981 不合作例外帳戶例外管理 by SamTu 2024.05.03
	 * @param body
	 * @return FatcaDataVO
	 */
	public FatcaDataVO getFatcaData(Object body) throws Exception {
		sot701InputVO = (SOT701InputVO) body;
		String custID = sot701InputVO.getCustID();
		
		String exceptionAcctFlag  = getExceptionAcctFlag(custID);

		WMS032275OutputVO wms032275OutputVO = queryCustW8BenFATCA(custID);

		FatcaDataVO fatcaDataVO = new FatcaDataVO();
		if (wms032275OutputVO != null) {

			// 目前FATCA身分辨識狀態
			String idfS = wms032275OutputVO.getIDF_S();
			// 目前FATCA身分
			String idfN = wms032275OutputVO.getIDF_N();
			// 前次辨識身分
			String idfP = wms032275OutputVO.getIDF_P();

			fatcaDataVO.setIDF_N(idfN);
			fatcaDataVO.setIDF_P(idfP);
			fatcaDataVO.setIDF_S(idfS);

			if (StringUtils.isNotBlank(idfN)) // 依照idfN去設置fatcaType
				// 判斷身份
				if (idfN.matches("1201|1202|1203|1204|1205|C010|C020|C030|C040|C050|C060|C070|C090") && "N".equals(exceptionAcctFlag)) {
					fatcaDataVO.setFatcaType("N"); // 不合作
				} else if (idfN.matches("1301|1302|1303|B010")) {
					fatcaDataVO.setFatcaType("Y"); // 美國人
				} else if (idfN.matches("0800|1206|D013|C013|D023|C023")) {
					fatcaDataVO.setFatcaType("X"); // 未簽署
				}
		}
		return fatcaDataVO;
	}

	/**
	 * 查詢客戶期間議價效期
	 *
	 * for js client 使用電文: NFBRVCRM3/NRBRVC3
	 *
	 * @param body
	 * @param header
	 */
	public void getCustFeePrdDueDate(Object body, IPrimitiveMap header) throws Exception {
		sot701OutputVO = new SOT701OutputVO();
		sot701OutputVO.setDueDate(this.getCustFeePrdDueDate(body));
		sendRtnObject(sot701OutputVO);
	}

	/**
	 * 查詢客戶期間議價效期
	 *
	 * 使用電文: NFBRVCRM3/NRBRVC3
	 *
	 *
	 * @param body
	 * @return
	 */
	public Date getCustFeePrdDueDate(Object body) throws Exception {
		sot701InputVO = (SOT701InputVO) body;

		String custID = sot701InputVO.getCustID();
		String prodType = sot701InputVO.getProdType();

		// 欄位檢核
		if (StringUtils.isBlank(custID) || StringUtils.isBlank(prodType)) {
			throw new JBranchException("客戶ID或產品類別未輸入");
		}

		Date dueDate = null;
		Date sysdate = DateUtils.truncate(new Date(), Calendar.DATE);
		// 取得基金期間議價資料
		if (StringUtils.equals("1", prodType)) {
			SOT709 sot709 = (SOT709) PlatformContext.getBean(SOT709.class.getSimpleName().toLowerCase());
			SOT709InputVO inputVO = new SOT709InputVO();
			SOT709OutputVO outputVO = null;

			inputVO.setCustId(custID);
			inputVO.setType("1"); // 全部

			outputVO = sot709.getPeriodFeeRate(inputVO);

			if (CollectionUtils.isNotEmpty(outputVO.getPeriodFeeRateList())) {
				for (PeriodFeeRateVO vo : outputVO.getPeriodFeeRateList()) {
					Date beginDate = DateUtils.truncate(vo.getBrgBeginDate(), Calendar.DATE);
					Date endDate = DateUtils.truncate(vo.getBrgEndDate(), Calendar.DATE);

					// 有覆核日期表示已覆核，已覆核資料才算有效
					// 判斷系統日是否介於適用優惠期間起日和適用優惠期間迄日,若符合則回傳適用優惠期間迄日
					if (vo.getAuthDate() != null && beginDate.compareTo(sysdate) <= 0 && endDate.compareTo(sysdate) >= 0) {
						dueDate = vo.getBrgEndDate();
					}
				}
			}
		}
		// 取得海外ETF/股票期間議價資料
		else if (StringUtils.equals("2", prodType)) {
			SOT710 sot710 = (SOT710) PlatformContext.getBean(SOT710.class.getSimpleName().toLowerCase());
			SOT710InputVO inputVO = new SOT710InputVO();
			SOT710OutputVO outputVO = null;

			// startDate為系統日–1年
			Calendar startCal = Calendar.getInstance();
			startCal.add(Calendar.YEAR, -1);
			Date startDate = startCal.getTime();
			// endDate為系統日
			Date endDate = new Date();

			inputVO.setCustID(custID);
			inputVO.setEmpID((String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID));
			inputVO.setStartDate(startDate);
			inputVO.setEndDate(endDate);

			outputVO = sot710.getPeriodFeeRate(inputVO);

			if (CollectionUtils.isNotEmpty(outputVO.getPeriodFeeRateList())) {
				for (com.systex.jbranch.app.server.fps.sot710.PeriodFeeRateVO vo : outputVO.getPeriodFeeRateList()) {
					Date begin_Date = DateUtils.truncate(vo.getBrgBeginDate(), Calendar.DATE);
					Date end_Date = DateUtils.truncate(vo.getBrgEndDate(), Calendar.DATE);

					// 有覆核日期表示已覆核，已覆核資料才算有效
					// 判斷系統日是否介於適用優惠期間起日和適用優惠期間迄日,若符合則回傳適用優惠期間迄日
					if (vo.getAuthDate() != null && begin_Date.compareTo(sysdate) <= 0 && end_Date.compareTo(sysdate) >= 0) {
						dueDate = vo.getBrgEndDate();
					}
				}
			}
		}

		return dueDate;
	}

	/**
	 * 取得客戶帳號資訊 (下單) for js client 使用電文: FC032671 ProdType =2-ETF =4-SI =6-保險
	 */
	public void getCustAcctData(Object body, IPrimitiveMap header) throws Exception {
		sot701OutputVO = new SOT701OutputVO();
		sot701OutputVO.setCustAcctDataVO(this.getCustAcctData(body));
		sendRtnObject(sot701OutputVO);
	}

	public CustAcctDataVO getCustAcctData(Object body) throws Exception {
		sot701InputVO = (SOT701InputVO) body;
		String custID = sot701InputVO.getCustID();
		String prodType = sot701InputVO.getProdType();
		String tradeType = sot701InputVO.getTradeType();

		FP032671OutputVO fp032671OutputVO = new FP032671OutputVO();
		NFEI003OutputVO nfei003OutputVO = new NFEI003OutputVO();
		HashMap<String, String> results = new HashMap<String, String>();

		CustAcctDataVO custAcctData = new CustAcctDataVO();
		custAcctData.trustAcctList = new ArrayList<AcctVO>();
		custAcctData.debitAcctList = new ArrayList<AcctVO>();
		custAcctData.creditAcctList = new ArrayList<AcctVO>();
		custAcctData.prodAcctList = new ArrayList<AcctVO>();

		trustAcctList = new HashSet<String>();
		debitAcctList = new HashSet<String>();

		for (FP032671OutputVO vo : getFp032671AcctData(custID)) {
			for (FP032671OutputDetailsVO Fp032671OutputDetailsVO : vo.getDetails()) {
				if (StringUtils.isNotBlank(Fp032671OutputDetailsVO.getACNO_1())) {
					classifyAcct(Fp032671OutputDetailsVO.getACNO_1(), Fp032671OutputDetailsVO.getACNO_CATG_1(), prodType,
							Fp032671OutputDetailsVO.getWA_X_ATYPE(), Fp032671OutputDetailsVO.getWA_X_ICAT());
				}
			}
		}

		// 因為getFp032671AcctData來的168帳號不一定是信託帳號，所以在classifyAcct所有帳號後，刪掉所有168邏輯的帳號，由底下的NFEI003電文信託帳號補上
		Iterator it = trustAcctList.iterator();
		List store = new ArrayList();
		while (it.hasNext()) {
			String acct = it.next().toString();
			if(equal168Acct(acct)){
				store.add(acct);
			}
		}

		if (store.size() >= 1) {
			for (int i = 0; i < store.size(); i++) {
				trustAcctList.remove(store.get(i).toString());
			}
		}

		// NFEI003 (客戶368及168帳號查詢信託帳號)
		List<ESBUtilOutputVO> vos2 = getTrustAcct(custID);

		for (ESBUtilOutputVO esbUtilOutputVO : vos2) {
			nfei003OutputVO = esbUtilOutputVO.getNfei003OutputVO();
			if (StringUtils.isBlank(nfei003OutputVO.getEMSGTXT())) {
				for (NFEI003OutputDetailsVO nfei003OutputDetailsVO : nfei003OutputVO.getDetails()) {
					if(!nfei003OutputDetailsVO.getAR104().trim().equals("Y")){
						classifyAcct(cbsservice.checkAcctLength((nfei003OutputDetailsVO.getAR101().trim())), "暫無帳號描述", prodType);
					}

				}
			} else {
				Log.info("SOT701.java  NFEI003查無帳號");
			}
		}

		// 取得帳號幣別以及餘額 FC032675
		List<AcctVO> acctBal = getCBSCustAcctBalData(custID);
		String[] digitTypeCheckProdType = {"1","2","3"};
		Boolean filterDigitAcct = Arrays.asList(digitTypeCheckProdType).contains(prodType);
		/*
		 * 0695
		 * 基金、海外ETF/股票、海外債　三種下單帳號需有數存戶篩選邏輯
		 */
		if(filterDigitAcct){
			List<AcctVO> tempList = new ArrayList<>();
			String[] digitTypeCheck = {"0","1","2","3","4","5"};
			// 處理扣款帳號
			for (String dacno : debitAcctList) {
				for (AcctVO avo : acctBal) {
					if (StringUtils.equals(dacno, avo.acctNo)) {
						if(!Arrays.asList(digitTypeCheck).contains(avo.getDigitType())){
							custAcctData.debitAcctList.add(avo);
						}
						tempList.add(avo);
					}
				}
			}
			custAcctData.setIncludingDigitDebitAcctList(tempList);
			// 處理收益入帳帳號
			for (AcctVO debitAcct : tempList) {
				// 判斷是否有重複
				boolean noRepeat = true;
				for (AcctVO creditAcct : custAcctData.creditAcctList) {
					if (StringUtils.equals(debitAcct.getAcctNo(), creditAcct.getAcctNo())) {
						noRepeat = false;
					}
				}

				if (noRepeat)
					custAcctData.creditAcctList.add(debitAcct);
			}

			// 處理信託帳號
			for (String acno : trustAcctList) {
				AcctVO avo = new AcctVO();
				avo.acctNo = acno;
				avo.setBranch(getTrustAcctBranch(acno, custID));
				if(!equal168Acct(acno)){
					custAcctData.trustAcctList.add(avo);
					continue; //168以外信託帳號直接加入
				}
				for (AcctVO acctVO : acctBal) {
					if (StringUtils.equals(acno, acctVO.acctNo)) {
						if(!Arrays.asList(digitTypeCheck).contains(acctVO.getDigitType())){
							custAcctData.trustAcctList.add(avo);
							break; //不出現重覆信託帳號
						}
					}
				}
			}
		} else {

			// 合併扣款帳號資料(同收益入帳帳號)：FC032671帳號與FC032675幣別、餘額
			for (String dacno : debitAcctList) {
				for (AcctVO avo : acctBal) {
					if (StringUtils.equals(dacno, avo.acctNo)) {
						custAcctData.debitAcctList.add(avo);
					}
				}
			}

			// 放入收益帳號，邏輯同扣款帳號，但不需有幣別餘額，不應重複
			for (AcctVO debitAcct : custAcctData.debitAcctList) {
				// 判斷是否有重複
				boolean noRepeat = true;
				for (AcctVO creditAcct : custAcctData.creditAcctList) {
					if (StringUtils.equals(debitAcct.getAcctNo(), creditAcct.getAcctNo())) {
						noRepeat = false;
					}
				}

				if (noRepeat)
					custAcctData.creditAcctList.add(debitAcct);
			}

			// 放入信託帳號
			for (String acno : trustAcctList) {
				AcctVO avo = new AcctVO();
				avo.acctNo = acno;
				avo.setBranch(getTrustAcctBranch(acno, custID));

				if (prodType.matches("4|7")) { // SI,FCI 放入組合式商品帳號
					custAcctData.prodAcctList.add(avo);
				} else {
					custAcctData.trustAcctList.add(avo);
				}
			}

		}

		// 回傳值
		return custAcctData;

	}

	/**
	 * 取得長度12碼帳號資料 (客管) 只會放帳號以及帳號類別欄位
	 *
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void getCustAcct12(Object body, IPrimitiveMap header) throws Exception {
		sot701OutputVO = new SOT701OutputVO();
		sot701OutputVO.setAcct12List(this.getCustAcct12(body));
		sendRtnObject(sot701OutputVO);
	}

	/**
	 * 取得長度12碼帳號資料 (客管) 只會放帳號以及帳號類別欄位 20200218 排除放款帳號
	 * detail.getACNO_CATG_1().equals("LON")
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public List<AcctVO> getCustAcct12(Object body) throws Exception {
		sot701InputVO = (SOT701InputVO) body;
		String custID = sot701InputVO.getCustID();
		acct12List = new ArrayList<>();

		for (FP032671OutputVO vo : getFp032671AcctData(custID)) {
			for (FP032671OutputDetailsVO detail : vo.getDetails()) {
				if (!detail.getACNO_CATG_1().equals("LON") & StringUtils.isNotBlank(detail.getACNO_1()))
					classifyAcct14(detail);
			}
		}

		// 回傳值
		return acct12List;
	}

	/**
	 * CRM814 明細查詢排部分帳號
	 *
	 * @param acct
	 * @return
	 */
	private boolean excludeAcct(String acct) {

		String[] OldAcctThreeByte = { "177", "377", "128", "677", "126", "626", "236", "636", "256", "656", "645", "647", "368" };
		String[] OldAcctEightByte = { "00560677", "00560368" };
		String[] NewAcctFourByte = { "8241", "8245", "8249", "8345", "8341", "8395", "8391", "8401", "8178", "8388" };

		acct = cbsservice.checkAcctLength(acct);
		if (acct.substring(0, 1).equals("0")) {
			if (Arrays.asList(OldAcctThreeByte).contains(acct.substring(5, 8))) {
				return false;
			}
			if (acct.substring(5, 7).equals("27")) {
				return false;
			}
			if (acct.substring(0, 7).equals("0056027")) {
				return false;
			}
			if (Arrays.asList(OldAcctEightByte).contains(acct.substring(0, 8))) {
				return false;
			}

		} else if (acct.substring(0, 1).equals("8")) {
			if (Arrays.asList(NewAcctFourByte).contains(acct.substring(0, 4))) {
				return false;
			}
			if (Integer.parseInt(acct.substring(0, 5)) >= 82500 && Integer.parseInt(acct.substring(0, 5)) < 82800) {
				return false;
			}
			if (Integer.parseInt(acct.substring(0, 5)) >= 83500 && Integer.parseInt(acct.substring(0, 5)) < 83800) {
				return false;
			}
			if (Integer.parseInt(acct.substring(0, 5)) >= 83960 && Integer.parseInt(acct.substring(0, 5)) < 84000) {
				return false;
			}
		} else {
			return true;
		}
		return true;
	}

	private void classifyAcct12(String acct, String acctCat) {
		if (StringUtils.isNotBlank(acct) && acct.length() == 12) {
			AcctVO acctVO = new AcctVO();

			acctVO.setAcctNo(acct); // 帳號
			acctVO.setAcctCatagory(acctCat); // 帳號類別

			// 判斷是否有重複
			boolean noRepeat = true;
			for (AcctVO chkdata : acct12List) {
				if (StringUtils.equals(acct, chkdata.getAcctNo())) {
					noRepeat = false;
				}
			}

			if (noRepeat)
				acct12List.add(acctVO);
		}
	}

	/**
	 *
	 * @param acct
	 * @param acctCat
	 *            CBS電文統一處理成14碼 所以這邊也改成14碼
	 *            #20221221 excludeAcct()改成台外幣活存&&台外幣支存判斷
	 */
	private void classifyAcct14(FP032671OutputDetailsVO detail) {
		String sys = detail.getACNO_CATG_1();
		String product = detail.getTYPE();
		String curr = detail.getCURR();

		String acct = detail.getACNO_1().trim();
		// 帳號不為空&&帳號長度14碼&&(活存or支存帳號)
		if (StringUtils.isNotBlank(acct) && acct.length() == 14 &&
				(mediatorService.isCheckTW(curr,product)||mediatorService.isCurrentDeposit(sys, product))) {
			AcctVO acctVO = new AcctVO();
			acctVO.setAcctNo(acct); // 帳號
			acctVO.setBranch(detail.getBRA());
//			acctVO.setAcctCatagory(acctCat); // 帳號類別，無使用先註解，之後沒問題直接刪除

			// 判斷是否有重複
			boolean noRepeat = true;
			for (AcctVO chkdata : acct12List) {
				if (StringUtils.equals(acct, chkdata.getAcctNo())) {
					noRepeat = false;
				}
			}

			if (noRepeat)
				acct12List.add(acctVO);
		}
	}

	private List<ESBUtilOutputVO> getFc032671AcctData(String custId) throws Exception {

		String htxtid = FC032671_DATA;

		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		txHead.setPAGEFLG("3");
		esbUtilInputVO.setTxHeadVO(txHead);

		// body
		FC032671InputVO fc032671InputVO = new FC032671InputVO();
		fc032671InputVO.setDATA_IND("1");
		fc032671InputVO.setINP_DATA(custId); // 客戶ID
		fc032671InputVO.setENQ_IND("1");
		fc032671InputVO.setFUNC("0");
		esbUtilInputVO.setFc032671InputVO(fc032671InputVO);

		// 發送電文
		return send(esbUtilInputVO);
	}

	/** 發送電文取得客戶的帳戶資料 **/
	public List<FP032671OutputVO> getFp032671AcctData(String custId) throws Exception {
		return fp032671Service.searchCustAcct(custId);
	}

	/***
	 * 依照帳號規則取得信託帳號、收益帳號、扣款帳號、組合式商品帳號
	 *
	 * @param acct
	 *            ：帳號
	 * @param acctCat
	 *            ：帳號中文類別
	 * @param prodType
	 *            ：商品類別
	 * @throws Exception
	 */
	private void classifyAcct(String acct, String acctCat, String prodType) throws Exception {
		if (isBlank(acct) || length(acct) != 14)
			return;

		String flag = acct.substring(0, 1);
		boolean isOld = flag.equals("0");
		boolean isNew = flag.equals("8");

		if (!isOld && !isNew)
			return;
		AcctRuler ruler = (AcctRuler) PlatformContext.getBean(isOld ? "oldVersionAcct" : "newVersionAcct");

		ruler.setInfo(acct, acctCat, prodType);
		ruler.classifyAcct(trustAcctList, debitAcctList);

	}

	/***
	 * 依照帳號規則取得信託帳號、收益帳號、扣款帳號、組合式商品帳號
	 * #0978 改用產品大類小類來區分信託扣款帳號 2022.08.12 Sam Tu
	 *
	 * @param acct
	 *            ：帳號
	 * @param acctCat
	 *            ：帳號中文類別
	 * @param prodType
	 *            ：商品類別
	 *  @param wa_x_type :產品大類
	 *  @param wa_x_icat :產品小類
	 * @throws Exception
	 */
	private void classifyAcct(String acct, String acctCat, String prodType, String wa_x_type, String wa_x_icat) throws Exception {
		if (isBlank(acct) || length(acct) != 14)
			return;


		AcctRuler ruler = (AcctRuler) PlatformContext.getBean("commonVersionAcct");

		ruler.setInfo(acct, acctCat, prodType, wa_x_type, wa_x_icat);
		ruler.classifyAcct(trustAcctList, debitAcctList);

	}

	/**
	 * 取得客戶帳號資訊，包含幣別及餘額
	 *
	 * 使用電文: FC032675
	 *
	 * @return CustAcctDataVO
	 */
	private List<AcctVO> getCustAcctBalData(String custID) throws Exception {
		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, FC032675_DATA);
		esbUtilInputVO.setModule("SOT701.getCustAcctData");

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();
		txHead.setPAGEFLG("3");

		// body
		FC032675InputVO txBodyVO = new FC032675InputVO();
		esbUtilInputVO.setFc032675InputVO(txBodyVO);
		txBodyVO.setCUSID(custID);

		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		// init
		List<AcctVO> acctVOs = new ArrayList<>(); // 帳號明細資料
		String rActNo = null; // 帳號

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			FC032675OutputVO fc036275OutputVO = esbUtilOutputVO.getFc032675OutputVO();

			for (FC032675OutputDetailsVO detailVO : fc036275OutputVO.getDetails()) {
				String actNo = detailVO.getACT_NO(); // 帳號

				// 檢核往來帳號是否為數值型態
				if (BigDecimalValidator.getInstance().isValid(actNo)) {
					rActNo = actNo.trim();
				}
				// 各帳號明細資料
				else {
					String curCod = detailVO.getCUR_COD().trim(); // 幣別

					// 帳號及幣別都要有值,帳號為16碼，帳號中文不包含"定存"、"合計"或空白帳號
					if (StringUtils.isNotBlank(curCod) && StringUtils.isNotBlank(rActNo) && StringUtils.length(rActNo) == 16 && StringUtils.isNotBlank(actNo) && !(StringUtils.contains(actNo, "定存") || StringUtils.contains(actNo, "定期存款") || StringUtils.contains(actNo, "合計"))) {

						String openDateStr = detailVO.getOPN_DATE(); // 開戶日期
						// 開戶日期轉換西元年
						Date openDate = null;
						if (StringUtils.isNotBlank(openDateStr))
							openDate = new SimpleDateFormat("yyyyMMdd").parse((Integer.valueOf(openDateStr.substring(0, 3)) + 1911) + openDateStr.substring(3));

						// 轉換BigDecimal:排除空值,預設為0
						String amtStr = detailVO.getAMT();
						amtStr = (StringUtils.isNotBlank(amtStr)) ? amtStr.trim() : "0";
						String courtAmtStr = detailVO.getCOURT_AMT();
						courtAmtStr = (StringUtils.isNotBlank(courtAmtStr)) ? courtAmtStr.trim() : "0";

						// use formatter format currency to decimal type
						DecimalFormat dFormat = new DecimalFormat();

						BigDecimal amt = new BigDecimal(dFormat.parse(amtStr, new ParsePosition(0)).toString());
						BigDecimal courtAmt = new BigDecimal(dFormat.parse(courtAmtStr, new ParsePosition(0)).toString());

						AcctVO acctVO = new AcctVO();
						acctVO.setAcctNo(rActNo); // 帳號
						acctVO.setOpenDate(openDate); // 開戶日期 (西元年月日)
						acctVO.setCurrency(curCod); // 幣別
						acctVO.setAvbBalance(amt.subtract(courtAmt)); // 可用餘額
																		// (帳戶餘額
																		// -
																		// 圈存金額)
						acctVO.setAcctBalance(amt); // 帳戶餘額
						acctVO.setBckBalance(courtAmt); // 圈存金額

						// 判斷是否有重複
						boolean noRepeat = true;
						for (AcctVO chekdata : acctVOs) {
							if (StringUtils.equals(rActNo, chekdata.getAcctNo()) && StringUtils.equals(curCod, chekdata.getCurrency())) {
								noRepeat = false;
							}
						}

						if (noRepeat)
							acctVOs.add(acctVO);
					}
				}
			}
		}

		return acctVOs;
	}

	/**
	 * 查詢客戶是否為首購
	 *
	 * for js client 使用電文: NFEE012/NKNE01
	 *
	 * @param body
	 * @param header
	 */
	public void getIsCustFirstTrade(Object body, IPrimitiveMap header) throws Exception {
		sot701OutputVO = new SOT701OutputVO();
		sot701OutputVO.setIsFirstTrade(this.getIsCustFirstTrade(body));
		sendRtnObject(sot701OutputVO);
	}

	/**
	 * 查詢客戶是否為首購
	 *
	 * 使用電文: NFEE012/NKNE01
	 *
	 * @param body
	 * @throws Exception
	 */
	public Boolean getIsCustFirstTrade(Object body) throws Exception {
		sot701InputVO = (SOT701InputVO) body;
		String prodType = sot701InputVO.getProdType();
		String custID = sot701InputVO.getCustID();

		if (StringUtils.isBlank(prodType) || StringUtils.isBlank(custID)) {
			throw new JBranchException("未輸入客戶ID/商品類別");
		}

		String isFirstTrade = null;
		String dayFirstTrade = null;

		if ("1".equals(prodType)) { // 基金
			// init util
			ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, FIRST_TRADE_FUND);
			esbUtilInputVO.setModule("SOT701.getIsCustFirstTrade");

			// head
			TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
			esbUtilInputVO.setTxHeadVO(txHead);
			txHead.setDefaultTxHead();

			NFEE012InputVO txBodyVO = new NFEE012InputVO();
			esbUtilInputVO.setNfee012InputVO(txBodyVO);
			txBodyVO.setCustId(custID); // 客戶ID

			// 發送電文
			List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

			for (ESBUtilOutputVO esbUtilOutputVO : vos) {
				NFEE012OutputVO outputVO = esbUtilOutputVO.getNfee012OutputVO();

				isFirstTrade = outputVO.getFstTrade();
				dayFirstTrade = outputVO.getDayFstTrade();
			}

		} else if ("2".equals(prodType)) { // ETF
			// init util
			ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, StringUtils.equals("M", sot701InputVO.getTrustTS()) ? FIRST_TRADE_ETF_M : FIRST_TRADE_ETF);
			esbUtilInputVO.setModule("SOT701.getIsCustFirstTrade");

			// head
			TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
			esbUtilInputVO.setTxHeadVO(txHead);
			txHead.setDefaultTxHead();

			NKNE01InputVO txBodyVO = new NKNE01InputVO();
			esbUtilInputVO.setNkne01InputVO(txBodyVO);
			txBodyVO.setCustId(custID); // 客戶ID

			// 發送電文
			List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
			NKNE01OutputVODetailsVO outputVO = new NKNE01OutputVODetailsVO();
			
			for (ESBUtilOutputVO esbUtilOutputVO : vos) {
				if(StringUtils.equals("M", sot701InputVO.getTrustTS())) {
					outputVO = esbUtilOutputVO.getNknet1OutputVO().getDetails(); //金錢信託
				} else {
					outputVO = esbUtilOutputVO.getNkne01OutputVO().getDetails(); //特金
				}

				isFirstTrade = outputVO.getFstTrade();
				dayFirstTrade = outputVO.getDayFstTrade();
			}
		}

		// return (StringUtils.equals("Y", isFirstTrade) &&
		// StringUtils.equals("Y", dayFirstTrade))
		// ? Boolean.TRUE : Boolean.FALSE;
		return (StringUtils.equals("Y", isFirstTrade)) ? Boolean.TRUE : Boolean.FALSE;
	}

	/**
	 * 客戶註記資料 for js client
	 *
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void getCustNoteData(Object body, IPrimitiveMap header) throws Exception {
		sot701OutputVO = new SOT701OutputVO();
		sot701OutputVO.setCustNoteDataVO(this.getCustNoteData(body));
		sendRtnObject(sot701OutputVO);
	}

	/**
	 * 客戶註記資料
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public CustNoteDataVO getCustNoteData(Object body) throws Exception {
		sot701InputVO = (SOT701InputVO) body;

		dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sql = new StringBuffer().append("select ").append("coalesce(SIGN_RISK_YN, 'N'), ").append("coalesce(INTERDICT_YN, 'N') ").append("from TBCRM_CUST_NOTE ").append("WHERE CUST_ID = :CUST_ID ");

		condition.setObject("CUST_ID", sot701InputVO.getCustID());
		condition.setQueryString(sql.toString());

		List<Map<String, Object>> list = dam.exeQuery(condition);

		Boolean isSignRisk = Boolean.FALSE;
		Boolean isInterdict = Boolean.FALSE;

		if (CollectionUtils.isNotEmpty(list)) {
			isSignRisk = StringUtils.equals("Y", (String) list.get(0).get("SIGN_RISK_YN")) ? Boolean.TRUE : Boolean.FALSE;
			isInterdict = StringUtils.equals("Y", (String) list.get(0).get("INTERDICT_YN")) ? Boolean.TRUE : Boolean.FALSE;
		}

		CustNoteDataVO custNoteDataVO = new CustNoteDataVO();
		custNoteDataVO.setSignRisk(isSignRisk);
		custNoteDataVO.setInterdict(isInterdict);

		return custNoteDataVO;
	}

	/**
	 * 查詢客戶 Q 值
	 *
	 * @param body
	 *            SOT701InputVO
	 * @return
	 * @throws Exception
	 */
	public String getCustQValue(Object body) throws Exception {
		FC81OutputVO outputVO = queryFC81Data(body);
		if (null == outputVO)
			return "Q0";
		return defaultString(outputVO.getTX_DESC(), "Q0");
	}

	/**
	 * 查詢：客戶投資經驗 for js client
	 *
	 * 使用電文: FC81
	 *
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void getCustComExp(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.getCustComExp(body));
	}

	/**
	 * 查詢：客戶投資經驗
	 *
	 * @param body
	 *            SOT701InputVO
	 * @return
	 * @throws Exception
	 */
	public String getCustComExp(Object body) throws Exception {
		FC81OutputVO outputVO = queryFC81Data(body);
		if (null == outputVO)
			return "";
		return outputVO.getTX_RSN();
	}

	/**
	 * 發送電文，取得 FC81 回傳電文 VO。 TX_DESC：客戶 Q 值 TX_RSN：客戶投資經驗
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	private FC81OutputVO queryFC81Data(Object body) throws Exception {
		return fc81Service.search(((SOT701InputVO) body).getCustID());
	}

	/**
	 * 上傳客戶經驗FC81電文 TX_RSN:客戶投資經驗
	 *
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void updateFC81Data(Object body, IPrimitiveMap header) throws Exception {
		this.updateFC81Data(body);

		this.sendRtnObject(null);
	}

	/**
	 * 上傳客戶經驗FC81電文 TX_RSN:客戶投資經驗
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public void updateFC81Data(Object body) throws Exception {
		SOT701InputVO sot701InputVO = (SOT701InputVO) body;
		String custId = sot701InputVO.getCustID();

		// 電文 InputVO
		FC81InputVO fc81InputVO = new FC81InputVO();
		fc81InputVO.setTX_DATE(new SimpleDateFormat("ddMMyyyy").format(new Date()));
		fc81InputVO.setTX_TIME(new SimpleDateFormat("HHmmss").format(new Date()));
		fc81InputVO.setCUST_NO(custId);
		fc81InputVO.setTX_RSN(defaultString(sot701InputVO.getComExp()));
		fc81InputVO.setMTN_BRH("0028");
		fc81InputVO.setMTN_EMP("000VP1");

		// 若沒有LOGINBRH，則改取客戶的歸屬行 (#4259)
		String brh = trim(defaultString((String) SysInfo.getInfoValue(LOGINBRH)));
		if (isBlank(brh) || "000".equals(brh)) {
			List<Map> result = Manager.manage(this.getDataAccessManager()).append("select BRA_NBR from TBCRM_CUST_MAST where CUST_ID = :custId ").put("custId", custId).query();

			if (result.size() > 0) {
				String braNbr = (String) result.get(0).get("BRA_NBR");
				if (isNotBlank(braNbr))
					fc81InputVO.setMTN_BRH(braNbr);
			}
		} else {
			fc81InputVO.setMTN_BRH(brh);
		}

		// 發送電文
		fc81Service.update(fc81InputVO);
	}

	/**
	 * 查詢：客戶利害關係人註記 for js client
	 *
	 * 使用電文: FP052650
	 *
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void isCustStakeholder(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.isCustStakeholder(body));
	}

	/**
	 * 查詢：客戶利害關係人註記
	 *
	 * 使用電文: FP052650
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public Boolean isCustStakeholder(Object body) throws Exception {
		sot701InputVO = (SOT701InputVO) body;

		String custID = sot701InputVO.getCustID();

		// 欄位檢核
		if (StringUtils.isBlank(custID)) {
			throw new JBranchException("客戶ID未輸入");
		}

		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, NEW_CUST_STACK_HOLDER);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();
		txHead.setHTLID("2004011");

		// body
		NB052650InputVO txBody = new NB052650InputVO();
		esbUtilInputVO.setNb052650InputVO(txBody);
		txBody.setI_CUST_NO(custID);
		txBody.setI_FUNC_SEL("18");
		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		Boolean flag2 = Boolean.FALSE;

		NB052650OutputVO nb052650OutputVO = vos.get(0).getNb052650OutputVO();

		if (StringUtils.equals("Y", nb052650OutputVO.getIS_R45())) {
			flag2 = Boolean.TRUE;
		}
		return flag2;
	}

	/**
	 * 查詢：客戶利害關係人註記
	 *
	 * 使用電文: FP052650
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	// public Boolean isCustStakeholder(Object body) throws Exception {
	// sot701InputVO = (SOT701InputVO) body;
	//
	// String custID = sot701InputVO.getCustID();
	//
	// // 欄位檢核
	// if (StringUtils.isBlank(custID)) {
	// throw new JBranchException("客戶ID未輸入");
	// }
	//
	// // init util
	// ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE,
	// CUST_STACK_HOLDER);
	// esbUtilInputVO.setModule(thisClaz + new Object() {
	// }.getClass().getEnclosingMethod().getName());
	//
	// // head
	// TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
	// esbUtilInputVO.setTxHeadVO(txHead);
	// txHead.setDefaultTxHead();
	//
	// // body
	// FP052650InputVO txBody = new FP052650InputVO();
	// esbUtilInputVO.setFp052650InputVO(txBody);
	// txBody.setCUST_ID(custID);
	// txBody.setFUNC("18");
	//
	// // 發送電文
	// List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
	//
	// Boolean flag2 = Boolean.FALSE;
	//
	// for(ESBUtilOutputVO esbUtilOutputVO : vos) {
	// FP052650OutputVO fp052650OutputVO =
	// esbUtilOutputVO.getFp052650OutputVO();
	// if (StringUtils.equals("Y", fp052650OutputVO.getFLAG2())) {
	// flag2 = Boolean.TRUE;
	// }
	// }
	// return flag2;
	// }

	/**
	 * 查詢客戶註記資料
	 *
	 * for js client 使用電文: FC032675
	 *
	 *
	 * @param body
	 * @param header
	 */
	public void getFC032675NewCustData(Object body, IPrimitiveMap header) throws Exception {
		FC032675DataVO fc032675VO = new FC032675DataVO();
		fc032675VO = this.getFC032675NewCustData(body);

		sendRtnObject(fc032675VO);
	}

	/**
	 * 查詢客戶註記資料
	 *
	 * 使用電文: FC032675
	 *
	 * @param body
	 * @return FC032675DataVO
	 */
	public FC032675DataVO getFC032675NewCustData(Object body) throws Exception {
		sot701InputVO = (SOT701InputVO) body;
		String custID = sot701InputVO.getCustID();

		// 電文代號
		String htxtid = FC032675_DATA;

		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
		esbUtilInputVO.setModule("SOT701.getFC032675NewCustData");

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);

		// body
		FC032675InputVO fc032675InputVO = new FC032675InputVO();
		fc032675InputVO.setIDU_COD("CIBRHD");
		fc032675InputVO.setCUSID(custID); // 客戶ID
		esbUtilInputVO.setFc032675InputVO(fc032675InputVO);

		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		FC032675OutputVO fc032675OutputVO = new FC032675OutputVO();
		FC032675DataVO fc032675VO = new FC032675DataVO();

		Boolean isOpenAcct = Boolean.FALSE;

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			fc032675OutputVO = esbUtilOutputVO.getFc032675OutputVO();

			fc032675VO.setCustName(fc032675OutputVO.getCUST_NAME());
			fc032675VO.setbDay((StringUtils.isBlank(fc032675OutputVO.getBDAY())) ? null : (new SimpleDateFormat("yyyyMMdd").parse(fc032675OutputVO.getBDAY())));

			List<FC032675OutputDetailsVO> details = fc032675OutputVO.getDetails();
			details = (CollectionUtils.isEmpty(details)) ? new ArrayList<FC032675OutputDetailsVO>() : details;
			for (FC032675OutputDetailsVO detail : details) {
				if (!StringUtils.isBlank(detail.getBRH_COD())) {
					isOpenAcct = Boolean.TRUE;
				}
			}
		}

		if (!isOpenAcct) {
			// 若沒有開戶分行，則出錯誤"客戶尚未開戶"
			throw new APException("ehl_01_common_029");
		}

		return fc032675VO;
	}

	@SuppressWarnings("unchecked")
	public List<ESBUtilOutputVO> callerFc032154Htlid(FC032153InputVO fc032153inputVO) throws Exception {
		// init util
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> paramConfig = xmlInfo.doGetVariable("CRM.FC032154_HTLID", FormatHelper.FORMAT_3);
		String htlid = paramConfig.get("1");

		// 電文代號
		String htxtid = FC032153_DATA;

		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();

		// head
		txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		txHead.setHTLID(htlid);
		esbUtilInputVO.setTxHeadVO(txHead);

		// body
		esbUtilInputVO.setFc032153InputVO(fc032153inputVO);

		// 發送電文
		return send(esbUtilInputVO);
	}

	public List<String> doGetFc032154Cods(FC032154InputVO fc032154inputVO) throws Exception {
		List<String> codeList = new ArrayList();
		// 電文代號
		String htxtid = FC032154_DATA;

		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();

		// head
		txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		txHead.setHTLID("");
		txHead.setHWSID("VIP_FP");
		txHead.setHSTANO("取交易序號");

		esbUtilInputVO.setTxHeadVO(txHead);

		// body
		esbUtilInputVO.setFc032154InputVO(fc032154inputVO);

		// 發送電文
		List<ESBUtilOutputVO> result = send(esbUtilInputVO);

		for (ESBUtilOutputVO esbUtilOutputVo : result) {
			FC032154OutputVO output = esbUtilOutputVo.getFc032154OutputVO();

			for (FC032154OutputDetailsVO detail : output.getDetails()) {
				System.out.println("code : " + detail.getCOD());
				if (StringUtils.isNotBlank(detail.getCOD()) && codeList.indexOf(detail.getCOD()) == -1) {
					codeList.add(detail.getCOD());
				}
			}
		}

		Collections.sort(codeList);
		return codeList;
	}

	// 新增上送KYC基本資料至390(學歷,婚姻,子女人數,職業,重大傷病)
	public List<ESBUtilOutputVO> checkKycMiddleWare(Object body, IPrimitiveMap header) throws Exception {
		FC032153DataVO inputVO = (FC032153DataVO) body;

		String cust_id = inputVO.getCustID();

		// 電文代號
		String htxtid = TP032675_CUST_DATA;

		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);

		// body
		TP032675InputVO tp032675InputVO = new TP032675InputVO();
		tp032675InputVO.setFUNCTION("CR");
		tp032675InputVO.setRANGE("RISK");
		tp032675InputVO.setCUSID(cust_id);
		tp032675InputVO.setPRINT_NO(inputVO.getCUST_DATA());

		esbUtilInputVO.setTp032675InputVO(tp032675InputVO);

		// 發送電文
		return send(esbUtilInputVO);

	}

	public void sendFC032154Data(Object body, IPrimitiveMap header) throws Exception {

		FC032153DataVO inputVO = (FC032153DataVO) body;
		Object[] obj = inputVO.getPhoneData().keySet().toArray();
		String[] phoneData = null;

		String cust_id = inputVO.getCustID();
		// 電文代號
		String htxtid = CUST_MAINTAIN;

		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		txHead.setHTLID("1714103");
		esbUtilInputVO.setTxHeadVO(txHead);

		// body
		FC032154InputVO fc032154InputVO = new FC032154InputVO();
		fc032154InputVO.setCUST_NO(inputVO.getCustID());

		for (int i = 0; i < obj.length; i++) {
			phoneData = (String[]) inputVO.getPhoneData().get(obj[i]);
			boolean isCellPhone = "1".equals(phoneData[4]);

			fc032154InputVO.setFUNC_01(phoneData[6]);
			fc032154InputVO.setCOD_01(phoneData[0]);
			fc032154InputVO.setTEL_NO_01(phoneData[1]);
			fc032154InputVO.setTYP_01(isCellPhone ? "2" : "1");

			fc032154InputVO.setDAY_USE_01(phoneData[2]);
			fc032154InputVO.setNIGHT_USE_01(phoneData[3]);
			fc032154InputVO.setVOICE_USE_01(phoneData[4]);
			fc032154InputVO.setFAX_USE_01(phoneData[5]);
		}
		esbUtilInputVO.setFc032154InputVO(fc032154InputVO);

		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

	}

	/**
	 * 解說人員需持有信託證照
	 *
	 * @param empNO
	 * @return
	 */
	public Boolean chkLicense(String empNO) throws Exception {
		Boolean isCert = Boolean.FALSE;

		// 電文代號
		String htxtid = NJCHKLC2_DATA;

		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);

		// body
		NJCHKLC2InputVO njchklc2InputVO = new NJCHKLC2InputVO();
		njchklc2InputVO.setCHSOURCE("B");
		njchklc2InputVO.setTXTYPE("MF000");
		njchklc2InputVO.setEMPID(empNO);

		esbUtilInputVO.setNjchklc2InputVO(njchklc2InputVO);

		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NJCHKLC2OutputVO njchklc2OutputVO = esbUtilOutputVO.getNjchklc20OutputVO();
			if ("0000".equals(njchklc2OutputVO.getABEND_CODE()) && "Y".equals(njchklc2OutputVO.getRESULT_CHK())) {
				isCert = Boolean.TRUE;
			}
		}
		return isCert;
	}

	/**
	 * 解說人員需持有衍商推介資格
	 *
	 * @param empNO
	 * @return
	 */
	public Boolean chkLicense_derive(String empNO) throws Exception {
		Boolean isCert = Boolean.FALSE;

		// 電文代號
		String htxtid = NJCHKLC2_DATA;

		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);

		// body
		NJCHKLC2InputVO njchklc2InputVO = new NJCHKLC2InputVO();
		njchklc2InputVO.setCHSOURCE("B");
		njchklc2InputVO.setTXTYPE("NJ002");
		njchklc2InputVO.setEMPID(empNO);

		esbUtilInputVO.setNjchklc2InputVO(njchklc2InputVO);

		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NJCHKLC2OutputVO njchklc2OutputVO = esbUtilOutputVO.getNjchklc20OutputVO();
			if ("0000".equals(njchklc2OutputVO.getABEND_CODE()) && "Y".equals(njchklc2OutputVO.getRESULT_CHK())) {
				isCert = Boolean.TRUE;
			}
		}
		return isCert;
	}

	/**
	 * 查詢：網銀取得戶名電文 電文FC032659 傳入參數：custID, 客戶ID trustAcct 信託帳號
	 * 20200319_CBS_麗文_下單_網銀快速下單出錯 >> 改用067050 - 067108
	 * 20200325_CBS_麗文_#80593_網銀快速下單 >> 改用085081-085105的 WA-X-ACCT-SUBID
	 *
	 * @return
	 */
	public String getFC032659AcnoCode(Object body) throws Exception {
		sot701InputVO = (SOT701InputVO) body;

		// // 電文代號
		// String htxtid = FC032659_DATA;// 網銀取得戶名電文
		//
		// // init util
		// ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
		// esbUtilInputVO.setModule("SOT701.getFC032659AcnoCode");
		//
		// // head
		// TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		// txHead.setDefaultTxHead();
		// esbUtilInputVO.setTxHeadVO(txHead);
		//
		// // body
		// FC032659InputVO fc032659InputVO = new FC032659InputVO();
		// fc032659InputVO.setFUNC("1");
		// fc032659InputVO.setCUST_NO(custID); // 客戶ID
		// esbUtilInputVO.setFc032659InputVO(fc032659InputVO);
		//
		// // 發送電文
		// List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
		//
		// FC032659OutputVO fc032659OutputVO = new FC032659OutputVO();
		//
		// String acnoCode = "";
		// String lastCode = "";
		//
		// acno_outter: for (ESBUtilOutputVO esbUtilOutputVO : vos) {
		// fc032659OutputVO = esbUtilOutputVO.getFc032659OutputVO();
		//
		// if (fc032659OutputVO != null && fc032659OutputVO.getDetails() !=
		// null) {
		// for (FC032659OutputDetailsVO vo : fc032659OutputVO.getDetails()) {
		// if (StringUtils.equals(vo.getACNO_CATG(), "基金客戶")) {
		// // 相同時，以 ACNO_CATG = 基金客戶的為主，取 COD 資料作為戶名
		// acnoCode = vo.getCOD();
		// // 擁有多個基金帳戶時，再判斷基金帳戶的帳號為頁面輸入的"扣款帳號"
		// if (StringUtils.equals(vo.getACNO(), sot701InputVO.getDebitAcct()))
		// break acno_outter;
		// } else {
		// lastCode = vo.getCOD();
		// }
		// }
		// }
		// }
		//
		// // 如果沒有基金客戶，則取最後一筆 COD 資料為戶名
		// acnoCode = (StringUtils.isBlank(acnoCode) ? lastCode : acnoCode);

		return fc032659Service.search(sot701InputVO).getCOD();
	}

	/**
	 * 客戶基本資料
	 *
	 * @param
	 * @return
	 * @return
	 */
	public basicVO getCustBasicData(Object body, IPrimitiveMap header) throws Exception {
		SOT701InputVO inputVO = (SOT701InputVO) body;
		basicVO outputVO = new basicVO();
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		Date dayLstMtnDate = sdf.parse("1911/01/01");// (日)日期暫存
		Date nightLstMtnDate = sdf.parse("1911/01/01");// (夜)日期暫存
		// 取客戶分行代碼
		dam = getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		sb.append(" Select BRA_NBR from TBCRM_CUST_MAST where CUST_ID = :custid ");
		qc.setObject("custid", inputVO.getCustID());
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> custList = dam.exeQuery(qc);

		String bra_nbr = null;

		if (custList.get(0).get("BRA_NBR") != null) {
			bra_nbr = custList.get(0).get("BRA_NBR").toString();
		}

		// 發送電文
		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, FC032675_DATA);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);

		// body
		FC032675InputVO fc032675InputVO = new FC032675InputVO();
		fc032675InputVO.setIDU_COD("CIB" + bra_nbr);
		fc032675InputVO.setCUSID(inputVO.getCustID().toUpperCase());// 客戶ID
		esbUtilInputVO.setFc032675InputVO(fc032675InputVO);

		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			FC032675OutputVO fc032675OutputVO = esbUtilOutputVO.getFc032675OutputVO();

			// 生日
			if (fc032675OutputVO.getBDAY().trim().length() == 8) {
				String bdy = fc032675OutputVO.getBDAY().trim();
				java.util.Date date = fmt.parse(bdy);
				long birthday = date.getTime();
				outputVO.setBirthday(date);
			}

			// 電話、地址、Emai
			for (FC032675OutputDetailsVO map : fc032675OutputVO.getDetails()) {
				if (map != null) {
					String cod = ObjectUtils.toString(map.getCOD()).replaceAll(" |　", "");
					String lstMtnDate = map.getLST_MTN_DATE();
					String mtnDate = map.getMTN_DATE();
					String endMtnDate = StringUtils.isNotBlank(lstMtnDate) ? lstMtnDate : StringUtils.isNotBlank(mtnDate) ? mtnDate : null;
					String data = map.getDATA();

					// 取得地址 (排除 0000戶籍地址&9001英文地址)
					if ("01".equals(map.getTYPE()) && !cod.matches("(0000)|(9001)")) {
						outputVO.setCUSTADDR_COD(cod);
						outputVO.setCUST_ADDR(data);
					} else if ("02".equals(map.getTYPE())) {
						// TEL_TYP = 1: 市話(含傳真) 2: 手機
						if ("1".equals(map.getTEL_TYP())) {
							// 取得傳真
							if ("9001".equals(cod)) {
								outputVO.setFAX_COD(cod);
								outputVO.setFAX(data);
							}
							// 取得日間(和最新維護日期)
							else if ("1".equals(map.getDAY()) && endMtnDate != null && fmt.parse(endMtnDate).getTime() > dayLstMtnDate.getTime()) {
								outputVO.setDAY_COD(StringUtils.isNotBlank(cod) ? cod : null);
								outputVO.setDAY(StringUtils.isNotBlank(data) ? data.replaceAll(" ", "") : outputVO.getDAY());
								dayLstMtnDate = fmt.parse(endMtnDate);
							}
							// 與夜間電話 (和最新維護日期)
							else if ("1".equals(map.getNIGHT()) && endMtnDate != null && fmt.parse(endMtnDate).getTime() > nightLstMtnDate.getTime()) {
								outputVO.setNIGHT_COD(StringUtils.isNotBlank(cod) ? cod : null);
								outputVO.setNIGHT(StringUtils.isNotBlank(data) ? data.replaceAll(" ", "") : outputVO.getNIGHT());
								nightLstMtnDate = fmt.parse(endMtnDate);
							}
						}
						// 取得手機
						else if ("2".equals(map.getTEL_TYP())) {
							outputVO.setTELNO_COD(cod);
							outputVO.setTEL_NO(data.replaceAll(" ", ""));
						}
					} else if ("03".equals(map.getTYPE())) {
						outputVO.setEMAILADDR_COD(cod);
						outputVO.setEMAIL_ADDR(data);
					}
				}
			}
		}

		return outputVO;
	}

	public FC032151DataVO GetFC032151Data(Object body, IPrimitiveMap header) throws Exception {
		SOT701InputVO inputVO = (SOT701InputVO) body;
		FC032151DataVO fc032151dataVO = new FC032151DataVO();

		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, FC032151_DATA);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);

		// body
		FC032151InputVO fc032151InputVO = new FC032151InputVO();
		fc032151InputVO.setFUNC("0");
		fc032151InputVO.setCUST_NO(inputVO.getCustID().toUpperCase());
		esbUtilInputVO.setFc032151InputVO(fc032151InputVO);

		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			FC032151OutputVO fc032151OutputVO = esbUtilOutputVO.getFc032151OutputVO();

			if (fc032151OutputVO.getEDUCATION() != null) {
				fc032151dataVO.setEDUCATION(fc032151OutputVO.getEDUCATION());
			}

			if (fc032151OutputVO.getCAREER() != null) {
				fc032151dataVO.setCAREER(fc032151OutputVO.getCAREER());
			}

			if (fc032151OutputVO.getMARRAGE() != null) {
				fc032151dataVO.setMARRAGE(fc032151OutputVO.getMARRAGE());
			}

			if (fc032151OutputVO.getCHILD_NO() != null) {
				fc032151dataVO.setCHILD_NO(fc032151OutputVO.getCHILD_NO());
			}
		}

		sendRtnObject(sot701OutputVO);
		/* 發送電文功能 - end */
		return fc032151dataVO;
	}

	/**
	 * 查詢結構型商品推介同意註記
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public void getSDACTQ8Data(Object body, IPrimitiveMap header) throws Exception {
		sot701OutputVO = new SOT701OutputVO();
		sot701OutputVO.setSiPromDataVO(this.getSDACTQ8Data(body));

		sendRtnObject(sot701OutputVO);
	}

	/**
	 * 查詢結構型商品推介同意註記
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public CustSIPromotionDataVO getSDACTQ8Data(Object body) throws Exception {
		sot701InputVO = (SOT701InputVO) body;
		String custID = sot701InputVO.getCustID();
		CustSIPromotionDataVO outputVO = new CustSIPromotionDataVO();

		if (StringUtils.isNotBlank(custID)) {
			// 電文代號
			String htxtid = SDACTQ8;

			// init util
			ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
			esbUtilInputVO.setModule("SOT701.getSDACTQ8Data");

			// head
			TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
			txHead.setDefaultTxHead();
			esbUtilInputVO.setTxHeadVO(txHead);

			// body
			SDACTQ8InputVO sdactq8InputVO = new SDACTQ8InputVO();
			sdactq8InputVO.setSDIVID(custID); // 客戶ID
			esbUtilInputVO.setSdactq8InputVO(sdactq8InputVO);

			// 發送電文
			List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

			for (ESBUtilOutputVO esbUtilOutputVO : vos) {
				SDACTQ8OutputVO sdactq8VO = esbUtilOutputVO.getSdactq8OutputVO();

				outputVO.setCustID(sdactq8VO.getSDIVID()); // 身分證號
				outputVO.setIsSign(sdactq8VO.getSDRECO()); // 是否簽署推介同意書
				outputVO.setSignDate((StringUtils.isBlank(sdactq8VO.getSDSDAY())) ? null : (new SimpleDateFormat("yyyyMMdd").parse(sdactq8VO.getSDSDAY()))); // 登錄日期
				outputVO.setEffectiveDate((StringUtils.isBlank(sdactq8VO.getSDEDAY())) ? null : (new SimpleDateFormat("yyyyMMdd").parse(sdactq8VO.getSDEDAY()))); // 有效日期
				outputVO.setEndDate((StringUtils.isBlank(sdactq8VO.getSDCDAY())) ? null : (new SimpleDateFormat("yyyyMMdd").parse(sdactq8VO.getSDCDAY()))); // 終止日期
				outputVO.setStatus(sdactq8VO.getSDSTATUS()); // 狀態註記
			}
		}

		return outputVO;
	}

	/**
	 * 取得高中職以上學歷註記
	 *
	 * @param inputVO
	 * @return true = 待確認
	 * @throws Exception
	 */
	public String getCustEduHighSchool(SOT701InputVO inputVO) throws Exception {
		String custID = inputVO.getCustID();
		String mark = "N";

		if (StringUtils.isNotBlank(custID)) {
			List<CBSUtilOutputVO> data = _067050_067115dao.search(custID, cbsservice.getCBSIDCode(custID));

			try{
				mark = data.get(0).getCbs067115OutputVO().getFinManInstrmt1().substring(20,21);
			} catch (Exception e) {}
		}

		return StringUtils.equals(mark, "Y") ? "Y" : "N";
	}

	/**
	 * 查詢客戶 KYC 資料
	 */
	public FP032151OutputVO getFP032151Data(Object body, IPrimitiveMap header) throws Exception {
		return fp032151Service.search(((SOT701InputVO) body).getCustID());
	}

	/* 發送電文功能 - end */
	/**
	 * 查詢客戶註記資料
	 *
	 * for js client 使用電文: FC032675
	 *
	 *
	 * @param body
	 * @param header
	 */
	public void getFP032675Data(Object body, IPrimitiveMap header) throws Exception {
		sot701OutputVO = new SOT701OutputVO();
		sot701OutputVO.setFp032675DataVO(this.getFP032675Data(body));

		sendRtnObject(sot701OutputVO);
	}

	/**
	 * 取得客戶帳戶資料
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void getCustAccountDetail(Object body, IPrimitiveMap header) throws Exception {
		sot701InputVO = (SOT701InputVO) body;
		String custID = sot701InputVO.getCustID();
		sendRtnObject(_085081_085105dao.search(custID, cbsservice.getCBSIDCode(custID)));
	}

	/**
	 * 取得客戶 Obu 註記（Obu 身分為 'Y'，否則為 'N'）
	 * #0874 OBU註記改用IDTYPE判斷
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void getCustObuFlag(Object body, IPrimitiveMap header) throws Exception {
		sot701InputVO = (SOT701InputVO) body;
//		String obuFlag = (getObuFlagFrom085081_085105(sot701InputVO.getAcctData()).equals("Y") ||
//				getObuFlagFromNFEI003(sot701InputVO.getCustID()).equals("Y")) ? "Y": "N";
		String obuFlag = checkOBUbyIDType(sot701InputVO.getCustID());
		sendRtnObject(obuFlag);
	}

	/**
	 * 依照 Type 取得相關資料
	 *
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void getData067050ByType(Object body, IPrimitiveMap header) throws Exception {
		sot701InputVO = (SOT701InputVO) body;
		String custId = sot701InputVO.getCustID();
		String idType = cbsservice.getCBSIDCode(custId);

		List<CBSUtilOutputVO> data = null;

		switch (sot701InputVO.getData067050Type()) {
			case "67050_067101_2":
				if (cbsservice.isNaturalPerson(idType)) // 自然人
					data =_067050_067101dao.search(custId, idType);
				else // 法人
					data =_067050_067102dao.search(custId, idType);
				break;
			case "067050_067000":
				data =_067050_067000dao.search(custId, idType);
				break;
			case "067050_067112":
				data = _067050_067112dao.search(custId, idType);
		}
		sendRtnObject(data);
	}

	public FP032675DataVO getFP032675Data(String custId) throws Exception {
		SOT701InputVO inputVO = new SOT701InputVO();
		inputVO.setCustID(custId);
		return getFP032675Data(inputVO);
	}
	
	/**
	 * 查詢客戶註記資料
	 *
	 * 使用電文: FP032675
	 *
	 * @param body
	 * @return FP032675DataVO
	 */
	public FP032675DataVO getFP032675Data(Object body) throws Exception {
		sot701InputVO = (SOT701InputVO) body;
		String custID = sot701InputVO.getCustID();
		FP032675DataVO fp032675VO = new FP032675DataVO();

		// 2017/9/21 判斷如果 custID 是空，就不繼續執行CustProDate
		if (StringUtils.isNotBlank(custID)) {
			String plType = ""; // 取得專業投資人類型
			String custName = null;
			FP032675OutputVO fp032675OutputVO = fp032675Service.searchMarkInfo(custID,
					sot701InputVO.getData067050_067101_2(),
					sot701InputVO.getData067050_067000(),
					sot701InputVO.getData067164_067165(),
					sot701InputVO.getData067050_067115(),
					sot701InputVO.getData060425_060433());

			custName = getCustNameByID(custID);

			// 取得專業投資人類型
			// TX_FLG = "Y"，則為專業投資人
			// TX_FLG = "Y" 或 (DESC = "J2" or "L2")，則為小專投 ；其他為大專投
			if (!StringUtils.isEmpty(fp032675OutputVO.getTX_FLG()) && "Y".equals(fp032675OutputVO.getTX_FLG())) {
				if (!StringUtils.isEmpty(fp032675OutputVO.getFinancialStatment()) && ("J2".equals(fp032675OutputVO.getFinancialStatment()) || "L2".equals(fp032675OutputVO.getFinancialStatment())))
					plType = "2";
				else
					plType = "1";
			}

			logger.debug("專業投資人類型：" + plType);

			/* 電文VO與交易VO - 資料轉換 */

			/* 20200507_CBS_Sam_FP032675_DESC欄位_電文相關規格確認
			 * #1298  投資商品諮詢服務邏輯調整
			 * 導致65-69歲的特定客戶 DESC可能是Y or N  (原本應該給X)
			 * 2022.09.22 V1 DESC欄位改成不看特定客戶，只看NFEI002電文的結果
			 * 2022.09.26 V2 DESC改為原邏輯，並補上年齡條件 */
			if (sot701InputVO.isNeedDesc()) { // 不需要 DESC 相關欄位可將此設為 false，可避免發送 NFEI002
				if(fp032675OutputVO.getINVEST_TYP().equals("Y") && getCustAge(custID) >= 70){  //若為特定客戶則DESC = "X"
					fp032675OutputVO.setDESC("X");
				}else{
					fp032675OutputVO.setDESC(this.getCreditAgreeMark(custID));
				}

				if ("Y".equals(fp032675OutputVO.getTX_FLG()) ||
						"Y".equals(fp032675OutputVO.getDESC())) {
					fp032675OutputVO.setINVEST_FLG("Y".equals(fp032675OutputVO.getINVEST_DUE()) ? "Y" : "N");
				} else {
					fp032675OutputVO.setINVEST_FLG("N");
				}
			}

			fp032675VO.setCustName(custName);
			fp032675VO.setCustRemarks(fp032675OutputVO.getVUL_FLAG());
			// 其中一個非為Y就是弱勢，modify by jimmy at 2017/03/24
			fp032675VO.setCustRemarks(fp032675OutputVO.getINVEST_TYP());
			fp032675VO.setCustProFlag(StringUtils.equals("Y", fp032675OutputVO.getTX_FLG()) ? "Y" : "N");
			fp032675VO.setCustProType(plType);
			fp032675VO.setCustProDate((StringUtils.isBlank(fp032675OutputVO.getDUE_END_DATE())) ? null : (new SimpleDateFormat("yyyyMMdd").parse(fp032675OutputVO.getDUE_END_DATE())));
			fp032675VO.setPiDueDateUseful((StringUtils.isBlank(fp032675OutputVO.getDUE_END_DATE())) ? false : isPiDateUseful(new SimpleDateFormat("yyyyMMdd").parse(fp032675OutputVO.getDUE_END_DATE())));
			fp032675VO.setCustTxFlag(("Y".equals(fp032675OutputVO.getDESC()) ? "Y" : "N"));
			fp032675VO.setCustProRemark(fp032675OutputVO.getDESC());
			fp032675VO.setRejectProdFlag(fp032675OutputVO.getPROD_TYP());
			fp032675VO.setDeathFlag(fp032675OutputVO.getDEAD_TYP());
			fp032675VO.setNoSale(fp032675OutputVO.getREJ_TYP());
			fp032675VO.setBillType(fp032675OutputVO.getBILLS_CHECK());
			fp032675VO.setInvestFlag(fp032675OutputVO.getINVEST_FLG());
			fp032675VO.setInvestType(fp032675OutputVO.getINVEST_TYP());
			fp032675VO.setInvestExp(fp032675OutputVO.getINVEST_EXP());
			fp032675VO.setInvestDue(fp032675OutputVO.getINVEST_DUE());
			fp032675VO.setAgeUnder70Flag(fp032675OutputVO.getAGE_UN70_FLAG());
			fp032675VO.setEduJrFlag(fp032675OutputVO.getEDU_OV_JR_FLAG());
			fp032675VO.setHealthFlag(fp032675OutputVO.getHEALTH_FLAG());
			fp032675VO.setSickType(fp032675OutputVO.getSICK_TYPE());
			fp032675VO.setDmFlag(fp032675OutputVO.getDM_FLG());
			fp032675VO.setEdmFlag(fp032675OutputVO.getEDM_FLG());
			fp032675VO.setSmsFlag(fp032675OutputVO.getSMS_FLG());
			fp032675VO.setTmFlag(fp032675OutputVO.getTM_FLG());
			fp032675VO.setInfoFlag(fp032675OutputVO.getINFO_FLG());
			fp032675VO.setAcc1Flag(fp032675OutputVO.getACC1_FLG());
			fp032675VO.setAcc2Flag(fp032675OutputVO.getACC2_FLG());
			fp032675VO.setAcc3Flag(fp032675OutputVO.getACC3_FLG());
			fp032675VO.setAcc4Flag(fp032675OutputVO.getACC4_FLG());
			fp032675VO.setAcc5Flag(fp032675OutputVO.getACC5_FLG());
			fp032675VO.setAcc6Flag(fp032675OutputVO.getACC6_FLG());
			fp032675VO.setTrustFlag(fp032675OutputVO.getTRUST_FLAG());
			fp032675VO.setAcc7Flag(fp032675OutputVO.getACC7_FLG());
			fp032675VO.setAcc8Flag(fp032675OutputVO.getACC8_FLG());
			fp032675VO.setAcc1Other(fp032675OutputVO.getACC1_OTHER());
			fp032675VO.setAcc2Other(fp032675OutputVO.getACC2_OTHER());
			fp032675VO.setAcc3Other(fp032675OutputVO.getACC3_OTHER());
			fp032675VO.setAcc4Other(fp032675OutputVO.getACC4_OTHER());
			fp032675VO.setAcc6Other(fp032675OutputVO.getACC6_OTHER());
			fp032675VO.setAcc7Other(fp032675OutputVO.getACC7_OTHER());
			fp032675VO.setAcc8Other(fp032675OutputVO.getACC8_OTHER());

			if (StringUtils.isNotBlank(sot701InputVO.getIsOBU())) {
				fp032675VO.setObuFlag(sot701InputVO.getIsOBU());
			} else {
				fp032675VO.setObuFlag(checkOBUbyIDType(custID));
			}

			fp032675VO.setDegrade(fp032675OutputVO.getDegrade());			// 免降等註記
			fp032675VO.setDegradeDate(fp032675OutputVO.getDEGRADE_DATE());	// 免降等註記到期日
			fp032675VO.setProCorpInv("N");
			fp032675VO.setHighYieldCorp("N");
			fp032675VO.setSiProCorp("N");
			fp032675VO.setTrustProCorp("N");
			fp032675VO.setPro3000("N");
			fp032675VO.setPro1500("N");

			// 專業投資人為有效 (TX_FLG=Y)且專業投資人註記(DESC)
			// a. 專業機構投資人 (DESC=01-16)
			// b. 高淨值法人 (DESC=24)
			// c. 具衍商資格專業法人 (DESC=17, 18, 20)；(DESC=31, 32 信託專業法人，目前沒有用到)
			if (StringUtils.equals("Y", fp032675OutputVO.getTX_FLG())) {
				switch (fp032675OutputVO.getLegalTX()) {
				case "01":
				case "02":
				case "03":
				case "04":
				case "05":
				case "06":
				case "07":
				case "08":
				case "09":
				case "10":
				case "11":
				case "12":
				case "13":
				case "14":
				case "15":
				case "16":
					fp032675VO.setProCorpInv("Y");
					break;
				case "24":
					fp032675VO.setHighYieldCorp("Y");
					break;
				case "17":
				case "18":
				case "20":
					fp032675VO.setSiProCorp("Y");
					break;
				case "31":
				case "32":
					fp032675VO.setTrustProCorp("Y");
					break;
				case "40":
				case "41":
					fp032675VO.setProCorpInv2("Y");
					break;
				default:
				}
			}
			// 專業投資人為有效 (TX_FLG=Y)且財力證明╱聲明(financestatement)
			// d. 專業自然人DBU/OBU提供3000萬財力證明(DESC=L1, J1)
			// e. 專業自然人DBU/OBU提供1500萬財力證明(DESC=L2, J2)
			if (StringUtils.equals("Y", fp032675OutputVO.getTX_FLG())) {
				switch (fp032675OutputVO.getFinancialStatment()) {
				case "L1":
				case "J1":
					fp032675VO.setPro3000("Y");
					break;
				case "L2":
				case "J2":
					fp032675VO.setPro1500("Y");
					break;
				default:
				}
			}
		}
		return fp032675VO;
	}
	
	/**
	 * #1865
	 * 商品主檔篩選錄音提醒
	 *
	 * 使用電文: 067000 067101 067115
	 *
	 * @param body
	 * @return FP032675DataVO
	 */
	public FP032675DataVO getInvestType(Object body) throws Exception {
		sot701InputVO = (SOT701InputVO) body;
		String custID = sot701InputVO.getCustID();
		FP032675DataVO fp032675VO = new FP032675DataVO();

		if (StringUtils.isNotBlank(custID)) {
			FP032675OutputVO fp032675OutputVO = fp032675Service.searchMarkInfo(custID,
													sot701InputVO.getData067050_067101_2(), 
													sot701InputVO.getData067050_067000(),
													sot701InputVO.getData067050_067115(),
													sot701InputVO.getData060425_060433());
			
			fp032675VO.setInvestType(fp032675OutputVO.getINVEST_TYP());
			fp032675VO.setEduJrFlag(fp032675OutputVO.getEDU_OV_JR_FLAG());
			fp032675VO.setHealthFlag(fp032675OutputVO.getHEALTH_FLAG());
			fp032675VO.setSickType(fp032675OutputVO.getSICK_TYPE());
			fp032675VO.setCustProFlag(StringUtils.equals("Y", fp032675OutputVO.getTX_FLG()) ? "Y" : "N");
		}
		return fp032675VO;
	}

	// FOR CBS測試日期修改 專業投資人校期
	private boolean isPiDateUseful(Date piDate) throws Exception {
		SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
		Date CBSTestDate = ft.parse(cbsservice.getCBSTestDate().substring(0, 8));
		// 參考SOT110.js原有邏輯 if ($scope.toJsDate(tota[0].body.kycDueDate) <
		// $scope.toJsDate($scope.toDay)) { //若KYC過期，需有訊息提示客戶，並清空客戶ID。
		if (piDate.before(CBSTestDate)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 取得客戶連絡方式
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public WMS032154OutputDetailsVO getPhoneData(Object body) throws Exception {
		return wms032154Service.search(((SOT701InputVO) body).getCustID()).getDetails().get(0);
	}

	public FP032675OutputVO getAddrandMail(Object body) throws Exception {
		return fp032675Service.searchBasicInfo(((SOT701InputVO) body).getCustID());
	}

	private List<AcctVO> getCBSCustAcctBalData(String custID) throws Exception {
		List<WMS032675OutputVO> vos = wms032675Service.searchAcct(custID);

		// init
		List<AcctVO> acctVOs = new ArrayList<>(); // 帳號明細資料
		String rActNo = null; // 帳號

		for (WMS032675OutputVO wms036275OutputVO : vos) {
			for (WMS032675OutputDetailsVO detailVO : wms036275OutputVO.getDetails()) {

				// 帳號及幣別都要有值,帳號為14碼，帳號中文不包含"定存"、"合計"或空白帳號
				if (StringUtils.isNotBlank(detailVO.getCURRENCY()) && StringUtils.isNotBlank(detailVO.getACT_NO()) && StringUtils.length(detailVO.getACT_NO()) == 14) {

					String actNo = detailVO.getACT_NO(); // 帳號
					String curCod = detailVO.getCURRENCY().trim(); // 幣別
					String openDateStr = detailVO.getOPEN_DATE(); // 開戶日期
					// 開戶日期轉換西元年
					Date openDate = null;
					if (StringUtils.isNotBlank(openDateStr))
						openDate = new SimpleDateFormat("yyyyMMdd").parse(openDateStr.substring(4, 8) + openDateStr.substring(2, 4) + openDateStr.substring(0, 2));

					// 轉換BigDecimal:排除空值,預設為0
					// String amtStr = detailVO.getAMT();
					// amtStr = (StringUtils.isNotBlank(amtStr)) ? amtStr.trim()
					// : "0";
					// String courtAmtStr = detailVO.getCOURT_AMT();
					// courtAmtStr = (StringUtils.isNotBlank(courtAmtStr)) ?
					// courtAmtStr.trim() : "0";

					// use formatter format currency to decimal type
					// DecimalFormat dFormat = new DecimalFormat();
					BigDecimal AcctBalance; //帳戶餘額
					BigDecimal AvbBalance; //可用餘額

					try {
						AcctBalance = new BigDecimal(cbsservice.amountFormat(detailVO.getTOTAL_SUM()));
					} catch (Exception e) {
						AcctBalance = new BigDecimal("0");
					}

					try {
						AvbBalance = new BigDecimal(cbsservice.amountFormat(detailVO.getAVAILABLE_AMT_BAL()));
					} catch (Exception e) {
						AvbBalance = new BigDecimal("0");
					}

					AcctVO acctVO = new AcctVO();
					acctVO.setAcctNo(actNo); // 帳號
					acctVO.setOpenDate(openDate); // 開戶日期 (西元年月日)
					acctVO.setCurrency(curCod); // 幣別
					acctVO.setAvbBalance(AvbBalance);
					acctVO.setAcctBalance(AcctBalance);
					acctVO.setDigitType(detailVO.getDIGIT_TYPE()); //實體戶數位戶
					// acctVO.setAvbBalance(amt.subtract(courtAmt)); // 可用餘額
					// (帳戶餘額 - 圈存金額)
					// acctVO.setAcctBalance(amt); // 帳戶餘額
					// acctVO.setBckBalance(courtAmt); // 圈存金額

					// 判斷是否有重複
					boolean noRepeat = true;
					for (AcctVO chekdata : acctVOs) {
						if (StringUtils.equals(actNo, chekdata.getAcctNo()) && StringUtils.equals(curCod, chekdata.getCurrency())) {
							noRepeat = false;
						}
					}
					if (noRepeat)
						acctVOs.add(acctVO);
				}

			}
		}

		return acctVOs;
	}

	/**
	 * 信託推介同意書查詢
	 *
	 * 使用電文: NFEI002
	 *
	 * @return 信託推介同意註記
	 */
	public String getCreditAgreeMark(String custID) throws Exception {

		if (StringUtils.isNotBlank(custID)) {

			// init util
			ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, "NFEI002");
			esbUtilInputVO.setModule("SOT701.getCreditAgreeMark");

			// head
			TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
			txHead.setDefaultTxHead();
			txHead.setHFMTID("0001"); // 規格書提到要加
			txHead.setHTLID("2004128");
			esbUtilInputVO.setTxHeadVO(txHead);

			// body
			NFEI002InputVO InputVO = new NFEI002InputVO();
			InputVO.setVQACID(custID);
			InputVO.setVQIDTY(cbsservice.getCBSIDCode(custID));
			InputVO.setVQKIND("1"); // 1現況 2歷史資料
			esbUtilInputVO.setNfei002InputVO(InputVO);

			// 發送電文
			List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
			if (vos.size() >= 1 && StringUtils.isBlank(vos.get(0).getNfei002OutputVO().getEMSGID())) {
				NFEI002OutputVO outputVO = vos.get(0).getNfei002OutputVO();
				NFEI002OutputDetailsVO outputDetailsVO = outputVO.getDetails().get(0);

				return outputDetailsVO.getAR102().trim().equals("Y") ? outputDetailsVO.getAR102().trim() : "N";
			}
		}

		return "N";
	}

	/**
	 * 信託帳號查詢 查詢168與368的帳號
	 *
	 * 使用電文: NFEI003
	 *
	 * @return 客戶信託帳號查詢
	 */
	public List<ESBUtilOutputVO> getTrustAcct(String custID) throws Exception {
		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, TRUST_ACCT_SEARCH);
		esbUtilInputVO.setModule("SOT701.getTrustAcct");

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		txHead.setHFMTID("0001"); // 規格書提到要加
		txHead.setHTLID("2004128");
		esbUtilInputVO.setTxHeadVO(txHead);

		// body
		NFEI003InputVO InputVO = new NFEI003InputVO();
		InputVO.setCustId(custID.trim());
		esbUtilInputVO.setNfei003InputVO(InputVO);

		return send(esbUtilInputVO);
	}

	/**
	 *
	 * @param acct
	 * @param custID
	 * @return //20200305_CBS_彥德_期間議價E008錯誤_分行送錯 每筆信託帳號在用NFEI003查分行上送
	 * @throws Exception
	 */
	private String getTrustAcctBranch(String acct, String custID) throws Exception {
		NFEI003OutputVO nfei003OutputVO = new NFEI003OutputVO();
		List<ESBUtilOutputVO> vos2 = getTrustAcct(custID);

		for (ESBUtilOutputVO esbUtilOutputVO : vos2) {
			nfei003OutputVO = esbUtilOutputVO.getNfei003OutputVO();
			if (StringUtils.isBlank(nfei003OutputVO.getEMSGTXT())) {
				for (NFEI003OutputDetailsVO nfei003OutputDetailsVO : nfei003OutputVO.getDetails()) {
					if (acct.equals(nfei003OutputDetailsVO.getAR101().trim())) {
						return nfei003OutputDetailsVO.getAR103().trim();
					}
				}
			} else {
				return "XXX";
			}
		}
		return "XXX";
	}

	public boolean isObu(String custID) throws Exception {
	    return "Y".equals(checkOBUbyIDType(custID));
    }

	/*
	 * 20220309之前名稱為checkOBUbyAcct
	 * 20220309 改用IDtype判斷，故同時更改functionName
	 *
	 */
	public String checkOBUbyIDType(String custID) throws Exception {
		// TODO Auto-generated method stub
		// OBU DBU要用帳號判斷
		// 發送電文
//		try{
//			for (FP032671OutputVO vo : getFp032671AcctData(custID)) {
//				for (FP032671OutputDetailsVO detail : vo.getDetails()) {
//					if (cbsservice.checkOBU(detail.getACNO_1()).equals("O")) {
//						return "Y";
//					}
//				}
//			}
//
//			return getObuFlagFromNFEI003(custID);
//
//			} catch(Exception e){
//				if(!e.getMessage().contains("ErrorCode: 2214")){
//				throw new Exception(e.getMessage());
//			}
//		}
//
//		return "N";
		String IDType = cbsservice.getCBSIDCode(custID);
		String[] OBUTypeList = {"13","22","29"};
		if(Arrays.asList(OBUTypeList).contains(IDType)){
			return "Y";
		} else {
			return "N";
		}
	}

	private String getObuFlagFrom085081_085105(List<CBSUtilOutputVO> acctData) {
		for (CBSUtilOutputVO each : acctData) {
			for (CBS085105OutputDetailsVO cbsDtlVO: each.getCbs085105OutputVO().getDetails()) {
				if ("O".equals(cbsservice.checkOBU(cbsservice.checkAcctLength(cbsDtlVO.getWA_X_ACCTNO()))))
					return "Y";
			}
		}
		return "N";
	}

	private String getObuFlagFromNFEI003(String custID) throws Exception {
		List<ESBUtilOutputVO> vos2 = getTrustAcct(custID);

		for (ESBUtilOutputVO esbUtilOutputVO : vos2) {
			NFEI003OutputVO nfei003OutputVO = esbUtilOutputVO.getNfei003OutputVO();
			if (StringUtils.isBlank(nfei003OutputVO.getEMSGTXT())) {
				for (NFEI003OutputDetailsVO nfei003OutputDetailsVO : nfei003OutputVO.getDetails()) {
					if(!nfei003OutputDetailsVO.getAR104().trim().equals("Y")){
						if (cbsservice.checkOBU(cbsservice.checkAcctLength((nfei003OutputDetailsVO.getAR101().trim()))).equals("O")) {
							return "Y";
						}
					}
				}
			}
		}
		return "N";
	}

	/*
	 * KYC更新信箱
	 * 20211005_#0777_偲偲_KYC加入信箱檢核邏輯
	 * 20220104_#0828_KYC調整email檢核電文上送欄位
	 */
	public void kycUpdateEmail(KYC311InputVO inputVO) throws Exception {
//		// body
//		WMS032154InputVO esbVO = new WMS032154InputVO();
//		esbVO.setCUST_NO(inputVO.getCUST_ID());
//		// 67000
//		esbVO.setMail(inputVO.getEMAIL_ADDR());
//		wms032154Service.kycUpdateEmail(esbVO);

		if(StringUtils.equals(inputVO.getEMAIL_ADDR(), inputVO.getCUST_EMAIL_BEFORE())){
			return;
		}
		if(StringUtils.isNotBlank(inputVO.getCUST_EMAIL_BEFORE()) && StringUtils.isBlank(inputVO.getEMAIL_ADDR())){
			return;
		}
		dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sql = new StringBuffer()
		.append("select INVEST_BRANCH_NBR ")
		.append("from TBKYC_INVESTOREXAM_M where 1=1 ")
		.append("and CUST_ID = :CUST_ID ");

		condition.setObject("CUST_ID", inputVO.getCUST_ID());
		condition.setQueryString(sql.toString());

		List<Map<String, Object>> list = dam.exeQuery(condition);
		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, MVC110001);
		esbUtilInputVO.setModule("SOT701.kycUpdateEmail");

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);

		// body
		MVC110001InputVO txBodyVO = new MVC110001InputVO();
		txBodyVO.setUUID("0Z31" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + txHead.getHSTANO() + "MVC001"); //4碼(通路+子通路)+西元日期8碼+13碼(流水號7碼 + MVC001 6碼)
		txBodyVO.setTELLER_NO("99940119");
		txBodyVO.setTX_CODE("110001");
		txBodyVO.setCUST_ID(inputVO.getCUST_ID());
		txBodyVO.setID_TYPE(cbsservice.getCBSIDCode(inputVO.getCUST_ID()));
		txBodyVO.setAFTER_EMAIL_ADDR(inputVO.getEMAIL_ADDR());
		txBodyVO.setCHNL("0Z");
		txBodyVO.setSUB_CHNL("31");
		txBodyVO.setON_OFF_LINE("N");
		try{
			txBodyVO.setBRANCH((String)list.get(0).get("INVEST_BRANCH_NBR"));
		} catch (Exception e) {
			txBodyVO.setBRANCH(inputVO.getBRANCH());
		}

		txBodyVO.setREASON(inputVO.getSAMEEMAIL_CHOOSE());
		if(StringUtils.equals("5", inputVO.getSAMEEMAIL_CHOOSE())){
			txBodyVO.setREMARK(inputVO.getSAMEEMAIL_REASON());
		}
		esbUtilInputVO.setMvc110001InputVO(txBodyVO);

		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
	}

	/**
	 * KYC更新地址
	 *
	 * @param KYC311InputVO
	 *            inputVO
	 * @throws Exception
	 */
	public void kycUpdateAddress(KYC311InputVO inputVO) throws Exception {
		// body
		WMS032154InputVO esbVO = new WMS032154InputVO();
		esbVO.setCUST_NO(inputVO.getCUST_ID());
		// 67000
		esbVO.setAddr(inputVO.getCUST_ADDR_1());

		wms032154Service.kycUpdateAddress(esbVO);
	}

	/**
	 * KYC更新聯絡方式
	 *
	 * @param KYC311InputVO
	 *            inputVO
	 * @throws Exception
	 */
	public void kycUpdatePhone(KYC311InputVO inputVO) throws Exception {
		// body
		WMS032154InputVO esbVO = new WMS032154InputVO();
		esbVO.setCUST_NO(inputVO.getCUST_ID());
		// 67000
		esbVO.setCon_tel(inputVO.getNIGHT());
		esbVO.setHandphone(inputVO.getTEL_NO());
		esbVO.setFax(inputVO.getFAX());
		// 67101
		esbVO.setResd_tel(inputVO.getDAY());

		wms032154Service.kycUpdatePhone(esbVO);
	}

	/**
	 * KYC更新基本資料
	 *
	 * @param KYC311InputVO
	 *            inputVO
	 * @throws Exception
	 */
	public void kycUpdateBasic(KYC311InputVO inputVO) throws Exception {
		// body
		WMS032154InputVO esbVO = new WMS032154InputVO();
		esbVO.setCUST_NO(inputVO.getCUST_ID());


		kycBasicDataTransfer(inputVO, esbVO);

		// 67115
		esbVO.setSICK_TYPE(inputVO.getSICK_TYPE());

		wms032154Service.kycUpdateBasic(esbVO);
	}

	/** 將 KYC 基本資料從 KYC311InputVO 新理專格式轉變為 WMS032154InputVO 電文格式 **/
	public void kycBasicDataTransfer(KYC311InputVO inputVO, WMS032154InputVO esbVO) {
		// 67101
		if(StringUtils.isNotBlank(inputVO.getEDUCATION())){
			// 20200519_CBS_麗文_KYC_教育程度問題 參考fp032151Service.java的dealEducation
			// 預防其他管道直接帶入7(其他)的結果，故回送電文時給08
			if(inputVO.getEDUCATION().equals("7")){
				esbVO.setEDUCATION("08");
			}else{
				esbVO.setEDUCATION("0" + inputVO.getEDUCATION()); // 系統端只顯示1碼(1-7)，但是電文資料傳遞給2碼，前面加一個0
			}

		}

		if(StringUtils.isNotBlank(inputVO.getCHILD_NO())){
			esbVO.setCHILD_NO(dealUpdateChildNo(inputVO.getCHILD_NO()));
		}
		if(StringUtils.isNotBlank(inputVO.getMARRAGE())){
			if ("1".equals(inputVO.getMARRAGE())) {
				esbVO.setMARRAGE("S");
			} else if ("2".equals(inputVO.getMARRAGE())) {
				esbVO.setMARRAGE("M");
			} else {
				esbVO.setMARRAGE(" ");
			}
		}
		if (StringUtils.isNotBlank(inputVO.getCAREER())) {
			String career = inputVO.getCAREER().trim();
			if (career.length() == 1) {
				esbVO.setCAREER("000" + career); // 系統端只顯示1-2碼(1-47)，但是電文資料傳遞給4碼，補對應數量的0
													// (
			} else {
				esbVO.setCAREER("00" + career); // 系統端只顯示1-2碼，但是電文資料傳遞給4碼，補對應數量的0
			}
		}
	}

	/**
	 * KYC更新C值
	 *
	 * @param KYC311InputVO
	 *            inputVO
	 * @throws Exception
	 */
	public void kycUpdateCValue(KYC311InputVO inputVO) throws Exception {
		// body
		WMS032154InputVO esbVO = new WMS032154InputVO();
		esbVO.setCUST_NO(inputVO.getCUST_ID());
		// 67157
		esbVO.setCVALUE(inputVO.getCUST_RISK_AFR());
		esbVO.setBRANCH(inputVO.getBRANCH());
		esbVO.setKYC_EXPIRY_DATE(inputVO.getEXPIRY_DATE());
		esbVO.setKYC_TEST_DATE(inputVO.getKYC_TEST_DATE());

		wms032154Service.kycUpdateCValue(esbVO);
	}

	/**
	 * KYC更新主管已覆核欄位
	 * @throws Exception
	 */
	public void kycUpdateSupervisorCheck(KYC311InputVO inputVO) throws Exception {
		// body
		WMS032154InputVO esbVO = new WMS032154InputVO();
		esbVO.setCUST_NO(inputVO.getCUST_ID());
		wms032154Service.kycUpdateSupervisorCheck(esbVO);
	}

	public String dealUpdateChildNo(String childNo) {
		// 根據電文給的值跟KYC.CHILD_NO做轉換
		if (isNotBlank(trim(childNo))) {
			return childNo.equals("0") ? "5" : childNo;
		} else {
			return "";
		}
	}

	/**
	 * 取得客戶統編查詢契約
	 *
	 * 使用電文: NMVP6A
	 *
	 * version: 2019-12-23 add by ocean : WMS-CR-20191009-01_金錢信託套表需求申請單
	 *
	 */
	public List<ContractVO> getContractList(String custID) throws Exception {

		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, NMVP6A);
		esbUtilInputVO.setModule("SOT701.getContractList");

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);

		// body
		NMVP6AInputVO txBodyVO = new NMVP6AInputVO();
		txBodyVO.setCUST_ID(custID);
		esbUtilInputVO.setNmvp6aInputVO(txBodyVO);

		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		List<ContractVO> contractList = new ArrayList<ContractVO>();

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NMVP6AOutputVO nmvp6aOutputVO = esbUtilOutputVO.getNmvp6aOutputVO();

			if (null != nmvp6aOutputVO && CollectionUtils.isNotEmpty(nmvp6aOutputVO.getDetails())) {
				for (NMVP6AOutputDetailsVO detailVO : nmvp6aOutputVO.getDetails()) {
					ContractVO contractVO = new ContractVO();
					contractVO.setCONTRACT_ID(detailVO.getCONTRACT_ID());
					contractVO.setACC(cbsservice.checkAcctLength(detailVO.getACC().trim()));
					contractVO.setCUR1(StringUtils.equals("NTD", detailVO.getCUR1()) ? "TWD" : detailVO.getCUR1());
					contractVO.setVALUE1(new EsbUtil().decimalPoint(detailVO.getVALUE1(), 2));
					contractVO.setCUR2(StringUtils.equals("NTD", detailVO.getCUR2()) ? "TWD" : detailVO.getCUR2());
					contractVO.setVALUE2(new EsbUtil().decimalPoint(detailVO.getVALUE2(), 2));
					contractVO.setCUR3(StringUtils.equals("NTD", detailVO.getCUR3()) ? "TWD" : detailVO.getCUR3());
					contractVO.setVALUE3(new EsbUtil().decimalPoint(detailVO.getVALUE3(), 2));
					contractVO.setCUR4(StringUtils.equals("NTD", detailVO.getCUR4()) ? "TWD" : detailVO.getCUR4());
					contractVO.setVALUE4(new EsbUtil().decimalPoint(detailVO.getVALUE4(), 2));
					contractVO.setCUR5(StringUtils.equals("NTD", detailVO.getCUR5()) ? "TWD" : detailVO.getCUR5());
					contractVO.setVALUE5(new EsbUtil().decimalPoint(detailVO.getVALUE5(), 2));
					contractVO.setCONTRACT_P_TYPE(detailVO.getCONTRACT_P_TYPE());
					contractVO.setCONTRACT_SPE_FLAG(detailVO.getCONTRACT_SPE_FLAG());
					contractVO.setCREDIT_FLAG(detailVO.getCREDIT_FLAG());
					contractVO.setTRUST_PEOP_NUM(new EsbUtil().decimalPoint(detailVO.getTRUST_PEOP_NUM(), 0));
					contractVO.setGUARDIANSHIP_FLAG(nmvp6aOutputVO.getGUARDIANSHIP_FLAG());
					contractVO.setCONTRACT_END_DAY(new EsbUtil().toAdYearMMdd(detailVO.getCONTRACT_END_DAY(), false));
					contractList.add(contractVO);
				}
			}
		}

		return contractList;
	}

    /**
 	 * 洗錢防制電文 (AML & Precheck)
     * 使用電文: FC032168 (原) -> EB032168 (新)
     * WMS-CR-20200102-01_依保代管理規則33-1第1項修改保險鍵機作業
     *
     */
 	public EB032168OutputVO getEB032168(String custID) throws Exception {

 		//init util
 		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, EB032168);
 		esbUtilInputVO.setModule(thisClaz + new Object(){}.getClass().getEnclosingMethod().getName());

 		//head
 		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
 		txHead.setDefaultTxHead();
		txHead.setHTLID("2004011");
		esbUtilInputVO.setTxHeadVO(txHead);

 		//body
 		EB032168InputVO eb032168InputVO = new EB032168InputVO();
 		eb032168InputVO.setFUNC("0");
 		eb032168InputVO.setFUNC_01("0");
 		eb032168InputVO.setCUST_NO(custID);
 		eb032168InputVO.setID_TYPE(cbsservice.getCBSIDCode(custID));
        esbUtilInputVO.setEb032168InputVO(eb032168InputVO);

        //發送電文
        List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

        EB032168OutputVO eb032168OutputVO = new EB032168OutputVO();

        for (ESBUtilOutputVO esbUtilOutputVO : vos) {
        	eb032168OutputVO = esbUtilOutputVO.getEb032168OutputVO();
        }

 		return StringUtils.isBlank(eb032168OutputVO.getCUST_NAME())? null: eb032168OutputVO;
 	}

 	/**
 	 * 取得金錢信託關係人-管理運用人
     * 使用電文: NMVP8A
     * WMS-CR-20210723-02_新增信託財產管理運用指示書-有權指示人套表
     * */
 	public NMVP8AOutputVO getNMVP8AData(String contractNo) throws Exception {

 		//init util
 		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, NMVP8A);
 		esbUtilInputVO.setModule(thisClaz + new Object(){}.getClass().getEnclosingMethod().getName());

 		//head
 		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
 		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);

 		//body
		NMVP8AInputVO nmvp8aInputVO = new NMVP8AInputVO();
		nmvp8aInputVO.setCONTRACT_NO(contractNo);
        esbUtilInputVO.setNmvp8aInputVO(nmvp8aInputVO);

        //發送電文
        List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

        NMVP8AOutputVO nmvp8aOutputVO = new NMVP8AOutputVO();

        for (ESBUtilOutputVO esbUtilOutputVO : vos) {
        	NMVP8AOutputVO outputVO = esbUtilOutputVO.getNmvp8aOutputVO();
        	if (outputVO != null && StringUtils.isNotBlank(outputVO.getINS_TYPE()) && CollectionUtils.isNotEmpty(outputVO.getDetails())) {
        		nmvp8aOutputVO.setINS_TYPE(outputVO.getINS_TYPE());
        		nmvp8aOutputVO.setCON_TYPE(outputVO.getCON_TYPE());

        		List<NMVP8AOutputDetailsVO> dList = new ArrayList<NMVP8AOutputDetailsVO>();
        		for(NMVP8AOutputDetailsVO details : outputVO.getDetails()) {
        			dList.add(details);
        		}

        		nmvp8aOutputVO.setDetails(dList);
        	}
        }

 		return nmvp8aOutputVO;
 	}

 	public int getCustAge(String CustID) throws Exception{
 		return fp032151Service.getCustAge(CustID);
 	}

 	private boolean equal168Acct(String acct){
		if (acct.substring(0, 1).equals("0")) {
			if (acct.substring(5, 8).equals("168")) {
				return true;
			}
		} else if (acct.substring(0, 1).equals("8")) {
			if (acct.substring(0, 4).equals("8168")) {
				return true;
			}
		}
		return false;
 	}

 	//取得客戶高資產註記資料(High Net Worth Client Data)
 	//KYC用
 	public void getHNWCData(Object body, IPrimitiveMap header) throws Exception {
 		sot701InputVO = (SOT701InputVO) body;
		sot701OutputVO = new SOT701OutputVO();
		sot701OutputVO.setHnwcDataVO(this.getHNWCData(sot701InputVO.getCustID()));
		sendRtnObject(sot701OutputVO);
	}
 	 	
 	/***
 	 * 取得客戶高資產註記資料(High Net Worth Client Data)
 	 * KYC用
 	 * -- 高資產註記到期日
 	 * -- 高資產註記註銷日
 	 * -- 高資產客戶註記
 	 * @param custID
 	 * @return
 	 * @throws Exception
 	 */
 	public CustHighNetWorthDataVO getHNWCData(String custID) throws Exception {
 		CustHighNetWorthDataVO hnwcVO = new CustHighNetWorthDataVO();
 		hnwcVO.setValidHnwcYN("N"); //非高資產客戶或已失效
 		Date dueDate = null;
 		Date invalidDate = null;	

		List<CBSUtilOutputVO> list = _060425_060433dao.search(custID, cbsservice.getCBSIDCode(custID), "0005");
		CBS060433OutputVO outputVO = list.get(0).getCbs060433OutputVO();

		if (isNotBlank(outputVO.getIDNo()) && isNotBlank(outputVO.getMemo1())) {
			String dueDateStr = outputVO.getMemo1().substring(24, 32);
			String invalidDateStr = outputVO.getMemo1().substring(16, 24);
			
			//到期日
			if(isNotBlank(dueDateStr)) {
				dueDate = new SimpleDateFormat("yyyyMMdd").parse(dueDateStr);
				hnwcVO.setDueDate(dueDate); 
			}
			//註銷日
			if(isNotBlank(invalidDateStr)) {
				invalidDate = new SimpleDateFormat("yyyyMMdd").parse(invalidDateStr);
				hnwcVO.setInvalidDate(invalidDate); 
			}
			
			//有效高資產客戶的判斷：「到期日有值且沒有過期」跟「註銷日期還沒到」
			if((dueDate != null && compareWithSysDate(dueDate)) &&
					(invalidDate == null || compareWithSysDate(invalidDate))) {
				hnwcVO.setValidHnwcYN("Y"); //有效高資產客戶
			}
		}
		
		return hnwcVO;
	}
 	
	/**
	 * 取得不合作例外帳戶例外註記
	 *
	 * @param inputVO
	 * @return true = 待確認
	 * @throws Exception
	 */
	private String getExceptionAcctFlag(String custID) throws Exception {
		String mark = "N";

		if (StringUtils.isNotBlank(custID)) {
			List<CBSUtilOutputVO> data = _067050_067115dao.search(custID, cbsservice.getCBSIDCode(custID));

			try{
				mark = data.get(0).getCbs067115OutputVO().getFinManInstrmt1().substring(35,36);
			} catch (Exception e) {}
		}

		return StringUtils.equals(mark, "Y") ? "Y" : "N";
	}
 		
 	
 	/* 發送電文功能 - end */
}
