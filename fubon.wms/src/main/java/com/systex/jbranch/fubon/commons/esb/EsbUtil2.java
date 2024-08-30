package com.systex.jbranch.fubon.commons.esb;

import static com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer.ENCODING_BIG5;
import static com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer.ESB_ERROR;
import static com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer.ESB_RESPONSE;
import static com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer.ESB_SEND;
import static com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer.FONTREMAPPING_OFF;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.esb.vo.ESBErrorVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.TxHeadVO;
import com.systex.jbranch.fubon.commons.esb.xml.parse.EsbXmlParseUtil;
import com.systex.jbranch.fubon.jlb.FmpJRunTx;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

/**
 * 
 * @author samtu
 * 無用了
 */
@Component
public class EsbUtil2 {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	private static Logger esblog = LoggerFactory.getLogger("ESB_LOG");
	private SimpleDateFormat SDFYYYY_MM_DD = new SimpleDateFormat("yyyy/MM/dd");
	private FmpJRunTx fmpJRunTx;
	private String type;
	private String htxtid;
	private String esbID;
	private String esbUrl;
	private String hretrn = null; // 是否還有下頁資料; C:還有資料 E:已無資料
	private String hstano; // 電文流水號
	// private String newEsbSnoLog = null; //SHOW交易序號 FOR UI
	
	private String module; // 模組代號

	// regex
	private Pattern pattern = Pattern.compile("TxBody");

	public FmpJRunTx getFmpJRunTx() {
		return fmpJRunTx;
	}

	public void setFmpJRunTx(FmpJRunTx fmpJRunTx) {
		this.fmpJRunTx = fmpJRunTx;
	}

	/**
	 * init FmpJRunTx by using ESB_URL and HTXTID
	 *
	 * by using PARAM_TYPE and htxtid
	 * 
	 * @see EsbUtil2#getEsbUrl(String)
	 * @see EsbUtil2#getEsbID(String)
	 */
	public void initEsbFmpJRunTx() throws Exception {
		// using PARAM_TYPE to query login info
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(htxtid)) {
			esbUrl = "http://172.19.241.10:9201/";
			esbID = "WMSR";
			setFmpJRunTx(new FmpJRunTx(esbUrl, htxtid));
			logger.debug("[ESB] ESB_ID: " + esbID + ", ESB_URL: " + esbUrl);
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
		// this.hstano = getEsbSeq();

		initEsbFmpJRunTx();

		// init ESBUtilInputVO
		ESBUtilInputVO esbUtilInputVO = new ESBUtilInputVO();
		esbUtilInputVO.setTxid(htxtid);
		esbUtilInputVO.setEncoding(ENCODING_BIG5);
		esbUtilInputVO.setFontremapping(FONTREMAPPING_OFF);
		TxHeadVO txHead = new TxHeadVO();
		txHead.setId(esbID);
		// txHead.setSeq(hstano);
		txHead.setHtxtid(htxtid);
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
		try {
			this.module = esbUtilInputVO.getModule();

			if (StringUtils.isBlank(this.module)) {
				throw new Exception("沒有交易沒組代號!");
			}

			StringWriter writer = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(ESBUtilInputVO.class);

			Marshaller marshal = context.createMarshaller();
			marshal.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			marshal.marshal(esbUtilInputVO, writer);

			fmpJRunTx.setInboundXML(writer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		esbSend();

		return checkStatus(esbUtilInputVO);
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

	public List<ESBUtilOutputVO> checkStatus(ESBUtilInputVO esbUtilInputVO) throws Exception {
		List<ESBUtilOutputVO> resultList = new ArrayList<>();
		// first data
		ESBUtilOutputVO outputVO = rescive();
		// get HRETRN return code
		String hretrn = outputVO.getTxHeadVO().getHRETRN();
		resultList.add(outputVO);

		// check HRETRN return code
		if (StringUtils.equals(hretrn, "C")) {
			esbUtilInputVO.getTxHeadVO().setHRETRN(hretrn);
			List<ESBUtilOutputVO> outputs = send(esbUtilInputVO);
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
		String herrid = txHeadElement.getChild("HERRID").getText().trim(); // 執行狀態
		String hretrn = txHeadElement.getChild("HRETRN").getText(); // 是否還有下頁資料
																	// C:還有資料
																	// E:已無資料
		// 有錯誤
		if (!passed(herrid)) {
			ESBUtilOutputVO outputVO = unmarshaller(fcOutboundXML);
			ESBErrorVO errVO = outputVO.getEsbErrorVO();
			String htxid = txHeadElement.getChild("HTXTID").getText().trim();
			esbLog(ESB_ERROR);
			throw new JBranchException(String.format("ESB_LOG HTXTID:%s ,HERRID:%s ,EMSGID:%s ,EMSGTXT:%s , 電文發送Log檔.esb_sno:%s", htxid, herrid, errVO.getEMSGID(), errVO.getEMSGTXT(), this.hstano));
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

		String statusCode = "0000,V003,EK63,V555,SP02,E005,V301";

		if (StringUtils.isNotBlank(statusCode)) {
			String[] codes = statusCode.split(",");
			isPass = Arrays.asList(codes).contains(herrid);
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
	private ESBUtilOutputVO unmarshaller(String fcOutboundXML) throws Exception {
		// JAXBContext context = JAXBContext.newInstance(ESBUtilOutputVO.class);
		//
		// Unmarshaller unmarshaller = context.createUnmarshaller();

		// return (ESBUtilOutputVO) unmarshaller.unmarshal(new
		// StringInputStream(fcOutboundXML));

		ESBUtilOutputVO vo = new ESBUtilOutputVO();
		EsbXmlParseUtil xmlUtil = new EsbXmlParseUtil(vo.getClass());

		logger.info("#this.htxtid:" + this.htxtid);
		logger.info("解析電文：\r\n" + fcOutboundXML);
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

		String hstano = isSendTop ? fmpJRunTx.getInboundXML().getChild("TxHead").getChildText("HSTANO") : fmpJRunTx.getOutboundXML().getChild("TxHead").getChildText("HSTANO");

		esblog.info(new StringBuffer().append("[").append(this.hstano).append("]").append("[").append(sendType).append("]").append("[").append(htxtid).append("]").append("[").append(hstano).append("]").append("[").append(this.module).append("]").append("\nsendMsg:\n").append(content).append("\n").toString());
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
}
