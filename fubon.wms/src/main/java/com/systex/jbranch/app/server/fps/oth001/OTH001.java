package com.systex.jbranch.app.server.fps.oth001;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * For 平測模式所需邏輯
 */
@Component("oth001")
@Scope("request")
public class OTH001 extends FubonWmsBizLogic {
    public void exec(Object body, IPrimitiveMap header) throws JBranchException {
        OTH001InputVO inputVO = (OTH001InputVO) body;
        OTH001OutputVO outputVO = new OTH001OutputVO();
        if ("crm211".equals(inputVO.getTxn())) {
            switch (inputVO.getMethod()) {
                case "queryAocodes": // 查詢登入者可視的 AO_CODE
                    queryAocodes(inputVO, outputVO);
                    break;
//                case "inquireCustForEmp": // 查詢登入 ID 轄下客戶
//                    outputVO.setResult(queryCustOfIDByAocode(inputVO.getAocode()));
//                    break;
            }
        }
        this.sendRtnObject(outputVO);
    }

    private void queryAocodes(OTH001InputVO inputVO, OTH001OutputVO outputVO) throws JBranchException {
        List<Map<String, Object>> data = queryAocodes(inputVO.getId());
        if (data.isEmpty() ||
                StringUtils.isBlank(ObjectUtils.toString(data.get(0).get("AO_CODE"))))
            throw new APException("查無 " + inputVO.getId() + " AO CODE 資訊");

        outputVO.setResult(data);
    }

    private List<Map<String, Object>> queryCustOfIDByAocode(String aocode) throws JBranchException {
        return Manager.manage(this.getDataAccessManager())
                .append("select CUST_ID, CUST_NAME from TBCRM_CUST_MAST where AO_CODE = :aocode ")
                .put("aocode", aocode)
                .query();
    }

    private List<Map<String, Object>> queryAocodes(String id) throws JBranchException {
        return Manager.manage(this.getDataAccessManager())
                .append("select AO_CODE, AO_NAME from ID_AO_MAP_JSB where 1=1 ")
                // 使用虛擬員編進行登入視為管理者，可以查詢所有日盛的 AO_CODE
                .condition(!id.equals(getVirtualEmpID()), "and AO_ID=:id ", "id", id)
                .query();
    }

    /**
     *
     * 是否允許使用臨時 ID 登入系統
     *
     * @param loginId 登入 ID
     * @return
     * @throws JBranchException
     */
    public boolean allowLoginByTempID(String loginId) throws JBranchException {
        boolean isFcJSB = CollectionUtils.isNotEmpty(queryAocodes(loginId)); // 只允許專員
        boolean allowLoginByTempID = allowLoginByTempID();

        if (!allowLoginByTempID && isFcJSB)
            throw new APException("很抱歉，目前不開放使用日盛員工 ID 進行登入系統！");

        return allowLoginByTempID && isFcJSB;
    }

    public List<Map<String, Object>> queryTempEmpInfo(String loginId) throws JBranchException {
        if (allowLoginByTempEmpID())
            return Manager.manage(this.getDataAccessManager())
                .append("select * from TBORG_JSB_MEMBER where EMP_ID = :loginId ")
                .put("loginId", loginId)
                .query();
        return null;
    }

    /**
     * 是否開啟可使用臨時 ID 登入系統
     **/
    private boolean allowLoginByTempID() {
        return "Y".equals(getTestModeParameters().get("ALLOW_LOGIN_BY_TEMP_ID"));
    }

    /**
     * 是否開啟臨時員編（還未納入員工檔）登入功能
     **/
    public boolean allowLoginByTempEmpID() {
        return "Y".equals(getTestModeParameters().get("ALLOW_LOGIN_BY_TEMP_EMP_ID"));
    }

    private Map<String, Object> getTestModeParameters() {
        Map<String, Object> pairs = new HashMap<>();
        try {
            List<Map<String, Object>> parameters = Manager.manage(this.getDataAccessManager())
                    .append("select * from TBSYSPARAMETER ")
                    .append("where PARAM_TYPE = 'OTH001' ")
                    .query();
            if (parameters.isEmpty())
                return pairs;
            for (Map<String, Object> each : parameters) {
                pairs.put(
                        ObjectUtils.toString(each.get("PARAM_CODE")).trim(),
                        ObjectUtils.toString(each.get("PARAM_NAME")).trim());
            }
        } catch (JBranchException e) {
            logger.error(e.toString());
        }
        return pairs;
    }

    /**
     * 臨時員工將透過該虛擬帳號作為登入系統的途徑
     **/
    public String getVirtualEmpID() {
        return ObjectUtils.toString(getTestModeParameters().get("VIRTUAL_EMP_ID"), "000000");
    }

    /** 跳過 KYC LDAP 檢核 **/
    public boolean skipKycLdap() {
        return "Y".equals(getTestModeParameters().get("SKIP_KYC_LDAP"));
    }

    /** 跳過新增轉介的 Mail SMTP  **/
    public boolean skipRefMail() {
        return "Y".equals(getTestModeParameters().get("SKIP_REF_MAIL"));
    }

    /** 跳過議價M+主管覆核通知 **/
    public boolean skipMPlusBargainAuth() {
        return "Y".equals(getTestModeParameters().get("SKIP_MPlus_BARGAIN_AUTH"));
    }

    /**
     * 當錄音序號平測模式開啟時，全商品的共用序號功能將開啟
     * 當待驗證的錄音序號與共用序號相同時，則不須檢核錄音序號
     *
     * @param recSeq 待驗證的錄音序號
     */
    public boolean voiceRecordTestModePass(String recSeq) {
        String combination = ObjectUtils.toString(getTestModeParameters().get("VOICE_RECORDING_TEST_MODE"));
        String[] arr = combination.split(",");
        return arr.length == 2
                && "Y".equals(arr[0])
                && StringUtils.defaultString(recSeq).equals(arr[1]);
    }
}
