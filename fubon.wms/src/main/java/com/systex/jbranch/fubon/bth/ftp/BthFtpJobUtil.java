package com.systex.jbranch.fubon.bth.ftp;

import java.io.File;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSFTPVO;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSREMOTEHOSTVO;
import com.systex.jbranch.platform.common.scheduler.AuditLogUtil;
import com.systex.jbranch.platform.common.util.NullTK;
import com.systex.jbranch.platform.common.util.PlatformContext;

/**
 * created by SebastianWu on 2016/06/20
 * modified by Jemmy Tsai on 2017/08/10
 *
 * Copy From FTPJobUtil.java
 * 2017/08/10：重構，增加處理SFTP功能。
 * 	BthFtpJobUtil  使用TBSYSFTP
 *  SftpUtil、SystexFtpUtil 使用TBSYSREMOTEHOST
 */
public class BthFtpJobUtil { 
// ------------------------------ FIELDS ------------------------------

    private DataAccessManager dam;
    private FtpUtilInf ftpUtil;
    private TBSYSFTPVO tbsysftpvo;
    private TBSYSFTPLOG logVo;
    private AuditLogUtil audit;
    private StringBuffer auditMsg = new StringBuffer();
    private Logger logger = LoggerFactory.getLogger(BthFtpJobUtil.class);

    private String srcdirectory;
    private String srcfilename;
    private String checkfile;
    private String desdirectory;
    private String desfilename;
    private String repeat;
    private String repeatinterval; 
// --------------------------- CONSTRUCTORS ---------------------------

    public BthFtpJobUtil() {
        this(null);
    }

    public BthFtpJobUtil(AuditLogUtil audit) {
        this.audit = audit;
        try {
        	this.dam = PlatformContext.getBean(DataAccessManager.class);
        	if (this.dam == null) {
        		logger.info("***** No Data Access Manager *****");
        		dam = new DataAccessManager();
        	}
            dam.setAutoCommit(true);
        }
        catch (DAOException e) {
            logger.error(e.getMessage(), e);
        }
        catch (JBranchException e) {
            logger.error(e.getMessage(), e);
        }
    }

// -------------------------- OTHER METHODS --------------------------
    public void checkValue(TBSYSFTPVO ftpVo) {
    	auditMsg.append("檢查 FTP 檔案設定\n");
    	
    	if (ftpVo == null) {
            return;
        }
    	
        this.srcdirectory = NullTK.checkNull(ftpVo.getSRCDIRECTORY(), this.srcdirectory);
        this.srcfilename = NullTK.checkNull(ftpVo.getSRCFILENAME(), this.srcfilename);
        this.checkfile = NullTK.checkNull(ftpVo.getCHECKFILE(), this.checkfile);
        this.desdirectory = NullTK.checkNull(ftpVo.getDESDIRECTORY(), this.desdirectory);
        this.desfilename = NullTK.checkNull(ftpVo.getDESFILENAME(), this.desfilename);
        this.repeat = NullTK.checkNull(String.valueOf(ftpVo.getREPEAT()), this.repeat);
        this.repeatinterval = NullTK.checkNull(String.valueOf(ftpVo.getREPEATINTERVAL()), this.repeatinterval);
    }    
    
    // 實例化FtpUtilInf
    private void newFtpUtilInstance(String ftpsettingid) throws Exception {
    	auditMsg.append("FTPSETTINGID [" + ftpsettingid + "]\n");
    	
        if (ftpsettingid == null || ftpsettingid.trim().length() == 0) {
            throw new Exception("FTPSetting ID為空值");
        }

        try {
        	TBSYSREMOTEHOSTVO tbsysremotehostvo = null;
        	boolean cam230 = false;		// CAM230不適用
        	logger.info("Before get FTPSetting = {}", ftpsettingid);
            this.tbsysftpvo = 
            		(TBSYSFTPVO) this.dam.findByPKey(TBSYSFTPVO.TABLE_UID, ftpsettingid);
            if (tbsysftpvo == null) {
            	logger.info("Before get HOSTID by FTPSetting = {}", ftpsettingid);
            	tbsysremotehostvo = 
            			(TBSYSREMOTEHOSTVO) this.dam.findByPKey(TBSYSREMOTEHOSTVO.TABLE_UID, ftpsettingid);
            	if (tbsysremotehostvo == null)
            		throw new Exception("FTPSetting ID[" + ftpsettingid + "] 未定義於資料庫");
            	else
            		cam230 = true;
            } else {     
            	logger.info("Before get HOSTID = {} from FTP[{}]", tbsysftpvo.getHOSTID(), ftpsettingid);
	        	tbsysremotehostvo = 
	        			(TBSYSREMOTEHOSTVO) this.dam.findByPKey(TBSYSREMOTEHOSTVO.TABLE_UID, tbsysftpvo.getHOSTID());
	        	if (tbsysremotehostvo == null)
	        		throw new Exception("HOST ID[" + tbsysftpvo.getHOSTID() + "] 未定義於資料庫");
            }
        	List<Map<String, Object>> result = null;
        	try {
	        	QueryConditionIF queryCondition = this.dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	        	queryCondition.setObject("HOSTID", tbsysftpvo.getHOSTID());
	        	queryCondition.setQueryString("select PROTOCOL from TBSYSREMOTEHOST where HOSTID = :HOSTID");
	        	result = this.dam.exeQuery(queryCondition);
        	} catch (Exception e) {
        		logger.warn("Query err", e);
        	}
        	if (result == null || result.size() == 0 ) {
	        	// if ("SFTP".equals(tbsysremotehostvo.getPROTOCOL())) {
	        	if (22 == tbsysremotehostvo.getPORT().intValue()) {
	        		this.ftpUtil = new SftpUtil(tbsysremotehostvo);
	        	} else {	// Other is FTP
	        		this.ftpUtil = new SystexFtpUtil(tbsysremotehostvo);
	        	}
        	} else {
        		String protocol = (String) result.get(0).get("PROTOCOL");
        		if ("SFTP".equals(protocol)) {
	        		this.ftpUtil = new SftpUtil(tbsysremotehostvo);
        		} else if ("SFTP2".equals(protocol)) {	// sha2 演算法
        			this.ftpUtil = new Sftp2Util(tbsysremotehostvo);
	        	} else {								// Other is FTP
	        		this.ftpUtil = new SystexFtpUtil(tbsysremotehostvo);
	        	}
        	}
        	// 記錄Log
        	this.logVo = new TBSYSFTPLOG();
        	if (cam230) {
        		this.logVo.setFTPSETTINGID("CAM230");
        		this.logVo.setHOSTID(ftpsettingid);
        	} else {
	        	this.logVo.setFTPSETTINGID(ftpsettingid);
	        	this.logVo.setSRCDIRECTORY(this.tbsysftpvo.getSRCDIRECTORY());
	        	this.logVo.setSRCFILENAME(this.tbsysftpvo.getSRCFILENAME());
	        	this.logVo.setCHECKFILE(this.tbsysftpvo.getCHECKFILE());
	        	this.logVo.setDESDIRECTORY(this.tbsysftpvo.getDESDIRECTORY());
	        	this.logVo.setDESFILENAME(this.tbsysftpvo.getDESFILENAME());
	        	this.logVo.setHOSTID(this.tbsysftpvo.getHOSTID());
        	}
        	this.logVo.setIP(tbsysremotehostvo.getIP());
        	this.logVo.setPORT(tbsysremotehostvo.getPORT().intValue());
        	this.logVo.setUSERNAME(tbsysremotehostvo.getUSERNAME());
        	this.logVo.setCREATOR(null);
        	java.sql.Timestamp sqlDate = new java.sql.Timestamp(System.currentTimeMillis());
        	
        	this.logVo.setCREATETIME(sqlDate);
        	this.logVo.setPROCESSOR(InetAddress.getLocalHost().getHostName());
        } catch (Exception e) {
        	logger.warn("new instance err", e);
        	auditMsg.append(e.getMessage());
            throw new JBranchException(e);
        }
    }
    
    // 儲存FTP LOG
    private void saveLog(String op, String msg) {
    	if (this.logVo == null) return;
    	if ("".equals(msg)) msg = "SUCCESS";
    	this.logVo.setRESULT(msg);
    	this.logVo.setOPERATOR(op);
//    	try {
//    		
//        	this.logVo.setCREATOR(this.tbsysftpvo.getCreator());    		
//    	} catch (Exception e) {
//    		logger.warn("get login id err: {}", e.getMessage());
//    		this.logVo.setCREATOR("Unknown");
//    	}
    	try {
    		StringBuffer sb = new StringBuffer();
    		sb.append("INSERT INTO TBSYSFTPLOG (SEQ, FTPSETTINGID, SRCDIRECTORY, SRCFILENAME, CHECKFILE, DESDIRECTORY, DESFILENAME, HOSTID, IP, PORT, USERNAME, PROCESSOR, OPERATOR, RESULT,CREATOR, CREATETIME, FINISHTIME) ");
    		sb.append("VALUES (TBSYSFTPLOG_SEQ.nextval, :FTPSETTINGID, :SRCDIRECTORY, :SRCFILENAME, :CHECKFILE, :DESDIRECTORY, :DESFILENAME, :HOSTID, :IP, :PORT, :USERNAME, :PROCESSOR, :OPERATOR, :RESULT, :CREATOR, :CREATETIME, sysdate) ");
        	QueryConditionIF queryCondition = this.dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        	queryCondition.setQueryString(sb.toString());
        	queryCondition.setObject("FTPSETTINGID", this.logVo.getFTPSETTINGID());
        	queryCondition.setObject("SRCDIRECTORY", this.logVo.getSRCDIRECTORY());
        	queryCondition.setObject("SRCFILENAME", this.logVo.getSRCFILENAME());
        	queryCondition.setObject("CHECKFILE", this.logVo.getCHECKFILE());
        	queryCondition.setObject("DESDIRECTORY", this.logVo.getDESDIRECTORY());
        	queryCondition.setObject("DESFILENAME", this.logVo.getDESFILENAME());
        	queryCondition.setObject("HOSTID", this.logVo.getHOSTID());
        	queryCondition.setObject("IP", this.logVo.getIP());
        	queryCondition.setObject("PORT", this.logVo.getPORT());
        	queryCondition.setObject("USERNAME", this.logVo.getUSERNAME());
        	queryCondition.setObject("PROCESSOR", this.logVo.getPROCESSOR());
        	queryCondition.setObject("OPERATOR", this.logVo.getOPERATOR());
        	queryCondition.setObject("RESULT", this.logVo.getRESULT());
        	queryCondition.setObject("CREATOR", this.logVo.getCREATOR());
        	queryCondition.setObject("CREATETIME", this.logVo.getCREATETIME());
        	
        	int rows = this.dam.exeUpdate(queryCondition);
        	logger.info("insert {} row log", rows);
    	} catch (Exception e) {
    		logger.warn("Query err", e);
    	}
    }
    
    public String replaceRootPath(String desdirectory) throws JBranchException {
    	char fileSeparator = File.separatorChar;
        if (desdirectory.trim().startsWith("%JBRANCH_ROOT%")) {
            desdirectory = desdirectory.replaceFirst("%JBRANCH_ROOT%", "");
            String rootPath = DataManager.getRoot();
            if (rootPath.endsWith("\\") || rootPath.endsWith("/")) {
                rootPath = rootPath.substring(0, rootPath.length() - 1);
            }
            desdirectory = rootPath + desdirectory;
        }
        else if (desdirectory.trim().startsWith("%APP_ROOT%")) {
            desdirectory = desdirectory.replaceFirst("%APP_ROOT%", "");
            String appPath = null;
            try {
                appPath = DataManager.getRealPath();
                if (appPath.endsWith("\\") || appPath.endsWith("/")) {
                    appPath = appPath.substring(0, appPath.length() - 1);
                }
            }
            catch (Exception e) {
            	auditMsg.append("無法取得APP_ROOT的參數，APP_ROOT僅可在AP Server取得。請修正資料庫的設定");
                throw new JBranchException("無法取得APP_ROOT的參數，APP_ROOT僅可在AP Server取得。請修正資料庫的設定");
            }
            desdirectory = appPath + desdirectory;
        }
        desdirectory.replace('\\', fileSeparator);
        return desdirectory;
    }

    /**
     * 針對檔案日期做天數加/減
     *
     * @param date
     * @param days
     * @return
     */
    private Date dateModified(Date date, Integer days, char sign){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        switch (sign) {
        case 0:		// 預設單位為天數
        	cal.add(Calendar.DATE, days);
        	break;
        case 'M':	// 單位為月數
        	cal.add(Calendar.MONTH, days);
        	break;
        case 'E':	// 單位為月數，回傳月底日期
        	cal.add(Calendar.MONTH, days);
        	cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        	break;
        case 'H':	// 單位為時數
        	cal.add(Calendar.HOUR_OF_DAY, days);
        	break;
        }
        
        return new Timestamp(cal.getTimeInMillis());
    }
    
    // 民國年
    private Date yearModified(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, -1911);
        return new Timestamp(cal.getTimeInMillis());
    }
    
    /**
     * 取得正確的檔名
     * 日期格式須以大括弧{}包覆之
     * 
     * pattern1 系統日期
     * 			{SYSDATE}：系統日期，格式預設為yyyyMMdd
     * 			{SYSDATE,N}：系統日期加上N日，N可以為負值，如-2
     * 			{SYSDATEpattern,N}：系統日期經格式化後加上N日，N可以為負值，如-2，pattern可參照pattern2
     * pattern2 批次日期。支援以下日期格式，批次日期：
     * 四碼西元年：{yyyy-MM-dd-HH} | {yyyy_MM_dd_HH} | {yyyyMMddHH}
     * 二碼西元年：{yy-MM-dd-HH} | {yy_MM_dd_HH} | {yyMMddHH}
     * 三碼民國年：{cyy-MM-dd-HH} | {cyy_MM_dd_HH} | {cyyMMddHH}
     *			{yyyy-MM-dd,N}：批次日前加上N日，N可以為負值，如-2
     *			年(y)、月(M)、日(d)、時(H)皆可省略，連接符為-和_亦可省略。
     * @param fileName
     * @return
     * @throws Exception
     */
    public String getRealFileName(String fileName) throws Exception {
		if (!Pattern.matches(".*[\\{|\\}]+.*", fileName)) {
			return fileName;
		}

//		String pattern1 = "\\{SYSDATE([,]+[-]?\\d+)?\\}";
		// 系統日期
		String patYMD = "(([yY]{2}|[cC])?[yY]{2})?[-_]?([mM]{2})?[-_]?([dD]{2})?[-|_]?([H]{2})?";
		String pattern1 = "\\{SYSDATE(" + patYMD + ")?(,-?\\d+)?[EHM]?\\}";
//		String pattern2 = "\\{([y|Y]{4}|[y|Y]{2})?[-|_]?([m|M]{2})?[-|_]?([d|D|]{2})?[-|_]?([H]{2})?([,]+[-]?\\d+)?\\}.*";
		// 批次日期，
		String pattern2 = "\\{(([yY]{2}|[cC])?[yY]{2})?[-_]?([mM]{2})?[-_]?([dD]{2})?[-_]?([H]{2})?([,]+[-]?\\d+)?[EHM]?\\}.*";
		
		//mantis 5363: 增加 Pattern 3
		String pattern3 = "\\{BUSDATE(" + patYMD + ")?(,-?\\d+)?[EHM]?\\}";
		
        //包覆日期之符號
        String syntax1 = "{";
        String syntax2 = "}";

        //計算大括弧,確認沒有未關閉之括弧
        int syntaxCnt = StringUtils.countMatches(fileName, syntax1) + StringUtils.countMatches(fileName, syntax2);
        boolean formatIsError = (syntaxCnt%2 >0) ? true : false;

        //檢核傳入格式
        if (!formatIsError) {
        	int cnt = 0;
        	while (fileName.indexOf(syntax1) > -1 && cnt < 50) {
        		cnt++;
        		
    			int fromIdx = fileName.indexOf(syntax1)+1;
                int endIdx = fileName.indexOf(syntax2);

                //將切割後的日期放入陣列
                String splitStr = fileName.substring(fromIdx, endIdx);
                
                //日期是否需做加減
                Integer days = 0;
                String dateFormat = null;
                char sign = 0;
                logger.info("splitStr={}, days={}", splitStr, days);
    			if (splitStr.contains(",")) {
    				int commaIdx = splitStr.indexOf(",");
    				dateFormat = splitStr.substring(0, commaIdx);
    				String sDay = splitStr.substring(commaIdx + 1);
    				if (sDay.matches(".+[EHM]$")) {
    					sign = sDay.charAt(sDay.length()-1);
    					days = Integer.parseInt(sDay.substring(0, sDay.length()-1));
    				} else {
    					days = Integer.valueOf(splitStr.substring(commaIdx + 1));
    				}
    			} else {
    				dateFormat = splitStr;
    			}
    			logger.info("dataFromat={}", dateFormat);
    			
    			//原始字串
                String oldStr = syntax1 + splitStr + syntax2;
                SimpleDateFormat sdf; // = new SimpleDateFormat();
                Date date;
                
    			if (oldStr.matches(pattern1)) {
    				date = new Date();
    				
    				//判斷是否需做天數增減
        			date = (days != 0)? dateModified(date, days, sign) : date;
    				if ("SYSDATE".equals(dateFormat)) {
    					// 採預設yyyyMMdd
    					sdf = new SimpleDateFormat("yyyyMMdd");
    					dateFormat = sdf.format(date);
    				} else {
    					dateFormat = dateFormat.substring(7);
    					try {
    						if (dateFormat.matches("^[Cc].+")) {
    							// 使用民國年
    	        				date = this.yearModified(date);
    	        				dateFormat = dateFormat.replaceAll("[Cc]", "y");
    							sdf = new SimpleDateFormat(dateFormat);
	    						dateFormat = sdf.format(date);
    						} else {
	    						sdf = new SimpleDateFormat(dateFormat);
	    						dateFormat = sdf.format(date);
    						}
    					} catch (Exception e) {
    						logger.warn("parse error = {} ", dateFormat);
    						sdf = new SimpleDateFormat("yyyyMMdd");
        					dateFormat = sdf.format(date);
    					}
    				}
    			} else if ((oldStr.matches(pattern2))) {
    				//取得批次日期
                    QueryConditionIF condition = this.dam
        					.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);

        			StringBuffer sql = new StringBuffer()
        					.append("SELECT PABTH_BATCHCTRL.FC_GetData_Date BatchDate FROM DUAL");
        			condition.setQueryString(sql.toString());

        			List<Map<String, Timestamp>> result = this.dam.exeQuery(condition);
        			Timestamp stamp = result.get(0).get("BATCHDATE");
        			date = new Date(stamp.getTime());
        			
        			//判斷是否需做天數增減
        			date = (days != 0)? dateModified(date, days, sign) : date;
        			
        			if (dateFormat.matches("^[Cc].+")) {
        				// 使用民國年
        				date = this.yearModified(date);
        				dateFormat = dateFormat.replaceAll("[Cc]", "y");
        			} 
        			sdf = new SimpleDateFormat(dateFormat);
        			dateFormat = sdf.format(date);
    			} else if ((oldStr.matches(pattern3))) {
    				//取得批次日期
                    QueryConditionIF condition = this.dam
        					.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);

        			StringBuffer sql = new StringBuffer()
        					.append("select PABTH_UTIL.FC_getBusiDay(SYSDATE, 'TWD', -1) BATCHDATE from dual ");
        			condition.setQueryString(sql.toString());

        			List<Map<String, Timestamp>> result = this.dam.exeQuery(condition);
        			Timestamp stamp = result.get(0).get("BATCHDATE");
        			date = new Date(stamp.getTime());
        			
        			//判斷是否需做天數增減
        			date = (days != 0)? dateModified(date, days, sign) : date;
        			
        			if ("BUSDATE".equals(dateFormat)) {
    					// 採預設yyyyMMdd
    					sdf = new SimpleDateFormat("yyyyMMdd");
    					dateFormat = sdf.format(date);
    				} else {
    					dateFormat = dateFormat.substring(7);
    					try {
    						if (dateFormat.matches("^[Cc].+")) {
    							// 使用民國年
    	        				date = this.yearModified(date);
    	        				dateFormat = dateFormat.replaceAll("[Cc]", "y");
    							sdf = new SimpleDateFormat(dateFormat);
	    						dateFormat = sdf.format(date);
    						} else {
	    						sdf = new SimpleDateFormat(dateFormat);
	    						dateFormat = sdf.format(date);
    						}
    					} catch (Exception e) {
    						logger.warn("parse error = {} ", dateFormat);
    						sdf = new SimpleDateFormat("yyyyMMdd");
        					dateFormat = sdf.format(date);
    					}
    				}
        			
    			} else {
    				this.saveLog("GET", "[FTP] 輸入檔案之命名格式不符: " + fileName);
    				this.auditMsg.append("[FTP] 輸入檔案之命名格式不符: " + fileName);
    				throw new JBranchException("[FTP] 輸入檔案之命名格式不符: " + fileName);
    			}
    			
    			fileName = fileName.replace(oldStr, dateFormat);
    		}
        }

        return fileName;
    }

    /**
     * 判斷遠端是否有指定的檔案
     *
     * @param remotefiles
     * @param filename
     * @return
     */
//    private boolean haveFile(String[] remotefiles, String filename) {    	
//        for (int i = 0; i < remotefiles.length; i++) {
//        	// logger.info("@@ " + remotefiles[i] + " vs " + filename);
//            if (remotefiles[i].equals(filename)) {
//                return true;
//            }
//        }
//        return false;
//    }

    // 顯示檔案清單
    public List<String> listNames(String hostId, String srcdir) throws Exception {
    	return this.listNames(hostId, srcdir, null);
    }
    
    public List<String> listNames(String hostId, String srcdir, Pattern pattern) throws Exception {
    	try {
	    	this.newFtpUtilInstance(hostId);
	    	this.checkValue(this.tbsysftpvo);
	    	
	    	srcdir = this.replaceRootPath(srcdir);
	    	List<String> result = this.ftpUtil.listNames(srcdir, pattern);
    		this.saveLog("listNames", "");
    		return result;
    	} catch (Exception e) {
    		this.saveLog("listNames", e.getMessage());
    		this.auditMsg.append("listNames: " + e.getMessage());
    		throw e;
    	}
    }
    
    private boolean repeatFlag = false;
    private void repeatGetFile(String ftpsettingid) throws Exception  {
    	Exception repeatFailException = null;
    	for (int i=0, n=this.tbsysftpvo.getREPEAT(); i<n; i++) {
    		try {
    			this.ftpGetFile(ftpsettingid);
    			this.repeatFlag = false;
    			return;
    		} catch (Exception e) {
    			repeatFailException = e;
    			logger.info("{} GET wait {} secs #{}", ftpsettingid, this.tbsysftpvo.getREPEATINTERVAL(), i+1);
    			Thread.sleep(this.tbsysftpvo.getREPEATINTERVAL() * 1000);
    		}
    	}
    	throw repeatFailException;
    }
    
    private String repeatPutFile(String ftpsettingid) throws Exception  {
    	Exception repeatFailException = null;
    	for (int i=0, n=this.tbsysftpvo.getREPEAT(); i<n; i++) {
    		try {
    			String ret = this.ftpPutFile(ftpsettingid);
    			this.repeatFlag = false;
    			return ret;
    		} catch (Exception e) {
    			repeatFailException = e;
    			logger.info("{} PUT wait {} secs #{}", ftpsettingid, this.tbsysftpvo.getREPEATINTERVAL(), i+1);
    			Thread.sleep(this.tbsysftpvo.getREPEATINTERVAL() * 1000);
    		}
    	}
    	throw repeatFailException;
    }
    // 下載
	public void ftpGetFile(String ftpsettingid) throws Exception {
    	List<String> result;
    	boolean isFuzzy = false;
    	try {
    		logger.debug("{} before GetFile", ftpsettingid);
    		if (this.tbsysftpvo == null) {
	    		this.newFtpUtilInstance(ftpsettingid);
	        	this.checkValue(this.tbsysftpvo);
    		}
    		if (0 < this.tbsysftpvo.getREPEAT() && !this.repeatFlag) {
    			this.repeatFlag = true;
    			this.repeatGetFile(ftpsettingid);
    			return;
    		}
        	
        	this.desdirectory = this.replaceRootPath(this.desdirectory);
        	isFuzzy = this.isFuzzyFile(this.srcfilename) || this.isFuzzyFile(desfilename);
            //取得正確的檔案名稱，檔名規則有固定檔名或是以日期為規則的檔名兩種
	    	this.srcfilename = this.getRealFileName(this.srcfilename);
	    	this.desfilename = this.getRealFileName(this.desfilename);
	    	this.checkfile = (StringUtils.isNotBlank(this.checkfile)) ? getRealFileName(this.checkfile) : null;
    		result = this.ftpUtil.listNames(this.srcdirectory);
    	} catch (Exception e) {
    		this.saveLog("GET", e.getMessage());
    		this.auditMsg.append("GET: " + e.getMessage());
    		throw e;
    	}
    	if (isFuzzy) {
    		// 模糊下載
    		int downloadCount = 0;				// 下載次數
    		for (String filename : result) {
    			logger.info("@@@ {} vs {} is {}", filename, srcfilename, fuzzyMatch(srcfilename, filename));
    			if (fuzzyMatch(srcfilename, filename)) {  				
      				this.ftpGetFile(filename, getDesFileName(filename));
      				downloadCount++;
    			}
    			if (checkfile != null) {
	    			if (filename.startsWith(checkfile)) {
	    				this.ftpGetFile(checkfile, checkfile);
	    				downloadCount++;
	    			}
    			}
    		}
    		if (downloadCount > 0)
    			this.saveLog("GET", "");
    		else {
    			this.saveLog("GET", "無遠端檔案可下載");
    			this.auditMsg.append("GET: 無遠端檔案可下載");
        		throw new JBranchException("無遠端檔案可下載");
    		}
    	} else {
        	String[] files = new String[result.size()];
        	for (int i=0; i<files.length; i++) {
        		files[i] = result.get(i);
        	}
        	if (ArrayUtils.contains(files, this.srcfilename)) {
	        // if (this.haveFile(files, this.srcfilename)) {
	        	try {
	        		this.ftpGetFile(srcfilename, desfilename);
	        	} catch (Exception e) {
	        		this.saveLog("GET", e.getMessage());
	        		this.auditMsg.append("GET: " + e.getMessage());
	        		throw e;
	        	}
	        	//下載控制檔
	            if (this.checkfile != null && !this.checkfile.trim().isEmpty()) {
	                this.checkfile = getRealFileName(this.checkfile);
	                try {
	                	this.ftpGetFile(this.checkfile, this.checkfile);
	            	} catch (Exception e) {
	            		this.saveLog("GET", e.getMessage());
	            		this.auditMsg.append("GET: " + e.getMessage());
	            		throw e;
	            	}
	            }
	            this.saveLog("GET", "");
	        }
	        //只有checkfile時
	        // else if(this.haveFile(files, this.checkfile)){
	        else if (ArrayUtils.contains(files, this.checkfile)) {
	        	try {
                	this.ftpGetFile(this.checkfile, this.checkfile);
	        		this.saveLog("GET", "");
	        	} catch (Exception e) {
	        		this.saveLog("GET", e.getMessage());
	        		throw e;
	        	}
	        }
	        else {
	        	this.saveLog("GET", "找不到遠端檔案");
	        	this.auditMsg.append("GET: 找不到遠端檔案");
	            throw new JBranchException("找不到遠端檔案");
	        }
    	}
    }
	
	/**取得目的檔檔名 (include fuzzy)*/
	private String getDesFileName(String filename) {
		if (isFuzzyFile(desfilename)) 
			if (fuzzyMatch(desfilename, filename)) return filename;
			else return fuzzyFileName(srcfilename, filename, desfilename);
		else return desfilename;
	}
	
	/**
	 * ex : 
	 * 		source 為 %example%.java
	 * 		fuzzied 為 1example1.java ,  2example2.java
	 * 		target (%target%.csv) =>  1target1.csv , 2target2.csv 
	 * 
	 * @param source
	 * @param fuzzied
	 * @param target
	 * @return
	 */
	private String fuzzyFileName(String source, String fuzzied, String target) {
		String[] commonStrArr = source.split("%");
		for (String common : commonStrArr) {
			if (common.trim().length() > 0) //避免%在最前面所擷取的 空 String
				fuzzied = fuzzied.replace(common, "%");  
		}
		
		String[] diffStrArr = fuzzied.split("%");
		for (String diff : diffStrArr) {
			if (diff.trim().length() > 0) //避免%在最前面所擷取的 空 String
				target = target.replaceFirst("%", diff);
		}
		return target;
	}

	// 單筆下載 for 模糊查詢
	private void ftpGetFile(String srcfile, String desfile) throws Exception {
    	try {
    		logger.info("download from {} / {} to {} / {}", 
    				this.srcdirectory, srcfile, this.desdirectory, desfile);
    		this.ftpUtil.download(this.srcdirectory, srcfile, this.desdirectory,desfile);
    		
			long srcSize = this.ftpUtil.exist(this.srcdirectory, srcfile);
			File desFile = new File(this.desdirectory, desfile);
			long desSize = desFile.length();
			if (desFile.exists() ) {
				if (srcSize == desSize || (srcSize == -1 && desSize > 0)) {	// 來源檔為-1表示被刪，也允許刪除。
					if (this.isDeleteRemote()) { // "Y".equals(this.tbsysftpvo.getSRCDELETE())) {
	    				String remotePath = this.srcdirectory + srcfile;
	        			if (!this.srcdirectory.endsWith(File.separator))
	        				remotePath = this.srcdirectory + File.separator + srcfile;
	        			logger.info("delete {}", remotePath);
	        			this.ftpUtil.delete(remotePath);
					}
				} else {
					throw new JBranchException("[" + srcfile + "] size no match! local= " + desSize + ", remote=" + srcSize);
				}
			} else {
	    		throw new JBranchException(this.srcdirectory + " / " +  srcfile + " no exists!");
			}
    		
    	} catch (Exception e) {
    		logger.warn(e.getMessage());
    		this.saveLog("GET", e.getMessage());
    		this.auditMsg.append("GET: " + e.getMessage());
    		throw e;
    	}
    }

	private boolean isDeleteRemote() {
		try {
        	QueryConditionIF queryCondition = this.dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        	queryCondition.setObject("FTPSETTINGID", tbsysftpvo.getFTPSETTINGID());
        	queryCondition.setQueryString("select SRCDELETE from TBSYSFTP where ftpsettingid = :FTPSETTINGID");
        	List<Map<String, Object>> result = this.dam.exeQuery(queryCondition);
        	
        	if (result == null || result.size() == 0 ) {
        		logger.info("FTPID= {} NO DATA", tbsysftpvo.getFTPSETTINGID());
        		return false;
        	} else {
        		String srcdelete = (String) result.get(0).get("SRCDELETE");
        		logger.info("FTPID= {}, DEL={}", tbsysftpvo.getFTPSETTINGID(), srcdelete);
        		return "Y".equals(srcdelete);
        	}
    	} catch (Exception e) {
    		logger.warn("Query err", e);
    		return false;
    	}
	}

    // 上傳
    public String ftpPutFile(String ftpsettingid) throws Exception {    	
    	try {
    		logger.debug("{} before PutFile", ftpsettingid);
    		if (this.tbsysftpvo == null) {
	        	this.newFtpUtilInstance(ftpsettingid);
	        	this.checkValue(this.tbsysftpvo);
    		}
    		if (0 < this.tbsysftpvo.getREPEAT() && !this.repeatFlag) {
    			this.repeatFlag = true;
    			return this.repeatPutFile(ftpsettingid);
    		}
        	this.srcdirectory = this.replaceRootPath(this.srcdirectory);
            //取得正確的檔案名稱，檔名規則有固定檔名或是以日期為規則的檔名兩種
	    	this.srcfilename = this.getRealFileName(this.srcfilename);
	    	this.desfilename = this.getRealFileName(this.desfilename);
	    	logger.info("src={}, des={}", this.srcfilename, this.desfilename);
    	} catch (Exception e) {
    		this.saveLog("PUT", e.getMessage());
    		throw e;
    	}
    	String[] files = null;
        File file = new File(CleanPath.cleanString(this.srcdirectory));
        if (file.exists()) {
            files = file.list();
        } else {
        	this.saveLog("PUT", "找不到來源路徑");
        	this.auditMsg.append("PUT: 找不到來源路徑");
            throw new JBranchException("找不到來源路徑");
        }
        // if (this.haveFile(files, this.srcfilename)) {
        if (ArrayUtils.contains(files,  this.srcfilename)) {
            //上傳控制檔
            if (this.checkfile != null && !this.checkfile.trim().isEmpty()) {
                this.checkfile = getRealFileName(this.checkfile);
                try {
                	logger.info("chkfile upload from {} / {} to {} / {}", 
            				this.srcdirectory, this.checkfile, this.desdirectory, this.checkfile);
                	this.ftpUtil.upload(this.srcdirectory, this.checkfile, this.desdirectory, this.checkfile);
                } catch (Exception e) {
            		this.saveLog("PUT", e.getMessage());
            		this.auditMsg.append("PUT:" + e.getMessage());
            		throw e;
            	}
            }

            //放檔案
            try {
            	logger.info("upload from {} / {} to {} / {}", 
        				this.srcdirectory, this.srcfilename, this.desdirectory, this.desfilename);
            	this.ftpUtil.upload(this.srcdirectory, this.srcfilename, this.desdirectory, this.desfilename);           	
        	} catch (Exception e) {
        		this.saveLog("PUT", e.getMessage());
        		this.auditMsg.append("PUT:" + e.getMessage());
        		throw e;
        	}
            this.saveLog("PUT", "");
        }
        else {
        	this.saveLog("PUT", "找不到檔案");
        	this.auditMsg.append("PUT: 找不到檔案");
            throw new JBranchException("找不到檔案");
        }
    	return this.auditMsg.toString();
    }
    
    // 讀檔
    public List<String> readFile(String hostId, String filepath, String filename) throws Exception {
    	try {
        	this.newFtpUtilInstance(hostId);
        	this.checkValue(this.tbsysftpvo);
    		List<String> result = this.ftpUtil.readFile(filepath, filename);
    		this.saveLog("readFile", "");
    		return result;
    	} catch (Exception e) {
    		this.saveLog("readFile", e.getMessage());
    		this.auditMsg.append("readFile: 找不到檔案");
    		throw e;
    	}
    }

// --------------------- GETTER / SETTER METHODS ---------------------    
    
    public void setAuditLog(AuditLogUtil audit) {
        this.audit = audit;
    }

    public StringBuffer getAuditMsg() {
		return auditMsg;
	}
    
    public List<TBSYSFTPVO> fuzzySearchCompare(String[] files, String filename, String checkfile) throws JBranchException {
    	List<TBSYSFTPVO> nameList = new ArrayList<TBSYSFTPVO>();

    	try {
            filename = this.getRealFileName(filename);
        } catch (Exception e) {
        	logger.warn("{} parse error : {}" + filename , e.getMessage());
        }
        for(String file : files){
            TBSYSFTPVO ftpVO = new TBSYSFTPVO();

            
            //比對files檔名是否符合模糊查詢條件
            boolean matchFuzzySearchFile = Pattern.matches(filename.replaceAll("%", ".*"), file)
        		|| Pattern.matches(filename.replaceAll("%", "*"), file);
//            System.out.println("# " + filename.replaceAll("%", ".*") + " " + matchFuzzySearchFile + " " + file);
            //符合查詢條件者加入TBSYSFTPVO.SRCFILENAME
            if (matchFuzzySearchFile) {
                ftpVO.setSRCFILENAME(file);
                ftpVO.setDESFILENAME(file);
            }

            if(StringUtils.isNotBlank(checkfile)){
                //確認輸入格式符合模糊查詢
                if(!isFuzzyFile(checkfile)) throw new JBranchException("[FTP] 模糊查詢時檢核檔欄位需加入 '%'");

                //模糊查詢取checkfile name
                checkfile = checkfile.replace("%", file.replace(filename, ""));
                ftpVO.setCHECKFILE(checkfile);
            }
            if (ftpVO.getDESFILENAME() != null)		// 空的目的檔不加
            	nameList.add(ftpVO);
        }
        return nameList;
    }
    
    /**
     * 判斷是否為模糊查詢
     *
     * @param filename
     */
    public boolean isFuzzyFile(String filename){
        return filename.indexOf("%") > -1;
    }

    public boolean isFuzzyFile() {
    	return this.srcfilename.indexOf("%") > -1 || this.desfilename.indexOf("%") > -1;
    }
    
    /**
     * 模糊配對
     * @param matched
     * @param match
     * @return
     */
    private boolean fuzzyMatch(String matched, String match) {
    	return Pattern.compile(matched.replaceAll("%", ".*")).matcher(match).matches();
    }
    
    public static void main(String[] args) throws Exception {
//    	BthFtpJobUtil jobUtil = new BthFtpJobUtil();
//    	String[] files = (new File("/Users/jemmy/Downloads/0926-LOG")).list(null);
//    	List<TBSYSFTPVO> list = jobUtil.fuzzySearchCompare(files, "EimMember_{SYSDATE,-6}%", null);
//    	for (TBSYSFTPVO vo : list) {
//    		System.out.println(vo.getSRCFILENAME());
//    	}
    	
    	try {
			new BthFtpJobUtil().getRealFileName("AA20170912_123456.csv");
		} catch (Exception e) {
			e.printStackTrace();
		}

    }
    
    /**
     * 
     * @param CMMGR023使用 確認GET檔案是否到位
     * @author SamTu
     * @date 2019/09/04
     * @return
     * @throws Exception
     */
	public boolean ftpCheckGetFile(String ftpsettingid) throws Exception {
    	List<String> result;
    	boolean isFuzzy = false;
    	try {
    		logger.debug("{} before GetFile", ftpsettingid);
    		if (this.tbsysftpvo == null) {
	    		this.newFtpUtilInstance(ftpsettingid);
	        	this.checkValue(this.tbsysftpvo);
    		}
        	
        	this.desdirectory = this.replaceRootPath(this.desdirectory);
        	isFuzzy = this.isFuzzyFile(this.srcfilename) || this.isFuzzyFile(desfilename);
            //取得正確的檔案名稱，檔名規則有固定檔名或是以日期為規則的檔名兩種
	    	this.srcfilename = this.getRealFileName(this.srcfilename);
	    	this.desfilename = this.getRealFileName(this.desfilename);
	    	this.checkfile = (StringUtils.isNotBlank(this.checkfile)) ? getRealFileName(this.checkfile) : null;
    		result = this.ftpUtil.listNames(this.srcdirectory);
    	} catch (Exception e) {
    		this.saveLog("GET", e.getMessage());
    		this.auditMsg.append("GET: " + e.getMessage());
    		throw e;
    	}
    	if (isFuzzy) {
    		// 模糊下載
    		int downloadCount = 0;				// 下載次數
    		for (String filename : result) {
    			logger.info("@@@ {} vs {} is {}", filename, srcfilename, fuzzyMatch(srcfilename, filename));
    			if (fuzzyMatch(srcfilename, filename)) {  				
    				return this.ftpCheckGetFile2(filename);
    			}
    			if (checkfile != null) {
	    			if (filename.startsWith(checkfile)) {
	    				return this.ftpCheckGetFile2(checkfile);
	    			}
    			}
    		}
    		if (downloadCount > 0)
    			this.saveLog("GET", "");
    		else {
    			this.saveLog("GET", "無遠端檔案可下載");
    			this.auditMsg.append("GET: 無遠端檔案可下載");
        		throw new JBranchException("無遠端檔案可下載");
    		}
    	} else {
        	String[] files = new String[result.size()];
        	for (int i=0; i<files.length; i++) {
        		files[i] = result.get(i);
        	}
        	if (ArrayUtils.contains(files, this.srcfilename)) {
	        // if (this.haveFile(files, this.srcfilename)) {
	        	try {
	        		return this.ftpCheckGetFile2(srcfilename);
	        	} catch (Exception e) {
	        		this.saveLog("GET", e.getMessage());
	        		this.auditMsg.append("GET: " + e.getMessage());
	        		throw e;
	        	}
	        }
	        //只有checkfile時
	        // else if(this.haveFile(files, this.checkfile)){
	        else if (ArrayUtils.contains(files, this.checkfile)) {
	        	try {
                	return this.ftpCheckGetFile2(this.checkfile);
	        	} catch (Exception e) {
	        		this.saveLog("GET", e.getMessage());
	        		throw e;
	        	}
	        }
	        else {
	        	this.saveLog("GET", "找不到遠端檔案");
	        	this.auditMsg.append("GET: 找不到遠端檔案");
	            throw new JBranchException("找不到遠端檔案");
	        }
    	}
		return false;
    }
	
	  
    /**
     * 
     * @param CMMGR023使用 確認GET檔案是否到位
     * @author SamTu
     * @date 2019/09/04
     * @return
     * @throws Exception
     */
		private boolean ftpCheckGetFile2(String srcfile) throws Exception {
	    	try {
	    		logger.info("檢查來源檔是否存在 {} / {}", 
	    				this.srcdirectory, srcfile);
	    		long srcSize = this.ftpUtil.exist(this.srcdirectory, srcfile);
	    		if(srcSize > 0){
	    			return true;
	    		}else{
	    			return false;
	    		}    		
	    	} catch (Exception e) {
	    		logger.warn(e.getMessage());
	    		this.saveLog("GET", e.getMessage());
	    		this.auditMsg.append("GET: " + e.getMessage());
	    		throw e;
	    	}
	    }

}
