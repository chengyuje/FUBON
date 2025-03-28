package com.systex.jbranch.fubon.commons.esb;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.esb.vo.ESBErrorVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.TxHeadVO;
import com.systex.jbranch.fubon.commons.esb.xml.parse.EsbXmlParseUtil;
import com.systex.jbranch.fubon.commons.tx.tool.Journal;
import com.systex.jbranch.fubon.commons.tx.tool.Navigation;
import com.systex.jbranch.fubon.jlb.DataFormat;
import com.systex.jbranch.fubon.jlb.FmpJRunTx;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSPARAMETERVO;
import com.systex.jbranch.platform.common.util.PlatformContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer.*;
import static java.lang.System.currentTimeMillis;

/**
 * Created by SebastianWu on 2016/8/10.
 *
 * @version 2016/10/20 Sebastian add
 *          {@link EsbUtil#decimalPadding(BigDecimal, int)} for 電文傳送小數位轉換使用
 *
 * @version 2016/10/27 Sebastian add {@link EsbUtil#passed(String)} for by
 *          pass特定電文下行回覆代碼
 */
@Component
public class EsbUtil extends FubonWmsBizLogic {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	private static Logger esblog = LoggerFactory.getLogger("ESB_LOG");
	private SimpleDateFormat SDFYYYY_MM_DD = new SimpleDateFormat("yyyy/MM/dd");
	private FmpJRunTx fmpJRunTx;
	private DataAccessManager dam = null;
	private QueryConditionIF condition = null;
	private String type;
	private String htxtid;
	private String esbID;
	private String esbUrl;
	private String hretrn = null; // 是否還有下頁資料; C:還有資料 E:已無資料
	private String hstano; // 電文流水號
	// private String newEsbSnoLog = null; //SHOW交易序號 FOR UI
	private List resList = new ArrayList(); // 回傳資料集合
	private String module; // 模組代號
	private static Map<String, JAXBContext> JAXBContextMap = new HashMap<>();
	
	private static Map<Integer, JaxbInstance> jaxbMap = new HashMap<>();
	// regex
	private Pattern pattern = Pattern.compile("TxBody");
	
	public EsbUtil() {
		this.initPool();
	}
	
	private synchronized void initPool() {
		try {
			int MAX_JAXB_INSTANCES = "AIX".equals(System.getProperty("os.name")) ? 5 : 2;
			if (jaxbMap.isEmpty()) {
				for (int i=1; i<=MAX_JAXB_INSTANCES; i++) {
					JAXBContext inContext = JAXBContext.newInstance(ESBUtilInputVO.class);
					JaxbInstance instance = new JaxbInstance();
					instance.id = i;
					instance.inContext = inContext;
					instance.used = false;
					jaxbMap.put(i, instance);
					logger.info("newInstance JaxbInstance #" + i);
				}
			}
		} catch (Exception e) {
			logger.warn("init fail", e);		
		}
	}
	
	private synchronized void increaseJaxbInstance() {
		try {
			JAXBContext inContext = JAXBContext.newInstance(ESBUtilInputVO.class);
			JaxbInstance instance = new JaxbInstance();
			instance.id = jaxbMap.size() + 1;
			instance.inContext = inContext;
			instance.used = false;
			jaxbMap.put(instance.id, instance);
			logger.info("increase JaxbInstance #" + instance.id);
		} catch (Exception e) {
			logger.warn("increase JaxbInstance #" + (jaxbMap.size() + 1) + " fail", e);
		}
	}
	
	private int countFree() {
		int cnt = 0;
		for (JaxbInstance instance : jaxbMap.values()) {
			if (!instance.used) cnt++;
		}
		if (cnt == 0) {	// 用完, 就主動加一instance
			this.increaseJaxbInstance();
		}
		return cnt;
	}

	
	private JaxbInstance getFreeInstance() {
		for (JaxbInstance instance : jaxbMap.values()) {
			if (!instance.used) {
				instance.used = true;
				int cnt = this.countFree();
				logger.info("Get #{}, free: {}", instance.id, cnt);
				return instance;
			}
		}
		return null;
	}
	
	private void freeJaxbInstance(JaxbInstance instance) {
		instance.used = false;
		logger.info("release #{}, free: {}", instance.id, this.countFree());
	}

	public FmpJRunTx getFmpJRunTx() {
		return fmpJRunTx;
	}

	public void setFmpJRunTx(FmpJRunTx fmpJRunTx) {
		this.fmpJRunTx = fmpJRunTx;
	}

	/**
	 * 取指定電文主機URL
	 *
	 * @param paramType
	 * @return
	 * @throws JBranchException
	 */
	public String getEsbUrl(String paramType) throws JBranchException {
		String esbUrl = "";

		StringBuffer sql = new StringBuffer("select PARAM_NAME from TBSYSPARAMETER where PARAM_TYPE = 'CBS.URL.HTTP' ");
		dam = getDataAccessManager();
		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setQueryString(sql.toString());

		List<Map> esbUrlRes = dam.exeQuery(condition);
		esbUrl = (!esbUrlRes.isEmpty()) ? (String) esbUrlRes.get(0).get("PARAM_NAME") : null;

		return esbUrl;
	}

	/**
	 * 取指定電文主機ID
	 *
	 * @param paramType
	 * @return
	 * @throws JBranchException
	 */
	public String getEsbID(String paramType) throws JBranchException {
		String esbID = "";
		paramType = new StringBuffer(paramType).append(".ID").toString();

		StringBuffer sql = new StringBuffer("select PARAM_NAME from TBSYSPARAMETER where PARAM_TYPE = :ESB_ID ");
		dam = getDataAccessManager();
		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setObject("ESB_ID", paramType);
		condition.setQueryString(sql.toString());
		List<Map> esbIDRes = dam.exeQuery(condition);

		esbID = (!esbIDRes.isEmpty()) ? (String) esbIDRes.get(0).get("PARAM_NAME") : null;

		return esbID;
	}

	/**
	 * init FmpJRunTx by using ESB_URL and HTXTID
	 *
	 * by using PARAM_TYPE and htxtid
	 * 
	 * @see EsbUtil#getEsbUrl(String)
	 * @see EsbUtil#getEsbID(String)
	 */
	public void initEsbFmpJRunTx() throws Exception {

		// using PARAM_TYPE to query login info
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(htxtid)) {
			esbUrl = getEsbUrl(type);
			esbID = getEsbID(type);
			setFmpJRunTx(new FmpJRunTx(esbUrl, htxtid));
//			logger.debug("[ESB] ESB_ID: " + esbID + ", ESB_URL: " + esbUrl);
		}
	}

	/**
	 * init EsbFmpJRunTx
	 *
	 * @param type
	 * @param htxtid
	 * @throws Exception
	 */
	public ESBUtilInputVO getTxInstance(String type, String htxtid) throws Exception {
		this.type = type;
		this.htxtid = htxtid;
		this.hstano = getEsbSeq();

		initEsbFmpJRunTx();

		// init ESBUtilInputVO
		ESBUtilInputVO esbUtilInputVO = new ESBUtilInputVO();
		esbUtilInputVO.setTxid(htxtid);
		esbUtilInputVO.setEncoding(ENCODING_BIG5);
		esbUtilInputVO.setFontremapping(FONTREMAPPING_OFF);
		TxHeadVO txHead = new TxHeadVO();
		txHead.setId(esbID);
		txHead.setSeq(hstano);
		txHead.setHtxtid(htxtid);
		txHead.setHDRVQ1(PlatformContext.getBean(Navigation.class).getHostDriveQueue(htxtid));
		esbUtilInputVO.setTxHeadVO(txHead);

		return esbUtilInputVO;
	}

	/**
	 * JAXB 發送電文 return response data directorly
	 *
	 * @return List
	 * @throws Exception
	 */
	public List<ESBUtilOutputVO> send(ESBUtilInputVO esbUtilInputVO) throws Exception {
		List onMsg = new ArrayList();
		List offMsg = new ArrayList();
		Timestamp startTime = new Timestamp(System.currentTimeMillis());
		List result;
		try {
			result = send(esbUtilInputVO, onMsg, offMsg);
		} finally {
			Journal journal = PlatformContext.getBean(Journal.class);
			journal.setSeq(hstano);
			journal.setItemId(htxtid);
			journal.setOnMsg(onMsg);
			journal.setOffMsg(offMsg);
			journal.setStartTime(startTime);
			journal.setEndTime(new Timestamp(currentTimeMillis()));

			EsbRecorder.getInstance()
					.record(journal);
		}
		return result;
	}
	
	

	public List<ESBUtilOutputVO> send(ESBUtilInputVO esbUtilInputVO, List onMsg, List offMsg) throws Exception {
			JaxbInstance instance = null;
			try {
				this.module = esbUtilInputVO.getModule();

				if (StringUtils.isBlank(this.module)) {
					throw new Exception("沒有交易沒組代號!");
				}

				StringWriter writer = new StringWriter();
				// JAXBContext context = JAXBContext.newInstance(ESBUtilInputVO.class);
//				JAXBContext context = JAXBContextMap.get(ESBUtilInputVO.class.getName());
//				if (context == null ) {
//					context = JAXBContext.newInstance(ESBUtilInputVO.class);
//					JAXBContextMap.put(ESBUtilInputVO.class.getName(), context);
//				}
				instance = this.getFreeInstance();
				if (instance == null) throw new JBranchException("電文系統忙碌中，請稍侯再試");
				JAXBContext context = instance.inContext;
				Marshaller marshal = context.createMarshaller();
				marshal.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

				marshal.marshal(esbUtilInputVO, writer);
				fmpJRunTx.setInboundXML(writer.toString());
			} catch (JBranchException je) {
				throw je;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (instance != null) this.freeJaxbInstance(instance);
			}

			esbSend();

			onMsg.add(fmpJRunTx.getInboundXMLString());
			offMsg.add(fmpJRunTx.getOutboundXMLString());

			return checkStatus(esbUtilInputVO, onMsg, offMsg);
		}

	/**
	 * send esb xml content
	 * 
	 * @see FmpJRunTx#run(String, String)
	 *
	 *      depart the send and get method using {@link #getData()} to get
	 *      response data, which response data is editable before calling
	 *      {@link #getData()}
	 *
	 * @throws Exception
	 */
	protected void esbSend() throws Exception {
		fmpJRunTx.run(esbID, esbID);
		esbLog(ESB_SEND);
	}

	public List<ESBUtilOutputVO> checkStatus(ESBUtilInputVO esbUtilInputVO, List onMsg, List offMsg) throws Exception {
		List<ESBUtilOutputVO> resultList = new ArrayList<>();
		// first data
		ESBUtilOutputVO outputVO = rescive();
		// get HRETRN return code
		String hretrn = outputVO.getTxHeadVO().getHRETRN();
		resultList.add(outputVO);

		// check HRETRN return code
		if (StringUtils.equals(hretrn, "C")) {
			esbUtilInputVO.getTxHeadVO().setHRETRN(hretrn);
			List<ESBUtilOutputVO> outputs = send(esbUtilInputVO, onMsg, offMsg);
			resultList.addAll(outputs);
		}

		return resultList;
	}

	/**
	 * get ESB response data unmarshaller to ESBUtilOutputVO
	 * 
	 * @return
	 * @throws Exception
	 */
	public ESBUtilOutputVO rescive() throws Exception {
		// 取得電文回覆內容
		String fcOutboundXML = fmpJRunTx.getOutboundXMLString(); // ESB回傳電文內容
		esbLog(ESB_RESPONSE);

		// check error & return code
		String hretrn = parseResStatus(fcOutboundXML);

		// TxBody to HTXTID
		fcOutboundXML = pattern.matcher(fcOutboundXML).replaceAll(htxtid);

		return unmarshaller(fcOutboundXML);
	}

	/**
	 * parse TxHead.HERRID for response status 當主機回覆制式化錯誤訊息時,直接將訊息顯示於畫面上
	 *
	 * @param fcOutboundXML
	 * @return String
	 * @throws JDOMException
	 * @throws IOException
	 */
    private String parseResStatus(String fcOutboundXML) throws Exception {
        Document xmlDoc;
        SAXBuilder sb = new SAXBuilder();
        xmlDoc = sb.build(new StringReader(fcOutboundXML));
        // 解析xml
        Element rootElement = xmlDoc.getRootElement();
        Element txHeadElement = rootElement.getChild("TxHead");
        String herrid = txHeadElement.getChild("HERRID").getText().trim(); //執行狀態
        String hretrn = txHeadElement.getChild("HRETRN").getText(); //是否還有下頁資料 C:還有資料  E:已無資料

        //有錯誤
        if(!passed(herrid)){
        	ESBUtilOutputVO outputVO = unmarshaller(fcOutboundXML);
	        ESBErrorVO errVO = outputVO.getEsbErrorVO();            
	        String htxid = txHeadElement.getChild("HTXTID").getText().trim();
	        esbLog(ESB_ERROR);
	        
	        try {
	            if(errVO == null ) {
	            	Element txBodyElement = rootElement.getChild("TxBody");
	            	String emsgid = txBodyElement.getChild("EMSGID").getText();
	            	String emsgtxt = txBodyElement.getChild("EMSGTXT").getText();
	            	
	            	errVO = new ESBErrorVO();
	            	errVO.setEMSGID(emsgid);
	            	errVO.setEMSGTXT(emsgtxt);
	            }    
        	} catch(Exception e) {
        		esblog.info("ESBUtile.parseResStatus ERROR: " + e.getMessage());
        		
        		throw new JBranchException(String.format("ESB_LOG HTXTID:%s ,HERRID:%s ,電文發送Log檔.esb_sno:%s",
						htxid, herrid, this.hstano));
        	}
	        
	        throw new JBranchException(String.format("ESB_LOG HTXTID:%s ,HERRID:%s ,EMSGID:%s ,EMSGTXT:%s , 電文發送Log檔.esb_sno:%s",
					htxid, herrid, errVO.getEMSGID(), errVO.getEMSGTXT(),this.hstano));
        }

        return hretrn;
    }

	/**
	 * 回覆代碼對照TABLE設定參數 若代碼存在參數表中則無須另做處理
	 * 
	 * @param herrid
	 * @return
	 */
	private Boolean passed(String herrid) throws JBranchException {
		Boolean isPass = Boolean.FALSE;

		String sql = new StringBuffer("from TBSYSPARAMETERVO vo ").append("where vo.comp_id.PARAM_TYPE = :PARAM_TYPE").toString();

		dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_HQL);
		condition.setObject("PARAM_TYPE", "SOT.ESB_ERROR_CODE_BYPASS");
		condition.setQueryString(sql);

		List<TBSYSPARAMETERVO> list = (List<TBSYSPARAMETERVO>) dam.exeQuery(condition);

		for (TBSYSPARAMETERVO vo : list) {
			String statusCode = vo.getPARAM_NAME_EDIT();

			if (StringUtils.isNotBlank(statusCode)) {
				String[] codes = statusCode.split(",");
				isPass = Arrays.asList(codes).contains(herrid);
			}
		}

		return isPass;
	}

	/**
	 * unmarshaller xml
	 *
	 * @param fcOutboundXML
	 * @return
	 * @throws JAXBException
	 */
	public ESBUtilOutputVO unmarshaller(String fcOutboundXML) throws Exception {
		// JAXBContext context = JAXBContext.newInstance(ESBUtilOutputVO.class);
		//
		// Unmarshaller unmarshaller = context.createUnmarshaller();

		// return (ESBUtilOutputVO) unmarshaller.unmarshal(new
		// StringInputStream(fcOutboundXML));

		ESBUtilOutputVO vo = new ESBUtilOutputVO();
		EsbXmlParseUtil xmlUtil = new EsbXmlParseUtil(vo.getClass());

//		logger.info("#this.htxtid:" + this.htxtid);
//		logger.info("解析電文：\r\n" + fcOutboundXML);
		for (String key : new String[] { "TxHead", "TxBody", this.htxtid }) {
			Field field = xmlUtil.MESSAGE_MAP.get(key);

			if (field == null)
				continue;

			field.setAccessible(true);
			field.set(vo, xmlUtil.xmlToObject(fcOutboundXML, key));
		}

		return vo;
	}

	/**
	 * @deprecated get ESB response data
	 *
	 * @return
	 * @throws Exception
	 */
	private List getResList() throws JBranchException {

		// 取得電文回覆內容
		Document xmlDoc;
		String fcOutboundXML = fmpJRunTx.getOutboundXMLString(); // ESB回傳電文內容

		SAXBuilder sb = new SAXBuilder();

		try {
			esbLog(ESB_RESPONSE);
			xmlDoc = sb.build(new StringReader(fcOutboundXML));

			// 解析xml
			Element rootElement = xmlDoc.getRootElement();
			Element txHeadElement = rootElement.getChild("TxHead");
			Element txBodyElement = rootElement.getChild("TxBody");

			this.hretrn = txHeadElement.getChild("HRETRN").getText(); // 是否還有下頁資料
																		// C:還有資料
																		// E:已無資料
			String herrid = txHeadElement.getChild("HERRID").getText().trim(); // 執行狀態

			if ("0000".equals(herrid)) {
				resList.addAll(txBodyElement.getChildren());

				/*
				 * if hretrn is "C" then recall esbSend() to get rest of data
				 */
				if ("C".equals(this.hretrn)) {
					esbSend();
					getResList();
				}
			}
			// Error
			else {
				String txid = fmpJRunTx.getInboundXML().getAttributeValue("txid");
				String errorEmsgid = txBodyElement.getChild("EMSGID").getText().trim();
				String errorEmsTxt = txBodyElement.getChild("EMSGTXT").getText().trim();
				String msg = String.format("NMVIPA ESB ERROR" + "\ntxid:%s\nHERRID:%s\nEMSGID:%s", txid, herrid, errorEmsgid);
				esblog.error(msg + "\n" + errorEmsTxt);
				throw new JBranchException(errorEmsTxt);
			}

		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JBranchException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return resList;
	}

	/**
	 * @deprecated call {@see #getResList()} transform List<Object> 2 List<Map>
	 *
	 * @return
	 * @throws Exception
	 */
	public List getData() throws Exception {
		List res = getResList();

		// to List<Map<String, String>>
		List<Map> data = new ArrayList<>();
		Map<String, Object> pa = new HashMap<>(); // Parent el
		List txRepeats = new ArrayList();

		for (Object repO : res) {
			Element rep = (Element) repO;
			Map<String, String> ch = new HashMap<>(); // Children el

			// 取TxRepeat的明細資料
			for (Object childO : rep.getChildren()) {
				Element child = (Element) childO;
				ch.put(child.getName(), child.getText());
			}

			// put ch el into pa el
			if (MapUtils.isNotEmpty(ch)) {
				txRepeats.add(ch);
			}

			// 若為TxBody欄位直接加入ch內
			if (rep.getChildren().size() == 0) {
				pa.put(rep.getName(), rep.getValue());
			}
		}
		if (CollectionUtils.isNotEmpty(txRepeats))
			data.addAll(txRepeats);
		data.add(pa);

		return data;
	}

	/**
	 * 記錄電文內容
	 *
	 * @param status
	 *            S:上行; R:下行
	 * @throws Exception
	 */
	public void esbLog(String status) throws Exception {
		boolean isSendTop = ESB_SEND.equals(status);
		String sendType = isSendTop ? "上行" : "下行";
		String content = isSendTop ? fmpJRunTx.getInboundXMLString() : fmpJRunTx.getOutboundXMLString();

		if (StringUtils.isBlank(content))
			throw new Exception(sendType + "電文內容為空");
		DataFormat dfObj = new DataFormat();
		
		
		// 遮罩CUST_ID
		content = dfObj.maskXml(content, "CUST_?[IN][DO]");

		String hstano = isSendTop ? fmpJRunTx.getInboundXML().getChild("TxHead").getChildText("HSTANO") : fmpJRunTx.getOutboundXML().getChild("TxHead").getChildText("HSTANO");

		String log = new StringBuffer().append("[").append(this.hstano).append("]").append("[").append(sendType).append("]").append("[").append(htxtid).append("]").append("[").append(hstano).append("]").append("[").append(this.module).append("]").toString();
		logger.info(log); // 紀錄電文重要參數以方便從 jbranch.log 追蹤 esb.log
		esblog.info(log + "\nsendMsg:\n" + content + "\n"); // esb.log 紀錄完整電文資訊
	}

	/***
	 * 取得TBSYS_ESB_LOG流水號
	 * 
	 * @return
	 * @throws JBranchException
	 */
	private String getEsbLogSno() throws JBranchException {
		DataAccessManager dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		condition.setQueryString("SELECT SQ_TBSYS_ESB_LOG_SNO.NEXTVAL FROM DUAL");
		List<Map<String, Object>> list = dam.exeQuery(condition);
		BigDecimal nextVal = (BigDecimal) list.get(0).get("NEXTVAL");

		return nextVal.toString();
	}

	/**
	 * 下單交易400主機(非客戶) 上行電文欄位格式 9(08) 要用民國年,如今日: 2016/10/18 => 01051018 EX:目前
	 * NFBRN1Z.TRADE_DATE 和EFF_DATE
	 *
	 * @param date
	 * @throws Exception
	 */
	public String toChineseYearMMdd(Date date) throws Exception {
		if (date != null) {
			String[] dates = StringUtils.split(SDFYYYY_MM_DD.format(date), "/");
			int chYear = Integer.valueOf(dates[0]) - 1911;
			String chyear = (chYear < 1000) ? "0" + String.valueOf(chYear) : String.valueOf(chYear);
			return chyear + dates[1] + dates[2];
		} else {
			return null;
		}
	}

	/**
	 * 電文欄位格式 9(07) 要用民國年,如今日: 2016/10/18 => 1051018
	 * 
	 * @param date
	 * @param is9_07
	 *            else return 9_08
	 * @return
	 * @throws Exception
	 */
	public String toChineseYearMMdd(Date date, boolean is9_07) throws Exception {
		if (date != null && is9_07) {
			return StringUtils.right(toChineseYearMMdd(date), 7);
		}
		if (date != null && !is9_07) {
			return toChineseYearMMdd(date);
		} else {
			return null;
		}
	}

	/**
	 * 電文欄位格式 下行回傳9(07)民國年,轉換成西元年。如今日: 1051018 => 20161018
	 * 
	 * @param chDate
	 * @return
	 * @throws Exception
	 */
	public Date toAdYearMMdd(String chDate) throws Exception {
		// if (chDate != null && Integer.parseInt(chDate) != 0) {
		if (StringUtils.isNotBlank(chDate)) {
			chDate = StringUtils.replace(chDate.trim(), "/", "");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			// int chYear = Integer.valueOf(StringUtils.left(chDate, 3)) + 1911;
			int chYear = 1900;
			if (StringUtils.length(chDate) == 8) {
				chYear = Integer.valueOf(StringUtils.left(chDate, 4)) + 1911;
			}
			if (StringUtils.length(chDate) == 7) {
				chYear = Integer.valueOf(StringUtils.left(chDate, 3)) + 1911;
			}
			if (StringUtils.length(chDate) == 6) {
				chYear = Integer.valueOf(StringUtils.left(chDate, 2)) + 1911;
			}
			return sdf.parse(String.valueOf(chYear) + StringUtils.right(chDate, 4));
		} else {
			System.out.println("is null");
			return null;
		}
	}

	/**
	 * 電文欄位格式 下行回傳9(07)民國年,轉換成西元年。如今日: 1051018 => 20161018
	 * 
	 * @param chDate
	 *            , dateformat(自定義的輸出格式)
	 * @return
	 * @throws Exception
	 */
	public Date toAdYearMMdd(String chDate, String dateformat) throws Exception {

		// if (chDate != null && Integer.parseInt(chDate) != 0) {
		if (StringUtils.isNotBlank(chDate)) {
			chDate = StringUtils.replace(chDate.trim(), "/", "");
			SimpleDateFormat sdf = new SimpleDateFormat(dateformat);

			int chYear = 1900;
			if (StringUtils.length(chDate) == 7) {
				chYear = Integer.valueOf(StringUtils.left(chDate, 3)) + 1911;
			}
			if (StringUtils.length(chDate) == 6) {
				chYear = Integer.valueOf(StringUtils.left(chDate, 2)) + 1911;
			}

			return sdf.parse(String.valueOf(chYear) + StringUtils.right(chDate, 4));
		} else {
			return null;
		}
	}

	/**
	 * 電文欄位格式 下行回傳9(07) or 9(08)民國年,轉換成西元年。如今日: 1051018 => 20161018, 01051018 =>
	 * 20161018
	 * 
	 * @param date
	 * @param is9_07
	 *            else return 9_08
	 * @return
	 * @throws Exception
	 */
	public Date toAdYearMMdd(String date, boolean is9_07) throws Exception {
		if (date != null && is9_07) {
			return toAdYearMMdd(date);
		}
		if (date != null && !is9_07) {
			return toAdYearMMdd(StringUtils.right(date, 7));
		} else {
			return null;
		}
	}

	/**
	 * 去除小數點,並用0向右補足小數位長度 因esb主機反應,僅能接收整數數值,若傳入小數點則會發生錯誤
	 *
	 * @param vul
	 * @param digit
	 * @return
	 */
	public BigDecimal decimalPadding(BigDecimal vul, int digit) {
		// 排除null值
		if (vul == null) {
			return BigDecimal.ZERO;
		}

		NumberFormat nf = new DecimalFormat("#." + StringUtils.repeat("0", digit));
		nf.setMinimumIntegerDigits(digit);

		String valStr = nf.format(vul).replace(".", "");

		return new BigDecimal(valStr);
	}
	
    /**
     * 去除小數點,並用0向左補足小數位長度
     * 因esb主機反應,僅能接收整數數值,若傳入小數點則會發生錯誤
     *
     * @param vul
     * @param digit
     * @return
     */
    public String decimalPadding(BigDecimal vul, int digitInteger, int degitFraction){

    	NumberFormat nf = new DecimalFormat(StringUtils.repeat("0", digitInteger) + "." + StringUtils.repeat("0", degitFraction));
    	nf.setMinimumIntegerDigits(digitInteger);
    	nf.setMinimumFractionDigits(degitFraction);
    	
        //排除null值
        if(vul == null){
            return nf.format(BigDecimal.ZERO).replace(".", "");
        }

        String valStr = nf.format(vul).replace(".", "");

        return valStr;
    }

	/**
	 * 
	 * 因esb主機傳送整數數值，將整數格式轉換成小數點
	 *
	 * @param vul
	 * @param point
	 * @return
	 */
	public BigDecimal decimalPoint(String vul, int point) {
		// 排除null值
		if (StringUtils.isBlank(vul)) {
			return BigDecimal.ZERO;
		}
		// 負值判斷
		String Neg = "N";
		if (!ObjectUtils.equals(vul.indexOf("-"), -1)) {
			vul = vul.replace("-", "");
			Neg = "Y";
		}
		// 補0
		String zero = StringUtils.repeat("0", point);
		vul = zero + vul.replace(' ', '0');
		// 負號補上
		if (ObjectUtils.equals(Neg, "Y")) {
			vul = "-" + vul;
		}
		StringBuilder sb = new StringBuilder(vul);
		sb.insert(sb.length() - point, '.');
		return new BigDecimal(sb.toString());
	}

	/**
	 * 
	 * 因esb主機傳送整數數值，將整數格式轉換成小數點
	 *
	 * @param vul
	 * @param point
	 * @return
	 */
	public BigDecimal decimalPointforCBS(String vul, int point) {
		// 排除null值
		if (StringUtils.isBlank(vul)) {
			return BigDecimal.ZERO;
		}
		// 負值判斷
		String Neg = "N";
		if (!ObjectUtils.equals(vul.indexOf("_"), -1)) {
			vul = vul.replace("_", "");
			Neg = "Y";
		}
		// 補0
		String zero = StringUtils.repeat("0", point);
		vul = zero + vul.replace(' ', '0');
		// 負號補上
		if (ObjectUtils.equals(Neg, "Y")) {
			vul = "-" + vul;
		}
		StringBuilder sb = new StringBuilder(vul);
		sb.insert(sb.length() - point, '.');
		return new BigDecimal(sb.toString());
	}

	public String getHtxtid() {
		return htxtid;
	}

	public void setHtxtid(String htxtid) {
		this.htxtid = htxtid;
	}

	public String[] getCBSList() throws DAOException, JBranchException {
		String List;
		String[] CBSList;
		StringBuffer sql = new StringBuffer("select PARAM_NAME from TBSYSPARAMETER where PARAM_TYPE = 'CBS.LIST' ");
		dam = getDataAccessManager();
		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setQueryString(sql.toString());
		List<Map> esbIDRes = dam.exeQuery(condition);

		List = (!esbIDRes.isEmpty()) ? (String) esbIDRes.get(0).get("PARAM_NAME") : null;
		CBSList = List.split(",");
		return CBSList;
	}
	public boolean isNumeric(String str){
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
		
	}
}
