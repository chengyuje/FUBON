package com.systex.jbranch.fubon.jlb;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FmpJRunTx
{
  public static final int RUNTX_HTTPREQUEST_FAIL = 0;
  public static final int RUNTX_SUCCESS = 1;
  public static final int RUNTX_ECHANNEL_FAIL = 2;
  public static final int RUNTX_HTTPREQUEST_OVER_TIMEOUT = 3;
  private static String TX = "Tx";
  private static String TXHEAD = "TxHead";
  private static String TXBODY = "TxBody";
  private static String TXSYSERROR = "TxSysError";
  private static String TXSIGNATURE = "Signature";
  private static String ENCODING_BIG5 = "Big5";
  private static String ENCODING_UNICODE = "Unicode";
  private static String FONTREMAPPING_OFF = "OFF";
  private static String FONTREMAPPING_ON = "ON";
  private static String ATTR_TXID = "txid";
  private static String ATTR_ENCODING = "encoding";
  private static String ATTR_FONTREMAPPING = "fontremapping";
  private String str_url = null;
  private String str_txid = null;
  private String str_encoding = null;
  private String str_fontremapping = null;
  private Element elm_ib = null;
  private Element elm_ob = null;
  
  protected Logger logger = LoggerFactory.getLogger(this.getClass());
  
  public FmpJRunTx(String s_url, String s_txid)
  {
    this.str_url = s_url;
    this.str_txid = s_txid;
    this.str_encoding = ENCODING_BIG5;
    this.str_fontremapping = FONTREMAPPING_OFF;
    
    this.elm_ib = new Element(TX);
    
    this.elm_ib.setAttribute(ATTR_TXID, this.str_txid);
    this.elm_ib.setAttribute(ATTR_ENCODING, this.str_encoding);
    this.elm_ib.setAttribute(ATTR_FONTREMAPPING, this.str_fontremapping);
    
    Element elm_head = new Element(TXHEAD);
    Element elm_body = new Element(TXBODY);
    
    this.elm_ib.addContent(elm_head);
    this.elm_ib.addContent(elm_body);
    if (!this.str_url.startsWith("http://")) {
      this.str_url = ("http://" + this.str_url);
    }
  }
  
  public void setInboundField(String str_fldname, String str_value)
  {
    String str_name1 = "";
    String str_name2 = "";
    
    Element elm_head = this.elm_ib.getChild(TXHEAD);
    Element elm_body = this.elm_ib.getChild(TXBODY);
    Element elm_fld = null;
    
    int i_dot = str_fldname.indexOf('.');
    if (i_dot > 0)
    {
      str_name1 = str_fldname.substring(0, i_dot);
      str_name2 = str_fldname.substring(i_dot + 1);
    }
    if (str_name1.compareToIgnoreCase(TXHEAD) == 0)
    {
      if ((elm_head != null) && (str_name2.length() > 0))
      {
        elm_fld = new Element(str_name2);
        elm_fld.setText(str_value);
        
        elm_head.addContent(elm_fld);
      }
    }
    else if (str_name1.compareToIgnoreCase(TXBODY) == 0) {
      if ((elm_body != null) && (str_name2.length() > 0))
      {
        elm_fld = new Element(str_name2);
        elm_fld.setText(str_value);
        
        elm_body.addContent(elm_fld);
      }
    }
  }
  
  public String getOutboundField(String str_fldname)
  {
    String str_value = null;
    
    String str_name1 = "";
    String str_name2 = "";
    
    Element elm_head = null;
    Element elm_body = null;
    Element elm_syserr = null;
    Element elm_fld = null;
    try
    {
      int i_dot = str_fldname.indexOf('.');
      if (this.elm_ob != null)
      {
        elm_head = this.elm_ob.getChild(TXHEAD);
        elm_body = this.elm_ob.getChild(TXBODY);
        elm_syserr = this.elm_ob.getChild(TXSYSERROR);
        if (i_dot > 0)
        {
          str_name1 = str_fldname.substring(0, i_dot).trim();
          str_name2 = str_fldname.substring(i_dot + 1).trim();
        }
        if (str_name1.compareToIgnoreCase(TXHEAD) == 0)
        {
          if ((elm_head != null) && (str_name2.length() > 0)) {
            str_value = elm_head.getChildText(str_name2);
          }
        }
        else if (str_name1.compareToIgnoreCase(TXBODY) == 0)
        {
          if ((elm_body != null) && (str_name2.length() > 0))
          {
            i_dot = str_name2.indexOf('.');
            if (i_dot > 0)
            {
              int i_p1 = str_name2.indexOf('[');
              int i_p2 = str_name2.indexOf(']');
              
              String str_name3 = str_name2.substring(0, i_p1).trim();
              String str_name4 = str_name2.substring(i_dot + 1).trim();
              String str_cnt = str_name2.substring(i_p1 + 1, i_p2).trim();
              int i_cnt = Integer.parseInt(str_cnt);
              
              List lst_children = elm_body.getChildren(str_name3);
              for (int i = 0; i < lst_children.size(); i++) {
                if (i == i_cnt)
                {
                  elm_fld = (Element)lst_children.get(i);
                  str_value = elm_fld.getChildText(str_name4);
                  break;
                }
              }
            }
            else
            {
              str_value = elm_body.getChildText(str_name2);
            }
          }
        }
        else if (str_name1.compareToIgnoreCase(TXSYSERROR) == 0) {
          if ((elm_syserr != null) && (str_name2.length() > 0)) {
            str_value = elm_syserr.getChildText(str_name2);
          }
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return str_value;
  }
  
  public int getOutboundFieldNumber(String str_fldname)
  {
    int i_num = 0;
    
    String str_name1 = "";
    String str_name2 = "";
    
    Element elm_head = null;
    Element elm_body = null;
    Element elm_syserr = null;
    Element elm_fld = null;
    try
    {
      int i_dot = str_fldname.indexOf('.');
      if (this.elm_ob != null)
      {
        elm_head = this.elm_ob.getChild(TXHEAD);
        elm_body = this.elm_ob.getChild(TXBODY);
        elm_syserr = this.elm_ob.getChild(TXSYSERROR);
        if (i_dot > 0)
        {
          str_name1 = str_fldname.substring(0, i_dot).trim();
          str_name2 = str_fldname.substring(i_dot + 1).trim();
        }
        if (str_name1.compareToIgnoreCase(TXHEAD) == 0) {
          elm_fld = elm_head;
        } else if (str_name1.compareToIgnoreCase(TXBODY) == 0) {
          elm_fld = elm_body;
        } else if (str_name1.compareToIgnoreCase(TXSYSERROR) == 0) {
          elm_fld = elm_syserr;
        }
        if (elm_fld != null)
        {
          List lst_children = elm_fld.getChildren(str_name2);
          i_num = lst_children.size();
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return i_num;
  }
  
  public void setInboundXML(String str_xml)
  {
    try
    {
      logger.info("setInboundXML's argument str_xml:\r\n" + (str_xml == null ? "" : str_xml));
      Document doc_tmp = new SAXBuilder(false).build(new StringReader(str_xml));
      Element elm_tmp = doc_tmp.getRootElement();
      logger.info("root element:" + (elm_tmp == null ? "" : elm_tmp.getName()));
      this.elm_ib = elm_tmp;
    }
    catch (Exception localException) {
    	localException.printStackTrace();
    }
  }
  
  public void setInboundXML(Element elm_inbound)
  {
    this.elm_ib = ((Element)elm_inbound.clone());
  }
  
  public String getInboundXMLString()
  {
    String str_xml = null;
    if (this.elm_ib != null)
    {
      XMLOutputter xout = new XMLOutputter();
      str_xml = xout.outputString(this.elm_ib);
    }
    return str_xml;
  }
  
  public Element getInboundXML()
  {
    Element elm_tmp = null;
    if (this.elm_ib != null) {
      elm_tmp = (Element)this.elm_ib.clone();
    }
    return elm_tmp;
  }
  
  public String getOutboundXMLString()
  {
    String str_xml = null;
    if (this.elm_ob != null)
    {
      XMLOutputter xout = new XMLOutputter();
      str_xml = xout.outputString(this.elm_ob);
    }
    return str_xml;
  }
  
  public Element getOutboundXML()
  {
    Element elm_tmp = null;
    if (this.elm_ob != null) {
      elm_tmp = (Element)this.elm_ob.clone();
    }
    return elm_tmp;
  }
  
  class FMPConnectionHelper
  {
    private String m_url;
    private String m_spname;
    private String m_loginid;
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
    
    public FMPConnectionHelper(String str_url, String str_spname, String str_loginid)
    {
      this.m_url = str_url;
      this.m_spname = str_spname;
      this.m_loginid = str_loginid;
    }
    
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
    
    public String PostData(String data)
      throws Exception
    {
      Document xdoc = new SAXBuilder(false).build(new StringReader(data));
      MakeFMPNode(xdoc);
      
      XMLOutputter xout = new XMLOutputter();
      String final_data = xout.outputString(xdoc.getRootElement());
      
      URL url_obj = new URL(this.m_url);
      URLConnection url_conn = url_obj.openConnection();
      
      url_conn.setDoOutput(true);
      
      DataOutputStream wr = new DataOutputStream(url_conn.getOutputStream());
      


      wr.write(final_data.getBytes("UTF8"));
      
      wr.flush();
      logger.info("ESB_LOG Post OK!!");
      

      BufferedReader rd = new BufferedReader(
        new InputStreamReader(url_conn.getInputStream(), "UTF8"));
      

      String ret = "";
      String line;
      while ((line = rd.readLine()) != null)
      {
        ret = ret + line;
      }
      return ret;
    }
    
    int SumOfEvenOdd(String strNum, int even)
    {
      int sum = 0;
      for (int i = 0; i < strNum.length(); i++) {
        if ((i & 0x1) == even) {
          sum += Character.getNumericValue(strNum.charAt(i));
        }
      }
      return sum;
    }
    
    private int GetKeyTextIndex(int sumEvenHWSID, int sumOddHSTANO)
    {
      String strDiff = String.valueOf(
        Math.abs(sumEvenHWSID - sumOddHSTANO));
      switch (strDiff.charAt(0))
      {
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
    
    private String MakePassword(Element elm_head)
    {
      String strHWSID = elm_head.getChildText("HWSID");
      String strHSTANO = elm_head.getChildText("HSTANO");
      String strHTLID = elm_head.getChildText("HTLID");
      String strHTXTID = elm_head.getChildText("HTXTID");
      
      int KIndex = GetKeyTextIndex(
        SumOfEvenOdd(VerifyNumber(strHWSID), 1), 
        SumOfEvenOdd(VerifyNumber(strHSTANO), 0));
      

      String final_value = strHWSID + strHSTANO + strHTLID + strHTXTID + this.KeyTable[KIndex];
      try
      {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] final_value_bytes = final_value.getBytes();
        md.update(final_value_bytes, 0, final_value_bytes.length);
        return new Base64().encodeToString(md.digest());
      }
      catch (NoSuchAlgorithmException e)
      {
        e.printStackTrace();
      }
      return null;
    }
    
    private void MakeFMPNode(Document xdoc)
    {
      Element elm_root = xdoc.getRootElement();
      Element elm_head = elm_root.getChild("TxHead");
      if (elm_root.getChild("FMPConnectionString") != null) {
        return;
      }
      String elm_hwsid_text = elm_head.getChildText("HWSID");
      String elm_txtid_text = elm_head.getChildText("HTXTID");
      
      Element elm_fmp = new Element("FMPConnectionString");
      
      Element elm_SPName = new Element("SPName");
      if (this.m_spname != null) {
        elm_SPName.setText(this.m_spname);
      } else {
        elm_SPName.setText(elm_hwsid_text);
      }
      Element elm_LoginID = new Element("LoginID");
      if (this.m_loginid != null) {
        elm_LoginID.setText(this.m_loginid);
      } else {
        elm_LoginID.setText(elm_hwsid_text);
      }
      Element elm_Password = new Element("Password");
      elm_Password.setText(MakePassword(elm_head));
      Element elm_TxnId = new Element("TxnId");
      elm_TxnId.setText(elm_txtid_text);
      
      elm_fmp.addContent(elm_SPName);
      elm_fmp.addContent(elm_LoginID);
      elm_fmp.addContent(elm_Password);
      elm_fmp.addContent(elm_TxnId);
      


      List elm_list = elm_root.getContent();
      elm_list.add(0, elm_fmp);
      


      XMLOutputter xout = new XMLOutputter();
      logger.info("ESB_LOG 上行:" + xout.outputString(elm_root));
    }
  }
  
  public int run(String SPNAME, String LoginID)
  {
    int i_val = 0;
    
    XMLOutputter xout = new XMLOutputter();
    String str_ibdata = xout.outputString(this.elm_ib);
    String str_obdata = null;
    
    FMPConnectionHelper helper = new FMPConnectionHelper(this.str_url, SPNAME, LoginID);
    try
    {
      str_obdata = helper.PostData(str_ibdata);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    logger.info("ESB_LOG 下行" + str_obdata);
    try
    {
      Document doc_tmp = new SAXBuilder(false).build(new StringReader(str_obdata));
      Element elm_tmp = doc_tmp.getRootElement();
      this.elm_ob = elm_tmp;
    }
    catch (Exception localException1) {}
    if (this.elm_ob != null)
    {
      Element elm_syserr = this.elm_ob.getChild(TXSYSERROR);
      if (elm_syserr != null) {
        i_val = 2;
      } else {
        i_val = 1;
      }
    }
    else
    {
      i_val = 3;
    }
    return i_val;
  }
  
  public int run()
  {
    return run(null, null);
  }
  
  public void clearInBoundFields()
  {
    if (this.elm_ib != null)
    {
      Element elm_head = this.elm_ib.getChild(TXHEAD);
      Element elm_body = this.elm_ib.getChild(TXBODY);
      if (elm_head != null) {
        elm_head.removeContent();
      }
      if (elm_body != null) {
        elm_body.removeContent();
      }
    }
  }
  
  public void clearOutBoundFields()
  {
    if (this.elm_ob != null)
    {
      Element elm_head = this.elm_ob.getChild(TXHEAD);
      Element elm_body = this.elm_ob.getChild(TXBODY);
      if (elm_head != null) {
        elm_head.removeContent();
      }
      if (elm_body != null) {
        elm_body.removeContent();
      }
    }
  }
  
  public static byte[] generateHMACMD5(byte[] bytesContent, byte[] bytesKey)
  {
    byte[] bytesResult = (byte[])null;
    try
    {
      Provider jce = new BouncyCastleProvider();
      Security.addProvider(jce);
      
      SecretKey sk = new SecretKeySpec(bytesKey, "HmacMD5");
      


      Mac mac = Mac.getInstance("HmacMD5");
      mac.init(sk);
      
      bytesResult = mac.doFinal(bytesContent);
    }
    catch (NoSuchAlgorithmException e)
    {
      e.printStackTrace(System.out);
      e.printStackTrace();
    }
    catch (InvalidKeyException e)
    {
      e.printStackTrace(System.out);
      e.printStackTrace();
    }
    return bytesResult;
  }
  
  public void addSignature(String strKey)
  {
    Element elm_signature = new Element(TXSIGNATURE);
    this.elm_ib.addContent(elm_signature);
    
    XMLOutputter xout = new XMLOutputter();
    String str_xml = xout.outputString(this.elm_ib);
    System.out.println("(JRunTx)Output XML 1 : " + str_xml);
    
    int iHead = str_xml.indexOf("<TxHead>");
    int iBody = str_xml.lastIndexOf("</TxBody>") + 9;
    if ((iHead != -1) && (iBody != -1)) {
      str_xml = str_xml.substring(iHead, iBody);
    }
    System.out.println("(JRunTx)HmacMD5 Data : " + str_xml);
    System.out.println("(JRunTx)HmacMD5 Key  : " + strKey);
    byte[] bytesMD5 = generateHMACMD5(str_xml.getBytes(), strKey.getBytes());
    if (bytesMD5 == null) {
      return;
    }
    String strHex = "0123456789ABCDEF";
    StringBuffer stbMD5 = new StringBuffer();
    for (int i = 0; i < bytesMD5.length; i++)
    {
      int iIndex = bytesMD5[i];
      if (iIndex < 0) {
        iIndex += 256;
      }
      stbMD5.append(strHex.charAt(iIndex / 16));
      stbMD5.append(strHex.charAt(iIndex % 16));
    }
    System.out.println("(JRunTx)HmacMD5 Result : " + stbMD5.toString());
    
    elm_signature.setText(stbMD5.toString());
  }
  
  public void addSignature()
  {
    try
    {
      String strKey = this.elm_ib.getChild("TxHead").getChildText("HWSID") + 
        new SimpleDateFormat("yyyyMMdd").format(new Date()) + 
        this.elm_ib.getChild("TxHead").getChildText("HSTANO") + 
        this.elm_ib.getChild("TxHead").getChildText("HTXTID");
      addSignature(strKey);
    }
    catch (Exception e)
    {
      e.printStackTrace(System.out);
      e.printStackTrace();
    }
  }
}
