package com.systex.jbranch.fubon.commons.esb;

import static com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer.ESB_SEND;

import java.io.DataOutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.platform.common.errHandle.JBranchException;

/**
 * author Jemmy Tsai
 * 以XML字串處理取代序列化Java Object
 */
public class EsbUtil3 { // extends FubonWmsBizLogic {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	private static Logger esblog = LoggerFactory.getLogger("ESB_LOG");
	
	private String esbUrl;							// ESB URL
	private String esbId;							// 主機
	private String esbSeq;							// 序號
	private List<String> dnXML = new ArrayList<String>();	// 下行電文
	// 以下欄位用於記錄
	private String module;									// 應用程式模組名稱
	private String HTXTID;									// 交易代碼
	private String HSTANO;									// 電文序號
	
	public EsbUtil3(String module, String esbUrl, String esbId, String esbSeq) {
		this.module = module;
		this.esbUrl = esbUrl;
		this.esbId = esbId;
		this.esbSeq = esbSeq;
	}
	
	// 電文完成Log模版，主要記錄該次電文運行時間
	private String endEsbLogFormat = "[%s][%s][%s] total spent %d ms";
	private String errEsbLogFormat = "[%s][%s][%s] HERRID[%s]:\n %s";
	/**
	 * 發送電文
	 * @param xml 上行電文
	 * @return 下行電文List
	 * @throws JBranchException
	 */
	public List<String> send(String xml) throws JBranchException {
		String hReturn = null;
		long start = System.currentTimeMillis();
		do {
			String txHead = this.getTagValue(xml, "TxHead"); 
        	if (txHead.indexOf("<HRETURN>") >= 0 && hReturn != null) {		// 表示已有<HRETURN>標籤
        		// NOT TO DO
        	} else {
        		xml = xml.replaceFirst("</TxHead>", "<HRETURN>C</HRETURN></TxHead>");
        	}
            hReturn = this.sendESB(xml);
            if (hReturn != null && hReturn.length() > 2) {
            	logger.warn(String.format(errEsbLogFormat, this.HSTANO, this.HTXTID, this.module, hReturn, this.dnXML.get(this.dnXML.size() -1)));
            	throw new JBranchException("HERRID is " + hReturn);
            }
		} while (hReturn.equals("C"));
        long end = System.currentTimeMillis();
        logger.info(String.format(endEsbLogFormat, this.HSTANO, this.HTXTID, this.module, (end -start)));
        return this.dnXML;
	}
	
	// 上行電文模版
	private String baseUpXml = 
			"<Tx txid=\"%s\" encoding=\"Big5\" fontremapping=\"OFF\">%s<TxHead>%s</TxHead><TxBody>%s</TxBody></Tx>";
	private String sendESB(String xml) throws JBranchException {
		try {
            String HWSID = this.getTagValue(xml, "HWSID");
            String HSTANO = this.getTagValue(xml, "HSTANO");
            String HTLID = this.getTagValue(xml, "HTLID");
            HTLID = StringUtils.isEmpty(HTLID) ? "" : HTLID;	// 可忽略的欄位，必須用空字串取代，用於計算密碼
            String HTXTID = this.getTagValue(xml, "HTXTID");
            String txHead = this.getTagValue(xml, "TxHead");
            String txBody = this.getTagValue(xml, "TxBody");
            String fmpStr = this.makeFMPConnectionString(HWSID, HSTANO, HTLID, HTXTID);
            String inXML = String.format(baseUpXml, HTXTID, fmpStr, txHead, txBody);
            long start = System.currentTimeMillis();
            this.esbLog(HSTANO, HTXTID, inXML);
            // esbLog(upMsg);
            URL url = new URL(esbUrl);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setDoOutput(true);
            DataOutputStream dataOutputStream = new DataOutputStream(urlConnection.getOutputStream());
            dataOutputStream.write(inXML.getBytes("UTF-8"));
            dataOutputStream.flush();
            dataOutputStream.close();

            String outXML = IOUtils.toString(urlConnection.getInputStream());
            this.dnXML.add(outXML);
            long end = System.currentTimeMillis();
            long spent = end - start;
            this.esbLog(HSTANO, HTXTID, spent, outXML);
            if (this.HTXTID == null) {		// 記錄Log用
            	this.HTXTID = HTXTID;
            	this.HSTANO = HSTANO;
            }
            String HERRID = this.getTagValue(outXML, "HERRID");
            String hReturn = this.getTagValue(outXML, "HRETRN");
            return "0000".equals(HERRID) ? hReturn : HERRID;		// HERRID為0000則回HReturn，否則回HERRID
        } catch (Exception e) {
        	logger.warn("send ESB fail", e);
        	throw new JBranchException(e.getMessage());
        }
	}
	
	
	// [序號][上下行][交易代碼][模組名稱]spent毫秒ms
	private String upEsbLogFormat = "[%s][上行][%s][%s][%s]";
	private String dnEsbLogFormat = "[%s][下行][%s][%s][%s]spent %d ms";
	
	/**
	 * 記錄電文內容
	 * @param hstano
	 * @param htxtid
	 * @param content
	 * @throws Exception
	 */
	private void esbLog(String hstano, String htxtid, String content) throws Exception {
		String log = String.format(upEsbLogFormat, hstano, htxtid, hstano, this.module);
		logger.info(log); // 紀錄電文重要參數以方便從 jbranch.log 追蹤 esb.log
		esblog.info(log + "\nsendMsg:\n" + content + "\n"); // esb.log 紀錄完整電文資訊
	}
	
	private void esbLog(String hstano, String htxtid, long spent, String content) throws Exception {
		String log = String.format(dnEsbLogFormat, hstano, htxtid, hstano, this.module, spent);
		logger.info(log); // 紀錄電文重要參數以方便從 jbranch.log 追蹤 esb.log
		esblog.info(log + "\nsendMsg:\n" + content + "\n"); // esb.log 紀錄完整電文資訊
	}
	/**
	 * 從XML字串取得標籤值
	 * @param xml XML字串
	 * @param tag 標籤
	 * @return 無值傳回空字串，無此tag傳回null
	 */
    public String getTagValue(String xml, String tag) {
        Pattern pattern = Pattern.compile("<" + tag + ">(.+?)</" + tag + ">");
        Matcher mat = pattern.matcher(xml);
        if (mat.find()) {
            return mat.group(1);
        } else {
            Pattern pattern1 = Pattern.compile("<" + tag + " ?/>");
            mat = pattern1.matcher(xml);
            if (mat.find()) {
                return "";
            }
        }
        return null;
    }
    
    // 無此標籤時，回傳空字串
    public String getTagClearValue(String xml, String tag) {
        Pattern pattern = Pattern.compile("<" + tag + ">(.+?)</" + tag + ">");
        Matcher mat = pattern.matcher(xml);
        if (mat.find()) {
            return mat.group(1);
        } else {
            Pattern pattern1 = Pattern.compile("<" + tag + " ?/>");
            mat = pattern1.matcher(xml);
            if (mat.find()) {
                return "";
            }
        }
        return "";
    }

    public List<String> getTagValueList(String xml, String tag) {
    	List<String> valueList = new ArrayList<String>();
        Pattern pattern = Pattern.compile("<" + tag + ">(.+?)</" + tag + ">");
        Matcher mat = pattern.matcher(xml);
        while (mat.find()) {
        	valueList.add(mat.group(0));
        }
        return valueList;
    }
    /*************************************************************************************
     * 以下為組上行電文的公用函數
     *************************************************************************************/
    private String mapToXmlTag(Map<String, String> other) {
    	StringBuffer otherTag = new StringBuffer("");
		if (other != null && !other.isEmpty()) {
			for (Map.Entry<String, String> entry : other.entrySet()) {
				otherTag.append("<").append(entry.getKey()).append(">")
					.append(entry.getValue())
					.append("</").append(entry.getKey()).append(">");
			}
		}
		return otherTag.toString();
    }
    
	private static String baseTxHeadXml =
			"<TxHead><HWSID>%s</HWSID><HSTANO>%s</HSTANO><HTXTID>%s</HTXTID>%s</TxHead>";
	public String getTxHead(String HTXTID, Map<String, String> other) {
		try {
			return String.format(baseTxHeadXml, this.esbId, this.esbSeq, HTXTID, this.mapToXmlTag(other));
		} catch (Exception e) {
			logger.warn("getEsbSeq fail: " + e.getMessage());
			// DB取序號失敗改用UUID作為序號
			return String.format(baseTxHeadXml, this.esbId, UUID.randomUUID().toString(), HTXTID, this.mapToXmlTag(other));
		}
	}
	
	private static String baseTxBodyXml =
			"<TxBody>%s</TxBody>";
	public String getTxBody(Map<String, String> other) {
		return String.format(baseTxBodyXml, this.mapToXmlTag(other));
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
    /*************************************************************************************
     * 以下為ESB密碼計算方式，最終提供makeFMPConnectionString給呼叫端使用
     *************************************************************************************/
    private final String[] KeyTable = {
            "32D8E31777E2431EF7238586D6F725039A758855SDFGSF345F5GGOF3DF321FD8",
            "EF7238386D3F725039A32D8E31777E2431758B3FS1FD8DFGSF345F5GGOFDDF32",
            "345F5GGOFDDF32132D8E31377E2431EF7238586D6F725039A75885SFFD85SDFG",
            "31EF7238586D6F732D8E31787E24250F345F5GG3FDDF321FD839A758855SDFGS",
            "31777E243132D8EEF723825039A758SF34321GGOFD3F321FD3855SDFG586D6F7" };
    private final char[] NumberTable = {
            '2', '4', '3', '5', '6', '5', '7', '1', '4', '3',
            '4', '7', '6', '1', '9', '1', '9', '4', '1', '7',
            '2', '5', '8', '5', '9', '4' };

    private String VerifyNumber(String str)
    {
        String strNum = "";
        String inputStr = str.toUpperCase();
        for (int i = 0; i < inputStr.length(); i++) {
            if ((inputStr.charAt(i) >= 'A') && (inputStr.charAt(i) <= 'Z')) {
                strNum = strNum + this.NumberTable[(inputStr.charAt(i) - 'A')];
            } else if ((inputStr.charAt(i) >= '0') && (inputStr.charAt(i) <= '9')) {
                strNum = strNum + inputStr.charAt(i);
            } else {
                strNum = strNum + '0';
            }
        }

        int start_nonzero = 0;
        for (start_nonzero = 0; start_nonzero < strNum.length(); start_nonzero++) {
            if (strNum.charAt(start_nonzero) != '0') {
                break;
            }
        }
        return strNum.substring(start_nonzero);
    }

    private int SumOfEvenOdd(String strNum, int even) {
        int sum = 0;
        for (int i = 0; i < strNum.length(); i++) {
            if ((i & 0x1) == even) {
                sum += Character.getNumericValue(strNum.charAt(i));
            }
        }
        return sum;
    }

    private int GetKeyTextIndex(int sumEvenHWSID, int sumOddHSTANO) {
        String strDiff = String.valueOf(
                Math.abs(sumEvenHWSID - sumOddHSTANO));
        switch (strDiff.charAt(0)) {
            case '0':
            case '9':
                return 0;
            case '1':
            case '3':
                return 1;
            case '5':
            case '7':
                return 2;
            case '2':
            case '4':
                return 3;
            case '6':
            case '8':
                return 4;
        }
        return -1;
    }

    // 計算密碼值
    private String makePassword(String strHWSID, String strHSTANO, String strHTLID, String strHTXTID) {
        int KIndex = GetKeyTextIndex(
                SumOfEvenOdd(VerifyNumber(strHWSID), 1),
                SumOfEvenOdd(VerifyNumber(strHSTANO), 0));

        String final_value = strHWSID + strHSTANO + strHTLID + strHTXTID + this.KeyTable[KIndex];
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] final_value_bytes = final_value.getBytes();
            md.update(final_value_bytes, 0, final_value_bytes.length);
            return new Base64().encodeToString(md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 組成FMPConnectionString標籤
     * @param strHWSID 計算index參數 兼摘要參數
     * @param strHSTANO 計算index參數 兼摘要參數
     * @param strHTLID null值 摘要參數
     * @param strHTXTID 摘要參數
     * @return
     */
    public String makeFMPConnectionString(String strHWSID, String strHSTANO, String strHTLID, String strHTXTID) {
        StringBuffer sb = new StringBuffer("<FMPConnectionString>");
        sb.append("<SPName>").append(strHWSID).append("</SPName>");
        sb.append("<LoginID>").append(strHWSID).append("</LoginID>");
        sb.append("<Password>").append(this.makePassword(strHWSID, strHSTANO, strHTLID, strHTXTID)).append("</Password>");
        sb.append("<TxnId>").append(strHTXTID).append("</TxnId>").append("</FMPConnectionString>");
        return sb.toString();
    }
}
