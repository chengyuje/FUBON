package com.systex.jbranch.fubon.bth;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.errHandle.NotFoundException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSFTPVO;
import com.systex.jbranch.platform.common.scheduler.AuditLogUtil;
import com.systex.jbranch.platform.common.scheduler.SchedulerHelper;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Component("genfiletools")
@Scope("prototype")
public class GenFileTools extends BizLogic{
	private AuditLogUtil audit=null;
    private String format="MS950";
	private String separated="";
	private SimpleDateFormat sdfYYYYMMDD  = new SimpleDateFormat("yyyyMMdd");
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
     * 寫入檔案
     *
     * @param 檔名 副檔名,路徑,欄位名稱,資料,分隔符號,是否持續寫入,是否有雙引號
     * @return
     * @throws Exception
     */
	public void writeFile(String writeFileName,String attached_name,String path,String[] order,ResultSet rs,String separated,boolean append,boolean isDoubleQuotationMark)throws Exception
	{
		String fileName = writeFileName + "." + attached_name;
		if("".equals(attached_name))
		{
			fileName=writeFileName;
		}
		
		File file = new File(path  + "reports\\" + fileName);
		BufferedWriter bufWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,append),format));
		int number = 0;
        StringBuffer temp = new StringBuffer("");
        this.separated=separated;
        while (rs.next()) {
        	bufWriter.append(genLayOut(rs,order,isDoubleQuotationMark).toString());
			bufWriter.append("\r\n");
			number++;
			if(number%5000==0){
				bufWriter.write(temp.toString());
				temp.setLength(0);
				System.out.println("now write:"+number);
				//number=0;
			}
			
        }
        bufWriter.write(temp.toString());
        bufWriter.close();
	}
    /**
     * 寫入檔案
     *
     * @param 檔名 副檔名,路徑,欄位名稱,資料,分隔符號,是否持續寫入,是否有雙引號,編碼
     * @return
     * @throws Exception
     */
	public void writeFile(String writeFileName,String attached_name,String path,String[] order,ResultSet rs,String separated,boolean append,boolean isDoubleQuotationMark,String format)throws Exception
	{
		this.format=format;
		this.writeFile(writeFileName, attached_name, path, order, rs, separated, append, isDoubleQuotationMark);
	}
	
	private StringBuffer genLayOut(ResultSet rs, String[] order,boolean isDoubleQuotationMark) throws SQLException {
    	StringBuffer layout=new StringBuffer();
       	int j = 1;
    	for (Integer i = 0; i < order.length; i++) {
			//若固定分格的話空白需忠實呈現
    		//System.out.println(order[i]);
    		String dataString="";
    		//有些數值會出現轉型失敗 若轉型失敗強轉成BigDecimal
    		try{
    			dataString=(String) rs.getObject(order[i]);
    		}
    		catch(ClassCastException e)
    		{
    			dataString=((BigDecimal) rs.getObject(order[i])).toString();
    		}
    	
			if("".equals(separated))
			{
				layout.append(dataString);
			}
			//特殊狀況 ,要 也要固定空白
			else if(", ".equals(separated))
			{
				if(isDoubleQuotationMark)
				{
					if((dataString)==null)
					{
						layout.append("\"\"");	 
					}
					else
					{
					layout.append("\""+dataString+"\"");
					}
				}
				else
				{
					layout.append((dataString));	
				}
			}
			else
			{
			

				if(isDoubleQuotationMark)
				{
					layout.append("\""+(StringUtils.isBlank(dataString) ? "" : dataString)+"\"");	
				}
				else
				{

				    layout.append(StringUtils.isBlank(dataString) ? "" : dataString);
				}
				
			}
			if (j++ == order.length){
			}else{
				layout.append(separated.trim());
			}
		}
		return layout;
	}
	/**
     * 寫入檢核檔案
     *
     * @param 檔名 副檔名,/路徑,欄位名稱,筆數,分隔符號,是否持續寫入,JOB_NAME,寫檔日期 0為當天
     * @return
     * @throws Exception
     */
	public void writeZFile(String writeFileName,String attached_name,String path,int dataCount,String separated,String jobName,int days)throws Exception
	{
		/*
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("JOB_NAME", addBlankForString(jobName, 19));
		map.put("CNT", addZeroForNum(String.valueOf(totalCount),15));
		map.put("DATA_DATE", sdfYYYYMMDD.format(new Date()));
		list.add(map);
		String[] order = {"JOB_NAME", "CNT", "DATA_DATE"};
		*/
		Date fileDate=new Date();
		Date runDate =new Date(fileDate.getTime()+((long)days * 24 * 60 * 60 * 1000));
		StringBuffer sb=new StringBuffer();
		sb.append(addBlankForString(jobName, 30));
		sb.append(addZeroForNum(String.valueOf(dataCount),15));
		sb.append(sdfYYYYMMDD.format(runDate));
		this.writeFileByText(writeFileName, attached_name, path, sb,false);
	} 
	/**
     * 寫入檢核檔案
     *
     * @param 檔名 副檔名,/路徑,欄位名稱,筆數,分隔符號,是否持續寫入,JOB_NAME
     * @return
     * @throws Exception
     */
	public void writeZFile(String writeFileName,String attached_name,String path,int dataCount,String separated,String jobName)throws Exception
	{
		this.writeZFile(writeFileName, attached_name, path, dataCount, separated, jobName,0);
	} 
    /**
     * 寫入檔案(直接給結果並產出)
     * 提供給特殊格式之檔案
     * @param 檔名 副檔名 輸出內容
     * @return
     * @throws IOException 
     * @throws Exception
     */
	public void writeFileByText(String file_name,String attached_name,String path,StringBuffer sb,boolean append) throws IOException
	{
		String fileName = file_name + "." + attached_name;
		if("".equals(attached_name))
		{
			fileName=file_name;
		}
		File file = new File(path  + "reports\\" + fileName);
		BufferedWriter bufWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,append),format));
		int number = 0;
        bufWriter.write(sb.toString());
        bufWriter.close();
	}
	
    /**
     * 寫入檔案ByResultSet(直接給結果並產出 EX:SQL直接給完格式只要純粹產出 )
     * @param 檔名 副檔名 輸出內容(ResultSet)
     * @return
     * @throws IOException 
     * @throws SQLException 
     * @throws Exception
     */
	public void writeFileByResultSet(String file_name,String attached_name,String path,ResultSet rs,boolean append) throws IOException, SQLException
	{
		String fileName = file_name + "." + attached_name;
		if("".equals(attached_name))
		{
			fileName=file_name;
		}
		File file = new File(path  + "reports\\" + fileName);
		BufferedWriter bufWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,append),format));
		int number = 0;
		while(rs.next())
		{			
        bufWriter.write((String) rs.getObject("OUTPUT"));
        bufWriter.write("\r\n");
		}
        bufWriter.close();
	}
	
	
	//取得連線
	public Connection getConnection() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, SQLException, JBranchException 
    {
		/*
		File file =new File("conf/context.xml");
		//File file =new File("src/test/resources/META-INF/context.xml");本機
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(file);
        NodeList nodeList = document.getElementsByTagName("Resource");
        String driver = nodeList.item(0).getAttributes().getNamedItem("driverClassName").getNodeValue(); 
        String url = nodeList.item(0).getAttributes().getNamedItem("url").getNodeValue(); 
        String user = nodeList.item(0).getAttributes().getNamedItem("username").getNodeValue(); 
        String password = nodeList.item(0).getAttributes().getNamedItem("password").getNodeValue(); 			
		   Class.forName(driver);   
		   Connection conn = 
               DriverManager.getConnection(url, 
                                  user, password);
		*/
		 
        
		
		Connection conn = null;
		
		com.systex.jbranch.platform.common.dataaccess.datasource.DataSource ds = new
                com.systex.jbranch.platform.common.dataaccess.datasource.DataSource();
        SessionFactory sf = (SessionFactory) PlatformContext.getBean(ds.getDataSource());
        conn = SessionFactoryUtils.getDataSource(sf).getConnection();
        /*
        DataSource dataSource = SessionFactoryUtils.getDataSource(sf);
		conn = dataSource.getConnection();
		*/
		   return conn;
    }
    /**
     * 寫入檔案ByTempTable(透過tempTable產出指定格式檔案 )
     * @param 檔名 副檔名 路徑
     * @return
     * @throws IOException 
     * @throws SQLException 
     * @throws Exception
     */
	public void writeFilebyTempTable(Object body, IPrimitiveMap<?> header)throws Exception
	{

		audit = (AuditLogUtil) ((Map<?, ?>) body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);

	        //取得傳入參數
	    Map<String, Object> inputMap = (Map<String, Object>) body;
	    Map<String, Object> jobParameter = (Map<String, Object>) inputMap.get(SchedulerHelper.JOB_PARAMETER_KEY);

	    //排程使用參數
	    String fileName = (String) jobParameter.get("fileName");
	    String attachedName = (String) jobParameter.get("attachedName");
	    String path=(String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);	        
		StringBuffer sb=new StringBuffer();
		String[] order={"OUTPUT"};
		sb.append("SELECT OUTPUT FROM OUTPUT_TEMPTABLE ");
		ResultSet rs = null;
		Connection con=this.getConnection();
		Statement s = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
                java.sql.ResultSet.CONCUR_READ_ONLY);
        s.setFetchSize(3000);
		try {
		rs=this.getRS(sb,s);
		
		this.writeFile(fileName, attachedName, path, order, rs, "", false, false);
		
	        } finally {
				if (rs != null) try { rs.close(); } catch (Exception e) {}
				if (s != null) try { s.close(); } catch (Exception e) {}
				if (con != null) try { con.close(); } catch (Exception e) {}
	    }
		
	}
    /**
     * 取得FTP傳輸設定檔案名稱
     * @param FTPID,是否要轉換,來源檔名/目的檔名 SRC DES
     * @return
     * @throws JBranchException 
     * @throws Exception
     */	
	public String getFtpFileName(String ftpsettingid,boolean trans,String src) throws DAOException, JBranchException
	{
	    dam=new DataAccessManager();
	    //System.out.println(dam);
	    TBSYSFTPVO vo = (TBSYSFTPVO) this.dam.findByPKey(TBSYSFTPVO.TABLE_UID, ftpsettingid);
    	String fileName="";
    	String fileDate="";
        
    	if (vo==null)
    	{
    	return "";
    	}
    	//決定要用來源檔名還是目的檔名
    	if("SRC".equals(src))
    	{
    		fileName=vo.getSRCFILENAME();
    	}
    	else if("DES".equals(src))
    	{
    		fileName=vo.getDESFILENAME();
    	}
    	String[] fileNameSplit=fileName.split("\\.");
    	//將點後的值取出
    	if(fileNameSplit.length>=2)
    	{
    		fileDate=fileNameSplit[1].trim();
    	}
    	else
    		{return fileNameSplit[0];}
    	//是否需要轉換格式
    	if(trans)
    	{
    		if(fileDate.startsWith("{"))
    	{
    		fileDate=transFileDate(fileDate);
    	}
    	}
    	return fileNameSplit[0]+"."+fileDate;    	
	}
    /**
     * 取得FTP傳輸設定日期
     * @param FTPID,是否要轉換,來源檔名/目的檔名 SRC DES
     * @return
     * @throws JBranchException 
     * @throws Exception
     */	
	   public String getFtpFileDate(String ftpsettingid,boolean trans,String src) throws JBranchException
	    {	
		    dam=new DataAccessManager();
		    //System.out.println(dam);
		    TBSYSFTPVO vo = (TBSYSFTPVO) this.dam.findByPKey(TBSYSFTPVO.TABLE_UID, ftpsettingid);
	    	String fileName="";
	    	String fileDate="";
	        
	    	if (vo==null)
	    	{
	    	return "";
	    	}
	    	//決定要用來源檔名還是目的檔名
	    	if("SRC".equals(src))
	    	{
	    		fileName=vo.getSRCFILENAME();
	    	}
	    	else if("DES".equals(src))
	    	{
	    		fileName=vo.getDESFILENAME();
	    	}
	    	String[] fileNameSplit=fileName.split("\\.");
	    	//將點後的值取出
	    	if(fileNameSplit.length>=2)
	    	{
	    		fileDate=fileNameSplit[1].trim();
	    	}
	    	//是否需要轉換格式
	    	if(trans)
	    	{
	    		if(fileDate.startsWith("{"))
	    	{
	    		fileDate=transFileDate(fileDate);
	    	}
	    	}
	    	return fileDate;
	    }
	    /**
	     * 轉換檔案日期
	     * @param FTPID,是否要轉換,來源檔名/目的檔名 SRC DEC
	     * @return
	     * @throws NotFoundException, DAOException 
	     * @throws Exception
	     */	
	public String transFileDate(String fileDate)
	{
		fileDate=fileDate.toUpperCase();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf2=new SimpleDateFormat("yyyyMM");
		Date runDate=new Date();
		String transfileDate=fileDate.replaceAll("[\\{\\}]", "").toUpperCase();
		//目前已知格式 {SYSDATE},{SYSDATE,1},yyyyMMdd,yyyymm
		String[] fileDateSplit=fileDate.split(",");
		if(fileDateSplit.length>=2)
		{
			//System.out.println(fileDateSplit[0].replaceAll("[\\{\\}]", "").toUpperCase());
			//格式非{SYSDATE,-1}01 純吃格式設定的日期
			if(fileDate.startsWith("{")&&fileDate.endsWith("}")&&"SYSDATE".equals(fileDateSplit[0].replaceAll("[\\{\\}]", "")))
			{
				fileDateSplit[0]=fileDateSplit[0].replaceAll("[\\{\\}]", "");
				fileDateSplit[1]=fileDateSplit[1].replaceAll("[\\{\\}]", "");
				char sign=0;
				int unit=0;
				//判斷是否需要對日期做運算
				if(fileDateSplit[1].matches(".+[EHM]$"))
				{
					//取得運算單位
					sign=fileDateSplit[1].charAt(fileDateSplit[1].length()-1);
					unit=Integer.parseInt(fileDateSplit[1].substring(0, fileDateSplit[1].length()-1));
				}
				else
				{
					unit=Integer.parseInt(fileDateSplit[1]);
				}
				if (unit!=0)
				{
					return sdf.format(computeDate(runDate,unit,sign));
				}
				else
					return sdf.format(runDate);
			}
			//結尾非}  例如{SYSDATEYYYYMM,-1M)01
			else if(fileDate.startsWith("{SYSDATEYYYYMM")&&!fileDate.endsWith("}"))
			{   

				int rightBracketIdx = fileDate.indexOf("}");
				int leftBracketIdx = fileDate.indexOf("{");
				int comaBracketIdx = fileDate.indexOf(",");
				String defaultValue=fileDate.substring(rightBracketIdx+1,fileDate.length());
				char sign=0;
				int unit=0;
				String fileDate1=fileDate.substring(leftBracketIdx+1,comaBracketIdx);
				String fileDate2=fileDate.substring(comaBracketIdx+1,rightBracketIdx);
				if(fileDate2.matches(".+[EHM]$"))
				{
					sign=fileDate2.charAt(fileDate2.length()-1);
					unit=Integer.parseInt(fileDate2.substring(0,fileDate2.length()-1));		
				}
				if (unit!=0)
				{
					return sdf2.format(computeDate(runDate,unit,sign))+defaultValue;
				}
			}
		}
		else
		{
			//YYYYMMDD 或 SYSDATE
			if("YYYYMMDD".equals(transfileDate)||"SYSDATE".equals(transfileDate))
			{
			   return sdf.format(runDate);
			}
			//ex:YYYYMM01 只取年月
			else if(!transfileDate.endsWith("DD")&&transfileDate.startsWith("YYYYMM"))
			{
				return sdf2.format(runDate)+transfileDate.replaceAll("YYYYMM", "");
			}
		}

		return sdf.format(runDate);
	}
	private Date computeDate(Date runDate,int unit,char sign)
	{
	      Calendar cal = Calendar.getInstance();
	        cal.setTime(runDate);
	        switch (sign) {
	        case 0:		// 預設單位為天數
	        	cal.add(Calendar.DATE, unit);
	        	break;
	        case 'M':	// 單位為月數
	        	cal.add(Calendar.MONTH, unit);
	        	break;
	        case 'E':	// 單位為月數，回傳月底日期
	        	cal.add(Calendar.MONTH, unit);
	        	cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
	        	break;
	        case 'H':	// 單位為時數
	        	cal.add(Calendar.HOUR_OF_DAY, unit);
	        	break;
	        }
	        
	        return new Timestamp(cal.getTimeInMillis());
	}
	public ResultSet getRS(StringBuffer sb,Statement s) throws Exception 
	{
		
		ResultSet rs = null;
//		if(s != null && !s.isClosed())
//		{  c3p0 此方法無法被呼叫，先註解
             rs = s.executeQuery(sb.toString());
//		}
		
		return rs;
	}
	
    /**
     * 補0
     * @param 字串 長度
     * @return
     * @throws Exception
     */
	public String addZeroForNum(String str, int strLength) {
		
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
	 /**
     * 補空格
     * @param 字串 長度
     * @return
     * @throws Exception
     */
	public String addBlankForString(String str, int strLength) {
		
	    int strLen = str.length();
	    if (strLen < strLength) {
	        while (strLen < strLength) {
	            StringBuffer sb = new StringBuffer();
	            sb.append(str).append(" ");//右補空格
	            str = sb.toString();
	            strLen = str.length();
	        }
	    }
	    return str;
	}
	//產出空檔
	public void genEmptyFile(String file_name, String attached_name, String path) throws IOException
	{
		String fileName = file_name + "." + attached_name;
		FileWriter writer = new FileWriter(path  + "reports\\" + fileName,false);
		writer.flush();
		writer.close();
	}
	
 
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getSeparated() {
		return separated;
	}
	public void setSeparated(String separated) {
		this.separated = separated;
	}
	
}
