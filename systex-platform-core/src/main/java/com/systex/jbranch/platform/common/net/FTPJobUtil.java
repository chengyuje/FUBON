package com.systex.jbranch.platform.common.net;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSFTPVO;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSREMOTEHOSTVO;
import com.systex.jbranch.platform.common.scheduler.AuditLogUtil;
import com.systex.jbranch.platform.common.security.util.CryptoUtil;
import com.systex.jbranch.platform.common.util.NullTK;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.SysInfo;

public class FTPJobUtil {
private static final String FTP_UTIL = "ftpUtil";

// ------------------------------ FIELDS ------------------------------

    DataAccessManager dam = null;

    private String ip = null;
    private int port = -1;
    private String username = null;
    private String password = null;


    private String hostid = null;
    private String srcdirectory = null;
    private String srcfilename = null;
    private String checkfile = null;
    private String desdirectory = null;
    private String desfilename = null;
    private String repeat = null;
    private String repeatinterval = null;

    private FTPUtil ftpUtil = null;
    private AuditLogUtil audit = null;
	private Logger logger = LoggerFactory.getLogger(FTPJobUtil.class);

// --------------------------- CONSTRUCTORS ---------------------------

    public FTPJobUtil() {
        this(null);
    }

    public FTPJobUtil(AuditLogUtil audit) {
        this.audit = audit;
        try {
            dam = new DataAccessManager();
        }
        catch (DAOException e) {
            logger.error(e.getMessage(), e);
        }
        catch (JBranchException e) {
            logger.error(e.getMessage(), e);
        }
    }

// -------------------------- OTHER METHODS --------------------------

    public void distConnection() {
        try {
            ftpUtil.disConnection();
        }
        catch (Exception e) {
            //ignore
        }
        finally {
            ftpUtil = null;
        }
    }

    public String ftpGetFile(String ftpsettingid) throws Exception {
        return ftpGetFile(ftpsettingid, null);
    }

    public String ftpGetFile(Connection con, String ftpsettingid) throws Exception {
        return ftpGetFile(con, ftpsettingid, null);
    }

    public String ftpGetFile(String ftpsettingid, TBSYSFTPVO ftpVo) throws Exception {
        valueCheck(ftpsettingid);
        init(ftpsettingid);

        return ftpGutFile(ftpVo);
    }

    private String ftpGutFile(TBSYSFTPVO ftpVo) throws Exception {
        checkValue(ftpVo);
        if (ftpUtil == null) {
        	ftpUtil = (FTPUtil) PlatformContext.getBean(FTP_UTIL);
        	ftpUtil.setIp(ip);
        	ftpUtil.setPort(port);
        	ftpUtil.setUsername(username);
        	ftpUtil.setPassword(password);
        	ftpUtil.setAuditLog(audit);
        }

        return ftpUtil.ftpGetFile(this.srcdirectory, this.srcfilename,
                this.checkfile, this.desdirectory, this.desfilename,
                this.repeat, this.repeatinterval);
    }

    private void checkValue(TBSYSFTPVO ftpVo) {
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

    public String ftpGetFile(Connection con, String ftpsettingid, TBSYSFTPVO ftpVo) throws Exception {
        valueCheck(ftpsettingid);
        init(con, ftpsettingid);
        return ftpGutFile(ftpVo);
    }

    private void init(Connection con, String ftpsettingid) throws Exception {
        getFTPInfo(con, ftpsettingid);
        getRemoteHostInfo(con, hostid);
    }

    public void getFTPInfo(Connection con, String ftpsettingid) throws Exception {
        if (logger.isInfoEnabled()) {
            logger.info("GET FTPSettingID = " + ftpsettingid);
        }
        String sql = "select * from tbsysftp where ftpsettingid=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, ftpsettingid);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            srcdirectory = rs.getString("srcdirectory");
            srcfilename = rs.getString("srcfilename");
            checkfile = rs.getString("checkfile");
            desdirectory = rs.getString("desdirectory");
            desfilename = rs.getString("desfilename");
            repeat = rs.getString("repeat");
            repeatinterval = rs.getString("repeatinterval");
            hostid = rs.getString("hostid");
        }
        convertFtpSystemVariable();
        if (srcdirectory == null) {
            throw new Exception("pf_ftp_common_002");
        }
    }

    public void getRemoteHostInfo(Connection con, String hostid) throws Exception {
        String sql = "select * from tbsysremotehost where hostid=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, hostid);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            ip = rs.getString("ip");
            port = rs.getInt("port");
            username = rs.getString("username");

            //password = rs.getString("password");
            password = new String(CryptoUtil.getInstance().
                    symmetricDecrypt(StringUtil.fromHex(rs.getString("password"))));
        }

        if (ip == null) {
            throw new Exception("Host ID " + hostid + " not Found");
        }
    }

    public String ftpPutFile(String ftpsettingid) throws Exception {
        return ftpPutFile(ftpsettingid, null);
    }

    public String ftpPutFile(Connection con, String ftpsettingid) throws Exception {
        return ftpPutFile(con, ftpsettingid, null);
    }

    public String ftpPutFile(String ftpsettingid, TBSYSFTPVO ftpVo) throws Exception {
        valueCheck(ftpsettingid);
        init(ftpsettingid);

        return ftpPutFile(ftpVo);
    }

    public String ftpPutFile(Connection con, String ftpsettingid, TBSYSFTPVO ftpVo) throws Exception {
        valueCheck(ftpsettingid);
        init(con, ftpsettingid);

        return ftpPutFile(ftpVo);
    }

    public TBSYSFTPVO getVo(String ftpsettingid) throws Exception {
        valueCheck(ftpsettingid);
        init(ftpsettingid);
        TBSYSFTPVO vo = new TBSYSFTPVO();
        vo.setHOSTID(hostid);
        vo.setSRCDIRECTORY(srcdirectory);
        vo.setSRCFILENAME(srcfilename);
        vo.setCHECKFILE(checkfile);
        vo.setDESDIRECTORY(desdirectory);
        vo.setDESFILENAME(desfilename);
        vo.setREPEAT(Integer.parseInt(repeat));
        vo.setREPEATINTERVAL(Integer.parseInt(repeatinterval));
        return vo;
    }

    private void valueCheck(String ftpsettingid) throws Exception {
        if (ftpsettingid == null || ftpsettingid.trim().length() == 0) {
            throw new Exception("pf_ftp_common_001");
        }
    }

    private void init(String ftpsettingid) throws Exception {
        getFTPInfo(ftpsettingid);
        getRemoteHostInfo(hostid);
    }

    public void getFTPInfo(String ftpsettingid) throws Exception {
        //TBSYSFTPVO tbsysftpvo=new TBSYSFTPVO();
        try {
            TBSYSFTPVO tbsysftpvo = (TBSYSFTPVO) dam.findByPKey(TBSYSFTPVO.TABLE_UID, ftpsettingid);
            srcdirectory = tbsysftpvo.getSRCDIRECTORY();
            srcfilename = tbsysftpvo.getSRCFILENAME();
            checkfile = tbsysftpvo.getCHECKFILE();
            desdirectory = tbsysftpvo.getDESDIRECTORY();
            desfilename = tbsysftpvo.getDESFILENAME();
            repeat = tbsysftpvo.getREPEAT().toString();
            repeatinterval = tbsysftpvo.getREPEATINTERVAL().toString();
            hostid = tbsysftpvo.getHOSTID();
            convertFtpSystemVariable();
        }
        catch (Exception e) {
            throw new JBranchException("pf_ftp_common_007");
        }
    }

    private void convertFtpSystemVariable() throws JBranchException {
        srcdirectory = convertSystemVariable(srcdirectory);
        srcfilename = convertSystemVariable(srcfilename);
        checkfile = convertSystemVariable(checkfile);
        desdirectory = convertSystemVariable(desdirectory);
        desfilename = convertSystemVariable(desfilename);
        repeat = convertSystemVariable(repeat);
        repeatinterval = convertSystemVariable(repeatinterval);
        hostid = convertSystemVariable(hostid);
    }

    private String convertSystemVariable(String var) throws JBranchException {
        if (var == null) {
            return null;
        }
        if (!var.matches("^\\{.+\\}$")) {
            return var;
        }
        String result = var;
        Object o = SysInfo.getInfoValue(var.replaceAll("^\\{(.+)\\}$", "$1"));
        if (o instanceof String) {
            result = (String) o;
        }
        return result;
    }

    public void getRemoteHostInfo(String hostid) throws Exception {
        TBSYSREMOTEHOSTVO tbsysremotehostVO = (TBSYSREMOTEHOSTVO) dam.findByPKey(TBSYSREMOTEHOSTVO.TABLE_UID, hostid);
        ip = tbsysremotehostVO.getIP();
        port = tbsysremotehostVO.getPORT().intValue();
        username = tbsysremotehostVO.getUSERNAME();
        password = new String(CryptoUtil.getInstance().
                symmetricDecrypt(StringUtil.fromHex(tbsysremotehostVO.getPASSWORD())));

        if (ip == null) {
            throw new Exception("Host ID " + hostid + " not Found");
        }
    }

    public void setAuditLog(AuditLogUtil audit) {
        this.audit = audit;
    }

    private String ftpPutFile(TBSYSFTPVO ftpVo) throws Exception {
        checkValue(ftpVo);
        if (ftpUtil == null) {
        	ftpUtil = (FTPUtil) PlatformContext.getBean(FTP_UTIL);
        	ftpUtil.setIp(ip);
        	ftpUtil.setPort(port);
        	ftpUtil.setUsername(username);
        	ftpUtil.setPassword(password);
        	ftpUtil.setAuditLog(audit);
        }

        return ftpUtil.ftpPutFile(this.srcdirectory, this.srcfilename,
                this.checkfile, this.desdirectory, this.desfilename);
    }
}
