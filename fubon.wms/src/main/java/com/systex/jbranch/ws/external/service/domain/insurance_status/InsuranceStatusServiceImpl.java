package com.systex.jbranch.ws.external.service.domain.insurance_status;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.*;

@Service
public class InsuranceStatusServiceImpl
        extends FubonWmsBizLogic
        implements InsuranceStatusService {

    @Override
    public List<InsuranceStatusOutputVO> query(InsuranceStatusInputVO input) throws JBranchException {
        List<InsuranceStatusOutputVO> result = new ArrayList<>();

        List<Map<String, String>> data = getData(input);
        if (data.isEmpty())
            return result;

        for (Map<String, String> row: data) {
            InsuranceStatusOutputVO outputVO = new InsuranceStatusOutputVO();
            outputVO.setDocNum(row.get("INS_ID"));
            outputVO.setPolicyNum(row.get("POLICY_NUM"));
            outputVO.setApplyDate(row.get("APPLY_DATE"));
            outputVO.setHolderName(row.get("INSURED_NAME"));
            outputVO.setHolderIdentityNum(row.get("INSURED_ID"));
            outputVO.setInsuredName(row.get("PROPOSER_NAME"));
            outputVO.setInsuredIdentityId(row.get("CUST_ID"));
            outputVO.setProcessStatus(row.get("STATUS"));
            outputVO.setNotifyDate(row.get("NOTE_DT"));
            outputVO.setIssueDate(row.get("SEND_DATE"));
            outputVO.setBookingNum(row.get("REGISTER_NO"));
            outputVO.setInsuranceType(row.get("INSPRD_ID"));
            result.add(outputVO);
        }
        return result;
    }

    private List<Map<String, String>> getData(InsuranceStatusInputVO input) throws JBranchException {
        return Manager.manage(this.getDataAccessManager())
                .append("select M.INS_ID, ")
                .append("       case when M.POLICY_NO1 is null then null ")
                .append("       else concat(M.POLICY_NO1, '00') end as POLICY_NUM, ")
                .append("       TO_CHAR(M.APPLY_DATE, 'yyy/mm/dd', 'NLS_CALENDAR= ''ROC Official''') APPLY_DATE, ")
                .append("       M.INSURED_NAME, ")
                .append("       M.INSURED_ID, ")
                .append("       M.PROPOSER_NAME, ")
                .append("       M.CUST_ID, ")
                .append("       P.PARAM_NAME STATUS, ")
                .append("       TO_CHAR(TO_DATE(NUNAD.NOTE_DT, 'yyyymmdd'), 'yyy/mm/dd', 'NLS_CALENDAR= ''ROC Official''') NOTE_DT, ")
                .append("       TO_CHAR(NPOLM.SEND_DATE, 'yyy/mm/dd', 'NLS_CALENDAR= ''ROC Official''') SEND_DATE, ")
                .append("       NPOLM.REGISTER_NO, ")
                .append("       INS_MAIN.INSPRD_ID ")
                .append("from TBIOT_MAIN M ")
                .append("left join TBSYSPARAMETER P on ( P.PARAM_TYPE = 'IOT.MAIN_STATUS' and P.PARAM_CODE = M.STATUS ) ")
                .append("left join TBPMS_NUNAD NUNAD on (NUNAD.POLICY_NO = M.POLICY_NO1) ")
                .append("left join TBCRM_NPOLM NPOLM on (NPOLM.POLICY_NO = M.POLICY_NO1) ")
                .append("left join TBPRD_INS_MAIN INS_MAIN on (M.INSPRD_KEYNO = INS_MAIN.INSPRD_KEYNO) ")
                .append("where 1=1 ")
                // 文件編號
                .condition(isNotBlank(input.getDocNum()),"and M.INS_ID = :insId ", "insId", input.getDocNum())
                // 保單號碼（配合稽核，傳入參數後面將會多幾碼，這裡調整為 10 碼來查詢）
                .condition(isNotBlank(input.getPolicyNum()),
                        "and M.POLICY_NO1 = :policyNum ", "policyNum", substring(input.getPolicyNum(), 0, 10))
                // 業務員員編
                .condition(isNotBlank(input.getSalesEmployeeId()), "and M.RECRUIT_ID = :recruitId ", "recruitId", input.getSalesEmployeeId())
                // 被保險人
                .condition(isNotBlank(input.getHolderName()), "and M.INSURED_NAME = :holderName ", "holderName", input.getHolderName())

                // 申請時間（起訖範圍）
                .append("and M.APPLY_DATE between ")
                // 有起訖參數時
                .condition(isNotBlank(input.getQueryDateStart()),
                        "TO_DATE(:start, 'yyy/mm/dd', 'NLS_CALENDAR= ''ROC Official''') ", "start", input.getQueryDateStart())
                .condition(isNotBlank(input.getQueryDateEnd()),
                        "and TO_DATE(:end, 'yyy/mm/dd', 'NLS_CALENDAR= ''ROC Official''') ", "end", input.getQueryDateEnd())
                // 無起訖參數時，區間為 = 預設三個月前 ~ 今日
                .append(isBlank(input.getQueryDateStart())? "trunc(add_months(sysdate, -3), 'DD') ": "")
                .append(isBlank(input.getQueryDateEnd())? "and trunc(sysdate, 'DD') ": "")
                .query();
    }
}
