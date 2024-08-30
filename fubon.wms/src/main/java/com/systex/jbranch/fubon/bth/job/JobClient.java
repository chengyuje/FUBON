package com.systex.jbranch.fubon.bth.job;

import com.systex.jbranch.fubon.bth.code.service.CoderService;
import com.systex.jbranch.fubon.bth.ftp.BthFtpJobUtil;
import com.systex.jbranch.fubon.bth.job.business_logic.DataStageResolver;
import com.systex.jbranch.fubon.bth.job.business_logic.PckCreator;
import com.systex.jbranch.fubon.bth.job.business_logic.RptCommander;
import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.scheduler.SchedulerHelper;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.List;
import java.util.Map;

/**
 * 無特定模組需由Job來設定並執行之邏輯代理者
 *
 * @author Eli
 * @date 2017/10/04 翻寫DataStage中的Job
 * @date 2018/01/18 根據DB參數將報表寄送給相關User
 * @date 2018/08/06 DataStage 產出檔案由 CMMGR016 報表內容設定來維護
 * @date 2018/08/24 重構產出報表與寄送報表
 * @date 2019/01/08 genRpt添加 fptPutCode邏輯
 *
 * @author SamTu
 * @date 2018/01/11 新增手動創建package {@link JobClient#createPck()}
 */
@Component("JobClient")
@SuppressWarnings({"rawtypes", "unchecked"})
public class JobClient extends BizLogic {

    /**
     * 取得 {@link DataStageResolver }
     **/
    private DataStageResolver getDataStageResolver() throws JBranchException {
        return (DataStageResolver) PlatformContext.getBean("dataStageResolver");
    }

    /* DataStage 塞資料到新理專 START ============================================ */
    public void doCMSMonOpncnt00() throws Exception {
        getDataStageResolver().doCMSMonOpncnt00();
    }

    public void doCMSMonOpncnt01() throws Exception {
        getDataStageResolver().doCMSMonOpncnt01();
    }

    public void doCMSOpenAcc1() throws Exception {
        getDataStageResolver().doCMSOpenAcc1();
    }

    public void doCMSOPENCNTER1() throws Exception {
        getDataStageResolver().doCMSOPENCNTER1();
    }

    public void doCMSOPENCNTER2() throws Exception {
        getDataStageResolver().doCMSOPENCNTER2();
    }

    public void doCMSOPENCNTER5() throws Exception {
        getDataStageResolver().doCMSOPENCNTER5();
    }
    /* DataStage 塞資料到新理專 END ============================================ */


    /**
     * 排程手動新增pck
     *
     * @throws IOException
     * @throws JBranchException
     */
    public void createPck() throws IOException, JBranchException {
        PckCreator creator = (PckCreator) PlatformContext.getBean("pckCreator");
        creator.createPackage();
    }

    /**
     * 檢查參數
     **/
    private void checkArg(String arg) throws APException {
        if (StringUtils.isBlank(arg)) throw new APException("無設置參數 arg !");
    }

    /**
     * 寄送報表
     *
     * @param body
     * @param header
     * @throws Exception
     */
    public void mailRpt(Object body, IPrimitiveMap<?> header) throws Exception {
        String mailType = (String) (((Map) ((Map) body).get(SchedulerHelper.JOB_PARAMETER_KEY)).get("arg"));
        checkArg(mailType);
        RptCommander rptCommander = (RptCommander) PlatformContext.getBean("rptCommander");
        rptCommander.mailReport(mailType);
    }

    /**
     * 如果報表有資料，才寄送。
     */
    public void mailRptIfHasData(Object body, IPrimitiveMap<?> header) throws Exception {
        String mailType = (String) (((Map) ((Map) body).get(SchedulerHelper.JOB_PARAMETER_KEY)).get("arg"));
        checkArg(mailType);

        // mailType 慣例為 RPT.自動化報表代號。這裡利用代號取得自動化報表的 SQL，再判斷該語法的資料總數是否大於零
        if (reportHasData(mailType.substring(4))) {
            RptCommander rptCommander = (RptCommander) PlatformContext.getBean("rptCommander");
            rptCommander.mailReport(mailType);
        }
    }

    private boolean reportHasData(String rptCode) throws Exception {
        String sql;
        return StringUtils.isNotBlank(sql = getRptSql(rptCode)) &&
                getRptDataCounts(sql).compareTo(new BigDecimal(0)) > 0;
    }

    private BigDecimal getRptDataCounts(String sql) throws JBranchException {
        String countSql = "select count(1) CNT from (" + sql + ")";
        List<Map<String, BigDecimal>> data = Manager.manage(this.getDataAccessManager())
                .append(countSql)
                .query();
        return data.get(0).get("CNT");
    }

    private String getRptSql(String rptCode) throws Exception {
        List<Map<String, Object>> data = Manager.manage(this.getDataAccessManager())
                .append("select RPT_SQL from TBSYSRPTSENDER where RPT_CODE = :rptCode ")
                .put("rptCode", rptCode)
                .query();
        return data.isEmpty() ? "" : PlatformContext.getBean(RptCommander.class)
                .getSqlFromClob((Clob) data.get(0).get("RPT_SQL"));
    }

    /**
     * 產出報表(arg給定)
     * 利用指定的加解密實作方法加密報表(ftpCode、coder如有)
     * 放置指定位置(ftpPutCode如有)
     *
     * @param body
     * @param header
     * @throws Exception
     */
    public void genRpt(Object body, IPrimitiveMap<?> header) throws Exception {
        Map<String, String> inputMap = ((Map<String, Map>) body).get(SchedulerHelper.JOB_PARAMETER_KEY);
        checkArg(inputMap.get("arg"));
        RptCommander rptCommander = (RptCommander) PlatformContext.getBean("rptCommander");
        rptCommander.generateReport(inputMap.get("arg"));

        String ftpCode;
        String coder;
        if (StringUtils.isNotBlank(ftpCode = inputMap.get("ftpCode")) &&
                StringUtils.isNotBlank(coder = inputMap.get("coder")))
            PlatformContext.getBean(coder, CoderService.class).execute(ftpCode);

		if (StringUtils.isNotBlank(inputMap.get("ftpPutCode")))
			new BthFtpJobUtil().ftpPutFile(inputMap.get("ftpPutCode"));
    }
}
