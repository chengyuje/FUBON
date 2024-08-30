package com.systex.jbranch.app.server.fps.mao131;

import com.systex.jbranch.app.common.fps.table.TBCAM_CAL_SALES_TASKVO;
import com.systex.jbranch.app.common.fps.table.TBMAO_DEV_APL_PLISTVO;
import com.systex.jbranch.app.server.fps.crm121.CRM121;
import com.systex.jbranch.app.server.fps.fps200.FPS200;
import com.systex.jbranch.app.server.fps.fps210.FPS210;
import com.systex.jbranch.app.server.fps.ins810.INS810;
import com.systex.jbranch.app.server.fps.ins810.INS810InputVO;
import com.systex.jbranch.app.server.fps.sot701.CustKYCDataVO;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.esb.vo.fp032151.FP032151OutputVO;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.conversation.message.EnumTiaHeader;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.server.mail.FubonMail;
import com.systex.jbranch.platform.server.mail.FubonSendJavaMail;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author walalala
 * @date 2016/07/26
 */
@Component("mao131")
@Scope("request")
public class MAO131 extends FubonWmsBizLogic {

    private DataAccessManager dam = null;
    SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");
    @Autowired
    @Qualifier("fps200")
    private FPS200 fps200;
    @Autowired
    @Qualifier("fps210")
    private FPS210 fps210;
    @Autowired
    @Qualifier("crm121")
    private CRM121 crm121;

    public void inquire(Object body, IPrimitiveMap header) throws JBranchException{
        MAO131InputVO inputVO = (MAO131InputVO) body;
        MAO131OutputVO outputVO = new MAO131OutputVO();
        dam = this.getDataAccessManager();

        QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT E.BRANCH_NBR, E.AO_CODE, E.EMP_NAME, P.USE_DATE, P.VISIT_CUST_LIST, ");
        sql.append("       SUBSTR(P.USE_PERIOD_S_TIME, 1, 2) || ':00' AS START_TIME, ");
        sql.append("       CASE WHEN TO_NUMBER(SUBSTR(P.USE_PERIOD_E_TIME, 1, 2)) < 9 THEN '次日' ELSE '' END || SUBSTR(P.USE_PERIOD_E_TIME, 1, 2) || ':00' AS END_TIME, ");
        sql.append("       P.DEV_NBR, ");
        sql.append("       P.DEV_STATUS, P.APL_EMP_ID, P.SEQ ");
        sql.append("FROM TBMAO_DEV_APL_PLIST P, VWORG_BRANCH_EMP_DETAIL_INFO E ");
        sql.append("WHERE 1 = 1 ");
        sql.append("AND P.APL_EMP_ID = E.EMP_ID ");
        sql.append("AND P.DEV_STATUS IN ('B04') ");
        sql.append("AND NOT EXISTS (SELECT DISTINCT UEMP_ID FROM TBORG_CUST_UHRM_PLIST UP WHERE P.APL_EMP_ID = UP.UEMP_ID) ");

        if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getBranch_area_id())) {
            sql.append("AND E.REGION_CENTER_ID = :regionCenterID "); // 區域代碼
            queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
        } else {
            sql.append("AND E.REGION_CENTER_ID IN (:regionCenterIDList) ");
            queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
        }

        if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
            sql.append("AND E.BRANCH_AREA_ID = :branchAreaID "); // 營運區代碼
            queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
        } else {
            sql.append("AND E.BRANCH_AREA_ID IN (:branchAreaIDList) ");
            queryCondition.setObject("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
        }

        if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && Integer.valueOf(inputVO.getBranch_nbr()) > 0) {
            sql.append("AND E.BRANCH_NBR = :branchID "); // 分行代碼
            queryCondition.setObject("branchID", inputVO.getBranch_nbr());
        } else {
            sql.append("AND E.BRANCH_NBR IN (:branchIDList) ");
            queryCondition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
        }

        if (inputVO.getUse_date_bgn() != null) {
            sql.append("AND TRUNC(P.USE_DATE) >= TRUNC(:start) ");
            queryCondition.setObject("start", new Timestamp(inputVO.getUse_date_bgn().getTime()));
        }

        if (inputVO.getUse_date_end() != null) {
            sql.append("AND TRUNC(P.USE_DATE) <= TRUNC(:end) ");
            queryCondition.setObject("end", new Timestamp(inputVO.getUse_date_end().getTime()));
        }

        if (StringUtils.isNotBlank(inputVO.getSeq())) {
            sql.append("AND P.SEQ = :seqno ");
            queryCondition.setObject("seqno", inputVO.getSeq());
        }

        queryCondition.setQueryString(sql.toString());

        outputVO.setResultList(dam.exeQuery(queryCondition));

        this.sendRtnObject(outputVO);
    }

    public void reply(Object body, IPrimitiveMap header) throws JBranchException, Exception{

        MAO131InputVO inputVO = (MAO131InputVO) body;
        dam = this.getDataAccessManager();
        boolean reply = false;

        TBMAO_DEV_APL_PLISTVO vo = new TBMAO_DEV_APL_PLISTVO();
        vo = (TBMAO_DEV_APL_PLISTVO) dam.findByPKey(TBMAO_DEV_APL_PLISTVO.TABLE_UID, inputVO.getSeq());
        if (vo != null) {
            if (inputVO.getReply_type().equals("Y")) {
                vo.setDEV_STATUS("C05");
                vo.setLETGO_EMP_ID(inputVO.getEmp_id());
                vo.setLETGO_YN("Y");
                vo.setLETGO_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
                dam.update(vo);

                reply = true;
            } else {
                vo.setDEV_STATUS("A01");
                vo.setLETGO_EMP_ID(inputVO.getEmp_id());
                vo.setLETGO_YN("N");
                vo.setLETGO_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
                dam.update(vo);

                reply = false;
            }
        } else {
            throw new APException("ehl_01_common_008");
        }

        // 發送通知信給申請人
        QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT EMP_ID, EMP_NAME, EMP_EMAIL_ADDRESS AS EMAIL ");
        sql.append("FROM TBORG_MEMBER ");
        sql.append("WHERE EMP_ID = :emp_id ");

        queryCondition.setQueryString(sql.toString());
        queryCondition.setObject("emp_id", inputVO.getEmail_id());

        List<Map<String, Object>> list = dam.exeQuery(queryCondition);

        String email = list.get(0).get("EMAIL").toString();
        String name = list.get(0).get("EMP_NAME").toString();

        Map<String, Object> annexData = new HashMap<String, Object>();
        String START_TIME = vo.getUSE_PERIOD_S_TIME().substring(0, 2);
        String END_TIME = vo.getUSE_PERIOD_E_TIME().substring(0, 2);

        if (isEmail(email) == false) {
            this.sendRtnObject("Error");
        } else {
            FubonSendJavaMail sendMail = new FubonSendJavaMail();
            FubonMail mail = new FubonMail();

            List<Map<String, String>> mailList = new ArrayList<Map<String, String>>();
            Map<String, String> mailMap = new HashMap<String, String>();

            mailMap.put(FubonSendJavaMail.MAIL, email);
            mailList.add(mailMap);

            mail.setLstMailTo(mailList);
            mail.setFromName("台北富邦銀行");
            mail.setFromMail("wmsr_bank@fbt.com");
            // 設定信件主旨
            mail.setSubject("行動載具審核通知信");
            // 設定信件內容
            if (reply) {
                mail.setContent("親愛的" + name + " 您好：<br />您於" + sdfYYYYMMDD.format(vo.getUSE_DATE()) + " " + START_TIME + ":00~" + END_TIME + ":00 " + "時段申請借用行動載具，經主管審核完成。");
            } else {
                mail.setContent("親愛的" + name + " 您好：<br />您於" + sdfYYYYMMDD.format(vo.getUSE_DATE()) + " " + START_TIME + ":00~" + END_TIME + ":00 " + "時段申請借用行動載具，經主管退回。");
            }
            // 寄出信件-無附件
            sendMail.sendMail(mail, annexData);

            this.sendRtnObject(null);
        }

    }

    // 信箱Email格式檢查
    public static boolean isEmail(String email){
        Pattern emailPattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher matcher = emailPattern.matcher(email);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public void doGetCustomerList(Object body, IPrimitiveMap header) throws Exception{
        GenericMap input = new GenericMap((Map) body);
        // 取得設備編號
        String deviceID = input.getNotNullStr("deviceID");
        // 登入者員編
        String empId = ObjectUtils.toString(getUserVariable(FubonSystemVariableConsts.LOGINID));

        Date sysDate = Calendar.getInstance().getTime();
        SimpleDateFormat sdfYmd = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdfHms = new SimpleDateFormat("HHmmss");

        // 系統日期的年月日
        int sysDateYmd = Integer.parseInt(sdfYmd.format(sysDate));
        // 系統日期的時分秒
        int sysDateHms = Integer.parseInt(sdfHms.format(sysDate));

        Calendar sysDateNextYmd = Calendar.getInstance();
        sysDateNextYmd.add(Calendar.DATE, 1);
        int sysNextDateYmd = Integer.parseInt(sdfYmd.format(sysDateNextYmd.getTime()));

        SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");

        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sbr = new StringBuffer();
        sbr.append(" select  ");
        sbr.append("   APL_EMP_ID ,  ");
        sbr.append("   USE_PERIOD_S_TIME ,   ");
        sbr.append("   USE_PERIOD_E_TIME ,   ");
        sbr.append("   VISIT_CUST_LIST , ");
        sbr.append("   USE_DATE ");
        sbr.append(" from TBMAO_DEV_APL_PLIST ");
        sbr.append(" where dev_status = 'D06' ");
        sbr.append(" and APL_EMP_ID = :empid ");
        sbr.append(" and DEV_NBR = :deviceID ");

        queryCondition.setQueryString(sbr.toString());
        queryCondition.setObject("empid", empId);
        queryCondition.setObject("deviceID", deviceID);
        List<Map<String, Object>> result = dam.exeQuery(queryCondition);
        List<Map<String, String>> queryCustList = new ArrayList();

        for (Map requlstMap : result) {
            GenericMap resultGmap = new GenericMap(requlstMap);
            Date useDate = resultGmap.getDate("USE_DATE");
            String START_TIME = resultGmap.getNotNullStr("USE_PERIOD_S_TIME");
            String END_TIME = resultGmap.getNotNullStr("USE_PERIOD_E_TIME");

            // 使用日期的年月日
            int useDateYmd = Integer.parseInt(sdfYmd.format(useDate));

            boolean canUse = false;

//            // 當日上午09:00~13:00
//            if ("1".equals(usePeriod)) {
//                canUse = sysDateYmd == useDateYmd && sysDateHms >= 90000 && sysDateHms <= 130000;
//            }
//            // 當日下午13:00~17:00
//            else if ("2".equals(usePeriod)) {
//                canUse = sysDateYmd == useDateYmd && sysDateHms >= 130000 && sysDateHms <= 170000;
//            }
//            // 當日17:00~09:00(隔日)
//            else if ("3".equals(usePeriod)) {
//                // 同日
//                canUse = sysDateYmd == useDateYmd && sysDateHms >= 170000 && sysDateHms <= 235959;
//                // 次日
//                canUse = canUse || sysNextDateYmd == useDateYmd && sysDateHms >= 0 && sysDateHms <= 80000;
//            }
            
            if(Integer.parseInt(END_TIME)>Integer.parseInt(START_TIME)){
            	canUse = sysDateYmd == useDateYmd && sysDateHms >= Integer.parseInt(START_TIME+"00") && sysDateHms <= Integer.parseInt(END_TIME+"00");
            }else{
                // 同日
                canUse = sysDateYmd == useDateYmd && sysDateHms >= Integer.parseInt(START_TIME+"00") && sysDateHms <= 235959;
                // 次日
                canUse = canUse || sysNextDateYmd == useDateYmd && sysDateHms >= 0 && sysDateHms <= Integer.parseInt(END_TIME+"00");
            }
            
            // test
            // canUse = true;
            // resultGmap.put("VISIT_CUST_LIST", "A220427377:莊,Q220345944:陳,D120942101:林,A122825628:白,A122214867:白");

            if (!canUse) {
                continue;
            }

            for (String tempData : resultGmap.getNotNullStr("VISIT_CUST_LIST").split(",")) {
                if (StringUtils.isBlank(tempData)) continue;
                String tempDatas[] = tempData.split(":");
                if (tempDatas.length != 2) continue;
                String custId = tempDatas[0];
                String custName = tempDatas[1];
                String vipDegree = null;
                Date expiryDate = null;
                boolean agreement = true;
                boolean isExpired = true;

                SOT701InputVO sot701inputVO = new SOT701InputVO();
                sot701inputVO.setCustID(custId.toUpperCase());
                FP032675DataVO fp032675DataVO = sot701.getFP032675Data(sot701inputVO);

                sbr = new StringBuffer();
                sbr.append(" select  ");
                sbr.append("  mast.VIP_DEGREE, ");
                sbr.append("  kyc.EXPIRY_DATE, ");
                sbr.append("  NVL(NOTE.SIGN_AGMT_YN, 'N') SIGN_AGMT_YN, ");         // 有無簽屬推介同意書
                sbr.append("  NVL(NOTE.REC_YN, 'N') REC_YN, ");                     // 可否推介
                sbr.append("  NVL(NOTE.COMM_RS_YN, 'N') COMM_RS_YN, ");             // 拒銷註記
                sbr.append("  NVL(NOTE.COMM_NS_YN, 'N') COMM_NS_YN ");             // 撤銷註記
                sbr.append(" from tbcrm_cust_mast mast ");
                sbr.append(" left join TBKYC_INVESTOREXAM_M kyc on mast.CUST_ID = kyc.CUST_ID ");
                sbr.append(" LEFT JOIN TBCRM_CUST_NOTE NOTE ON MAST.CUST_ID = NOTE.CUST_ID ");
                sbr.append(" where mast.cust_id = :custid ");
                queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
                queryCondition.setQueryString(sbr.toString());
                queryCondition.setObject("custid", custId);

                List<Map<String, Object>> custDatas = dam.exeQuery(queryCondition);

                if (CollectionUtils.isNotEmpty(custDatas)) {
                    GenericMap custData = new GenericMap(custDatas.get(0));
                    vipDegree = custData.getNotNullStr("VIP_DEGREE");
                    expiryDate = custData.getDate("EXPIRY_DATE");
                    agreement = "Y".equals(custData.getNotNullStr("REC_YN")) || 
                                "N".equals(custData.getNotNullStr("COMM_RS_YN")) || 
                                "N".equals(custData.getNotNullStr("COMM_NS_YN"));
                }

                isExpired = expiryDate == null || sysDateYmd > Integer.parseInt(sdfYmd.format(expiryDate));

                sbr = new StringBuffer();
                sbr.append(" select CUST_ID_S from TBCRM_CUST_REL where cust_id_m = :empid and rel_type != '00' ");
                queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
                queryCondition.setQueryString(sbr.toString());
                queryCondition.setObject("empid", empId);

                List<Map<String, Object>> familyDatas = dam.exeQuery(queryCondition);
                List<String> relatedId = new ArrayList();

                if (CollectionUtils.isNotEmpty(familyDatas)) {
                    for (Map<String, Object> familyData : familyDatas) {
                        String custIds = ObjectUtils.toString(familyData.get("CUST_ID_S"));

                        if (StringUtils.isNotBlank(custIds)) {
                            relatedId.add(custIds);
                        }
                    }
                }
                
                queryCustList.add(new GenericMap()
                                                  // 身分證字號
                                                  .put("custId", custId)
                                                  // 客戶姓名
                                                  .put("custName", custName)
                                                  // 客戶等級
                                                  .put("level", vipDegree)
                                                  // 有無, 能否推介同意書, 禁拒銷戶
                                                  .put("agreement_r", agreement && "Y".equals(fp032675DataVO.getCustTxFlag()))
                                                  // kyc過期
                                                  .put("kycVaild", isExpired)
                                                  // 家庭戶清單(身分證字號)
                                                  .put("relatedId", relatedId.toArray())
                                                  .getParamMap());
            }
        }

        sendRtnObject(new GenericMap().put("customers", queryCustList).getParamMap());
    }

    @SuppressWarnings("unchecked")
    public void doGetCustomerDetail(Object body, IPrimitiveMap<EnumTiaHeader> header) throws Exception{
        GenericMap input = new GenericMap((Map) body);
        String custId = input.getNotNullStr("custId").toUpperCase();
        Map result = new HashMap();

        SimpleDateFormat sdfYmd = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdfYmd2 = new SimpleDateFormat("yyyy.MM.dd");

        // 學歷
        SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
        SOT701InputVO sot701inputVO = new SOT701InputVO();
        sot701inputVO.setCustID(custId);
        FP032151OutputVO fp032151DataVO = sot701.getFP032151Data(sot701inputVO, header);
        String education = fp032151DataVO.getEDUCATION();
        String eduName = new XmlInfo().getVariable("KYC.EDUCATION", education, "F3");

        String plType = null;
        String obuType = null;
        String signYN = null;
        Date professionalDate = null;

        sot701inputVO = new SOT701InputVO();
        sot701inputVO.setCustID(custId);
        FP032675DataVO fp032675DataVO = sot701.getFP032675Data(sot701inputVO);

        // 是否為專業投資人
        plType = fp032675DataVO.getCustProFlag();

        // 是否有OBU註記
        obuType = fp032675DataVO.getObuFlag();

        // 專投效期
        professionalDate = fp032675DataVO.getCustProDate();

        // 有無簽署推介同意
        signYN = StringUtils.isBlank(fp032675DataVO.getCustTxFlag()) ? "N" : fp032675DataVO.getCustTxFlag();

        CustKYCDataVO custKYCData = sot701.getCustKycData(sot701inputVO);

        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sbr = new StringBuffer();
        sbr.append(" select  ");
        sbr.append("    mast.CUST_NAME , ");
        sbr.append("    mast.VIP_DEGREE ,  ");
        sbr.append("    mast.BIRTH_DATE , ");
        sbr.append("    mast.GENDER , ");
        sbr.append("    trunc((to_char(sysdate,'yyyyMMdd')-to_char(BIRTH_DATE,'yyyyMMdd'))/10000) AGE, ");
        sbr.append("    kyc.CUST_RISK_AFR , ");
        sbr.append("    kyc.EXPIRY_DATE ,  ");
        sbr.append("    contact.EMAIL , ");
        sbr.append("    case when mast.SAL_COMPANY is null then 'N' ELSE 'Y' END SALARY , ");
        sbr.append("    note.OBU_YN REMARKOBU, ");
        sbr.append("    mast.ANNUAL_INCOME_AMT as ANNUALINCOME, ");
        sbr.append("    NVL(note.SIGN_AGMT_YN, 'N') SIGN_AGMT_YN ");
        sbr.append(" from TBCRM_CUST_MAST mast  ");
        sbr.append(" left join  TBCRM_CUST_NOTE note ");
        sbr.append(" on mast.CUST_ID = note.CUST_ID ");
        sbr.append(" left join  TBCRM_CUST_CONTACT contact ");
        sbr.append(" on mast.CUST_ID = contact.CUST_ID ");
        sbr.append(" left join TBKYC_INVESTOREXAM_M kyc ");
        sbr.append(" on mast.CUST_ID = kyc.CUST_ID ");
        sbr.append(" where mast.CUST_ID = :custid ");
        queryCondition.setQueryString(sbr.toString());
        queryCondition.setObject("custid", custId);
        List<Map<String, Object>> custResultList = dam.exeQuery(queryCondition);

        // 客戶姓名
        result.put("custId", custId);
        // result.put("edu" , eduName);

        if (CollectionUtils.isNotEmpty(custResultList)) {
            INS810 ins810 = (INS810) PlatformContext.getBean("ins810");
            GenericMap custResult = new GenericMap(custResultList.get(0));

            INS810InputVO inputVO = new INS810InputVO();
            inputVO.setBirthday(custResult.getDate("BIRTH_DATE"));
            int insuranceAge = ins810.getAge(inputVO).getAge();
            float age = custResult.getBigDecimal("AGE").floatValue();

            int gender = custResult.getBigDecimal("GENDER").intValue();
            // C值
            // String kycRiskAfr = custResult.getNotNullStr("CUST_RISK_AFR");
            // String kycExDate = new
            // SimpleDateFormat("yyyyMMdd").format(custResult.getDate("EXPIRY_DATE"));
            //
            // String isProfessional = plType;

            result.put("custName", custResult.getNotNullStr("CUST_NAME"));
            result.put("level", custResult.getNotNullStr("VIP_DEGREE"));
            result.put("riskLevel", ObjectUtils.toString(custKYCData.getKycLevel()));
            result.put("age", age);
            result.put("gender", gender);
            result.put("birthday", new SimpleDateFormat("yyyyMMdd").format(custResult.getDate("BIRTH_DATE")));
            result.put("insuranceAge", insuranceAge);
            result.put("isProfessional", "Y".equals(plType));
            // Juan
            result.put("isOBU", "Y".equals(obuType));
            result.put("signYN", signYN);

            List items = new ArrayList();
            result.put("items", items);

            items.add(new GenericMap().put("itemName", "客戶姓名")
                                      .put("itemData", custResult.getNotNullStr("CUST_NAME"))
                                      .getParamMap());

            items.add(new GenericMap().put("itemName", "ID").put("itemData", custId).getParamMap());

            items.add(new GenericMap().put("itemName", "年齡").put("itemData", Integer.toString((int) age)).getParamMap());

            // String kycLevel = ObjectUtils.toString(custKYCData.getKycLevel());
            // kycDueDate
            items.add(new GenericMap().put("itemName", "KYC等級/效期")
                                      .put("itemData", ObjectUtils.toString(custKYCData.getKycLevel()) + "/" + (custKYCData.getKycDueDate() == null ? "" : sdfYmd2.format(custKYCData.getKycDueDate())))
                                      .getParamMap());

            items.add(new GenericMap().put("itemName", "OBU註記")
                                      .put("itemData", obuType != null ? obuType : custResult.getNotNullStr("REMARKOBU"))
                                      .getParamMap());

            items.add(new GenericMap().put("itemName", "專業投資人/效期")
                                      .put("itemData", plType + "/" + (fp032675DataVO.getCustProDate() == null ? "N" : sdfYmd2.format(fp032675DataVO.getCustProDate())))
                                      .getParamMap());

            items.add(new GenericMap().put("itemName", "薪資戶")
                                      .put("itemData", custResult.getNotNullStr("SALARY"))
                                      .getParamMap());

            items.add(new GenericMap().put("itemName", "信託推介同意")
                                      .put("itemData", StringUtils.isNotBlank(signYN) ? signYN : ("Y".equals(custResult.getNotNullStr("SIGN_AGMT_YN")) ? "已簽署" : "未簽屬"))
                                      .getParamMap());

            items.add(new GenericMap().put("itemName", "Email")
                                      .put("itemData", custResult.getNotNullStr("EMAIL"))
                                      .getParamMap());

            // TODO待補
            // items.add(new GenericMap()
            // .put("itemName", "SOW")
            // .put("itemData", "")
            // .getParamMap()
            // );
            //
            // TODO 待補
            // items.add(new GenericMap()
            // .put("itemName", "SI推介同意書")
            // .put("itemData", "")
            // .getParamMap()
            // );

            Map historyPlan = new HashMap();
            result.put("historyPlan", historyPlan);

            // 保險規劃
            queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
            sbr = new StringBuffer();
            sbr.append(" select CUST_ID");
            sbr.append(" from TBINS_PLAN_MAIN");
            sbr.append(" where CUST_ID = :custid");
            queryCondition.setQueryString(sbr.toString());
            queryCondition.setObject("custid", custId);
            int hasIP = ((List<Map<String, Object>>) dam.exeQuery(queryCondition)).size();

            // 保險特定目的
            queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
            sbr = new StringBuffer();
            sbr.append(" select CUST_ID");
            sbr.append(" from TBINS_SPP_MAIN");
            sbr.append(" where CUST_ID = :custid");
            queryCondition.setQueryString(sbr.toString());
            queryCondition.setObject("custid", custId);
            int hasIPP = ((List<Map<String, Object>>) dam.exeQuery(queryCondition)).size();

            queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
            sbr = new StringBuffer();
            sbr.append(" select CUST_ID");
            sbr.append(" from TBFPS_PORTFOLIO_PLAN_INV_HEAD");
            sbr.append(" where CUST_ID = :custid");
            queryCondition.setQueryString(sbr.toString());
            queryCondition.setObject("custid", custId);
            int hasFP = ((List<Map<String, Object>>) dam.exeQuery(queryCondition)).size();

            int hasFPP = 0;
            queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
            // 績效追蹤
            sbr = new StringBuffer();
            sbr.append(" SELECT CUST_ID ");
            sbr.append(" FROM TBFPS_SPP_PRD_RETURN_HEAD ");
            sbr.append(" WHERE CUST_ID = :custId ");
            queryCondition.setObject("custId", custId);
            queryCondition.setQueryString(sbr.toString());
            if (((List<Map<String, Object>>) dam.exeQuery(queryCondition)).size() > 0) {
                hasFPP = 2;
            } else {
                // 歷史規劃
                sbr = new StringBuffer();
                sbr.append(" SELECT CUST_ID ");
                sbr.append(" FROM TBFPS_PORTFOLIO_PLAN_SPP_HEAD ");
                sbr.append(" WHERE CUST_ID = :custId ");
                queryCondition.setObject("custId", custId);
                queryCondition.setQueryString(sbr.toString());
                if (((List<Map<String, Object>>) dam.exeQuery(queryCondition)).size() > 0) {
                    hasFPP = 1;
                }
            }
            
//            .put("hasStock", fps200.hasStock(dam, custId) > 0)	
//            從VWFPS_AST_ALLPRD_DETAIL判斷是否為再投資較準，但先以Web一致改成看 TBFPS_PORTFOLIO_PLAN_INV_HEAD（前一日的資料）
            
            List<Map<String, Object>> custAmt = fps210.getCustAmt(dam, custId);
//            $scope.inputVO.mfdProd = $scope.paramList.MFD_PROD_AMT || 0;
//            $scope.inputVO.etfProd = $scope.paramList.ETF_PROD_AMT || 0;
//            $scope.inputVO.insProd = 0;
//            $scope.inputVO.bondProd = $scope.paramList.BOND_PROD_AMT || 0;
//            $scope.inputVO.snProd = $scope.paramList.SN_PROD_AMT || 0;
//            $scope.inputVO.siProd = $scope.paramList.SI_PROD_AMT || 0;
            int mfdAmt = custAmt.size() > 0 ? Integer.parseInt(new GenericMap(custAmt.get(0)).getStr("MFD_PROD_AMT", "0")) : 0;
            int etfAmt = custAmt.size() > 0 ? Integer.parseInt(new GenericMap(custAmt.get(0)).getStr("ETF_PROD_AMT", "0")) : 0;
            int bondAmt = custAmt.size() > 0 ? Integer.parseInt(new GenericMap(custAmt.get(0)).getStr("BOND_PROD_AMT", "0")) : 0;
            int snAmt = custAmt.size() > 0 ? Integer.parseInt(new GenericMap(custAmt.get(0)).getStr("SN_PROD_AMT", "0")) : 0;
            int siAmt = custAmt.size() > 0 ? Integer.parseInt(new GenericMap(custAmt.get(0)).getStr("SI_PROD_AMT", "0")) : 0;
            int nanoAmt = custAmt.size() > 0 ? Integer.parseInt(new GenericMap(custAmt.get(0)).getStr("NANO_PROD_AMT", "0")) : 0;
            int totalAmt = mfdAmt + etfAmt + bondAmt + snAmt + siAmt + nanoAmt;
            
            historyPlan.putAll((new GenericMap().put("hasIP", hasIP > 0)
                                                .put("hasIPP", hasIPP > 0)
                                                .put("hasFP", hasFP > 0)
                                                .put("hasStock", totalAmt > 0)
                                                .put("hasFPP", hasFPP)
                                                .getParamMap()));

        }

        sendRtnObject(result);
    }

    // 2.2.2 geToDoCalendar取得行事曆(代辦事項)
    public void geToDoCalendar(Object body, IPrimitiveMap header) throws Exception{
        WorkStation ws = DataManager.getWorkStation(uuid);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Map<String, Object> inputMap = (Map<String, Object>) body;
        DataAccessManager dam = this.getDataAccessManager();

        Date startDate = null;
        Date endDate = null;
        if (inputMap.get("startDate") != null)
            startDate = sdf.parse(ObjectUtils.toString(inputMap.get("startDate")));
        if (inputMap.get("endDate") != null)
            endDate = sdf.parse(ObjectUtils.toString(inputMap.get("endDate")));

        String pri_id = crm121.api_login();
        // crm121 sql 不能直接call, 這邊拿他的sql改
        QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sql = new StringBuffer();
        if ("2".equals(pri_id)) {
            sql.append(" WITH AO_List AS ( ");
            sql.append(" SELECT AO_CODE, EMP_ID, EMP_NAME FROM VWORG_BRANCH_EMP_DETAIL_INFO WHERE BRANCH_NBR IN (:br_id )  ) ");
            // 主管評估行事曆
            sql.append(" SELECT NULL AS SEQ_NO, NULL AS CUST_ID, NULL AS CUST_NAME, '待主管評估'AS TITLE, FIN_TYPE AS STATUS, ");
            sql.append(" TRUNC(SCH.COACH_DATE) AS DATETIME,  '0800' AS STIME, '0900'AS ETIME, SCH.EMP_ID, INFO.EMP_NAME, INFO.AO_CODE, NULL AS TASK_MEMO, NULL AS TASK_SOURCE, 'manager' AS TYPE  ");
            sql.append(" FROM TBPMS_MNGR_EVAL_SCHEDULE SCH ");
            sql.append(" LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO INFO ON SCH.EMP_ID = INFO.EMP_ID ");
            sql.append(" WHERE 1 = 1  ");
            sql.append(" AND SCH.REGION_CENTER_ID IN (:rcIdList ) ");
            sql.append(" AND SCH.BRANCH_AREA_ID IN (:op_id ) ");
            sql.append(" AND SCH.BRANCH_ID IN (:br_id ) ");
            sql.append(" AND SCH.EMP_ID IN (SELECT EMP_ID FROM AO_List) ");
            if (startDate != null) {
                sql.append("and TRUNC(SCH.COACH_DATE) >= TRUNC(:start) ");
                queryCondition.setObject("start", startDate);
            }
            if (endDate != null) {
                sql.append("and TRUNC(SCH.COACH_DATE) < TRUNC(:end)+1 ");
                queryCondition.setObject("end", endDate);
            }

            sql.append(" UNION ALL ");
            sql.append(" SELECT TASK.SEQ_NO, TASK.CUST_ID, CUST.CUST_NAME, TASK.TASK_TITLE AS TITLE, TASK_STATUS AS STATUS, ");
            sql.append(" TRUNC(TASK.TASK_DATE) AS DATETIME, NVL(TASK.TASK_STIME, '0800') AS STIME, NVL(TASK.TASK_ETIME, '2030') AS ETIME, TASK.EMP_ID, D.EMP_NAME, '' AS AO_CODE, TASK.TASK_MEMO, TASK.TASK_SOURCE, 'TASK' AS TYPE ");
            sql.append(" FROM TBCAM_CAL_SALES_TASK TASK ");
            sql.append(" LEFT JOIN TBCRM_CUST_MAST CUST ON CUST.CUST_ID = TASK.CUST_ID  ");
            sql.append(" LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO D ON D.EMP_ID = TASK.EMP_ID  ");
            sql.append(" WHERE TASK.EMP_ID = :EMP ");
            if (startDate != null) {
                sql.append("and TRUNC(TASK.TASK_DATE) >= TRUNC(:start) ");
                queryCondition.setObject("start", startDate);
            }
            if (endDate != null) {
                sql.append("and TRUNC(TASK.TASK_DATE) < TRUNC(:end)+1 ");
                queryCondition.setObject("end", endDate);
            }

            queryCondition.setObject("EMP", ws.getUser().getUserID());
            queryCondition.setObject("op_id", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
            queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
            queryCondition.setObject("br_id", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
        } else {
            sql.append(" SELECT TASK.SEQ_NO, TASK.CUST_ID, CUST.CUST_NAME, TASK.TASK_TITLE AS TITLE, TASK.TASK_STATUS AS STATUS, TRUNC(TASK.TASK_DATE) AS DATETIME, NVL(TASK.TASK_STIME, '0800') AS STIME, NVL(TASK.TASK_ETIME, '2030') AS ETIME, ");
            sql.append(" TASK.TASK_MEMO, TASK.TASK_SOURCE, TASK.TASK_TYPE ");
            sql.append(" FROM TBCAM_CAL_SALES_TASK TASK ");
            sql.append(" LEFT JOIN TBCRM_CUST_MAST CUST ON CUST.CUST_ID = TASK.CUST_ID ");
            sql.append(" WHERE TASK.EMP_ID = :EMP ");
            if (startDate != null) {
                sql.append("and TRUNC(TASK.TASK_DATE) >= TRUNC(:start) ");
                queryCondition.setObject("start", startDate);
            }
            if (endDate != null) {
                sql.append("and TRUNC(TASK.TASK_DATE) < TRUNC(:end)+1 ");
                queryCondition.setObject("end", endDate);
            }

            queryCondition.setObject("EMP", ws.getUser().getUserID());
        }
        queryCondition.setQueryString(sql.toString());
        List<Map<String, Object>> crm121_list = dam.exeQuery(queryCondition);
        Map<String, List<Map<String, Object>>> ans_map = new HashMap<String, List<Map<String, Object>>>();
        for (Map<String, Object> map : crm121_list) {
            String date = sdf.format(map.get("DATETIME"));
            Map<String, Object> row_data = new HashMap<String, Object>();
            row_data.put("eventId", map.get("SEQ_NO").toString());
            row_data.put("custId", map.get("CUST_ID"));
            row_data.put("custName", map.get("CUST_NAME"));
            row_data.put("noticeTypeId", map.get("TASK_SOURCE"));
            row_data.put("startTime", map.get("STIME"));
            row_data.put("endTime", map.get("ETIME"));
            row_data.put("status", "I".equals(map.get("STATUS")) ? 0 : "C".equals(map.get("STATUS")) ? 1 : 2);
            row_data.put("title", map.get("TITLE"));
            row_data.put("content", map.get("TASK_MEMO"));

            if (!ans_map.containsKey(date)) {
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                list.add(row_data);
                ans_map.put(date, list);
            } else {
                ans_map.get(date).add(row_data);
            }
        }
        // finally
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (Entry<String, List<Map<String, Object>>> entry : ans_map.entrySet()) {
            Map<String, Object> data_map = new HashMap<String, Object>();
            data_map.put("date", entry.getKey());
            data_map.put("event", entry.getValue());
            data.add(data_map);
        }

        Map<String, Object> outputMap = new HashMap<String, Object>();
        outputMap.put("data", data);
        this.sendRtnObject(outputMap);
    }

    // 2.2.3 updateToDoCalendar 更新行事曆(代辦事項)
    public void updateToDoCalendar(Object body, IPrimitiveMap header) throws Exception{
        Map<String, Object> inputMap = (Map<String, Object>) body;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        DataAccessManager dam = this.getDataAccessManager();

        String date = inputMap.get("date").toString();
        Map<String, Object> event = (Map<String, Object>) inputMap.get("event");

        TBCAM_CAL_SALES_TASKVO vo = new TBCAM_CAL_SALES_TASKVO();
        vo = (TBCAM_CAL_SALES_TASKVO) dam.findByPKey(TBCAM_CAL_SALES_TASKVO.TABLE_UID, new BigDecimal(event.get("eventId")
                                                                                                           .toString()));
        if (vo != null) {
            vo.setTASK_DATE(new Timestamp(sdf.parse(date).getTime()));
            if (event.get("noticeTypeId") != null)
                vo.setTASK_SOURCE(event.get("noticeTypeId").toString());
            if (event.get("startTime") != null)
                vo.setTASK_STIME(event.get("startTime").toString());
            if (event.get("endTime") != null)
                vo.setTASK_ETIME(event.get("endTime").toString());
            if (event.get("status") != null)
                vo.setTASK_STATUS("0".equals(event.get("status")) ? "I" : "1".equals(event.get("status")) ? "C" : "X");
            vo.setTASK_TITLE(ObjectUtils.toString(event.get("title")));
            vo.setTASK_MEMO(ObjectUtils.toString(event.get("content")));

            dam.update(vo);
        } else
            throw new APException("無此資料");

        Map<String, Object> outputMap = new HashMap<String, Object>();
        this.sendRtnObject(outputMap);
    }

    // 2.2.4 getProductCalendar 取得行事曆(商品)
    public void getProductCalendar(Object body, IPrimitiveMap header) throws Exception{
        Map<String, Object> inputMap = (Map<String, Object>) body;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        WorkStation ws = DataManager.getWorkStation(uuid);
        DataAccessManager dam = this.getDataAccessManager();

        Date startDate = null;
        Date endDate = null;
        if (inputMap.get("startDate") != null)
            startDate = sdf.parse(ObjectUtils.toString(inputMap.get("startDate")));
        if (inputMap.get("endDate") != null)
            endDate = sdf.parse(ObjectUtils.toString(inputMap.get("endDate")));

        // ------------------------------------募集------------------------------------
        QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sql = new StringBuffer();
        sql.append("WITH INVEST AS ( ");
        sql.append("SELECT NUM, PTYPE, PRD_ID, PRD_NAME, BGN_DATE_OF_INVEST, END_DATE_OF_INVEST ");
        sql.append("FROM ( ");
        // 基金
        sql.append("SELECT '基金' as PTYPE, '1' as NUM, M.PRD_ID, FUND_CNAME as PRD_NAME, IPO_SDATE as BGN_DATE_OF_INVEST, IPO_EDATE as END_DATE_OF_INVEST ");
        sql.append("FROM TBPRD_FUND M, TBPRD_FUNDINFO I ");
        sql.append("WHERE M.PRD_ID = I.PRD_ID ");
        sql.append("AND ( TRUNC(I.IPO_SDATE) BETWEEN TRUNC(:sdate) AND TRUNC(:edate) AND TRUNC(I.IPO_EDATE) BETWEEN TRUNC(:sdate) AND TRUNC(:edate) ) ");
        sql.append("UNION ALL ");
        // SI
        sql.append("SELECT 'SI' AS PTYPE, '5' AS NUM, M.PRD_ID, M.SI_CNAME AS PRD_NAME, I.INV_SDATE as BGN_DATE_OF_INVEST, I.INV_EDATE as END_DATE_OF_INVEST ");
        sql.append("FROM TBPRD_SI M, TBPRD_SIINFO I ");
        sql.append("WHERE M.PRD_ID = I.PRD_ID ");
        sql.append("AND ( TRUNC(I.INV_SDATE) BETWEEN TRUNC(:sdate) AND TRUNC(:edate) AND TRUNC(I.INV_EDATE) BETWEEN TRUNC(:sdate) AND TRUNC(:edate) ) ");
        sql.append("UNION ALL ");
        // SN
        sql.append("SELECT 'SN' AS PTYPE, '6' AS NUM, M.PRD_ID, M.SN_CNAME AS PRD_NAME, I.INV_SDATE as BGN_DATE_OF_INVEST, I.INV_EDATE as END_DATE_OF_INVEST ");
        sql.append("FROM TBPRD_SN M, TBPRD_SNINFO I ");
        sql.append("WHERE M.PRD_ID = I.PRD_ID ");
        sql.append("AND ( TRUNC(I.INV_SDATE) BETWEEN TRUNC(:sdate) AND TRUNC(:edate) AND TRUNC(I.INV_EDATE) BETWEEN TRUNC(:sdate) AND TRUNC(:edate) ) ");
        sql.append("UNION ALL ");
        // ETF
        sql.append("SELECT 'ETF' AS PTYPE, '2' AS NUM, I.PRD_ID, ETF_CNAME AS PRD_NAME, INV_SDATE, INV_EDATE ");
        sql.append("FROM TBPRD_ETF I WHERE 1 = 1 ");
        sql.append("AND ( TRUNC(I.INV_SDATE) BETWEEN TRUNC(:sdate) AND TRUNC(:edate) AND TRUNC(I.INV_EDATE) BETWEEN TRUNC(:sdate) AND TRUNC(:edate) ) ");
        sql.append("UNION ALL ");
        // STOCK
        sql.append("SELECT 'STOCK' AS PTYPE, '3' AS NUM, I.PRD_ID, STOCK_CNAME AS PRD_NAME, INV_SDATE, INV_EDATE ");
        sql.append("FROM TBPRD_STOCK I WHERE 1 = 1 ");
        sql.append("AND ( TRUNC(I.INV_SDATE) BETWEEN TRUNC(:sdate) AND TRUNC(:edate) AND TRUNC(I.INV_EDATE) BETWEEN TRUNC(:sdate) AND TRUNC(:edate) ) ");
        sql.append("UNION ALL ");
        // 海外債
        sql.append("SELECT 'BOND' AS PTYPE, '4' AS NUM, M.PRD_ID, M.BOND_CNAME AS PRD_NAME, I.DATE_OF_FLOTATION, I.START_DATE_OF_BUYBACK ");
        sql.append("FROM TBPRD_BOND M, TBPRD_SNINFO I ");
        sql.append("WHERE M.PRD_ID = I.PRD_ID ");
        sql.append("AND ( TRUNC(I.INV_SDATE) BETWEEN TRUNC(:sdate) AND TRUNC(:edate) AND TRUNC(I.INV_EDATE) BETWEEN TRUNC(:sdate) AND TRUNC(:edate) ) ");
        sql.append("UNION ALL ");
        // 保險
        sql.append("SELECT DISTINCT 'INS' as PTYPE, '99' AS NUM, M.INSPRD_ID AS PRD_ID, INSPRD_NAME as PRD_NAME, I.IPO_SDATE as BGN_DATE_OF_INVEST, I.IPO_EDATE as END_DATE_OF_INVEST ");
        sql.append("FROM TBPRD_INS_MAIN M, TBPRD_INSINFO I ");
        sql.append("WHERE M.INSPRD_ID = I.PRD_ID ");
        sql.append("AND ( TRUNC(I.IPO_SDATE) BETWEEN TRUNC(:sdate) AND TRUNC(:edate) AND TRUNC(I.IPO_EDATE) BETWEEN TRUNC(:sdate) AND TRUNC(:edate) ) ");
        sql.append(") ORDER BY END_DATE_OF_INVEST, NUM, PRD_NAME ) ");

        sql.append("SELECT PTYPE, PRD_ID, PRD_NAME, BGN_DATE_OF_INVEST, END_DATE_OF_INVEST FROM INVEST ");

        queryCondition.setQueryString(sql.toString());
        queryCondition.setObject("sdate", startDate);
        queryCondition.setObject("edate", endDate);
        List<Map<String, Object>> list1 = dam.exeQuery(queryCondition);
        // ------------------------------------休市------------------------------------
        queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        sql = new StringBuffer();
        sql.append("WITH REST AS ( ");
        sql.append("SELECT NUM, PTYPE, MKT_NAME, DATE_OF_REST, PRD_ID, PRD_NAME ");
        sql.append("FROM ( ");
        // SI
        sql.append("SELECT 'SI' AS PTYPE, '5' AS NUM, '--' AS MKT_NAME, I.REST_DAY as DATE_OF_REST, I.PRD_ID, M.SI_CNAME AS PRD_NAME ");
        sql.append("FROM TBPRD_REST_DAY I ");
        sql.append("LEFT JOIN TBPRD_SI M ON M.PRD_ID = I.PRD_ID  ");
        sql.append("WHERE I.PTYPE = 'SI' ");
        sql.append("AND ( TRUNC(I.REST_DAY) BETWEEN TRUNC(:sdate) AND TRUNC(:edate) ) ");
        sql.append("UNION ALL ");
        // SN
        sql.append("SELECT 'SN' AS PTYPE, '6' AS NUM, '--' AS MKT_NAME, I.REST_DAY as DATE_OF_REST, I.PRD_ID, M.SN_CNAME AS PRD_NAME ");
        sql.append("FROM TBPRD_REST_DAY I ");
        sql.append("LEFT JOIN TBPRD_SN M ON M.PRD_ID = I.PRD_ID  ");
        sql.append("WHERE I.PTYPE = 'SN' ");
        sql.append("AND ( TRUNC(I.REST_DAY) BETWEEN TRUNC(:sdate) AND TRUNC(:edate) ) ");
        sql.append("UNION ALL ");
        // ETF
        sql.append("SELECT DISTINCT 'ETF' AS PTYPE, '2' AS NUM, I.STOCK_CODE AS MKT_NAME, I.REST_DAY as DATE_OF_REST, '--' AS PRD_ID, '--' AS PRD_NAME ");
        sql.append("FROM TBPRD_REST_DAY I ");
        sql.append("LEFT JOIN TBPRD_ETF M ON M.STOCK_CODE = I.STOCK_CODE ");
        sql.append("WHERE I.PTYPE = 'ETF' ");
        sql.append("AND ( TRUNC(I.REST_DAY) BETWEEN TRUNC(:sdate) AND TRUNC(:edate) ) ");
        sql.append("UNION ALL ");
        // STOCK
        sql.append("SELECT DISTINCT 'STOCK' AS PTYPE, '3' AS NUM, I.STOCK_CODE AS MKT_NAME, I.REST_DAY as DATE_OF_REST, '--' AS PRD_ID, '--' AS PRD_NAME ");
        sql.append("FROM TBPRD_REST_DAY I ");
        sql.append("LEFT JOIN TBPRD_STOCK M ON M.STOCK_CODE = I.STOCK_CODE ");
        sql.append("WHERE I.PTYPE = 'STOCK' ");
        sql.append("AND ( TRUNC(I.REST_DAY) BETWEEN TRUNC(:sdate) AND TRUNC(:edate) ) ");
        sql.append("UNION ALL ");
        // 海外債
        sql.append("SELECT 'BOND' AS PTYPE, '4' AS NUM, '--' AS MKT_NAME, I.REST_DAY as DATE_OF_REST, M.PRD_ID, M.BOND_CNAME AS PRD_NAME ");
        sql.append("FROM TBPRD_REST_DAY I ");
        sql.append("LEFT JOIN TBPRD_BOND M ON M.PRD_ID = I.PRD_ID ");
        sql.append("WHERE I.PTYPE = 'BOND' ");
        sql.append("AND ( TRUNC(I.REST_DAY) BETWEEN TRUNC(:sdate) AND TRUNC(:edate) ) ");
        sql.append(") ORDER BY DATE_OF_REST, NUM, MKT_NAME ) ");

        sql.append("SELECT PTYPE, MKT_NAME, DATE_OF_REST, PRD_ID, PRD_NAME FROM REST ");

        queryCondition.setQueryString(sql.toString());
        queryCondition.setObject("sdate", startDate);
        queryCondition.setObject("edate", endDate);
        List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
        // ------------------------------------配息------------------------------------
        queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        sql = new StringBuffer();
        sql.append("WITH DIVIDEND AS ( ");
        sql.append("SELECT NUM, PTYPE, PRD_NAME, DIV_RATE, DIV_DATE ");
        sql.append("FROM ( ");
        // SI
        sql.append("SELECT PRD_TYPE AS PTYPE, '5' AS NUM, PRD_ID AS PRD_NAME, DIVIDEND_RATE as DIV_RATE, CUS_DATE as DIV_DATE ");
        sql.append("FROM TBPRD_CALENDAR ");
        sql.append("WHERE  1 = 1");
        sql.append("AND CAL_TYPE = '1' ");
        sql.append("AND PRD_TYPE = 'SI' ");
        sql.append("AND ( TRUNC(CUS_DATE) BETWEEN TRUNC(:sdate) AND TRUNC(:edate) ) ");
        sql.append("UNION ALL ");
        // SN
        sql.append("SELECT PRD_TYPE AS PTYPE, '6' AS NUM, PRD_ID AS PRD_NAME, DIVIDEND_RATE as DIV_RATE, CUS_DATE as DIV_DATE ");
        sql.append("FROM TBPRD_CALENDAR ");
        sql.append("WHERE  1 = 1");
        sql.append("AND CAL_TYPE = '1' ");
        sql.append("AND PRD_TYPE = 'SN' ");
        sql.append("AND ( TRUNC(CUS_DATE) BETWEEN TRUNC(:sdate) AND TRUNC(:edate) ) ");
        sql.append("UNION ALL ");
        // 海外債
        sql.append("SELECT PRD_TYPE AS PTYPE, '4' AS NUM, PRD_ID AS PRD_NAME, DIVIDEND_RATE as DIV_RATE, CUS_DATE as DIV_DATE ");
        sql.append("FROM TBPRD_CALENDAR ");
        sql.append("WHERE  1 = 1");
        sql.append("AND CAL_TYPE = '1' ");
        sql.append("AND PRD_TYPE = 'BOND' ");
        sql.append("AND ( TRUNC(CUS_DATE) BETWEEN TRUNC(:sdate) AND TRUNC(:edate) ) ");
        // sql.append("UNION ALL ");
        // //保險
        // sql.append("SELECT DISTINCT 'INS' as PTYPE, '99' AS NUM, INSPRD_NAME as
        // PRD_NAME, NULL as DIV_RATE ");
        // sql.append("FROM TBPRD_INS_MAIN M, TBPRD_INSINFO I ");
        // sql.append("WHERE M.INSPRD_ID = I.PRD_ID ");
        // sql.append("AND ( TO_CHAR(I.SALE_SDATE,'YYYY/MM/DD') =
        // TO_CHAR(:date,'YYYY/MM/DD') OR ");
        // sql.append("TO_CHAR(I.SALE_EDATE,'YYYY/MM/DD') = TO_CHAR(:date,'YYYY/MM/DD')
        // ) ");
        sql.append(") ORDER BY DIV_DATE, NUM, PRD_NAME ) ");

        sql.append("SELECT PTYPE, PRD_NAME, DIV_RATE, DIV_DATE FROM DIVIDEND ");

        queryCondition.setQueryString(sql.toString());
        queryCondition.setObject("sdate", startDate);
        queryCondition.setObject("edate", endDate);
        List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
        // ------------------------------------到期------------------------------------
        queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        sql = new StringBuffer();
        sql.append("WITH EXPIRY AS ( ");
        sql.append("SELECT NUM, PTYPE, PRD_NAME, DATE_OF_MATURITY ");
        sql.append("FROM ( ");
        // SI-到期日
        sql.append("SELECT PRD_TYPE AS PTYPE, '5' AS NUM, PRD_ID AS PRD_NAME, CUS_DATE as DATE_OF_MATURITY ");
        sql.append("FROM TBPRD_CALENDAR ");
        sql.append("WHERE CAL_TYPE = '2' ");
        sql.append("AND PRD_TYPE = 'SI' ");
        sql.append("AND ( TRUNC(CUS_DATE) BETWEEN TRUNC(:sdate) AND TRUNC(:edate) ) ");
        sql.append("UNION ALL ");
        // SN-到期日
        sql.append("SELECT PRD_TYPE AS PTYPE, '6' AS NUM, PRD_ID AS PRD_NAME, CUS_DATE as DATE_OF_MATURITY ");
        sql.append("FROM TBPRD_CALENDAR ");
        sql.append("WHERE CAL_TYPE = '2' ");
        sql.append("AND PRD_TYPE = 'SN' ");
        sql.append("AND ( TRUNC(CUS_DATE) BETWEEN TRUNC(:sdate) AND TRUNC(:edate) ) ");
        sql.append("UNION ALL ");
        // BOND-到期日
        sql.append("SELECT PRD_TYPE AS PTYPE, '4' AS NUM, PRD_ID AS PRD_NAME, CUS_DATE as DATE_OF_MATURITY ");
        sql.append("FROM TBPRD_CALENDAR ");
        sql.append("WHERE CAL_TYPE = '2' ");
        sql.append("AND PRD_TYPE = 'BOND' ");
        sql.append("AND ( TRUNC(CUS_DATE) BETWEEN TRUNC(:sdate) AND TRUNC(:edate) ) ");
        sql.append("UNION ALL ");
        // 保險
        sql.append("SELECT DISTINCT 'INS' as PTYPE, '99' AS NUM, INSPRD_NAME as PRD_NAME, I.SALE_EDATE as DATE_OF_MATURITY ");
        sql.append("FROM TBPRD_INS_MAIN M, TBPRD_INSINFO I ");
        sql.append("WHERE M.INSPRD_ID = I.PRD_ID ");
        sql.append("AND ( TRUNC(I.SALE_EDATE) BETWEEN TRUNC(:sdate) AND TRUNC(:edate) ) ");
        sql.append(") ORDER BY DATE_OF_MATURITY, NUM, PRD_NAME ) ");

        sql.append("SELECT PTYPE, PRD_NAME, DATE_OF_MATURITY FROM EXPIRY ");

        queryCondition.setQueryString(sql.toString());
        queryCondition.setObject("sdate", startDate);
        queryCondition.setObject("edate", endDate);
        List<Map<String, Object>> list4 = dam.exeQuery(queryCondition);
        // 募集
        Map<String, Map<String, List<Map<String, Object>>>> ans_map = new HashMap<String, Map<String, List<Map<String, Object>>>>();
        for (Map<String, Object> map : list1) {
            // sa date
            queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
            sql = new StringBuffer();
            sql.append("select to_char(to_date(:start,'YYYYMMDD') + (level-1), 'YYYYMMDD') as INTEVAL from dual ");
            sql.append("connect by level < (to_date(:end,'YYYYMMDD') - to_date(:start,'YYYYMMDD') + 2 ) ");
            queryCondition.setObject("start", sdf.format(map.get("BGN_DATE_OF_INVEST")));
            queryCondition.setObject("end", sdf.format(map.get("END_DATE_OF_INVEST")));
            queryCondition.setQueryString(sql.toString());
            List<Map<String, Object>> inteval_list = dam.exeQuery(queryCondition);
            for (Map<String, Object> map2 : inteval_list) {
                String date = map2.get("INTEVAL").toString();
                Map<String, Object> row_data = new HashMap<String, Object>();
                row_data.put("productType", map.get("PTYPE"));
                row_data.put("productId", map.get("PRD_ID"));
                row_data.put("productName", map.get("PRD_NAME"));
                row_data.put("startDate", sdf.format(map.get("BGN_DATE_OF_INVEST")));
                row_data.put("endDate", sdf.format(map.get("END_DATE_OF_INVEST")));

                if (!ans_map.containsKey(date)) {
                    Map<String, List<Map<String, Object>>> new_map = new HashMap<String, List<Map<String, Object>>>();
                    List<Map<String, Object>> new_list = new ArrayList<Map<String, Object>>();
                    new_list.add(row_data);
                    new_map.put("raise", new_list);
                    ans_map.put(date, new_map);
                } else {
                    Map<String, List<Map<String, Object>>> old_map = ans_map.get(date);
                    List<Map<String, Object>> raise_list = new ArrayList<Map<String, Object>>();
                    if (old_map.containsKey("raise"))
                        raise_list = old_map.get("raise");
                    raise_list.add(row_data);
                    old_map.put("raise", raise_list);
                    ans_map.put(date, old_map);
                }
            }
        }
        // 休市
        for (Map<String, Object> map : list2) {
            String date = sdf.format(map.get("DATE_OF_REST"));
            Map<String, Object> row_data = new HashMap<String, Object>();
            row_data.put("productType", map.get("PTYPE"));
            row_data.put("productId", map.get("PRD_ID"));
            row_data.put("productName", map.get("PRD_NAME"));
            row_data.put("market", map.get("MKT_NAME"));

            if (!ans_map.containsKey(date)) {
                Map<String, List<Map<String, Object>>> new_map = new HashMap<String, List<Map<String, Object>>>();
                List<Map<String, Object>> new_list = new ArrayList<Map<String, Object>>();
                new_list.add(row_data);
                new_map.put("closed", new_list);
                ans_map.put(date, new_map);
            } else {
                Map<String, List<Map<String, Object>>> old_map = ans_map.get(date);
                List<Map<String, Object>> raise_list = new ArrayList<Map<String, Object>>();
                if (old_map.containsKey("closed"))
                    raise_list = old_map.get("closed");
                raise_list.add(row_data);
                old_map.put("closed", raise_list);
                ans_map.put(date, old_map);
            }
        }
        // 配息
        for (Map<String, Object> map : list3) {
            String date = sdf.format(map.get("DIV_DATE"));
            Map<String, Object> row_data = new HashMap<String, Object>();
            row_data.put("productType", map.get("PTYPE"));
            row_data.put("productId", null);
            row_data.put("productName", map.get("PRD_NAME"));
            row_data.put("yield", map.get("DIV_RATE"));

            if (!ans_map.containsKey(date)) {
                Map<String, List<Map<String, Object>>> new_map = new HashMap<String, List<Map<String, Object>>>();
                List<Map<String, Object>> new_list = new ArrayList<Map<String, Object>>();
                new_list.add(row_data);
                new_map.put("yield", new_list);
                ans_map.put(date, new_map);
            } else {
                Map<String, List<Map<String, Object>>> old_map = ans_map.get(date);
                List<Map<String, Object>> raise_list = new ArrayList<Map<String, Object>>();
                if (old_map.containsKey("yield"))
                    raise_list = old_map.get("yield");
                raise_list.add(row_data);
                old_map.put("yield", raise_list);
                ans_map.put(date, old_map);
            }
        }
        // 到期
        for (Map<String, Object> map : list4) {
            String date = sdf.format(map.get("DATE_OF_MATURITY"));
            Map<String, Object> row_data = new HashMap<String, Object>();
            row_data.put("productType", map.get("PTYPE"));
            row_data.put("productId", null);
            row_data.put("productName", map.get("PRD_NAME"));

            if (!ans_map.containsKey(date)) {
                Map<String, List<Map<String, Object>>> new_map = new HashMap<String, List<Map<String, Object>>>();
                List<Map<String, Object>> new_list = new ArrayList<Map<String, Object>>();
                new_list.add(row_data);
                new_map.put("maturity", new_list);
                ans_map.put(date, new_map);
            } else {
                Map<String, List<Map<String, Object>>> old_map = ans_map.get(date);
                List<Map<String, Object>> raise_list = new ArrayList<Map<String, Object>>();
                if (old_map.containsKey("maturity"))
                    raise_list = old_map.get("maturity");
                raise_list.add(row_data);
                old_map.put("maturity", raise_list);
                ans_map.put(date, old_map);
            }
        }
        // finally
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (Entry<String, Map<String, List<Map<String, Object>>>> entry : ans_map.entrySet()) {
            Map<String, Object> data_map = new HashMap<String, Object>();
            data_map.put("date", entry.getKey());
            data_map.put("raise", entry.getValue().get("raise"));
            data_map.put("closed", entry.getValue().get("closed"));
            data_map.put("yield", entry.getValue().get("yield"));
            data_map.put("maturity", entry.getValue().get("maturity"));
            data.add(data_map);
        }

        Map<String, Object> outputMap = new HashMap<String, Object>();
        outputMap.put("data", data);
        this.sendRtnObject(outputMap);
    }

    // 2.2.5 getSiteCalendar 取得行事曆(輔銷註點)
    public void getSiteCalendar(Object body, IPrimitiveMap header) throws Exception{
        WorkStation ws = DataManager.getWorkStation(uuid);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Map<String, Object> inputMap = (Map<String, Object>) body;
        DataAccessManager dam = this.getDataAccessManager();

        Date startDate = null;
        Date endDate = null;
        if (inputMap.get("startDate") != null)
            startDate = sdf.parse(ObjectUtils.toString(inputMap.get("startDate")));
        if (inputMap.get("endDate") != null)
            endDate = sdf.parse(ObjectUtils.toString(inputMap.get("endDate")));

        // crm124 sql 不能直接call, 這邊拿他的sql改
        QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT C.SEQ, C.STATUS, C.AS_EMP_ID, I.EMP_NAME, C.ONSITE_DATE AS DATETIME, I.ROLE_NAME, ");
        sql.append(" CASE C.ONSITE_PERIOD WHEN '1' THEN '0900' WHEN '2' THEN '1300' END AS STIME, ");
        sql.append(" CASE C.ONSITE_PERIOD WHEN '1' THEN '1200' WHEN '2' THEN '1700' END AS ETIME, ");
        sql.append(" C.ONSITE_BRH, C.NEW_ONSITE_BRH, V.BRANCH_NAME AS NEW_BRA, TO_CHAR(C.NEW_ONSITE_DATE,'YYYY/MM/DD') AS NEW_ONSITE_DATE, ");
        sql.append(" CASE C.NEW_ONSITE_PERIOD WHEN '1' THEN '上午' WHEN '2' THEN '下午' END AS NEW_ONSITE_PERIOD ");
        sql.append(" FROM TBCRM_WKPG_AS_CALENDAR C ");
        sql.append(" LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO I ON I.EMP_ID = C.AS_EMP_ID ");
        sql.append(" LEFT JOIN VWORG_DEFN_INFO V ON C.NEW_ONSITE_BRH = V.BRANCH_NBR ");
        sql.append(" WHERE 1 = 1 ");
        sql.append(" and C.ONSITE_BRH IN (:bra_nbr) ");
        if (startDate != null) {
            sql.append("and TRUNC(C.ONSITE_DATE) >= TRUNC(:start) ");
            queryCondition.setObject("start", startDate);
        }
        if (endDate != null) {
            sql.append("and TRUNC(C.ONSITE_DATE) < TRUNC(:end)+1 ");
            queryCondition.setObject("end", endDate);
        }
        queryCondition.setObject("bra_nbr", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
        queryCondition.setQueryString(sql.toString());
        List<Map<String, Object>> crm124_list = dam.exeQuery(queryCondition);
        Map<String, List<Map<String, Object>>> ans_map = new HashMap<String, List<Map<String, Object>>>();
        for (Map<String, Object> map : crm124_list) {
            String date = sdf.format(map.get("DATETIME"));
            Map<String, Object> row_data = new HashMap<String, Object>();
            row_data.put("eventId", map.get("SEQ"));
            row_data.put("noticeTypeId", null);
            row_data.put("empName", map.get("EMP_NAME"));
            row_data.put("startTime", map.get("STIME"));
            row_data.put("endTime", map.get("ETIME"));
            if ("2".equals(map.get("STATUS"))) {
                if (StringUtils.equals(ObjectUtils.toString(map.get("NEW_ONSITE_BRH")), ObjectUtils.toString(map.get("ONSITE_BRH"))))
                    row_data.put("title", "輔銷人員 " + map.get("ROLE_NAME") + " " + map.get("EMP_NAME") + " 變更(待覆核) 原駐點時間 : " + map.get("STIME") + " - " + map.get("ETIME") + "變更為: " + map.get("NEW_ONSITE_DATE") + map.get("NEW_ONSITE_PERIOD"));
                else
                    row_data.put("title", "輔銷人員 " + map.get("ROLE_NAME") + " " + map.get("EMP_NAME") + " 變更(待覆核) 原駐點時間 : " + map.get("STIME") + " - " + map.get("ETIME") + "變更駐點行至: " + map.get("NEW_BRA"));
            } else
                row_data.put("title", "輔銷人員 " + map.get("ROLE_NAME") + " " + map.get("EMP_NAME") + " 駐點時間 : " + map.get("STIME") + " - " + map.get("ETIME"));
            row_data.put("content", null);

            if (!ans_map.containsKey(date)) {
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                list.add(row_data);
                ans_map.put(date, list);
            } else
                ans_map.get(date).add(row_data);
        }
        // finally
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (Entry<String, List<Map<String, Object>>> entry : ans_map.entrySet()) {
            Map<String, Object> data_map = new HashMap<String, Object>();
            data_map.put("date", entry.getKey());
            data_map.put("event", entry.getValue());
            data.add(data_map);
        }

        Map<String, Object> outputMap = new HashMap<String, Object>();
        outputMap.put("data", data);
        this.sendRtnObject(outputMap);
    }

    // 2.2.6 updateSiteCalendar 更新行事曆(輔銷註點)
    public void updateSiteCalendar(Object body, IPrimitiveMap header) throws Exception{
        // no use

    }

    // 2.2.7 deleteToDoCalendar 刪除行事曆(代辦事項)
    public void deleteToDoCalendar(Object body, IPrimitiveMap header) throws Exception{
        Map<String, Object> inputMap = (Map<String, Object>) body;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        DataAccessManager dam = this.getDataAccessManager();

        // String date = inputMap.get("date").toString();
        String eventId = inputMap.get("eventId").toString();

        // crm121 delTodo
        TBCAM_CAL_SALES_TASKVO vo = (TBCAM_CAL_SALES_TASKVO) dam.findByPKey(TBCAM_CAL_SALES_TASKVO.TABLE_UID, new BigDecimal(eventId));
        if (vo != null)
            dam.delete(vo);

        Map<String, Object> outputMap = new HashMap<String, Object>();
        this.sendRtnObject(outputMap);
    }

}
