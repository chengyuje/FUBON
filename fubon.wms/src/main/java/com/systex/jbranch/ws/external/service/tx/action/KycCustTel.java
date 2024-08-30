package com.systex.jbranch.ws.external.service.tx.action;

import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.fubon.commons.cbs.service.WMS032154Service;
import com.systex.jbranch.fubon.commons.esb.vo.wms032154.WMS032154OutputDetailsVO;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isBlank;

@Component("kycCustTelTxAction")
@Scope("prototype")
@SuppressWarnings({"rawtypes", "unchecked"})
public class KycCustTel extends TxAction {
    @Autowired
    private WMS032154Service wms032154Service;

    @Override
    public String execute(String custId) throws Exception {
        return isBlank(custId)? listData(): sync(custId);
    }

    private String sync(String custId) {
        return "To do";
    }

    private String listData() throws Exception {
        List<Map<String, String>> incorrectData =  findIncorrectData();
        if (incorrectData.isEmpty()) return "<result>查無資料</result>";

        interpreteTelInfo(incorrectData);
        getTelInfoFromTx(incorrectData);
        return getXml(incorrectData);
    }

    private String getXml(List<Map<String, String>> incorrectData) {
        StringBuilder sb = new StringBuilder();
        sb.append("<result>\n");
        sb.append("CUST_ID,KEY_DATE,CUST_TEL,DAY,TX_DAY,NIGHT,TX_NIGHT,TEL_NO,TX_TEL_NO,FAX,TX_FAX\n");
        for (Map<String, String> eachCustMap: incorrectData) {
            sb.append(eachCustMap.get("CUST_ID") + ",");
            sb.append(eachCustMap.get("CREATE_DATE") + ",");
            sb.append(eachCustMap.get("CUST_TEL") + ",");
            sb.append(eachCustMap.get("DAY") + ",");
            sb.append(eachCustMap.get("TX_DAY") + ",");
            sb.append(eachCustMap.get("NIGHT") + ",");
            sb.append(eachCustMap.get("TX_NIGHT") + ",");
            sb.append(eachCustMap.get("TEL_NO") + ",");
            sb.append(eachCustMap.get("TX_TEL_NO") + ",");
            sb.append(eachCustMap.get("FAX") + ",");
            sb.append(eachCustMap.get("TX_FAX") + "\n");
        }

        sb.append("</result>\n");
        return sb.toString();
    }

    /** 從電文取得日、夜、行動、傳真 **/
    private void getTelInfoFromTx(List<Map<String, String>> incorrectData) throws Exception {
        for (Map<String, String> eachCustMap: incorrectData) {
            WMS032154OutputDetailsVO detailsVO = wms032154Service.search(eachCustMap.get("CUST_ID")).getDetails().get(0);
            eachCustMap.put("TX_DAY", detailsVO.getResd_tel());
            eachCustMap.put("TX_NIGHT", detailsVO.getCon_tel());
            eachCustMap.put("TX_TEL_NO", detailsVO.getHandphone());
            eachCustMap.put("TX_FAX", detailsVO.getFax());
        }
    }

    /**
     *  解析電話欄位 TBKYC_INVESTOREXAM_D.CUST_TEL
     *  類型:日、夜、行動、傳真分別以「;」分隔。每一種以「:」分隔的資訊分別代表勾選類型、cod、電話
     * **/
    private void interpreteTelInfo(List<Map<String, String>> incorrectData) {
        final String[] groupLabels = {"DAY", "NIGHT", "TEL_NO", "FAX"};
        for (Map<String, String> eachCustMap: incorrectData) {
            String[] telGroup = eachCustMap.get("CUST_TEL").split(";");

            for(int groupIndex = 0; groupIndex < telGroup.length; groupIndex++){
                String [] detail = telGroup[groupIndex].split(":");
                // 將四種聯絡電話（0:日 1:夜 2:手機 3:傳真）放入原本的 map
                eachCustMap.put(groupLabels[groupIndex], detail.length >=3 ? detail[2]: "");
            }
        }
    }

    /** 尋找 Kyc 可能被改到異常電話的資料 **/
    private List<Map<String, String>> findIncorrectData() throws JBranchException {
        return Manager.manage(this.getDataAccessManager())
                .append("select * from ( ")
                .append("    select M_HIS.CUST_ID, TO_CHAR(M_HIS.CREATE_DATE, 'YYYY-MM-DD HH24:MI:SS') CREATE_DATE, D_HIS.CUST_TEL, ")
                .append("           ROW_NUMBER() over (partition by M_HIS.CUST_ID order by M_HIS.CREATE_DATE desc) RN ")
                .append("    from TBKYC_INVESTOREXAM_M_HIS M_HIS ")
                .append("    join TBKYC_INVESTOREXAM_D_HIS D_HIS ")
                .append("    on (M_HIS.SEQ = D_HIS.SEQ) ")
                .append("    where CREATE_DATE > to_date(:start, 'yyyyMMdd') ")
                .append("    and M_HIS.INVEST_BRANCH_NBR <> '999' ") // 臨櫃
                .append("    and D_HIS.CUST_TEL is not null ")
                .append(") where RN = 1 ")
                .put("start", "20201012")
                .query();
    }
}
