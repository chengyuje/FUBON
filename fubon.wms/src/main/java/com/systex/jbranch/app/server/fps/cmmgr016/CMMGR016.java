package com.systex.jbranch.app.server.fps.cmmgr016;

import com.systex.jbranch.fubon.bth.ftp.BthFtpJobUtil;
import com.systex.jbranch.fubon.bth.job.business_logic.ReportConfig;
import com.systex.jbranch.fubon.bth.job.business_logic.RptCommander;
import com.systex.jbranch.fubon.bth.job.business_logic.RptResolver;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Clob;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 報表內容設定
 *
 * @author Eli
 * @date 20180727
 * @date 20181026 新增修改 增加檔案編碼與雙引號欄位
 * @date 20181102 RPT_SQL DB資料型態改為CLOB，修改因應邏輯
 * @date 20190502 新增驗證檔邏輯
 * @date 20190705 將 FTP 資訊設定、Mail 參數設定、Job 設定連結到報表功能，使得報表的設定能夠連貫一體化
 *
 *
 */
@Component("cmmgr016")
@Scope("request")
@SuppressWarnings({"rawtypes", "unchecked"})
public class CMMGR016 extends FubonWmsBizLogic {

    private ReportConfig configVO;
    private CMMGR016InputVO inputVO;

    @Autowired
    private CMMGR016OutputVO returnVO;

    /**
     * 設定 ReportConfig
     */
    private void setReportConfig(Object body) {
        configVO = (ReportConfig) body;
    }

    /**
     * 設定 InputVO
     */
    private void setInputVO(Object body) {
        inputVO = (CMMGR016InputVO) body;
    }

    /**
     * 查詢方法
     *
     * @param body
     * @param header
     * @throws DAOException
     * @throws JBranchException
     */
    public void inquire(Object body, IPrimitiveMap header) throws Exception {
        setInputVO(body);

        StringBuffer sql = new StringBuffer();
        Map params = new HashMap();
        prepareInquireStatement(sql, params);

        returnVO.setResultList(exeQueryForMap(sql.toString(), params));
        sendRtnObject(returnVO);
    }

    /**
     * 準備查詢語法
     **/
    private void prepareInquireStatement(StringBuffer sql, Map params) {
        sql.append("select R.RPT_CODE, R.RPT_NAME, R.RPT_TYPE, R.RPT_DESC, R.FILE_CODING, ")
           .append("       R.CHK_FILE_NAME, F.FTPSETTINGID, P.PARAM_TYPE, J.JOBID ")
           .append("from TBSYSRPTSENDER R ")
           .append("left join TBSYSFTP F ")
           .append("on 'RPT.' || R.RPT_CODE = F.FTPSETTINGID ")
           .append("left join TBSYSPARAMTYPE P ")
           .append("on 'RPT.' || R.RPT_CODE = P.PARAM_TYPE ")
           .append("left join TBSYSSCHDJOB J ")
           .append("on 'RPT.' || R.RPT_CODE = J.JOBID ")
           .append("where 1=1 ");

        if (StringUtils.isNotBlank(inputVO.getCode())) {
            sql.append("and R.RPT_CODE like :code ");
            params.put("code", "%" + inputVO.getCode().trim() + "%");
        }

        if (StringUtils.isNotBlank(inputVO.getName())) {
            sql.append("and R.RPT_NAME like :name ");
            params.put("name", "%" + inputVO.getName().trim() + "%");
        }

        if (StringUtils.isNotBlank(inputVO.getType())) {
            sql.append("and R.RPT_TYPE = :type ");
            params.put("type", inputVO.getType());
        }

        /** Filter **/
        if (StringUtils.isNotBlank(inputVO.getCheck()))
            sql.append("and R.CHK_FILE_NAME is not null ");

        if (StringUtils.isNotBlank(inputVO.getFtp()))
            sql.append("and F.FTPSETTINGID is not null ");

        if (StringUtils.isNotBlank(inputVO.getMail()))
            sql.append("and P.PARAM_TYPE is not null ");

        if (StringUtils.isNotBlank(inputVO.getJob()))
            sql.append("and J.JOBID is not null ");

        sql.append("order by R.CREATETIME desc ");
    }

    /**
     * 查詢詳細資料（報表、FTP、MAIL、JOB）
     * @param body
     * @param header
     * @throws Exception
     */
    public void detail(Object body, IPrimitiveMap header) throws Exception {
        setInputVO(body);

        Map data = (Map) (exeQueryForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
                /** 報表 **/
                .append("select R.RPT_CODE, R.RPT_NAME, R.RPT_TYPE, R.RPT_DESC, R.FILE_CODING, ")
                .append("       R.DELIMITER, R.NEED_HEADER, R.SQL_TYPE, R.FIX_WIDTH ,R.NEED_DOUBLE_QUOTES, ")
                .append("       R.RPT_SQL, R.CHK_FILE_NAME, R.CHK_NAME, R.CHK_DATE_FORMAT, R.CHK_WIDTH, ")
                /** FTP **/
                .append("       F.FTPSETTINGID, F.SRCDIRECTORY, F.SRCFILENAME, F.CHECKFILE, F.DESDIRECTORY, ")
                .append("       F.DESFILENAME, F.REPEAT, F.REPEATINTERVAL, F.HOSTID, F.SRCDELETE, ")
                /** MAIL **/
                .append("       P.PARAM_TYPE, P.PTYPE_NAME, P.PTYPE_DESC, P.PTYPE_BUSS, ")
                /** JOB **/
                .append("       J.JOBID, J.JOBNAME, J.DESCRIPTION, J.PRECONDITION, J.BEANID, J.PARAMETERS, J.POSTCONDITION ")
                .append("from TBSYSRPTSENDER R ")
                .append("left join TBSYSFTP F ")
                .append("on 'RPT.' || R.RPT_CODE = F.FTPSETTINGID ")
                .append("left join TBSYSPARAMTYPE P ")
                .append("on 'RPT.' || R.RPT_CODE = P.PARAM_TYPE ")
                .append("left join TBSYSSCHDJOB J ")
                .append("on 'RPT.' || R.RPT_CODE = J.JOBID ")
                .append("where R.RPT_CODE = :code ")
                .toString())
                .setObject("code", inputVO.getCode())).get(0));

        /** Clob => String **/
        RptCommander rptCommander = (RptCommander) PlatformContext.getBean("rptCommander");
        String sql = rptCommander.getSqlFromClob((Clob) data.get("RPT_SQL"));
        data.remove("RPT_SQL");
        data.put("RPT_SQL", sql);

        List result = new ArrayList();
        result.add(data);
        returnVO.setResultList(result);

        /** Report 對於 Mail 參數為一對多關係，故另外查詢放置另個 List  **/
        returnVO.setCodeData(exeQueryForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
                .append("select PARAM_TYPE, PARAM_CODE, PARAM_NAME, PARAM_ORDER, PARAM_DESC from TBSYSPARAMETER ")
                .append("where PARAM_TYPE = :type order by PARAM_ORDER ")
                .toString())
                .setObject("type", "RPT." + inputVO.getCode())));
        sendRtnObject(returnVO);
    }


    /**
     * 新增方法
     *
     * @param body
     * @param header
     * @throws DAOException
     * @throws JBranchException
     */
    public void insert(Object body, IPrimitiveMap header) throws DAOException, JBranchException {
        setReportConfig(body);

        StringBuffer sql = new StringBuffer();
        Map params = new HashMap();
        prepareInsertStatement(sql, params);
        exeUpdateForMap(sql.toString(), params);
        sendRtnObject(null);
    }

    /**
     * 準備新增語法
     **/
    private void prepareInsertStatement(StringBuffer sql, Map params) throws JBranchException {
        sql.append("insert into TBSYSRPTSENDER(RPT_CODE, RPT_SQL, RPT_NAME, RPT_DESC, RPT_TYPE, DELIMITER, FIX_WIDTH, ")
                .append("NEED_HEADER, SQL_TYPE, NEED_DOUBLE_QUOTES, FILE_CODING, VERSION, CREATETIME, CREATOR, ")
                .append("CHK_FILE_NAME, CHK_NAME, CHK_DATE_FORMAT, CHK_WIDTH) ")
                .append("values ( :code, :sql, :name, :desc, :type, :delimiter, :width, :needHeader, :sqlType, :needDoubleQuotes, ")
                .append(":fileCoding, 1, :createtime, :creator, :chkFileName, :chkName, :chkDateFormat, :chkWidth) ");

        getMainParam(params);
        params.put("creator", SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID));
        params.put("createtime", new Timestamp(System.currentTimeMillis()));
    }

    /**
     * 取得主要參數
     **/
    private void getMainParam(Map params) {
        params.put("code", configVO.getCode());
        params.put("sql", configVO.getSql());
        params.put("name", configVO.getName());
        params.put("desc", configVO.getDesc());
        params.put("type", configVO.getType());
        params.put("delimiter", configVO.getDelimiter());
        params.put("width", configVO.getWidth());
        params.put("needHeader", configVO.getNeedHeader());
        params.put("sqlType", configVO.getSqlType());
        params.put("needDoubleQuotes", configVO.getNeedDoubleQuotes());
        params.put("fileCoding", configVO.getFileCoding());
        params.put("chkFileName", configVO.getChkFileName());
        params.put("chkName", configVO.getChkName());
        params.put("chkDateFormat", configVO.getChkDateFormat());
        params.put("chkWidth", configVO.getChkWidth());
    }

    /**
     * 修改方法
     *
     * @param body
     * @param header
     * @throws DAOException
     * @throws JBranchException
     */
    public void update(Object body, IPrimitiveMap header) throws DAOException, JBranchException {
        setReportConfig(body);

        StringBuffer sql = new StringBuffer();
        Map params = new HashMap();
        prepareUpdateStatement(sql, params);
        exeUpdateForMap(sql.toString(), params);
        sendRtnObject(null);
    }

    /**
     * 準備更新語法
     **/
    private void prepareUpdateStatement(StringBuffer sql, Map params) throws JBranchException {
        sql.append("update TBSYSRPTSENDER set RPT_SQL = :sql, RPT_NAME = :name, RPT_TYPE = :type, RPT_DESC = :desc, ")
                .append("DELIMITER = :delimiter, NEED_HEADER = :needHeader, SQL_TYPE = :sqlType, FIX_WIDTH= :width, ")
                .append("NEED_DOUBLE_QUOTES = :needDoubleQuotes, FILE_CODING = :fileCoding, MODIFIER = :modifier, LASTUPDATE = :lastUpdate, ")
                .append("CHK_FILE_NAME = :chkFileName, CHK_NAME = :chkName, CHK_DATE_FORMAT = :chkDateFormat, CHK_WIDTH = :chkWidth where RPT_CODE = :code");

        getMainParam(params);
        params.put("modifier", SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID));
        params.put("lastUpdate", new Timestamp(System.currentTimeMillis()));
    }


    /**
     * 刪除報表
     *
     * @param body
     * @param header
     * @throws DAOException
     * @throws JBranchException
     */
    public void delete(Object body, IPrimitiveMap header) throws DAOException, JBranchException {
        setInputVO(body);

        StringBuffer sql = new StringBuffer();
        Map params = new HashMap();
        prepareDeleteStatement(sql, params);
        exeUpdateForMap(sql.toString(), params);
        sendRtnObject(null);
    }

    /**
     * 準備刪除語法
     **/
    private void prepareDeleteStatement(StringBuffer sql, Map params) {
        sql.append("delete from TBSYSRPTSENDER where RPT_CODE = :code ");
        params.put("code", inputVO.getCode());
    }

    /**
     * 下載報表
     *
     * @param body
     * @param header
     * @throws Exception
     */
    public void download(Object body, IPrimitiveMap header) throws Exception {
        setInputVO(body);
        List<Map> data = exeQueryForQcf(genDefaultQueryConditionIF()
                .setQueryString("select * from TBSYSRPTSENDER where RPT_CODE = :code ")
                .setObject("code", inputVO.getCode()));

        RptResolver resolver = (RptResolver) PlatformContext.getBean("rptResolver");
        resolver.setConfig(configureCfgVO(data.get(0)));

        for (String file : resolver.generate())
            notifyClientToDownloadFile(file, resolver.getFormattedFileName());
    }

    /** 將 DB 資料配置到 ReportConfigVO **/
    private ReportConfig configureCfgVO(Map<String, Object> data) throws Exception {
        RptCommander rptCommander = (RptCommander) PlatformContext.getBean("rptCommander");
        return rptCommander.getReportConfigFromDB(data);
    }

    /** 取得格式化的檔案名稱 **/
    public void getRealName(Object body, IPrimitiveMap header) throws Exception {
        setInputVO(body);
        String msg;
        try {
            msg = new BthFtpJobUtil().getRealFileName(inputVO.getName());
        } catch (Exception e) {
            msg = "error";
        }

        sendRtnObject(msg);
    }
}
