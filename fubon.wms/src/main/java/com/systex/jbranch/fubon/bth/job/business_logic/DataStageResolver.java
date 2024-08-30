package com.systex.jbranch.fubon.bth.job.business_logic;


import com.systex.jbranch.fubon.bth.job.CMS_MON_OPNCNT.CmsMonStoredSQL;
import com.systex.jbranch.fubon.bth.job.CMS_OPENCNTER.CmsOpncnterStoredSQL;
import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import static com.systex.jbranch.fubon.bth.job.sevEntity.IntegrationService.exeQuerySQLServerBNK;
import static com.systex.jbranch.fubon.bth.job.sevEntity.IntegrationService.exeUpdateOracleWMS;

@Component
@Scope("prototype")
public class DataStageResolver {
    /** 從叫號系統取得資料源寫到新理專系統 **/
    private void dataFromBnkToWms(String sourceBnk, String truncateSql, String insertSql, String updateSql, String[] insertParam, String[] updateParam) throws Exception {
        final int LIMIT = 10000;

        List<Map<String, Object>> cntResult =
                exeQuerySQLServerBNK(RptResolver.COUNT_SQLSERVER_SQL.replace("#sql", sourceBnk));
        int dataCount = Integer.valueOf(cntResult.get(0).get("CNT").toString());

        boolean first = true;
        for (int index = 0; index <= dataCount; index += LIMIT) {
            List list = exeQuerySQLServerBNK(RptResolver.getGroupSQL(index, LIMIT, sourceBnk, RptResolver.GROUP_SQLSERVER_SQL));

            Vector sqls = new Vector();
            sqls.add(first? truncateSql: null);
            sqls.add(insertSql);
            sqls.add(updateSql);
            sqls.add(insertParam);
            sqls.add(updateParam);

            exeUpdateOracleWMS(sqls,  AccessContext.dataTransformedToStringOfList(list, insertParam));
            first = false;
        }
    }

    //DataStage Sequence CMS_MON_OPNCNT=============================================================
    /*
     * 201306100379-00每月初固定產出開戶平均服務時間   ,
     * 計算每日一般客戶來客數 ==> 寫入PS_CMS_BUSYDAY  SYC  ,
     * 每月以前6大,當峰日, 濾掉筆數小於1000筆者,當正常營業日    20130703  SYC
     */
    public void doCMSMonOpncnt00() throws Exception {
        dataFromBnkToWms(CmsMonStoredSQL.introduceSourceToPsCmsBusyDaySQL,
                CmsMonStoredSQL.truncatePsCmsBusyDaySQL,
                CmsMonStoredSQL.insertPsCmsBusyDaySQL,
                null,
                CmsMonStoredSQL.insertPsCmsBusyDayParam,
                null);
    }

    //20130703 計算各分行開櫃數 LOW DATA ==> 寫入PS_SA_NUMBERDATA_TMP (須每月十日前重跑,月份才不會推錯)
    public void doCMSMonOpncnt01() throws Exception {
        dataFromBnkToWms(CmsMonStoredSQL.introduceSourceToPsSaNumberDataTmpSQL,
                CmsMonStoredSQL.truncatePsSaNumberDataTmpSQL,
                CmsMonStoredSQL.insertPsSaNumberDataTmpSQL,
                null,
                CmsMonStoredSQL.insertPsSaNumberDataTmpParam,
                null);
    }

    //201306100379-00  每月初固定產出開戶平均服務時間  LOW DATA   20130617 SYC  寫入PS_SA_OPENACC_SOURCE
    public void doCMSOpenAcc1() throws Exception {
        dataFromBnkToWms(CmsMonStoredSQL.introduceSourceToPsSaOpenaccSourceSQL,
                CmsMonStoredSQL.truncatePsSaOpenaccSourceSQL,
                CmsMonStoredSQL.insertPsSaOpenaccSourceSQL,
                null,
                CmsMonStoredSQL.insertPsSaOpenaccSourceParam,
                null);
    }

    //DataStage Sequence CMS_OPENCNTER_S=============================================================
    //20130315 計算各分行開櫃數
    public void doCMSOPENCNTER1() throws Exception {
        dataFromBnkToWms(CmsOpncnterStoredSQL.introduceSourceToPsSaNumberdataTmp1SQL,
                CmsOpncnterStoredSQL.truncatePsSaNumberdataTmp1SQL,
                CmsOpncnterStoredSQL.insertPsSaNumberdataTmp1SQL,
                null,
                CmsOpncnterStoredSQL.insertPsSaNumberdataTmp1Param,
                null);
    }

    //20130315 匯入分行每十分鐘各服務類型開櫃數
    public void doCMSOPENCNTER2() throws Exception {
        dataFromBnkToWms(CmsOpncnterStoredSQL.introduceSourcePsSaMachcntservstTmpSQL,
                CmsOpncnterStoredSQL.truncatePsSaMachcntservstTmpSQL,
                CmsOpncnterStoredSQL.insertPsSaMachcntservstTmpSQL,
                null,
                CmsOpncnterStoredSQL.insertPsSaMachcntservstTmpParam,
                null);
    }

    /*
     * 201409170105-02 每日/每月固定產出每家分行個人金融,就學貸款,之來客數,平均服務時間及平均等待時間報表 - 產出lowdata   20141029  syc
     * */
    public void doCMSOPENCNTER5() throws Exception {
        dataFromBnkToWms(CmsOpncnterStoredSQL.introduceSourcePsSaNumberdataTmp2SOL,
                CmsOpncnterStoredSQL.truncatePsSaNumberdataTmp1SQL,
                CmsOpncnterStoredSQL.insertPsSaNumberdataTmp2SQL,
                null,
                CmsOpncnterStoredSQL.insertPsSaNumberdataTmp2Param,
                null);
    }
}
